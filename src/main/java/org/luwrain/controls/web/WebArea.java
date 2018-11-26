/*
   Copyright 2012-2017 Michael Pozhidaev <michael.pozhidaev@gmail.com>
   Copyright 2015-2016 Roman Volovodov <gr.rPman@gmail.com>

   This file is part of LUWRAIN.

   LUWRAIN is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   LUWRAIN is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

package org.luwrain.controls.web;

import java.net.*;
import java.util.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.browser.*;
import org.luwrain.controls.*;
import org.luwrain.controls.doc.*;
import org.luwrain.doctree.*;

public class WebArea implements Area
{
/**
 * An interface to thread manager. A vast majority of work in the browser
 * engine is performed in background thread. So, the engine actually is
 * unable to return anything back to the browser area directly.  In
 * addition, general area functionality doesn't have anything allowing it
 * to manage threads. 
 *
 * This interface gives access to some implementation-dependent class
 * which is able to run some code in the main thread of
 * application. There are two types of the code which could be requested
 * to run: synchronous and asynchronous. The first type implies that
 * there is no need to wait until provided code is executed. The second
 * one means that the corresponding method may not return until the
 * requested code is fully executed. This allows to get the method return
 * value back to the caller.
 *
 * @see BrowserArea
 */
public interface ClientThread
{
    /**
     * Runs the requested code in asynchronous mode (no need to wait finishing).
     *
     * @param runnable The runnable object with the necessary code
     */
    void runAsync(Runnable runnable);

    /**
     * Runs the requested code in synchronous mode (it is necessary to wait finishing).
     *
     * @param callable The callable object with the necessary code
     * @return The return value of the provided code after the execution
     */
    Object runSync(java.util.concurrent.Callable callable);
}

    
public interface BrowserFactory
{
    Browser newBrowser(BrowserEvents events);
}

    public interface Callback
{
    public enum MessageType {PROGRESS, ALERT, ERROR};

    void onBrowserRunning();
    void onBrowserSuccess(String title);
    void onBrowserFailed();
    int getAreaVisibleWidth(Area area);
    boolean confirm(String text);
    String prompt(String message, String text);
    void message(String text, MessageType type);
}

    
static public final class Params
{
    ControlEnvironment context = null;
    Callback callback = null;
    ClientThread clientThread = null;
    BrowserFactory browserFactory = null;
}

    protected final ControlEnvironment context = null;
    protected final Callback callback;
    protected final Browser browser;
    protected Events.State state = Events.State.READY;
    protected int progress = 0;

    public WebArea(Params params)
    {
		NullCheck.notNull(params, "params");
		NullCheck.notNull(params.context, "params.context");
		NullCheck.notNull(params.clientThread, "params.clientThread");
		NullCheck.notNull(params.callback, "params.callback");
		NullCheck.notNull(params.browserFactory, "params.browserFactory");
		this.browser = params.browserFactory.newBrowser(new Events(params.clientThread, this, params.callback));
	this.callback = params.callback;
    }

    /**
     * Performs DOM scanning with updating the auxiliary structures used for
     * user navigation. This method may be called only if the page is
     * successfully loaded and the browser isn't busy with background work.
     *
     * @return true if the browser is free and able to do the refreshing, false otherwise
     */
        boolean refresh()
    {
	//Without reloading the page
	browser.rescanDom();
	updateView();
	context.playSound(Sounds.OK);
	return true;
    }

    protected boolean updateView()
    {
   	final int x = 0;//getHotPointX();
	final int y = 0;//getHotPointY();
	final Object obj = browser.runSafely(()->{
		return null;
		//FIXME:return documentBuilder.build(browser);
	    });
	if (obj == null || !(obj instanceof Document))
	    return false;
	//setDocument((Document)obj, callback.getAreaVisibleWidth(this));
	//this.onMoveHotPoint(new MoveHotPointEvent(x,y,false));
	return true;
    }

    /**Checks if the browser has valid loaded page
     *
     * @return true if there is any successfully loaded page, false otherwise
     */ 
    boolean noWebContent()
    {
	return state != Events.State.SUCCEEDED;
    }

    /**
     * Starts loading of the page by the given URL. This method exits
     * immediately without waiting when the requested page is actually loaded
     * and ready for observing. In some cases the area may refuse to start
     * loading. Usually it means that the area is in inappropriate state
     * (already busy with loading of another page).
     *
     * @param url The URL of the page to open
     * @return True if the area starts the actual loading, false otherwise (the area is in inappropriate state)
     */
    public boolean open(String url)
    {
	NullCheck.notNull(url, "url");
	if (url.isEmpty())
	    return false;
	browser.loadByUrl(url);
	return true;
    }

    boolean stop()
    {
	browser.stop();
	return true;
    }

    public String getBrowserTitle()
    {
	return browser.getTitle();
    }

    public String getBrowserUrl()
    {
	return browser.getUrl();
    }

    @Override public int getHotPointX()
    {
	return 0;
    }

    @Override public int getHotPointY()
    {
	return 0;
    }

    @Override public int getLineCount()
    {
	return 1;
    }

    @Override public String getLine(int index)
    {
	return "";
    }

    @Override public String getAreaName()
    {
	final String title = browser.getTitle();
	return title != null?title:"";
    }

    @Override public boolean onInputEvent(KeyboardEvent event)
    {
	return false;
    }


    @Override public boolean onSystemEvent(EnvironmentEvent event)
    {
	NullCheck.notNull(event, "event");
	switch(event.getCode())
	{
	case REFRESH:
	    refresh();
	    return true;
	default:
	    return false;
	}
    }

    @Override public boolean onAreaQuery(AreaQuery query)
    {
	return false;
    }

        @Override public Action[] getAreaActions()
    {
	return new Action[0];
    }


    void onPageChangeState(Events.State state)
    {
	NullCheck.notNull(state, "state");
	this.state = state;
	switch(state)
	{
	case RUNNING:
	    callback.onBrowserRunning();
	    return;
	case SUCCEEDED:
	    refresh();
	    callback.onBrowserSuccess(browser.getTitle());
	    return;
	case FAILED:
	    callback.onBrowserFailed();
	    return;
	case CANCELLED:
	case READY:
	case SCHEDULED:
	    return;
	}
    }

    void onProgress(Number progress)
    {
	NullCheck.notNull(progress, "progress");
	this.progress = (int)(progress==null?0:Math.floor(progress.doubleValue()*100));
    }

void onDownloadStart(String url)
    {
	//FIXME:
    }

    protected void noContentMsg()
    {
	context.setEventResponse(DefaultEventResponse.hint(Hint.NO_CONTENT));
    }

    /*
    protected boolean noContent()
    {
	if (isEmpty())
	{
	    noContentMsg();
	    return true;
	}
	return false;
    }
    */
}

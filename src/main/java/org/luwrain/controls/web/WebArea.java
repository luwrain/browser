/*
   Copyright 2012-2018 Michael Pozhidaev <michael.pozhidaev@gmail.com>
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

//LWR_API 1.0

package org.luwrain.controls.web;

import java.net.*;
import java.util.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.browser.*;
import org.luwrain.controls.*;

public class WebArea implements Area
{
    static final String LOG_COMPONENT = "web";

    /**
     * An interface to thread manager. A vast majority of browser work
     * is performed in background thread. So, the engine actually is
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
	public enum MessageType {PROGRESS, ALERT, ERROR
	};

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
	public ControlEnvironment context = null;
	public Callback callback = null;
	public ClientThread clientThread = null;
	public BrowserFactory browserFactory = null;
    }

    protected final ControlEnvironment context;
    protected final Callback callback;
    protected final Browser browser;
    protected View view = null;
    protected View.Iterator it = null;
    protected int rowIndex = 0;
    protected Events.State state = Events.State.READY;
    protected int progress = 0;

    protected int itemIndex = 0;

    public WebArea(Params params)
    {
	NullCheck.notNull(params, "params");
	NullCheck.notNull(params.context, "params.context");
	NullCheck.notNull(params.clientThread, "params.clientThread");
	NullCheck.notNull(params.callback, "params.callback");
	NullCheck.notNull(params.browserFactory, "params.browserFactory");
	this.context = params.context;
	this.browser = params.browserFactory.newBrowser(new Events(params.clientThread, this, params.callback));
	if (this.browser == null)
	    throw new NullPointerException("Browser factory may not return null");
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
	/*
	if (browser.isBusy())
	    return false;
	*/
	browser.rescanDom();
	updateView();
	return true;
    }

    protected boolean updateView()
    {
	final Object obj = browser.runSafely(()->{
		try {
		    final Model model = new ModelBuilder().build(browser);
		    try { model.dumpToFile(new java.io.File(new java.io.File("/tmp"), Model.makeDumpFileName(browser.getUrl()))); } catch(Exception e) { Log.error(LOG_COMPONENT, "unable to make a dump file:" + e.getClass().getName() + ":" + e.getMessage()); }
		    Log.debug(LOG_COMPONENT, "prepared the model with " + model.containers.length + " containers");
		    return new ViewBuilder(model).build();
		}
		catch(Throwable e)
		{
		    Log.error(LOG_COMPONENT, "the construction of web view and model failed:" + e.getClass().getName() + ":" + e.getMessage());
		    e.printStackTrace();
		    return null;
		}
	    });
	if (obj == null || !(obj instanceof View))
	{
	    Log.warning(LOG_COMPONENT, "unable to build a view");
	    return false;
	}
	this.view = (View)obj;
	this.it = view.createIterator();
	this.rowIndex = 0;
	return true;
    }

    /**Checks if the browser has valid loaded page
     *
     * @return true if there is any successfully loaded page, false otherwise
     */ 
    boolean isEmpty()
    {
	return view == null || it == null;
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
	if (!url.toLowerCase().startsWith("http://") && !url.toLowerCase().startsWith("https://"))
	    browser.loadByUrl("http://" + url); else
	    browser.loadByUrl("http://" + url);
	return true;
    }

    public void stop()
    {
	browser.stop();
    }

    public String getTitle()
    {
	final String res = browser.getTitle();
	return res != null?res:"";
    }

    public String getUrl()
    {
	final String res = browser.getUrl();
	return res != null?res:"";
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
	NullCheck.notNull(event, "event");
	if (event.isSpecial() && !event.isModified())
	    switch(event.getSpecial())
	    {
	    case ARROW_DOWN:
		return onMoveDown(event);
	    case ARROW_UP:
		return onMoveUp(event);
	    }
	return false;
    }

        protected boolean onMoveUp(KeyboardEvent event)
    {
	NullCheck.notNull(event, "event");
	if (noContent())
	    return true;
	if (!it.movePrev())
	{
	    context.setEventResponse(DefaultEventResponse.hint(Hint.NO_ITEMS_ABOVE));
	    return true;
	}
	announceRow();
	return true;
    }


    protected boolean onMoveDown(KeyboardEvent event)
    {
	NullCheck.notNull(event, "event");
	if (noContent())
	    return true;
	if (!it.isLastRow(rowIndex))
	{
	    ++rowIndex;
	    announceRow();
	    return true;
	}
	if (!it.moveNext())
	{
	    context.setEventResponse(DefaultEventResponse.hint(Hint.NO_ITEMS_BELOW));
	    return true;
	}
	rowIndex = 0;
	announceRow();
	return true;
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

    void onNewState(Events.State state)
    {
	NullCheck.notNull(state, "state");
	this.state = state;
	switch(state)
	{
	case SUCCEEDED:
	    refresh();
	    callback.onBrowserSuccess(getTitle());
	    return;
	case RUNNING:
	    callback.onBrowserRunning();
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

    void onProgress(int progress)
    {
	this.progress = progress;
    }

    void onDownloadStart(String url)
    {
	//FIXME:
    }

    public void announceRow()
    {
	if (isEmpty())
	    return;

		final Sounds sound;
		if (rowIndex == 0)
	switch(it.getType())
	{
	case PARA:
	    sound = Sounds.PARAGRAPH;
	    break;
	case LIST_ITEM:
	    sound = Sounds.LIST_ITEM;
	    break;
	case HEADING:
	    sound = Sounds.DOC_SECTION;
	    break;
	default:
	    sound = null;
	} else
		    sound = null;
	final StringBuilder b = new StringBuilder();
	for(WebObject obj: it.getLine(rowIndex))
	    b.append(obj.getText());
	context.setEventResponse(DefaultEventResponse.text(sound, new String(b)));
    }

    protected void noContentMsg()
    {
	context.setEventResponse(DefaultEventResponse.hint(Hint.NO_CONTENT));
    }

    protected boolean noContent()
    {
	if (isEmpty())
	{
	    noContentMsg();
	    return true;
	}
	return false;
    }
}

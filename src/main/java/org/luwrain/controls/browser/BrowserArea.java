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

package org.luwrain.controls.browser;

import java.net.*;
import java.util.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.browser.*;
import org.luwrain.controls.*;
import org.luwrain.controls.doctree.*;
import org.luwrain.doctree.*;

public class BrowserArea extends DoctreeArea
{
    static private final String LOG_COMPONENT = "browser";

    protected final Callback callback;
    protected final DocumentBuilder documentBuilder;
    protected final Browser browser;
    protected Events.State state = Events.State.READY;
    protected int progress = 0;

    public BrowserArea(ControlEnvironment context, Browser browser,
		       Callback callback, ClientThread clientThread, DocumentBuilder documentBuilder,
		       Announcement announcement)
    {
	super(context, announcement);
	NullCheck.notNull(context, "context");
	NullCheck.notNull(browser, "browser");
	NullCheck.notNull(callback, "callback");
	NullCheck.notNull(clientThread, "clientThread");
	NullCheck.notNull(documentBuilder, "documentBuilder");
	this.browser = browser;
	this.callback = callback;
	this.documentBuilder = documentBuilder;
	this.browser.init(new Events(clientThread, this, callback));
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
   	final int x=getHotPointX();
	final int y=getHotPointY();
	final Object obj = browser.runSafely(()->{
return documentBuilder.build(browser);
	    });
	if (obj == null || !(obj instanceof Document))
	    return false;
	setDocument((Document)obj, callback.getAreaVisibleWidth(this));
	this.onMoveHotPoint(new MoveHotPointEvent(x,y,false));
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
	if (isEmpty())
	    return false;
	browser.stop();
	return true;
    }

    public String getBrowserTitle()
    {
	if (isEmpty())
	    return "";
	return browser.getTitle();
    }

    public String getBrowserUrl()
    {
	if (isEmpty())
	    return "";
	return browser.getUrl();
    }

    @Override public String getAreaName()
    {
	final String title = browser.getTitle();
	return title != null?title:"";
    }

    @Override public boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	NullCheck.notNull(event, "event");
	switch(event.getCode())
	{
	case REFRESH:
	    refresh();
	    return true;
	default:
	    return super.onEnvironmentEvent(event);
	}
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
	default:
	    Log.warning(LOG_COMPONENT, "unexpected new page state:" + state);
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
	context.hint(Hints.NO_CONTENT);
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

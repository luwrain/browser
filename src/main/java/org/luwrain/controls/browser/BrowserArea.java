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
    protected final Callback callback;
    protected final DocumentBuilder documentBuilder;
    protected final Browser browser;

    private Document doc=null;

    protected Events.State state = Events.State.READY;
    protected int progress = 0;

    //    protected final Vector<HistoryElement> elementHistory = new Vector<HistoryElement>();
    protected boolean complexMode = false;

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
	final long t1=new Date().getTime();
	browser.rescanDom();
	complexMode = false;
	updateView();
	return true;
    }

    protected void updateView()
    {
   	final int x=getHotPointX();
final int y=getHotPointY();
	doc = documentBuilder.build(browser);
	//   	page.setWatchNodes(builder.watch);
	doc.commit();
	setDocument(doc, callback.getAreaVisibleWidth(this));
	this.onMoveHotPoint(new MoveHotPointEvent(x,y,false));
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
	Log.debug("browser", "trying to cancel loading");
	browser.stop();
	return true;
    }

    @Override public boolean onAreaQuery(AreaQuery query)
    {
	return false;
    }

    @Override public String getAreaName()
    {
	return browser.getTitle()+" "+state.name()+" "+progress;
    }

    @Override protected boolean onSpace(KeyboardEvent event)
    {
    	onClick();
    	return super.onSpace(event);
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

public boolean onHistoryPrev()
	{
    	browser.executeScript("history.go(-1);");
		return true;
	}

    public void toggleVisibility()
    {
browser.setVisibility(!browser.getVisibility());
    }


    /*
	protected boolean onBackspace()
    {
	if(elementHistory.isEmpty()) return false;
	HistoryElement h=elementHistory.lastElement();
	elementHistory.remove(elementHistory.size()-1);
	complexMode=h.mode;
	//current = h.element;
	updateView();
	return true;
    }
    */

    /**Performs some default action relevant to the element under the hot
     * point. What the exact action will be done is dependent on the type of the
     * element under the hot point. If there is a form input edit, the user
     * will get an offer to enter some text. If there is a form list, the
     * user will get a popup to choose some item from the list and so
     * on. This operation may be performed only if the browser is free
     * (meaning, not empty and not busy).
     *
     * @return true if the operation has been done, false otherwise (usually the browser is busy or doesn't have a loaded page)
     */
    protected boolean onClick()
    {
	/*
    	if (isEmpty() || isBusy())
    	    return false;
    	Run run=this.getCurrentRun();
    	if(run instanceof TextRun)
    	{
    		Object t=((TextRun)run).getAssociatedObject();
    		if(t==null) return false;
    		if(t instanceof ElementAction)
    		{
    			ElementAction action=(ElementAction)t;
    			Boolean result=null;
    			switch(action.type)
    			{
    				case UNKNOWN:
					case CLICK:
						result = emulateClick(action.element);
						break;
					case EDIT:
						result = onFormEditText(action.element);
						break;
					case SELECT:
						result = onFormSelectFromList(action.element);
						break;
					default:
						Log.error("web-browser","unknown action type: "+action.type.name());
						return false;
    			}
				((Actions)callback).skipNextContentChangedNotify();
				page.doFastUpdate();
    		} else
    		{
    			Log.error("web-browser","current TextRun have unknown associated object, type "+t.getClass());
    		}
    	}
    	//System.out.println(run.getClass());
	*/
    	return false;
    }

    /**Asks the browser core to emulate the action which looks like the user
     * clicks on the given element. This operation may be performed only if
     * the browser is free (meaning, not empty and not busy).
     *
     * @param el The element to emulate click on
     * @return true if the operation has been done, false otherwise (usually the browser is busy or doesn't have a loaded page)
     */
    protected boolean emulateClick(BrowserIterator el)
    {
	NullCheck.notNull(el, "el");
	if (isEmpty())
	    return false;
	el.emulateClick();
	return true;
    }
    protected boolean emulateSubmit(BrowserIterator el)
    {
	NullCheck.notNull(el, "el");
	if (isEmpty())
	    return false;
	el.emulateSubmit();
	return true;
    }

    protected boolean onFormEditText(BrowserIterator el)
    {
	NullCheck.notNull(el, "el");
	if (isEmpty())
	    return false;
	final String oldValue = el.getText();
	final String newValue = callback.askFormTextValue(oldValue != null?oldValue:"");
	if (newValue == null) 
	    return true;
	el.setText(newValue);
	updateView();
	return true;
    }

    protected boolean onFormSelectFromList(BrowserIterator el)
    {
	NullCheck.notNull(el, "el");
	if (isEmpty())
	    return false;
	final String[] items = el.getMultipleText();
	if (items == null || items.length==0) 
	    return true; // FIXME:
	final String res = callback.askFormListValue(items, true);
	if (res == null)
	    return true;
	el.setText(res);
	updateView();
	return true;
    }

    protected void onPageChangeState(Events.State state)
    {
	NullCheck.notNull(state, "state");
	Log.debug("browser", "new page state:" + state);
	this.state = state;
	switch(state)
	{
	case RUNNING:
	    callback.onBrowserRunning();
	    return;
	case SUCCEEDED:
	    refresh();
	    goToStartPage();
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
	    Log.warning("browser", "unexpected new page state:" + state);
	}
    }

    private void goToStartPage()
	{
    	this.onMoveHotPoint(new MoveHotPointEvent(0,0,false));
	}

    void onContentChanged()
    {
    	refresh();
		callback.onBrowserContentChanged(browser.getLastTimeChanged());
    }

void onProgress(Number progress)
    {
	NullCheck.notNull(progress, "progress");
	this.progress = (int)(progress==null?0:Math.floor(progress.doubleValue()*100));
    }

    protected void onDownloadStart(String url)
    {
	//FIXME:
    }

    protected void noContentMsg()
    {
	environment.hint(Hints.NO_CONTENT);
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

/*
   Copyright 2012-2018 Michael Pozhidaev <michael.pozhidaev@gmail.com>
   Copyright 2015-2016 Roman Volovodov <gr.rPman@gmail.com>
o
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

import java.io.*;
import java.net.*;
import java.util.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.core.queries.*;
import org.luwrain.browser.*;
import org.luwrain.controls.*;

public class WebArea implements Area
{
    static final String LOG_COMPONENT = "web";
    static private final int MIN_VISIBLE_WIDTH = 20;

    interface Appearance
    {
	void announceFirstRow(Container.Type type, WebObject[] objs);
	void announceRow(WebObject[] objs);
	String getRowTextAppearance(WebObject[] objs);
    }

    public interface ClickHandler
    {
	boolean onWebClick(WebArea area, int rowIndex, WebObject webObj);
    }

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
	boolean confirm(String text);
	String prompt(String message, String text);
	void message(String text, MessageType type);
    }

    static public final class Params
    {
	public ControlEnvironment context = null;
	public Appearance appearance;
	public ClickHandler clickHandler = null;
	public Callback callback = null;
	public ClientThread clientThread = null;
	public BrowserFactory browserFactory = null;
    }

    protected final ControlEnvironment context;
    protected final Appearance appearance;
    protected final Callback callback;
    protected ClickHandler clickHandler = null;
    protected final Browser browser;
    protected View view = null;
    protected View.Iterator it = null;
    protected int rowIndex = 0;
    protected int hotPointX = 0;
    protected Events.State state = null;
    protected int progress = 0;

    protected int itemIndex = 0;

    public WebArea(Params params)
    {
	NullCheck.notNull(params, "params");
	NullCheck.notNull(params.context, "params.context");
	NullCheck.notNull(params.appearance, "params.appearance");
	NullCheck.notNull(params.clientThread, "params.clientThread");
	NullCheck.notNull(params.callback, "params.callback");
	NullCheck.notNull(params.browserFactory, "params.browserFactory");
	this.context = params.context;
	this.appearance = params.appearance;
	this.browser = params.browserFactory.newBrowser(new Events(params.clientThread, this, params.callback));
	if (this.browser == null)
	    throw new NullPointerException("Browser factory may not return null");
	this.callback = params.callback;
	this.clickHandler = params.clickHandler;
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
	//FIXME:if busy
	final int areaWidth = context.getAreaVisibleWidth(this);
	browser.rescanDom();
	updateView(areaWidth);
	return true;
    }

    public boolean updateView(int areaWidth)
    {
	final Object obj = browser.runSafely(()->{
		try {
		    final ContainersList containers = new ModelBuilder().build(browser);
		    Log.debug(LOG_COMPONENT, "containers prepared: " + containers.getContainerCount());
		    return new ViewBuilder(containers.getContainers()).build(appearance, Math.max(areaWidth, MIN_VISIBLE_WIDTH));
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
	try {
	    final String fileName = View.makeDumpFileName(browser.getUrl());
	    final File structFile = new File(new File("/tmp"), fileName);
	    final File textFile = new File(new File("/tmp"), fileName + ".txt");
	    view.dumpToFile(structFile);
	    final BufferedWriter w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(textFile)));
	    try {
		for(int i = 0;i < view.getLineCount();++i)
		{
		    w.write(view.getLine(i));
		    w.newLine();
		}
	    }
	    finally {
		w.close();
	    }
	}
	catch(Exception e)
	{
	    Log.error(LOG_COMPONENT, "unable to make a dump file:" + e.getClass().getName() + ":" + e.getMessage());
	}
	this.it = view.createIterator();
	this.rowIndex = 0;
	context.onAreaNewContent(this);
	context.onAreaNewHotPoint(this);
	context.onAreaNewName(this);
	return true;
    }

    /**Checks if the browser has valid loaded page
     *
     * @return true if there is any successfully loaded page, false otherwise
     */ 
    public boolean isEmpty()
    {
	return view == null || it == null;
    }

    boolean isBusy()
    {
	return state == Events.State.SCHEDULED || state == Events.State.RUNNING;
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
	    browser.loadByUrl(url);
	return true;
    }

    public void stop()
    {
	browser.stop();
    }

    public boolean goHistoryPrev()
    {
	return browser.goHistoryPrev();
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

    public WebObject getSelectedObj()
    {
	if (isEmpty())
	    return null;
		final WebObject[] row = it.getRow(rowIndex);
		int offset = 0;
		for(int i = 0;i < row.length;++i)
		{
		    if (hotPointX >= offset && hotPointX < offset + row[i].getWidth())
			return row[i];
		    offset += row[i].getWidth();
		}
		return null;
    }

    @Override public int getHotPointX()
    {
	if (isEmpty())
	return 0;
	return it.getX() + hotPointX;
    }

    @Override public int getHotPointY()
    {
	if (isEmpty())
	return 0;
	return it.getY() + rowIndex;
    }

    @Override public int getLineCount()
    {
	if (isEmpty())
	return 1;
	return view.getLineCount();
    }

    @Override public String getLine(int index)
    {
	if (index < 0)
	    throw new IllegalArgumentException("index (" + index + ") may not be negative");
	if (isEmpty())
	    return (index == 0)?noContentStr():"";
	return view.getLine(index);
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
	    case ENTER:
		return onClick();
	    case ARROW_RIGHT:
		return onMoveRight(event);
	    case ARROW_LEFT:
		return onMoveLeft(event);
	    case ARROW_DOWN:
		return onMoveDown(event);
	    case ARROW_UP:
		return onMoveUp(event);
	    }
	return false;
    }

    protected boolean onClick()
    {
	if (noContent())
	    return true;
	if (clickHandler == null)
	    return false;
	final WebObject webObj = getSelectedObj();
	if (webObj == null)
	    return false;
	return clickHandler.onWebClick(this, rowIndex, webObj);
    }

    protected boolean onMoveRight(KeyboardEvent event)
    {
	NullCheck.notNull(event, "event");
	if (noContent())
	    return true;
	final String text = appearance.getRowTextAppearance(it.getRow(rowIndex));
	if (hotPointX >= text.length())
	{
	    context.setEventResponse(DefaultEventResponse.hint(Hint.END_OF_LINE));
	    return true;
	    }
	++hotPointX;
		if (hotPointX >= text.length())
	{
	    context.setEventResponse(DefaultEventResponse.hint(Hint.END_OF_LINE));
	    return true;
	    }
		context.onAreaNewHotPoint(this);
		context.setEventResponse(DefaultEventResponse.letter(text.charAt(hotPointX)));
		return true;
    }

		    protected boolean onMoveLeft(KeyboardEvent event)
    {
	NullCheck.notNull(event, "event");
	if (noContent())
	    return true;
	final String text = appearance.getRowTextAppearance(it.getRow(rowIndex));
	if (hotPointX == 0)
	{
	    context.setEventResponse(DefaultEventResponse.hint(Hint.BEGIN_OF_LINE));
	    return true;
	    }
	--hotPointX;
		if (hotPointX >= text.length())
	{
	    context.setEventResponse(DefaultEventResponse.hint(Hint.END_OF_LINE));
	    return true;
	    }
		context.onAreaNewHotPoint(this);
		context.setEventResponse(DefaultEventResponse.letter(text.charAt(hotPointX)));
		return true;
    }

        protected boolean onMoveUp(KeyboardEvent event)
    {
	NullCheck.notNull(event, "event");
	if (noContent())
	    return true;
	if (rowIndex > 0)
	{
	    --rowIndex;
	    hotPointX = 0;
	    context.onAreaNewHotPoint(this);
	    announceRow();
	    return true;
	}
	if (!it.movePrev())
	{
	    context.setEventResponse(DefaultEventResponse.hint(Hint.NO_ITEMS_ABOVE));
	    return true;
	}
	final int count = it.getRowCount();
	rowIndex = count > 0?count - 1:0;
	hotPointX = 0;
	context.onAreaNewHotPoint(this);
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
	    hotPointX = 0;
	    context.onAreaNewHotPoint(this);
	    announceRow();
	    return true;
	}
	if (!it.moveNext())
	{
	    context.setEventResponse(DefaultEventResponse.hint(Hint.NO_ITEMS_BELOW));
	    return true;
	}
	rowIndex = 0;
	hotPointX = 0;
	context.onAreaNewHotPoint(this);
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
		    NullCheck.notNull(query, "query");
		    switch(query.getQueryCode())
		    {
		    case AreaQuery.UNIREF_AREA:
			if (isEmpty())
			    return false;
			{
			    final String title = getTitle();
			    final String url = getUrl();
			    ((UniRefAreaQuery)query).answer("link:" + title + ":web:" + url);
			}
			return true;
		    case AreaQuery.URL_AREA:
			if (isEmpty())
			    return false;
			((UrlAreaQuery)query).answer(getUrl());
			return true;
		    case AreaQuery.BACKGROUND_SOUND:
			if (isBusy())
			{
			    ((BackgroundSoundQuery)query).answer(new BackgroundSoundQuery.Answer(BkgSounds.FETCHING));
			    return true;
			}
			return false;
		    default:
			return false;
		    }
		}
    @Override public Action[] getAreaActions()
    {
	return new Action[0];
    }

    void onNewState(Events.State state)
    {
	NullCheck.notNull(state, "state");
	this.state = state;
	context.onAreaNewBackgroundSound(this);
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
		if (rowIndex == 0)
		    appearance.announceFirstRow(it.getType(), it.getRow(rowIndex)); else
		    		    appearance.announceRow(it.getRow(rowIndex));
    }

    protected String noContentStr()
    {
	return "Содержимое веб-страницы отсутствует";
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

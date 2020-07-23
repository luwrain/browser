
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
import org.luwrain.controls.block.*;

public class WebArea extends BlockArea
{
    static final String LOG_COMPONENT = "web";
    static private final int MIN_VISIBLE_WIDTH = 20;

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

    static public final class Params extends BlockArea.Params
    {
	public Callback callback = null;
	public ClientThread clientThread = null;
	public BrowserFactory browserFactory = null;
    }

    protected final Browser browser;
protected Callback callback = null;
protected ClientThread clientThread = null;

    
    protected Events.State state = null;
    protected int progress = 0;

    public WebArea(WebArea.Params params)
    {
	super(params);
	NullCheck.notNull(params.clientThread, "params.clientThread");
	NullCheck.notNull(params.callback, "params.callback");
	NullCheck.notNull(params.browserFactory, "params.browserFactory");
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
	//FIXME:if busy
	final int areaWidth = context.getAreaVisibleWidth(this);
	browser.update();
	updateView(areaWidth);
	return true;
    }

    public boolean updateView(int areaWidth)
    {
	final Object obj = browser.runSafely(()->{
		try {
		    final Container[] containers = new ModelBuilder().build(browser);
		    Log.debug(LOG_COMPONENT, "containers prepared: " + containers.length);
		    return containers;
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
	    clear();
	    return false;
	}
	setBlocks((Block[])obj, 100);
	return true;
    }

    /**Checks if the browser has valid loaded page
     *
     * @return true if there is any successfully loaded page, false otherwise
     */ 
    public boolean isEmpty()
    {
	return super.isEmpty();
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

    @Override public String getAreaName()
    {
	final String title = browser.getTitle();
	return title != null?title:"";
    }

    @Override public boolean onSystemEvent(SystemEvent event)
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

}

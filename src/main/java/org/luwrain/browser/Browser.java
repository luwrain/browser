/*
   Copyright 2012-2020 Michael Pozhidaev <msp@luwrain.org>
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

package org.luwrain.browser;

import java.io.*;
import java.util.concurrent.*;

import org.luwrain.core.*;
import org.luwrain.browser.BrowserEvents;
import org.luwrain.browser.BrowserParams;
import org.luwrain.graphical.*;

public final class Browser extends Base
{
    static final int LAST_MODIFIED_SCAN_INTERVAL = 100; // lastModifiedTime rescan interval in milliseconds
    static final String LUWRAIN_NODE_TEXT="luwrain_node_text"; // javascript window's property names for using in executeScrypt

    private final Luwrain luwrain;

    public Browser(BrowserParams params)
    {
	super(params);
	NullCheck.notNull(params.luwrain, "params.luwrain");
	this.luwrain = params.luwrain;
    }

    @Override public void update()
    {
	FxThread.runSync(()->super.update());
    }

    public void close()
    {
	//	interaction.closeBrowser(this);
    }

    public void showGraphical()
    {
	luwrain.showGraphical(()->{
		    webView.setVisible(true);
		    webView.requestFocus();
		    return webView;
	    });
	    }

    @Override public void setVisibility(boolean visible)
    {
	/*
	interaction.disableGraphicalMode();
	FxThread.runSync(()->webView.setVisible(false));
	*/
    }

    public boolean getVisibility()
    {
	return webView.isVisible();//FIXME:
    }

    public void loadByUrl(String url)
    {
	NullCheck.notNull(url, "url");
	    FxThread.runSync(()->webEngine.load(url));
    }

    public void loadByText(String text)
    {
	NullCheck.notNull(text, "text");
	    FxThread.runSync(()->webEngine.loadContent(text));
    }

    public boolean goHistoryPrev()
    {
	final Object res = FxThread.call(()->{
		if (webEngine.getHistory().getCurrentIndex() <= 0)
		    return new Boolean(false);
		webEngine.getHistory().go(-1);
		return new Boolean(true);
	    });
	    if (res == null || !(res instanceof Boolean))
		return false;
	    return ((Boolean)res).booleanValue();
    }

    public void stop()
    {
	FxThread.runSync(()->webEngine.getLoadWorker().cancel());
    }

    public String getTitle()
    {
	if(webEngine == null)
	    return "";
	return webEngine.titleProperty().get();
    }

    public synchronized String getUrl()
    {
	if(webEngine == null)
	    return "";
	return webEngine.getLocation();
    }

    public Object runSafely(Callable callable)
    {
	NullCheck.notNull(callable, "callable");
	return FxThread.call(callable);
    }

    @Override public synchronized Object executeScript(String script)
    {
	NullCheck.notNull(script, "script");
	if(script.trim().isEmpty() || webEngine == null)
	    return null;
	return FxThread.call(()->super.executeScript(script));
    }

    public org.luwrain.browser.BrowserIterator createIterator()
    {
	FxThread.ensure();
	return new BrowserIterator(this);
    }

    public int getElementCount()
    {
	if (domScanRes == null)
	    return 0;
	return domScanRes.dom.size();
    }
}

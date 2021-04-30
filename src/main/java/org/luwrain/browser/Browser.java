/*
   Copyright 2012-2021 Michael Pozhidaev <msp@luwrain.org>
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

import java.util.concurrent.*;

import org.luwrain.core.*;
import org.luwrain.graphical.*;

public final class Browser extends Base
{
    private final Luwrain luwrain;
    private org.luwrain.base.Interaction.GraphicalModeControl graphicalModeControl = null;

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
	hideGraphical();
    }

    public void showGraphical()
    {
	luwrain.showGraphical((control)->{
		webView.setVisible(true);
		webView.requestFocus();
		this.graphicalModeControl = control;
		return webView;
	    });
    }

    @Override protected void hideGraphical()
    {
	if (graphicalModeControl != null)
	    graphicalModeControl.close();
    }

    public void loadByUrl(String url)
    {
	NullCheck.notEmpty(url, "url");
	FxThread.runSync(()->webEngine.load(url));
    }

    public void loadByText(String text)
    {
	NullCheck.notNull(text, "text");
	FxThread.runSync(()->webEngine.loadContent(text));
    }

    public void stop()
    {
	FxThread.runSync(()->webEngine.getLoadWorker().cancel());
    }

    public boolean goPrev()
    {
	final Object res = FxThread.call(()->{
		if (webEngine.getHistory().getCurrentIndex() <= 0)
		    return new Boolean(false);
		webEngine.getHistory().go(-1);
		return new Boolean(true);
	    });
	return ((Boolean)res).booleanValue();
    }

    public String getTitle()
    {
	final Object res = FxThread.call(()->{ return webEngine.titleProperty().get(); });
	return res != null?res.toString():"";
    }

    public String getUrl()
    {
	final Object res = FxThread.call(()->{ return webEngine.getLocation(); });
	return res != null?res.toString():"";
    }

    public Object runSafely(Callable callable)
    {
	NullCheck.notNull(callable, "callable");
	return FxThread.call(callable);
    }

    @Override public Object executeScript(String script)
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

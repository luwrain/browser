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

package org.luwrain.app.webinspector;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.browser.*;
import org.luwrain.app.base.*;

public final class App extends AppBase <Strings>implements Application
{
    static final String LOG_COMPONENT = "webins";

    private final String arg;
    Item[] items = new Item[0];
    String[] attrs = new String[0];
    private MainLayout mainLayout = null;
    private Browser browser = null;
    private Conversations conv = null;

    public App(String arg)
    {
	super(Strings.NAME, Strings.class, "luwrain.webinspector");
	this.arg = arg;
    }

    public App()
    {
	this(null);
    }

    @Override public AreaLayout onAppInit()
    {
	this.conv = new Conversations(this);
	this.browser = BrowserFactory.newBrowser(getLuwrain(), new Events());
	if (browser == null)
	    return null;
	this.mainLayout = new MainLayout(this);
	setAppName(getStrings().appName());
	return mainLayout.getAreaLayout();
    }

    void fillAttrs(Item item)
    {
	NullCheck.notNull(item, "item");
	this.attrs = (String[])browser.runSafely(()->{
		final Map<String, String> attrMap = item.it.getAttrs();
		final List<String> res = new LinkedList();
		if (!item.it.getTagName().isEmpty())
		    res.add("<" + item.it.getTagName() + ">");
		res.add(item.it.getRect().toString());
		for(Map.Entry<String, String> e: attrMap.entrySet())
		{
		    res.add(e.getKey() + ": " + e.getValue());
		}
		final String style = item.it.getAllComputedStyles();
		if (style != null && !style.trim().isEmpty())
		{
		    res.add("Стили:");//FIXME:
		    final String[] styles = style.split(";", -1);
		    Arrays.sort(styles);
		    for(String s: styles)
			res.add(s.trim());
		}
		return res.toArray(new String[res.size()]);
	    });
    }

    void updateItems()
    {
    	browser.runSafely(()->{
		browser.update();
		final BrowserIterator it = browser.createIterator();
		final List<Item> res = new LinkedList();
		final int count = browser.getElementCount();
		for(int i = 0;i < count;++i)
		{
		    it.setPos(i);
		    res.add(new Item(it));
		}
		getLuwrain().runUiSafely(()->{
			this.items = res.toArray(new Item[res.size()]);
			getLuwrain().playSound(Sounds.DONE);
		    });
		return null;
	    });
    }

    @Override public boolean onEscape(InputEvent event)
    {
	closeApp();
	return true;
    }

        @Override public void closeApp()
    {
	this.browser.close();
	super.closeApp();
    }

    Browser getBrowser()
    {
	return this.browser;
    }

    Conversations getConv()
    {
	return this.conv;
    }

    private final class Events implements BrowserEvents
    {
	@Override public void onChangeState(State state)
	{
	    NullCheck.notNull(state, "state");
	    switch(state)
	    {
	    case SUCCEEDED:
		Log.debug(LOG_COMPONENT, "browser succeeded: callback in thread " + Thread.currentThread().getName());
		updateItems();
		return;
	    case FAILED:
		getLuwrain().playSound(Sounds.ERROR);
		return;
	    }
	}
	@Override public void onProgress(Number progress)
	{
	}
	@Override public void onAlert(String message)
	{
	    getLuwrain().message(message);
	}
	@Override public String onPrompt(String message,String value)
	{
	    return "FIXME";
	}
	@Override public void onError(String message)
	{
	    NullCheck.notNull(message, "message");
	    Log.error(LOG_COMPONENT, message);
	    getLuwrain().message(message, Luwrain.MessageType.ERROR);
	}
	@Override public boolean onDownloadStart(String url)
	{
	    //FIXME:
	    return true;
	}
	@Override public Boolean onConfirm(String message)
	{
	    //FIXME:
	    return true;
	}
    }
}

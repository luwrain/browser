/*
   Copyright 2012-2018 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

import java.net.*;
import java.util.*;
import java.util.concurrent.*;

import org.luwrain.core.*;
import org.luwrain.browser.*;
import org.luwrain.browser.selectors.*;

final class Base implements BrowserEvents
{
    static final String LOG_COMPONENT = "inspector";

    private final Luwrain luwrain;
    final Browser browser;
    private Item[] items = new Item[0];

    Base(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	this.luwrain = luwrain;
	this.browser = luwrain.createBrowser();
	this.browser.init(this);
    }

    @Override public void onChangeState(State state)
    {
	NullCheck.notNull(state, "state");
	Log.debug(LOG_COMPONENT, "changing state to " + state.toString());
	switch(state)
	{
	case SUCCEEDED:
	    updateItems();
	    return;
	}
    }

    @Override public void onProgress(Number progress)
    {
    }

    @Override public void onAlert(String message)
    {
	luwrain.message(message);
    }

    @Override public String onPrompt(String message,String value)
    {
	return "FIXME";
    }

    @Override public void onError(String message)
    {
	NullCheck.notNull(message, "message");
	luwrain.message(message, Luwrain.MessageType.ERROR);
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

    void updateItems()
    {
    	final Object obj = browser.runSafely(()->{
		browser.rescanDom();
		final AllNodesSelector selector = new AllNodesSelector(false);
		final BrowserIterator it = browser.createIterator();
		final List<Item> res = new LinkedList();
		if (selector.moveFirst(it))
		do {
		    res.add(new Item(it));
		} while(selector.moveNext(it));
		luwrain.runUiSafely(()->{
			this.items = res.toArray(new Item[res.size()]);
			luwrain.playSound(Sounds.DONE);
		    });
		return null;
	    });
    }

    Item[] getItems()
    {
	return items;
    }
    }

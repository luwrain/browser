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
import org.luwrain.controls.*;
import org.luwrain.browser.*;
import org.luwrain.browser.selectors.*;

final class Base implements BrowserEvents
{
    static final String LOG_COMPONENT = "inspector";

    private final Luwrain luwrain;
    final Browser browser;
    private Item[] items = new Item[0];
    private String[] attrs = new String[0]; 

    Base(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	this.luwrain = luwrain;
	this.browser = luwrain.createBrowser();
	this.browser.init(this);
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
	final String style = item.it.getComputedStyleAll();
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

    @Override public void onChangeState(State state)
    {
	NullCheck.notNull(state, "state");
	switch(state)
	{
	case SUCCEEDED:
	    updateItems();
	    return;
	case FAILED:
	    luwrain.playSound(Sounds.ERROR);
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
		final BrowserIterator it = browser.createIterator();
		final List<Item> res = new LinkedList();
		final int count = browser.numElements();
		for(int i = 0;i < count;++i)
		{
		    it.setPos(i);
		    res.add(new Item(it));
		}
		luwrain.runUiSafely(()->{
			this.items = res.toArray(new Item[res.size()]);
			luwrain.playSound(Sounds.DONE);
		    });
		return null;
	    });
    }

    ListArea.Model getItemsModel()
    {
	return new ItemsModel();
    }

        ListArea.Model getAttrsModel()
    {
	return new AttrsModel();
    }


    private final class ItemsModel implements org.luwrain.controls.ListArea.Model
    {
	@Override public int getItemCount()
	{
	    return items.length;
	}
	@Override public Object getItem(int index)
	{
	    return items[index];
	}
	@Override public void refresh()
	{
	}
    }

        private final class AttrsModel implements org.luwrain.controls.ListArea.Model
    {
	@Override public int getItemCount()
	{
	    return attrs.length;
	}
	@Override public Object getItem(int index)
	{
	    return attrs[index];
	}
	@Override public void refresh()
	{
	}
    }
}

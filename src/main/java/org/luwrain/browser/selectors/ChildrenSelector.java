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

package org.luwrain.browser.selectors;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.browser.*;

public class ChildrenSelector extends AllNodesSelector
{
    private int[] children = new int[0];
    int index = 0;

    public ChildrenSelector(Browser browser, boolean visible)
    {
	super(visible);
	NullCheck.notNull(browser, "browser");
	final Vector<Integer> res = new Vector<Integer>();
	final BrowserIterator it = browser.createIterator();
	final AllNodesSelector allNodesSelector = new AllNodesSelector(visible);
	if (allNodesSelector.moveFirst(it))
	    do {
		if(!it.hasParent())
		    res.add(it.getPos());
	    } while (allNodesSelector.moveNext(it));
	this.children = new int[res.size()];
	int k = 0;
	for(Integer i: res)
	    this.children[k++] = i.intValue();
    }

    public ChildrenSelector(BrowserIterator childrenOf, boolean visible)
    {
	super(visible);
	NullCheck.notNull(childrenOf, "childrenof");
	final Browser browser = childrenOf.getBrowser();
	final LinkedList<Integer> res = new LinkedList<Integer>();
	final BrowserIterator it = browser.createIterator();
	final AllNodesSelector allNodesSelector = new AllNodesSelector(visible);
	if (allNodesSelector.moveFirst(it))
	    do {
		//		if(it.isParent(childrenOf))
		    res.add(it.getPos());
	    }
	    while (allNodesSelector.moveNext(it));
	this.children = new int[res.size()];
	int k = 0;
	for(Integer i: res)
	    this.children[k++] = i.intValue();
    }

    @Override public boolean suits(BrowserIterator it)
    { // ... we never call this method, i think
    	return false;
    }

    public boolean moveFirst(BrowserIterator it)
    {
	NullCheck.notNull(it, "it");
    	if(children.length == 0)
	    return false;
	index = 0;
    	it.setPos(children[0]);
    	return true;
    }

    public boolean moveNext(BrowserIterator it)
    {
	NullCheck.notNull(it, "it");
    	if(index + 1 >= children.length)
	    return false;
	++index;
    	it.setPos(children[index]);
    	return true;
    }

    public boolean movePrev(BrowserIterator it)
    {
	NullCheck.notNull(it, "it");
    	if(index <= 0)
	    return false;
	--index;
    	it.setPos(children[index]);
    	return true;
    }

    public int getChildrenCount()
    {
	return children.length;
    }
}

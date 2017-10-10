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

package org.luwrain.browser.docbuilder;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.browser.*;
import org.luwrain.browser.selectors.*;

class PrenodeTreeBuilder
{
    static private final String LOG_COMPONENT = DocumentBuilder.LOG_COMPONENT;

    private final Browser browser;
    private final Prenode tempRoot;
private Prenode[] nodes = new Prenode[0];

    PrenodeTreeBuilder(Browser browser, Prenode tempRoot)
    {
	NullCheck.notNull(browser, "browser");
	NullCheck.notNull(tempRoot, "tempRoot");
	this.browser = browser;
	this.tempRoot = tempRoot;
    }

    Prenode[] build()
    {
	final AllNodesSelector allVisibleNodes = new AllNodesSelector(true);
	final List<BrowserIterator> nodesList = new LinkedList<BrowserIterator>();
	int maxPos = 0;
	final BrowserIterator it = browser.createIterator();
	if(allVisibleNodes.moveFirst(it))
	    do {
		maxPos = Math.max(maxPos, it.getPos());
		nodesList.add(it.clone());
		//		Log.debug(LOG_COMPONENT, "prenode:" + it.getHtmlTagName());
	    } while(allVisibleNodes.moveNext(it));
	nodes = new Prenode[maxPos + 1];
	for(int i = 0;i < nodes.length;++i)
	    nodes[i] = null;
	for(BrowserIterator itInList: nodesList)
	    ensureRegistered(itInList);
	return nodes;
    }

    private Prenode ensureRegistered(BrowserIterator it)
    {
	NullCheck.notNull(it, "it");
	final int pos = it.getPos();
	if (nodes[pos] != null)
	    return nodes[pos];
	final Prenode parentNodeInfo;
	final BrowserIterator parentIt = it.getParent();
	if (parentIt != null)
	{
	    final BrowserIterator visibleParent = getVisibleParent(parentIt);
	    if (visibleParent == null)
	    {
		Log .warning(LOG_COMPONENT, "node without visible parent");
		return null;
	    }
	    parentNodeInfo = ensureRegistered(visibleParent);
	    if (parentNodeInfo == null)
		return null;
	} else
	    parentNodeInfo = tempRoot;
	final Prenode newNodeInfo = new Prenode(parentNodeInfo, it);
	parentNodeInfo.children.add(newNodeInfo);
	nodes[pos] = newNodeInfo;
	return newNodeInfo;
    }

    private BrowserIterator getVisibleParent(BrowserIterator element)
    {
	NullCheck.notNull(element, "element");
	BrowserIterator el = element;
	while(el != null)
	{
	    if(el.isVisible())
		return el;
	    el = el.getParent();
	}
	return null;
    }
}

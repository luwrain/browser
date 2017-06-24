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

class NodeInfo
{
    final NodeInfo parent;
BrowserIterator browserIt;
    Vector<NodeInfo> children = new Vector<NodeInfo>();

final Vector<BrowserIterator> mixed = new Vector<BrowserIterator>();
    boolean toDelete = false;

    /**A constructor for the root node*/
    NodeInfo()
    {
	this.parent = null;
	this.browserIt = null;
    }

    NodeInfo(NodeInfo parent, BrowserIterator browserIt)
    {
	NullCheck.notNull(parent, "parent");
	NullCheck.notNull(browserIt, "browserIt");
	this.parent = parent;
	this.browserIt = browserIt.clone();
    }

    /** return element and reversed mixed in list */
    Vector<BrowserIterator> getMixedinfo()
    {
	final Vector<BrowserIterator> res = new Vector<BrowserIterator>();
	res.add(browserIt);
	// we already add mixed in reversed mode
	if(!mixed.isEmpty())
	    res.addAll(mixed);
	return res;
    }
}

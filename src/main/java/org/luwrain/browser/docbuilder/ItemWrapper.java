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

import org.luwrain.core.*;
import org.luwrain.browser.*;
import org.luwrain.doctree.*;

class ItemWrapper
{
    final Run run;
    final Node node;
    final NodeInfo nodeInfo;

    ItemWrapper(Run run, NodeInfo nodeInfo)
    {
	NullCheck.notNull(run, "run");
	NullCheck.notNull(nodeInfo, "nodeInfo");
	this.run = run;
	this.node = null;
	this.nodeInfo = nodeInfo;
    }

    ItemWrapper(Node node, NodeInfo nodeInfo)
    {
	NullCheck.notNull(node, "node");
	NullCheck.notNull(nodeInfo, "nodeInfo");
	this.run = null;
	this.node = node;
	this.nodeInfo = nodeInfo;
    }

    boolean isRun()
    {
	return run != null;
    }

    boolean isNode()
    {
	return node != null;
    }
}

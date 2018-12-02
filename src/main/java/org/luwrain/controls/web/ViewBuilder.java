/*
   Copyright 2012-2018 Michael Pozhidaev <michael.pozhidaev@gmail.com>
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

package org.luwrain.controls.web;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.browser.*;
import org.luwrain.controls.web.WebArea.Callback.MessageType;

final class ViewBuilder
{
    static private final String LOG_COMPONENT = WebArea.LOG_COMPONENT;
    
    private final Container[] containers;

    ViewBuilder(Container[] containers)
    {
	NullCheck.notNullItems(containers, "containers");
	this.containers = containers;
    }

    View build()
    {
	final List<Container> viewContainers = new LinkedList();
	for(Container c: containers)
	{
	    final ContainerRowsBuilder b = new ContainerRowsBuilder();
	    for(ContentItem i: c.getContent())
		processContentItem(b, i);
	    b.commitRow();
	    c.setRows(b.rows.toArray(new ContainerRow[b.rows.size()]));
	    if (c.getRowCount() > 0)
		viewContainers.add(c); else
		Log.warning(LOG_COMPONENT, "the container <" + c.tagName + "> without rows (has " + c.getContent().length + " content items)" + System.lineSeparator() + c.treeItem.toString());
	}
	return new View(viewContainers.toArray(new Container[viewContainers.size()]));
    }

    private void processContentItem(ContainerRowsBuilder builder, ContentItem item)
    {
	NullCheck.notNull(builder, "builder");
	NullCheck.notNull(item, "item");
	if (builder.process(item))
	    return;
	for(ContentItem i: item.getChildren())
	    processContentItem(builder, i);
    }
}

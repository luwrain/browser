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
	calcGeom(100);
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

    private void calcGeom(int width)
    {
	if (width < 10)
	    throw new IllegalArgumentException("width (" + width + ") may not be less than 10");
	calcTextXAndWidth(width);
	calcTextY();
    }

    private void calcTextXAndWidth(int width)
    {
	int graphicalWidth = 0;
	for(Container c: containers)
	    graphicalWidth = Math.max(graphicalWidth, c.x + c.width);
	Log.debug(LOG_COMPONENT, "graphical width is " + graphicalWidth);
	final float ratio = (float)graphicalWidth / width;
	Log.debug(LOG_COMPONENT, "ratio is " + String.format("%.2f", ratio));
	for(Container c: containers)
	{
	    final float textX = (float)c.x / ratio;
	    c.textX = new Float(textX).intValue();
	    final float textWidth = (float)c.width / ratio;
	    c.textWidth = new Float(textWidth).intValue();
	}
    }

    private void calcTextY()
    {
	int topLevel = 0;
	int nextTextY = 0;
	while(true)
	{
	    int baseContIndex = -1;
	    for(int i = 0;i < containers.length;++i)
	    {
		final Container c = containers[i];
		//Checking if the container already has the text Y
		if (c.textY >= 0)
		    continue;
		if (c.y < topLevel)
		    continue;
		if (baseContIndex < 0)
		    baseContIndex = i;
		if (c.y < containers[baseContIndex].y)
		{
		    baseContIndex = i;
		    continue;
		}
	    }
	    //Checking if all containers were processed
	    if (baseContIndex < 0)
		return;
	    containers[baseContIndex].textY = nextTextY;
	    nextTextY++;
	}
    }
}

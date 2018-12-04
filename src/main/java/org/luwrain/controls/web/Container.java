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
import java.awt.Rectangle;

import org.luwrain.core.*;
import org.luwrain.browser.*;

final class Container
{
    enum Type {LIST_ITEM, PARA, HEADING};

    final BrowserIterator it;
    final TreeItem treeItem;
    final String className;
    final String tagName;
    final Type type;
    final int x;
    final int y;
    final int width;
    final int height;

    int textX = -1;
    int textY = -1;
    int textWidth = -1;
    int textHeight = -1;

    final ContentItem[] content;
    ContainerRow[] rows = new ContainerRow[0];

    public Container(BrowserIterator it, TreeItem treeItem, ContentItem[] content)
    {
	NullCheck.notNull(it, "it");
	NullCheck.notNull(treeItem, "treeItem");
	NullCheck.notNullItems(content, "content");
	this.it = it;
	this.treeItem = treeItem;
	this.className = it.getClassName();
	this.tagName = it.getTagName();
	this.content = content;
	this.type = getType(className.trim().toLowerCase(), tagName.trim().toLowerCase());
	final Rectangle rect = it.getRect();
	if (rect == null)
	{
	    this.x = 0;
	    this.y = 0;
	    this.width = 0;
	    this.height = 0;
	    return;
	}
	this.x = rect.x;
	this.y = rect.y;
	this.width = rect.width;
	this.height = rect.height;
    }

    void setRows(ContainerRow[] rows)
    {
	NullCheck.notNullItems(rows, "rows");
	this.rows = rows.clone();
    }

    ContainerRow[] getRows()
    {
	return rows.clone();
    }

    String getText()
    {
	final StringBuilder b = new StringBuilder();
	for(ContentItem i: content)
	    b.append(i.getText());
	return new String(b);
    }

    int getRowCount()
    {
	return rows.length;
    }

    WebObject[] getRow(int index)
    {
	return rows[index].getWebObjs();
    }

    private Type getType(String className, String tagName)
    {
	NullCheck.notNull(className, "className");
	NullCheck.notNull(tagName, "tagName");
	switch(className)
	{
	case "li":
	    return Type.LIST_ITEM;
	case "paragraph":
	    return Type.PARA;
	case "heading":
	    return Type.HEADING;
	}
	return Type.PARA;
    }

    ContentItem[] getContent()
    {
	return content.clone();
    }

    @Override public String toString()
    {
			final StringBuilder b = new StringBuilder();
			b.append(it.getClassName()).append(" <").append(it.getTagName()).append(">: ");
	b.append("(").append(String.format("%d,%d,%d,%d", x, y, width, height)).append(")");
	b.append(": ").append(getText());
	return new String(b);
    }

    
}

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

        final List<Container> vertDepOn = new LinkedList();
    boolean actualTextY = false;

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

    boolean intersectsGraphically(Container c)
    {
	NullCheck.notNull(c, "c");
	final int sq1 = getGraphicalSquare();
	final int sq2 = c.getGraphicalSquare();
	if (sq1 == 0 && sq2 == 0)
	    return x == c.x && y == c.y;
	if (sq1 == 0)
	    return between(x, c.x, c.x + c.width) && between(y, c.y, c.y + c.height);
	if (sq2 == 0)
	    return between(c.x, x, x + width) && between(c.y, y, y + height);
	return intersects(x, width, c.x, c.width) &&
	intersects(y, height, c.y, c.height);
    }

        boolean intersectsText(Container c)
    {
	NullCheck.notNull(c, "c");
	final int sq1 = getTextSquare();
	final int sq2 = c.getTextSquare();
	if (sq1 == 0 && sq2 == 0)
	    return textX == c.textX && textY == c.textY;
	if (sq1 == 0)
	    return between(textX, c.textX, c.textX + c.textWidth) && between(textY, c.textY, c.textY + c.textHeight);
	if (sq2 == 0)
	    return between(c.textX, textX, textX + textWidth) && between(c.textY, textY, textY + textHeight);
	return intersects(textX, textWidth, c.textX, c.textWidth) &&
	intersects(textY, textHeight, c.textY, c.textHeight);
    }

    static private boolean between(int pos, int from, int to)
    {
	return pos >= from && pos < to;
    }

    static boolean intersects(int start1, int len1, int start2, int len2)
    {
	if (start1 < start2)
	    return start2 >= start1 && start2 < start1 + len1; else
	    return start1 >= start2 && start1 < start2 + len2;
    }

    int getGraphicalSquare()
    {
	return width * height;
    }

        int getTextSquare()
    {
	return textWidth * textHeight;
    }

    void calcActualTextY()
    {
	if (actualTextY)
	    return;
	for(Container c: vertDepOn)
	    c.calcActualTextY();
	int maxPos = 0;
	for(Container c: vertDepOn)
	    maxPos = Math.max(maxPos, c.textY + c.textHeight);
	this.textY = maxPos;
	actualTextY = true;
    }

    @Override public String toString()
    {
	final StringBuilder b = new StringBuilder();
	b.append(" <").append(it.getTagName()).append("> ");
	b.append("(gr:").append(String.format("%d,%d,%d,%d", x, y, x + width, y + height)).append(")");
	return new String(b);
    }
}

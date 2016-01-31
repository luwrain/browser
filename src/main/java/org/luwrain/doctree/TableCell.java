/*
   Copyright 2012-2015 Michael Pozhidaev <michael.pozhidaev@gmail.com>
   Copyright 2015 Roman Volovodov <gr.rPman@gmail.com>

   This file is part of the LUWRAIN.

   LUWRAIN is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   LUWRAIN is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

package org.luwrain.doctree;

public class TableCell extends NodeImpl
{
    TableCell()
    {
	super(Node.Type.TABLE_CELL);
    }

    @Override void calcWidth(int recommended)
    {
	final int minWidth = recommended >= MIN_TABLE_CELL_WIDTH?recommended:MIN_TABLE_CELL_WIDTH;
	width = minWidth;
	if (subnodes == null || subnodes.length < 1)
	    return;
	for(NodeImpl n: subnodes)
	{
	    n.calcWidth(minWidth);
	    if (width < n.width)
		width = n.width;
	}
    }

    @Override void calcHeight()
    {
	height = 0;
	if (subnodes == null)
	    return;
	for(NodeImpl n: subnodes)
	    n.calcHeight();
	for(NodeImpl n: subnodes)
	    height += n.height;
    }

    @Override void calcPosition()
    {
	int offset = 0;
	for(NodeImpl n: subnodes)
	{
	    n.x = x;
	    n.y = y + offset;
	    offset += n.height;
	    n.calcPosition();
	}
    }

    public Table getTable()
    {
	if (parentNode == null || parentNode.parentNode == null)
	    return null;
	final NodeImpl tableNode = parentNode.parentNode;
	if (tableNode == null || !(tableNode instanceof Table))
	    return null;
	return (Table)tableNode;
    }

    public int getColIndex()
    {
	return getIndexInParentSubnodes();
    }

    public int getRowIndex()
    {
	return parentNode.getIndexInParentSubnodes();
    }
}

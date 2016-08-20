/*
   Copyright 2012-2016 Michael Pozhidaev <michael.pozhidaev@gmail.com>
   Copyright 2015-2016 Roman Volovodov <gr.rPman@gmail.com>

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

public class Table extends NodeImpl
{
    Table()
    {
	super(Node.Type.TABLE);
    }

    @Override void commit()
    {
	super.commit();
	for(int i = 0;i < subnodes.length;++i)
	    if (subnodes[i].type != Node.Type.TABLE_ROW)
	    {
		final NodeImpl n = NodeFactory.newNode(Type.TABLE_ROW);
		n.subnodes = new NodeImpl[]{subnodes[i]};
		subnodes[i] = n;
	    }

    }

    /*
    @Override void calcWidth(int recommended)
    {
	width = recommended;
	if (subnodes == null || subnodes.length < 1)
	    return;
	for(NodeImpl n: subnodes)
	{
	    n.calcWidth(recommended);
	    if (width < n.width)
		width = n.width;
	}
    }
    */

    /*
    @Override void calcHeight()
    {
	height = 0;
	if (subnodes == null)
	    return;
	for(NodeImpl n: subnodes)
	    n.calcHeight();
	for(NodeImpl n: subnodes)
	    height += n.height;
	if (subnodes.length > 0)
	    height += (subnodes.length - 1);
    }
    */

    /*
    @Override void calcPosition()
    {
	int offset = 0;
	for(int i = 0;i < subnodes.length;++i)
	{
	    final NodeImpl n = subnodes[i];
	    if (i > 0)
		++offset;
	    n.x = x;
	    n.y = y + offset;
	    offset += n.height;
	    n.calcPosition();
	}
    }
    */

    public TableCell getCell(int col, int row)
    {
	if (row >= subnodes.length || col >= subnodes[row].subnodes.length)
	    return null;
	final NodeImpl cellNode = subnodes[row].subnodes[col];
	if (cellNode == null || !(cellNode instanceof TableCell))
	    return null;
	return (TableCell)cellNode;
    }

    public int getRowCount()
    {
	return subnodes.length;
    }

    public int getColCount()
    {
	int maxValue = 0;
	for(NodeImpl n: subnodes)
	    if (maxValue < n.subnodes.length)
		maxValue = n.subnodes.length;
	return maxValue;
    }

    public int getTableLevel()
    {
	int count = 1;
	NodeImpl n = parentNode;
	while(n != null)
	{
	    if (n.type == Node.Type.TABLE)
		++count;
	    n = n.parentNode;
	}
	return count;
    }

    public boolean isSingleLineRow(int index)
    {
	for(NodeImpl n: subnodes[index].subnodes)
	{
	    if (n.subnodes.length > 1)
		return false;
	    if (n.subnodes[0].type != Node.Type.PARAGRAPH || !(n.subnodes[0] instanceof ParagraphImpl))
		return false;
	    final ParagraphImpl p = (ParagraphImpl)n.subnodes[0];
	    if (p.height > 1)
		return false;
	}
	    return true;
    }
}

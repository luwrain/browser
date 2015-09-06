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

public class Table extends NodeImpl
{
    Table()
    {
	super(Node.TABLE);
    }

    @Override void commit()
    {
	super.commit();
	for(NodeImpl n: subnodes)
	{
	    if (n.type != Node.TABLE_ROW)
		System.out.println("warning:doctree:table has a subnode with type different than TABLE_ROW");
	    for(NodeImpl nn: n.subnodes)
		if (nn.type != Node.TABLE_CELL)
		System.out.println("warning:doctree:table row has a subnode with type different than TABLE_CELL");
	}
    }

    TableCell getCell(int col, int row)
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
	    if (n.type == Node.TABLE)
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
	    if (n.subnodes[0].type != Node.PARAGRAPH || !(n.subnodes[0] instanceof ParagraphImpl))
		return false;
	    final ParagraphImpl p = (ParagraphImpl)n.subnodes[0];
	    if (p.height > 1)
		return false;
	}
	    return true;
    }
}

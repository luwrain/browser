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

public class TableRow extends NodeImpl
{
    TableRow()
    {
	super(Node.Type.TABLE_ROW);
    }

    @Override void commit()
    {
	for(int i = 0;i < subnodes.length;++i)
	{
	    if (subnodes[i].type != Node.Type.TABLE_CELL)
	    {
		final NodeImpl n = NodeFactory.newNode(Type.TABLE_CELL);
		n.subnodes = new NodeImpl[]{subnodes[i]};
		subnodes[i] = n;
	    }
	}
	super.commit();
    }

    @Override void calcWidth(int recommended)
    {
	final int cellWidth = (recommended - subnodes.length + 1) >= subnodes.length?(recommended - subnodes.length + 1) / subnodes.length:1;
	width = 0;
	for(NodeImpl n: subnodes)
	    n.calcWidth(cellWidth);
	for(NodeImpl n: subnodes)
	    width += n.width;
	width += (subnodes.length - 1);//One additional empty column after each cell
	if (width < recommended)
	    width = recommended;
    }

    @Override void calcHeight()
    {
	height = 0;
	if (subnodes == null)
	    return;
	for(NodeImpl n: subnodes)
	    n.calcHeight();
	for(NodeImpl n: subnodes)
	    if (height < n.height)
		height = n.height;
    }

    @Override void calcPosition()
    {
	int offset = 0;
	for(NodeImpl n: subnodes)
	{
	    n.x = x + offset;
	    offset += (n.width + 1);
	    n.y = y;
	    n.calcPosition();
	}
    }
}

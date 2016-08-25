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

import org.luwrain.core.*;

public class TableRow extends NodeImpl
{
    TableRow()
    {
	super(Node.Type.TABLE_ROW);
    }

    @Override void commit()
    {
	super.commit();
	for(int i = 0;i < subnodes.length;++i)
	    if (subnodes[i].type != Node.Type.TABLE_CELL)
	    {
		final NodeImpl n = NodeFactory.newNode(Type.TABLE_CELL);
		n.subnodes = new NodeImpl[]{subnodes[i]};
		n.parentNode = this;
		n.commit();
		subnodes[i] = n;
	    }
    }

    void addEmptyCells(int num)
    {
	NullCheck.notNullItems(subnodes, "subnodes");
	if (subnodes.length >= num)
	    return;
	final NodeImpl[] newNodes = new NodeImpl[num];
	for(int i = 0;i < subnodes.length;++i)
	    newNodes[i] = subnodes[i];
	for(int i = subnodes.length;i < newNodes.length;++i)
	{
	    final TableCell cell = new TableCell();
	    cell.subnodes = new NodeImpl[]{NodeFactory.newPara("-")};
	    newNodes[i] = cell;
	}

    }
}

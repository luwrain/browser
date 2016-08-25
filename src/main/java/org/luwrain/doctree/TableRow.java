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
	NullCheck.notNullItems(subnodes, "subnodes");
	for(int i = 0;i < subnodes.length;++i)
	    if (!(subnodes[i] instanceof TableCell))
	    {
		final NodeImpl n = NodeFactory.newNode(Type.TABLE_CELL);
		n.subnodes = new NodeImpl[]{subnodes[i]};
		subnodes[i].parentNode = n;
		n.parentNode = this;
		//		n.commit();
		subnodes[i] = n;
	    }
	super.commit();
    }

    void addEmptyCells(int num)
    {
	NullCheck.notNullItems(subnodes, "subnodes");
	//	Log.debug("table", "has " + subnodes.length + ", required " + num);
	if (subnodes.length >= num)
	    return;
	final NodeImpl[] newNodes = new NodeImpl[num];
	for(int i = 0;i < subnodes.length;++i)
	    newNodes[i] = subnodes[i];
	for(int i = subnodes.length;i < newNodes.length;++i)
	{
	    //	    Log.debug("table", "adding new");
	    final TableCell cell = new TableCell();
	    cell.subnodes = new NodeImpl[]{NodeFactory.newPara("-")};
	    cell.subnodes[0].parentNode = cell;
	    newNodes[i] = cell;
	}
	subnodes = newNodes;
    }
}

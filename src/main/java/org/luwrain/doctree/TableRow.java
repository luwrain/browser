
package org.luwrain.doctree;

import java.util.*;

import org.luwrain.core.*;

public class TableRow extends Node
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
		Log.warning("doctree", "table row has a subnode of class " + subnodes[i].getClass().getName() + ", it will be put into newly created table cell");
		final Node n = NodeFactory.newNode(Type.TABLE_CELL);
		n.subnodes = new Node[]{subnodes[i]};
		n.subnodes[0].parentNode = n;
		n.parentNode = this;
		subnodes[i] = n;
	    }
	super.commit();
    }

    void addEmptyCells(int num)
    {
	NullCheck.notNullItems(subnodes, "subnodes");
	if (subnodes.length >= num)
	    return;
	final Node[] newNodes = Arrays.copyOf(subnodes, num);
	for(int i = subnodes.length;i < newNodes.length;++i)
	{
	    final TableCell cell = new TableCell();
	    cell.subnodes = new Node[]{NodeFactory.newEmptyLine()};
	    cell.subnodes[0].parentNode = cell;
	    cell.parentNode = this;
	    newNodes[i] = cell;
	}
	subnodes = newNodes;
    }
}

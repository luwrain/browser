
package org.luwrain.doctree;

import org.luwrain.core.*;

import org.luwrain.core.NullCheck;

public class Table extends Node
{
    Table()
    {
	super(Node.Type.TABLE);
    }

    @Override void preprocess()
    {
	NullCheck.notNullItems(subnodes, "subnodes");
	for(int i = 0;i < subnodes.length;++i)
	    if (!(subnodes[i] instanceof TableRow))
	    {
		Log.warning("doctree", "table has a subnode of class " + subnodes[i].getClass().getName() + ", it will be put into newly created table row");
		final Node n = NodeFactory.newNode(Type.TABLE_ROW);
		n.subnodes = new Node[]{subnodes[i]};
		n.subnodes[0].parentNode = n;
		n.parentNode = this;
		subnodes[i] = n;
	    }
	int maxCellCount = 0;
	for(Node n: subnodes)
	    if (maxCellCount < n.getSubnodeCount())
		maxCellCount = n.getSubnodeCount();
	for(Node n: subnodes)
	    ((TableRow)n).addEmptyCells(maxCellCount);
	super.preprocess();
    }

    public TableCell getCell(int col, int row)
    {
	if (row >= subnodes.length || col >= subnodes[row].subnodes.length)
	    return null;
	final Node cellNode = subnodes[row].subnodes[col];
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
	for(Node n: subnodes)
	    if (maxValue < n.subnodes.length)
		maxValue = n.subnodes.length;
	return maxValue;
    }

    public int getTableLevel()
    {
	int count = 1;
	Node n = parentNode;
	while(n != null)
	{
	    if (n.type == Node.Type.TABLE)
		++count;
	    n = n.parentNode;
	}
	return count;
    }

    public boolean isSingleCellTable()
    {
	NullCheck.notNullItems(subnodes, "subnodes");
	return subnodes.length == 1 || subnodes[0].getSubnodes().length == 1;
    }
}

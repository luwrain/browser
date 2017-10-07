
package org.luwrain.doctree.view;

import org.luwrain.core.*;
import org.luwrain.doctree.*;

class NodeGeom
{
    
    static void calcWidth(Node node, int recommended)
    {
	NullCheck.notNull(node, "node");
	final Node[] subnodes = node.getSubnodes();
	NullCheck.notNullItems(subnodes, "subnodes");
	if (node instanceof TableRow)
	{
	    final TableRow tableRow = (TableRow)node;
	    final int cellWidth = (recommended - subnodes.length + 1) >= subnodes.length?(recommended - subnodes.length + 1) / subnodes.length:1;
	    for(Node n: subnodes)
		calcWidth(n, cellWidth);
	    tableRow.width = 0;
	    for(Node n: subnodes)
		tableRow.width += n.width;
	    tableRow.width += (subnodes.length - 1);//One additional empty column after each cell
	    if (tableRow.width < recommended)
		tableRow.width = recommended;
	    return;
	}
	node.width = recommended;
	for(Node n: subnodes)
	{
	    calcWidth(n, recommended);
	    if (node.width < n.width)
	        node.width = n.width;
	}
    }

    static void calcHeight(Node node)
    {
	NullCheck.notNull(node, "node");
	if (node instanceof Paragraph)
	{
	    final Paragraph para = (Paragraph)node;
	    if (para.getRowParts().length == 0)
	    {
		Log.warning("doctree", "there is a paragraph without runs");
		para.setNodeHeight(0);
		return;
	    }
	    int maxRelRowNum = 0;
	    for(RowPart p: (RowPart[])para.getRowParts())
		if (p.relRowNum > maxRelRowNum)
		    maxRelRowNum = p.relRowNum;
	    para.setNodeHeight(maxRelRowNum + (para.withEmptyLine()?2:1));
	    return;
	}
	final Node[] subnodes = node.getSubnodes();
	NullCheck.notNullItems(subnodes, "subnodes");
	if (node instanceof TableRow)
	{
	    final TableRow tableRow = (TableRow)node;
	    for(Node n: subnodes)
		calcHeight(n);
	    tableRow.setNodeHeight(0);
	    for(Node n: subnodes)
		if (tableRow.getNodeHeight() < n.getNodeHeight())
		    tableRow.setNodeHeight(n.getNodeHeight());
	    if (hasTitleRun(node))
		node.setNodeHeight(node.getNodeHeight() + 1);//For title run
	    return;
	}
	for(Node n: subnodes)
	    calcHeight(n);
	node.setNodeHeight(0);
	for(Node n: subnodes)
	    node.setNodeHeight(node.getNodeHeight() + n.getNodeHeight());
	if (hasTitleRun(node))
	    node.setNodeHeight(node.getNodeHeight() + 1);//For title run
    }

    static void calcPosition(Node node)
    {
	NullCheck.notNull(node, "node");
	final Node[] subnodes = node.getSubnodes();
	NullCheck.notNullItems(subnodes, "subnodes");
	if (node instanceof TableRow)
	{
	    final TableRow tableRow = (TableRow)node;
	    int offset = 0;
	    for(Node n: subnodes)
	    {
		n.setNodeX(tableRow.getNodeX() + offset);
		offset += (n.width + 1);
		n.setNodeY(node.getNodeY());
		if (hasTitleRun(node))
		    n.setNodeY(n.getNodeY());
		calcPosition(n);
	    }
	    return;
	} //table row
	if  (node.getType() == Node.Type.ROOT)
	{
	    node.setNodeX(0);
	    node.setNodeY(0);
	}
	//Assuming node.x and node.y already set appropriately
	int offset = hasTitleRun(node)?1:0;//1 for title run
	if (node.getType() == Node.Type.PARAGRAPH && ((Paragraph)node).getRowParts().length > 0)
	    offset = 1;
	for(Node n: subnodes)
	{
	    n.setNodeX(node.getNodeX());
	    n.setNodeY(node.getNodeY() + offset);
	    offset += n.getNodeHeight();
	    calcPosition(n);
	}
    }

        //May be called after width calculation only
    static boolean hasTitleRun(Node node)
    {
	NullCheck.notNull(node, "node");
	switch(node.getType())
	{
	case LIST_ITEM:
	case SECTION:
	case ROOT:
	case TABLE:
	case TABLE_ROW:
	    return false;
	      case ORDERED_LIST:
	      case UNORDERED_LIST:
		  return false;
	case TABLE_CELL:
	    return !((TableCell)node).getTable().isSingleCellTable();
	default:
	    return true;
	}
    }
}

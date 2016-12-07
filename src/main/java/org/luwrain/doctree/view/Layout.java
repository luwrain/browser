
package org.luwrain.doctree.view;

import org.luwrain.core.*;
import org.luwrain.doctree.*;

class Layout
{
    private final Document document;
    private final Node root;
    private final Paragraph[] paragraphs; //Only paragraphs which appear in document, no paragraphs without row parts
    private final RowPart[] rowParts;
    private final Row[] rows;
    private Line[] lines = new Line[0];

    public Layout(Document document, Node root,
		  Row[] rows, RowPart[] rowParts,
Paragraph[] paragraphs)
    {
	NullCheck.notNull(document, "document");
	NullCheck.notNull(root, "root");
	NullCheck.notNullItems(rows, "rows");
	NullCheck.notNullItems(rowParts, "rowParts");
	NullCheck.notNullItems(paragraphs, "paragraphs");
	this.document = document;
	this.root = root;
	this.paragraphs = paragraphs;
	this.rows = rows;
	this.rowParts = rowParts;
    }

void calc()
    {
	final int lineCount = calcRowsPosition();
	lines = new Line[lineCount];
	for(int i = 0;i < lines.length;++i)
	    lines[i] = new Line();
	for(int k = 0;k < rows.length;++k)
	{
	    if (rows[k].isEmpty())
		continue;
	    final Line line = lines[rows[k].y];
	    final int[] oldRows = line.rows;
	    line.rows = new int[oldRows.length + 1];
	    for(int i = 0;i < oldRows.length;++i)
		line.rows[i] = oldRows[i];
	    line.rows[oldRows.length] = k;
	}
    }

    int getLineCount()
    {
	return lines != null?lines.length:0;
    }

    String getLine(int index)
    {
	final Line line = lines[index];
	StringBuilder b = new StringBuilder();
	for(int r: line.rows)
	{
	    final Row row = rows[r];
	    while(b.length() < row.x)
		b.append(" ");
	    b.append(row.text());
	}
	return b.toString();
    }

    private int calcRowsPosition()
    {
	int maxLineNum = 0;
	int lastX = 0;
	int lastY = 0;
	NullCheck.notNullItems(rows, "rows");
	for(Row r: rows)
	{
	    //Generally admissible situation as not all rows should have associated parts
	    if (r.isEmpty())
	    {
		r.x = lastX;
		r.y = lastY + 1;
		++lastY;
		continue;
	    }
	    final Run run = r.getFirstPart().run;
	    NullCheck.notNull(run, "run");
	    final Node parent = run.getParentNode();
	    NullCheck.notNull(parent, "parent");
	    if (parent instanceof Paragraph)
	    {
		final Paragraph paragraph = (Paragraph)parent;
		r.x = paragraph.getNodeX();
		r.y = paragraph.getNodeY() + r.getFirstPart().relRowNum;
	    } else 
	    {
		r.x = parent.getNodeX();
		r.y = parent.getNodeY();
	    }
	    lastX = r.x;
	    lastY = r.y;
	    if (r.y > maxLineNum)
		maxLineNum = r.y;
	}
	return maxLineNum + 1;
    }

    static void calcAbsRowNums(RowPart[] parts)
    {
	NullCheck.notNullItems(parts, "parts");
	if (parts.length < 1)
	    return;
	RowPart first = parts[0];
	parts[0].absRowNum = 0;
	for(int i = 1;i < parts.length;++i)
	{
	    final RowPart part = parts[i];
	    if (!first.onTheSameRow(part))
	    {
		part.absRowNum = first.absRowNum + 1;
		first = part;
	    } else
		part.absRowNum = first.absRowNum;
	}
    }

    static Row[] buildRows(RowPart[] parts)
    {
	NullCheck.notNullItems(parts, "parts");
	final Row[] rows = new Row[parts[parts.length - 1].absRowNum + 1];
	for(int i = 0;i < rows.length;++i)
	    rows[i] = new Row(parts);
	int current = -1;
	for(int i = 0;i < parts.length;++i)
	    rows[parts[i].absRowNum].mustIncludePart(i);
	return rows;
    }

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
	    /*
	    	    if (node.hasNonParagraphs())
		return true;
	    return node.width <= 0 || node.getCompleteText().length() >= node.width;
	    */
	    return false;
	case ROOT:
	case TABLE:
	case TABLE_ROW:
	    return false;
	      case ORDERED_LIST:
	      case UNORDERED_LIST:
		  //		  return node.getParentType() == Node.Type.LIST_ITEM;
		  return false;
	case TABLE_CELL:
	    return !((TableCell)node).getTable().isSingleCellTable();
	default:
	    return true;
	}
    }

    static private class Line
    {
	int[] rows = new int[0];
    }
}

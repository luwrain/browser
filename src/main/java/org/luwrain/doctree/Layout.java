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

import org.luwrain.core.NullCheck;

class Layout
{
    private Document document;
    private NodeImpl root;
    private Paragraph[] paragraphs; //Only paragraphs which appear in document, no paragraphs without row parts
    public RowPart[] rowParts;
    private Row[] rows;
    private Line[] lines = new Line[0];

    Layout(Document document)
    {
	NullCheck.notNull(document, "document");
	this.document = document;
    }

    private void init()
    {
	root = document.getRoot();
	paragraphs = document.getParagraphs();
	rowParts = document.getRowParts();
	rows = document.getRows();
    }

    void calc()
    {
	init();
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
	    final Run run = r.getFirstPart().run();
	    NullCheck.notNull(run, "run");
	    System.out.println(run.toString());
	    final NodeImpl parent = run.getParentNode();
	    NullCheck.notNull(parent, "parent");
	    if (parent instanceof Paragraph)
	    {
		final Paragraph paragraph = (Paragraph)parent;
	    r.x = paragraph.x;
	    r.y = paragraph.y + r.getFirstPart().relRowNum();
	    lastX = r.x;
	    lastY = r.y;
	    }
	    if (r.y > maxLineNum)
		maxLineNum = r.y;
	}
	return maxLineNum + 1;
    }

    /*
static void calcAbsRowNums(Paragraph[] paragraphs)
    {
	NullCheck.notNullItems(paragraphs, "paragraphs");
	int currentParaTop = 0;
	for(Paragraph p: paragraphs)
	{
	    //	    p.topRowIndex = currentParaTop;
	    for(RowPart r: p.rowParts)
		r.absRowNum = r.relRowNum() + currentParaTop;
	    currentParaTop += p.height;
	}
    }
    */

static void calcAbsRowNums(RowPart[] parts)
    {
	NullCheck.notNullItems(parts, "parts");
	if (parts.length < 1)
	    return;
	//	int current = 0;
	RowPart first = parts[0];
	parts[0].absRowNum = 0;
	for(int i = 1;i < parts.length;++i)
	{
	    final RowPart part = parts[i];
	    if (!first.onTheSameRow(part))
	    {
	    part.absRowNum = first.absRowNum + 1;
	    first = part;
	    System.out.println("new");
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

    //Launched before any other processing, RowPartsBuilder goes next
    static void calcWidth(NodeImpl node, int recommended)
    {
	NullCheck.notNull(node, "node");
	final NodeImpl[] subnodes = node.getSubnodes();
	NullCheck.notNullItems(subnodes, "subnodes");
	if (node instanceof TableRow)
	{
	    final TableRow tableRow = (TableRow)node;
	    final int cellWidth = (recommended - subnodes.length + 1) >= subnodes.length?(recommended - subnodes.length + 1) / subnodes.length:1;
	    for(NodeImpl n: subnodes)
		calcWidth(n, cellWidth);
	    tableRow.width = 0;
	    for(NodeImpl n: subnodes)
		tableRow.width += n.width;
	    tableRow.width += (subnodes.length - 1);//One additional empty column after each cell
	    if (tableRow.width < recommended)
		tableRow.width = recommended;
	    return;
	}
	node.width = recommended;
	for(NodeImpl n: subnodes)
	{
	    calcWidth(n, recommended);
	    if (node.width < n.width)
	        node.width = n.width;
	}
    }

    //Launched after RowPartsBuilder
    static void calcHeight(NodeImpl node)
    {
	NullCheck.notNull(node, "node");
	if (node instanceof Paragraph)
	{
	    final Paragraph para = (Paragraph)node;
	    if (para.getRowParts().length == 0)
	    {
		para.height = 0;
		return;
	    }
	    int maxRelRowNum = 0;
	    for(RowPart p: para.getRowParts())
		if (p.relRowNum() > maxRelRowNum)
		    maxRelRowNum = p.relRowNum();
	    para.height = maxRelRowNum + 1;
	    return;
	}
	final NodeImpl[] subnodes = node.getSubnodes();
	NullCheck.notNullItems(subnodes, "subnodes");
	if (node instanceof TableRow)
	{
	    final TableRow tableRow = (TableRow)node;
	    for(NodeImpl n: subnodes)
		calcHeight(n);
	    tableRow.height = 0;
	    for(NodeImpl n: subnodes)
		if (tableRow.height < n.height)
		    tableRow.height = n.height;
	    return;
	}
	for(NodeImpl n: subnodes)
	    calcHeight(n);
	node.height = 0;
	for(NodeImpl n: subnodes)
	    node.height += n.height;
    }

    //Launched after calcHeight;
    static void calcPosition(NodeImpl node)
    {
	NullCheck.notNull(node, "node");
	final NodeImpl[] subnodes = node.getSubnodes();
	NullCheck.notNullItems(subnodes, "subnodes");
	if (node instanceof TableRow)
	{
	    final TableRow tableRow = (TableRow)node;
	    int offset = 0;
	    for(NodeImpl n: subnodes)
	    {
		n.x = tableRow.x + offset;
		offset += (tableRow.width + 1);
		n.y = node.y;
		calcPosition(n);
	    }
	    return;
	}
	if  (node.type == Node.Type.ROOT)
	{
	    node.x = 0;
	    node.y = 0;
	}
	int offset = 0;
	for(NodeImpl n: subnodes)
	{
	    n.x = node.x;
	    n.y = node.y + offset;
	    offset += n.height;
	    if (n.type == Node.Type.ROOT)
		++offset;
	    calcPosition(n);
	}
    }

    static private class Line
    {
	int[] rows = new int[0];
    }
}

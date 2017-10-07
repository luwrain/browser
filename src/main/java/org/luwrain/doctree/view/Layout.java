/*
   Copyright 2012-2016 Michael Pozhidaev <michael.pozhidaev@gmail.com>
   Copyright 2015-2016 Roman Volovodov <gr.rPman@gmail.com>

   This file is part of LUWRAIN.

   LUWRAIN is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   LUWRAIN is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

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
	if (index < 0)
	    throw new IllegalArgumentException("index (" + index + ") may not be negative");
	final Line line = lines[index];
	StringBuilder b = new StringBuilder();
	for(int r: line.rows)
	{
	    final Row row = rows[r];
	    while(b.length() < row.x)
		b.append(" ");
	    b.append(row.getText());
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
	    final Run run = r.getFirstRun();
	    NullCheck.notNull(run, "run");
	    final Node parent = run.getParentNode();
	    NullCheck.notNull(parent, "parent");
	    if (parent instanceof Paragraph)
	    {
		final Paragraph paragraph = (Paragraph)parent;
		r.x = paragraph.getNodeX();
		r.y = paragraph.getNodeY() + r.getRelNum();
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


    static private class Line
    {
	int[] rows = new int[0];
    }
}

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
    static private class Line
    {
	int[] rows = new int[0];
    }

    private Document document;
    private NodeImpl root;
    private ParagraphImpl[] paragraphs; //Only paragraphs which appear in document, no paragraphs without row parts
    public RowPart[] rowParts;
    private RowImpl[] rows;
    private Line[] lines = new Line[0];

    Layout(Document document)
    {
	this.document = document;
	NullCheck.notNull(document, "document");
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
	    //	    System.out.println("y=" + rows[k].y);
	    final int[] oldRows = line.rows;
	    line.rows = new int[oldRows.length + 1];
	    for(int i = 0;i < oldRows.length;++i)
		line.rows[i] = oldRows[i];
	    line.rows[oldRows.length] = k;
	}
    }

    private int calcRowsPosition()
    {
	int maxLineNum = 0;
	int lastX = 0;
	int lastY = 0;
	for(RowImpl r: rows)
	{
	    //Generally admissible situation as not all rows should have associated parts;
	    if (r.isEmpty())
	    {
		r.x = lastX;
		r.y = lastY + 1;
		++lastX;
		continue;
	    }
	    final Run run = r.getFirstPart(rowParts).run;
	    final ParagraphImpl paragraph = run.parentParagraph;
	    r.x = paragraph.x;
	    r.y = paragraph.y + r.getFirstPart(rowParts).relRowNum;
	    lastX = r.x;
	    lastY = r.y;
	    if (r.y > maxLineNum)
		maxLineNum = r.y;
	}
	return maxLineNum + 1;
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
	    final RowImpl row = rows[r];
	    while(b.length() < row.x)
		b.append(" ");
	    b.append(row.text(rowParts));
	}
	return b.toString();
    }
}

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

import java.net.*;
import java.util.*;

import org.luwrain.core.*;
import org.luwrain.doctree.*;

public class View
{
    static public final String DEFAULT_ITERATOR_INDEX_PROPERTY = "defaultiteratorindex";

    protected final Document doc;
    protected final Node root;
    protected final Layout layout;
    protected final Paragraph[] paragraphs; //Only paragraphs which appear in document, no paragraphs without row parts
    protected final RowPart[] rowParts;
    protected final Row[] rows;

    public View(Document doc, int width)
    {
	NullCheck.notNull(doc, "doc");
	this.doc = doc;
	this.root = doc.getRoot();
	Layout.calcWidth(root, width);
	final RowPartsBuilder rowPartsBuilder = new RowPartsBuilder();
	rowPartsBuilder.onNode(root);
	rowParts = rowPartsBuilder.getRowParts();
	NullCheck.notNullItems(rowParts, "rowParts");
	if (rowParts.length <= 0)
	{
	    paragraphs = new Paragraph[0];
	    rows = new Row[0];
	    layout = null;
	    return;
	}
	paragraphs = rowPartsBuilder.getParagraphs();
	Layout.calcHeight(root);
	Layout.calcAbsRowNums(rowParts);
	Layout.calcPosition(root);
	rows = buildRows(rowParts);
	layout = new Layout(doc, root, rows, rowParts, paragraphs);
	layout.calc();
	setDefaultIteratorIndex();
    }

        static protected void calcAbsRowNums(RowPart[] parts)
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

    static protected Row[] buildRows(RowPart[] parts)
    {
	NullCheck.notNullItems(parts, "parts");
	final int rowCount = parts[parts.length - 1].absRowNum + 1;
	final int[] fromParts = new int[rowCount];
	final int[] toParts = new int[rowCount];
	for(int i = 0;i < rowCount;++i)
	{
	    fromParts[i] = -1;
	    toParts[i] = -1;
	}
	for(int i = 0;i < parts.length;++i)
	{
	    final int rowIndex = parts[i].absRowNum;
	    if (fromParts[rowIndex] == -1 || toParts[rowIndex] > i)
		fromParts[rowIndex] = i;
	    if(toParts[rowIndex] < i + 1)
		toParts[rowIndex] = i + 1;
	}
	final Row[] rows = new Row[rowCount];
	for (int i = 0;i < rowCount;++i)
	    if (fromParts[i] >= 0 && toParts[i] >= 0)
		rows[i] = new Row(parts, fromParts[i], toParts[i]); else
		rows[i] = new Row();
	return rows;
    }

    public org.luwrain.doctree.view.Iterator getIterator()
    {
	return new Iterator(doc, this);
    }

    public org.luwrain.doctree.view.Iterator getIterator(int startingIndex)
    {
	return new Iterator(doc, this, startingIndex);
    }

    public int getLineCount()
    {
	return layout.getLineCount();
    }

    public String getLine(int index)
    {
	return layout.getLine(index);
    }

    public Paragraph[] getParagraphs() { return paragraphs; }
    public Row[] getRows() { return rows; }
    public RowPart[] getRowParts() { return rowParts; }

    private void setDefaultIteratorIndex()
    {
	final String id = doc.getProperty("startingref");
		if (id.isEmpty())
	    return;
	Log.debug("doctree", "preparing default iterator index for " + id);
	final org.luwrain.doctree.view.Iterator it = getIterator();
	while (it.canMoveNext())
	{
	    if (!it.isEmptyRow())
	    {
		final ExtraInfo data = it.getNode().extraInfo;
		if (data != null && data.hasIdInChain(id))
		    break;
		final Run[] runs = it.getRunsOnRow();
		Run foundRun = null;
		for(Run r: runs)
		    if (r instanceof TextRun)
		    {
			final TextRun textRun = (TextRun)r;
			if (textRun.extraInfo.hasIdInChain(id))
			    foundRun = textRun;
		    }
		if (foundRun != null)
		    break;
	    }
	    it.moveNext();
	}
	if (!it.canMoveNext())//FIXME:
	{
	    Log.debug("doctree", "no iterator position found for " + id);
	    doc.setProperty(DEFAULT_ITERATOR_INDEX_PROPERTY, "");
	    return;
	}
	doc.setProperty("defaultiteratorindex", "" + it.getIndex());
	Log.debug("doctree", "default iterator index set to " + it.getIndex());
    }

    static public String[] getParagraphLines(Paragraph para, int width)
    {
	NullCheck.notNull(para, "para");
	final RowPartsBuilder builder = new RowPartsBuilder();
	builder.onNode(para, width);
	final RowPart[] parts = builder.getRowParts();
	for(RowPart r: parts)
	    r.absRowNum = r.relRowNum;
	final Row[] rows = buildRows(parts);
	final List<String> lines = new LinkedList<String>();
	for(Row r: rows)
	    lines.add(r.getText());
	return lines.toArray(new String[lines.size()]);
    }
}

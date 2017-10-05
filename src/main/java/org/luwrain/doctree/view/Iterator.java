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

public class Iterator
{
    protected final Document document;
    protected final View view ;
    protected final Node root;
    protected final Paragraph[] paragraphs;
    protected final Row[] rows;

    protected int current = 0;

    public Iterator(Document document, View view)
    {
	NullCheck.notNull(document, "document");
	NullCheck.notNull(view, "view");
	this.document = document;
	this.view = view;
	this.root = document.getRoot();
	this.paragraphs = view.getParagraphs();
	this.rows = view.getRows();
	current = 0;
    }

    public Iterator(Document document, View view, int index)
    {
	NullCheck.notNull(document, "document");
	NullCheck.notNull(view, "view");
	this.document = document;
	this.view = view;
	this.root = document.getRoot();
	this.paragraphs = view.getParagraphs();
	this.rows = view.getRows();
	if (index < 0 || index >= rows.length)
	    throw new IllegalArgumentException("INvalid row index:" + index);
	current = index;
    }

    public boolean noContent()
    {
	if (document == null)
	    return true;
	if (rows == null || rows.length < 1)
	    return true;
	return false;
    }

    @Override public boolean equals(Object o)
    {
	if (o == null || !(o instanceof Iterator))
	    return false;
	final Iterator it2 = (Iterator)o;
	return document == it2.document && current == it2.current;
    }

    @Override public org.luwrain.doctree.view.Iterator clone()
    {
	return new Iterator(document, view, current);
    }

    public boolean canMoveNext()
    {
	if (noContent())
	    return false;
	return current + 1 < rows.length;
    }

    public boolean canMovePrev()
    {
	if (noContent())
	    return false;
	return current > 0;
    }

    public boolean moveNext()
    {
	if (!canMoveNext())
	    return false;
	++current;
	return true;
    }

    public boolean movePrev()
    {
	if (!canMovePrev())
	    return false;
	--current;
	return true;
    }

    public void moveEnd()
    {
	current = rows.length > 0?rows.length - 1:0;
    }

    public void moveHome()
    {
	current = 0;
    }

    public String getText()
    {
	if (noContent())
	    return "";
	final Row row = rows[current];
	return !row.isEmpty()?row.text():"";
    }

    //returns -1 if no content
    public int getIndex()
    {
	if (noContent())
	    return -1;
	return current;
    }

    //returns -1 if no content
    public int getIndexInParagraph()
    {
	if (noContent())
	    return -1;
	//	return current - getParagraph().topRowIndex;
	return getRow().getFirstPart().relRowNum;
    }

    public boolean isParagraphBeginning()
    {
	return getIndexInParagraph() == 0;
    }

    public boolean hasRunOnRow(Run run)
    {
	NullCheck.notNull(run, "run");
	if (isEmptyRow())
	    return false;
	final Run[] runs = getRow().getRuns();
	for(Run r: runs)
	    if (run == r)
		return true;
	return false;
    }

    public Run[] getRunsOnRow()
    {
	if (isEmptyRow())
	    return new Run[0];
	return getRow().getRuns();
    }

    public int runBeginsAt(Run run)
    {
	NullCheck.notNull(run, "run");
	if (isEmptyRow())
	    return -1;
	return getRow().runBeginsAt(run);
    }

    public int getX()
    {
	return getRow().x;
    }

    public int getY()
    {
	return getRow().y;
    }

    public boolean isTitleRow()
    {
	final Row row = getRow();
	if (row == null)
	    return false;
	return row.getFirstPart().run instanceof TitleRun;
    }

    public Node getNode()
    {
	if (isTitleRow())
	    return getTitleParentNode();
	return getParaContainer();
    }

    public Paragraph getParagraph()
    {
	if (noContent() || isEmptyRow())
	    return null;
	final Node parent = getFirstRunOfRow().getParentNode();
	return (parent instanceof Paragraph)?(Paragraph)parent:null;
    }

    //Returns null if is at title row
    public Node getParaContainer()
    {
	if (noContent())
	    return null;
	final Paragraph para = getParagraph();
	return para != null?para.getParentNode():null;
    }

    public boolean isEmptyRow()
    {
	if (noContent())
	    return true;
	return rows[current].isEmpty();
    }

    public boolean coversPos(int x, int y)
    {
	if (noContent())
	    return false;
	if (isEmptyRow())
	    return false;
	final Row r = getRow();
	if (r.getRowY() != y)
	    return false;
	if (x < r.getRowX())
	    return false;
	if (x > r.getRowX() + getText().length())
	    return false;
	return true;
    }

    public Run getRunUnderPos(int pos)
    {
	if (noContent())
	    return null;
	if (pos < 0)
	    throw new IllegalArgumentException("pos may not be negative");
	return rows[current].getRunUnderPos(pos);
    }

    protected Row getRow()
    {
	if (noContent())
	    return null;
	if (current < 0 || current >= rows.length)
	    return null;
	return rows[current];
    }

    protected Run getFirstRunOfRow()
    {
	if (noContent())
	    return null;
	return rows[current].getFirstPart().run;
    }

    protected Node getTitleParentNode()
    {
	if (!isTitleRow())
	    return null;
	return getRow().getFirstPart().run.getParentNode();
    }
}

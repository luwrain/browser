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

public class Iterator
{
protected Document document;
    protected Node root;
    protected Paragraph[] paragraphs;
    protected Row[] rows;

    protected int current = 0;

    Iterator(Document document)
    {
	NullCheck.notNull(document, "document");
	this.document = document;
	this.root = document.getRoot();
	this.paragraphs = document.getParagraphs();
	this.rows = document.getRows();
	current = 0;
    }

    Iterator(Document document, int index)
    {
	NullCheck.notNull(document, "document");
	this.document = document;
	this.root = document.getRoot();
	this.paragraphs = document.getParagraphs();
	this.rows = document.getRows();
	current = index < rows.length?index:0;
    }

    public boolean noContent()
    {
	if (document == null)
	    return true;
	if (rows == null || rows.length < 1)
	    return true;
	return false;
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

    public int runBeginsAt(Run run)
    {
	NullCheck.notNull(run, "run");
	if (isEmptyRow())
	    return -1;
	return getRow().runBeginsAt(run);
    }

    @Override public boolean equals(Object o)
    {
	if (o == null || !(o instanceof Iterator))
	    return false;
	final Iterator it2 = (Iterator)o;
	return document == it2.document && current == it2.current;
    }

    @Override public Object clone()
    {
	return new Iterator(document, current);
    }

    public Row getRow()
    {
	if (noContent())
	    return null;
	if (current < 0 || current >= rows.length)
	    return null;
	return rows[current];
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
	return para != null?para.parentNode:null;
    }

    public boolean hasContainerInParents(Node container)
    {
	if (noContent())
	    return false;
	Node n = getParagraph();
	while (n != null && n != container)
	    n = n.parentNode;
	return n == container;
    }

    public boolean isTitleRow()
    {
	final Row row = getRow();
	if (row == null)
	    return false;
	return row.getFirstPart().run() instanceof TitleRun;
    }

    public Node getTitleParentNode()
    {
	if (!isTitleRow())
	    return null;
	return getRow().getFirstPart().run().getParentNode();
    }

    public Node getNode()
    {
	if (isTitleRow())
	    return getTitleParentNode();
	return getParaContainer();
    }




    //returns -1 if no content
    public int getRowAbsIndex()
    {
	if (noContent())
	    return -1;
	return current;
    }

    //returns -1 if no content
    public int getRowRelIndex()
    {
	if (noContent())
	    return -1;
	//	return current - getParagraph().topRowIndex;
	return getRow().getFirstPart().relRowNum();
    }

    public boolean isFirstRow()
    {
	return getRowRelIndex() == 0;
    }

    public boolean isEmptyRow()
    {
	if (noContent())
	    return true;
	return rows[current].isEmpty();
    }


    private Run getFirstRunOfRow()
    {
	if (noContent())
	    return null;
	return rows[current].getFirstPart().run();
    }

    //returns -1 if no content 
    public int getParaIndex()
    {
	if (noContent())
	    return -1;
	final Paragraph para = getParagraph();
	return para != null?para.getIndexInParentSubnodes():-1;
    }

    public boolean isFirstPara()
    {
	if (noContent())
	    return false;
	return getParaIndex() == 0;
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

    public String getText()
    {
	if (noContent())
	    return "";
	final Row row = rows[current];
	return !row.isEmpty()?row.text():"";
    }

    public Run getRunUnderPos(int pos)
    {
	if (noContent())
	    return null;
	return rows[current].getRunUnderPos(pos);
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
}

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
    protected NodeImpl root;
    protected Paragraph[] paragraphs;
    //    private RowPart[] rowParts;
    protected Row[] rows;

    protected int current = 0;

    Iterator(Document document)
    {
	NullCheck.notNull(document, "document");
	this.document = document;
	this.root = document.getRoot();
	this.paragraphs = document.getParagraphs();
	//	this.rowParts = document.getRowParts();
	this.rows = document.getRows();
	current = 0;
    }

    Iterator(Document document, int index)
    {
	NullCheck.notNull(document, "document");
	this.document = document;
	this.root = document.getRoot();
	this.paragraphs = document.getParagraphs();
	//	this.rowParts = document.getRowParts();
	this.rows = document.getRows();
	current = index < rows.length?index:0;
    }

    public boolean noContent()
    {
	if (document == null)
	    return true;
	if (rows == null || rows.length < 1)
	    return true;
	/*
	if (rowParts == null || rowParts.length < 1)
	    return true;
	*/
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
	return current - getParagraphImpl().topRowIndex;
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

    public Paragraph getParagraph()
    {
	if (noContent())
	    return null;
	return getParagraphImpl();
    }

    private Paragraph getParagraphImpl()
    {
	if (noContent())
	    return null;
	return getFirstRunOfRow().getParentParagraph();
    }

    private Run getFirstRunOfRow()
    {
	if (noContent())
	    return null;
	return rows[current].getFirstPart().run;
    }

    //returns -1 if no content 
    public int getParaIndex()
    {
	if (noContent())
	    return -1;
	return getParagraphImpl().getIndexInParentSubnodes();
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

    public boolean isFirstPara()
    {
	if (noContent())
	    return false;
	return getParaIndex() == 0;
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

    public NodeImpl getParaContainer()
    {
	if (noContent())
	    return null;
	return getParagraphImpl().parentNode;
    }

    public boolean isContainerTableCell()
    {
	if (noContent())
	    return false;
	if (isEmptyRow())
	    return false;
	final NodeImpl container = getParaContainer();
	return container.type == Node.Type.TABLE_CELL && (container instanceof TableCell);
    }

    public TableCell getTableCell()
    {
	if (noContent())
	    return null;
	final NodeImpl container = getParaContainer();
	if (container == null || !(container instanceof TableCell))
	    return null;
	return (TableCell)container;
    }

    public boolean isContainerListItem()
    {
	if (noContent())
	    return false;
	if (isEmptyRow())
	    return false;
	final NodeImpl container = getParaContainer();
	return container.type == Node.Type.LIST_ITEM && (container instanceof ListItem);
    }

    public ListItem getListItem()
    {
	if (noContent())
	    return null;
	final NodeImpl container = getParaContainer();
	if (container == null || !(container instanceof ListItem))
	    return null;
	return (ListItem)container;
    }

    public boolean isContainerSection()
    {
	if (noContent())
	    return false;
	if (isEmptyRow())
	    return false;
	final NodeImpl container = getParaContainer();
	return container.type == Node.Type.SECTION && (container instanceof Section);
    }

    public Section getSection()
    {
	if (noContent())
	    return null;
	final NodeImpl container = getParaContainer();
	if (container == null || !(container instanceof Section))
	    return null;
	return (Section)container;
    }

    public boolean hasContainerInParents(Node container)
    {
	if (noContent())
	    return false;
	NodeImpl n = getParagraphImpl();
	while (n != null && n != container)
	    n = n.parentNode;
	return n == container;
    }

    public boolean moveNext()
    {
	if (!canMoveNext())
	    return false;
	++current;
	return true;
    }

    public boolean moveNextUntilContainer(Node container)
    {
	final Iterator it = (Iterator)clone();
	while (!it.hasContainerInParents(container))
	    if (!it.moveNext())
		break;
	if (!it.hasContainerInParents(container))
	    return false;
	current = it.getRowAbsIndex();
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
}

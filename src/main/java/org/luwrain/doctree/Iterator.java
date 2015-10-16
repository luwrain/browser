/*
   Copyright 2012-2015 Michael Pozhidaev <michael.pozhidaev@gmail.com>
   Copyright 2015 Roman Volovodov <gr.rPman@gmail.com>

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
    private Document document;
    private NodeImpl root;
    private ParagraphImpl[] paragraphs;
    private RowPart[] rowParts;
    private RowImpl[] rows;

    private int current = 0;

    Iterator(Document document)
    {
	NullCheck.notNull(document, "document");
	this.document = document;
	this.root = document.getRoot();
	this.paragraphs = document.getParagraphs();
	this.rowParts = document.getRowParts();
	this.rows = document.getRows();
	current = 0;
    }

    Iterator(Document document, int index)
    {
	NullCheck.notNull(document, "document");
	this.document = document;
	this.root = document.getRoot();
	this.paragraphs = document.getParagraphs();
	this.rowParts = document.getRowParts();
	this.rows = document.getRows();
	current = index;
    }

    @Override public Object clone()
    {
	return new Iterator(document, current);
    }

    public Row getCurrentRow()
    {
	return rows[current];
    }

    public int getCurrentRowAbsIndex()
    {
	return current;
    }

    public int getCurrentRowRelIndex()
    {
	return current - getCurrentParagraphImpl().topRowIndex;
    }

    public boolean isCurrentRowFirst()
    {
	return getCurrentRowRelIndex() == 0;
    }

    public boolean isCurrentRowEmpty()
    {
	return !rows[current].hasAssociatedText();
    }

    public ParagraphImpl getCurrentParagraph()//FIXME:Should be simply Paragraph, not "Impl"
    {
	return getCurrentParagraphImpl();
    }

    private ParagraphImpl getCurrentParagraphImpl()
    {
	return getFirstRunOfCurrentRow().parentParagraph;
    }

    private Run getFirstRunOfCurrentRow()
    {
	return rows[current].getFirstPart(rowParts).run;
    }

    public int getCurrentParaIndex()
    {
	return getCurrentParagraphImpl().getIndexInParentSubnodes();
    }

    public boolean isCurrentParaFirst()
    {
	return getCurrentParaIndex() == 0;
    }

    public String getCurrentText()
    {
	final RowImpl row = rows[current];
	return row.hasAssociatedText()?row.text(rowParts):"";
    }

    public String getCurrentTextWithHref(String hrefPrefix)
    {
	final RowImpl row = rows[current];
	return row.hasAssociatedText()?row.textWithHrefs(rowParts, hrefPrefix):"";
    }

    //May return null if there is no href at the specified position
    public String getHref(int pos)
    {
	final RowImpl row = rows[current];
	return row.hasAssociatedText()?row.href(rowParts, pos):null;
    }



    public NodeImpl getCurrentParaContainer()
    {
	return getCurrentParagraphImpl().parentNode;
    }

    public boolean isCurrentParaContainerTableCell()
    {
	if (isCurrentRowEmpty())
	    return false;
	final NodeImpl container = getCurrentParaContainer();
	return container.type == Node.TABLE_CELL && (container instanceof TableCell);
    }

    public TableCell getTableCell()
    {
	final NodeImpl container = getCurrentParaContainer();
	if (container == null || !(container instanceof TableCell))
	    return null;
	return (TableCell)container;
    }

    public boolean isCurrentParaContainerListItem()
    {
	if (isCurrentRowEmpty())
	    return false;
	final NodeImpl container = getCurrentParaContainer();
	return container.type == Node.LIST_ITEM && (container instanceof ListItem);
    }

    public ListItem getListItem()
    {
	final NodeImpl container = getCurrentParaContainer();
	if (container == null || !(container instanceof ListItem))
	    return null;
	return (ListItem)container;
    }

    public boolean isCurrentParaContainerSection()
    {
	if (isCurrentRowEmpty())
	    return false;
	final NodeImpl container = getCurrentParaContainer();
	return container.type == Node.SECTION && (container instanceof Section);
    }

    public Section getSection()
    {
	final NodeImpl container = getCurrentParaContainer();
	if (container == null || !(container instanceof Section))
	    return null;
	return (Section)container;
    }

    public boolean hasContainerInParents(Node container)
    {
	NodeImpl n = getCurrentParagraphImpl();
	while (n != null && n != container)
	    n = n.parentNode;
	return n == container;
    }

    public boolean moveNext()
    {
	if (current + 1 >= rows.length)
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
	current = it.getCurrentRowAbsIndex();
	return true;
    }

    public boolean movePrev()
    {
	if (current == 0)
	    return false;
	--current;
	return true;
    }

    public void moveEnd()
    {
	current = rowParts.length > 0?rowParts.length - 1:0;
    }

    public void moveHome()
    {
	current = 0;
    }

    public boolean canMoveNext()
    {
	return current + 1 < rows.length;
    }

    public boolean canMovePrev()
    {
	return current > 0;
    }

    @Override public boolean equals(Object o)
    {
	if (o == null || !(o instanceof Iterator))
	    return false;
	final Iterator it2 = (Iterator)o;
	return document == it2.document && current == it2.current;
    }
}

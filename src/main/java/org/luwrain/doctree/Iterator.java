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

    public Iterator(Document document)
    {
	NullCheck.notNull(document, "document");
	this.document = document;
	this.root = document.getRoot();
	this.paragraphs = document.getParagraphs();
	this.rowParts = document.getRowParts();
	this.rows = document.getRows();
	current = 0;
    }

    public Iterator(Document document, int index)
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

    private ParagraphImpl getCurrentParagraphImpl()
    {
	return getFirstRunOfCurrentRow().parentParagraph;
    }

    private Run getFirstRunOfCurrentRow()
    {
	final int partIndex = rows[current].partsFrom;
	if (partIndex < 0 || partIndex >= rowParts.length)
	    throw new IllegalArgumentException("doctree:row " + current + " has negative partsFrom variable;, unable to find a corresponding run");//Maybe it is better to use some another exception type
	return rowParts[partIndex].run;
    }

    public int getCurrentParagraphIndex()
    {
	return getCurrentParagraphImpl().getIndexInParentSubnodes();
    }

    public String getCurrentText()
    {
	final RowImpl row = rows[current];
	return row.hasAssociatedText()?row.text(rowParts):"";
    }

    public NodeImpl getCurrentParaContainer()
    {
	return getCurrentParagraphImpl().parentNode;
    }

    public boolean hasContainerInParents(Node container)
    {
	NodeImpl n = getCurrentParagraphImpl();
	while (n != null && n != container)
	    n = n.parentNode;
	return n == container;
    }

    public boolean isCurrentParaContainerTableCell()
    {
	if (isCurrentRowEmpty())
	    return false;
	final NodeImpl container = getCurrentParaContainer();
	return container.type == Node.TABLE_CELL &&
	container.parentNode != null && container.parentNode.type == Node.TABLE_ROW &&
	container.parentNode.parentNode != null && container.parentNode.parentNode.type == Node.TABLE &&
	container.parentNode.parentNode instanceof Table;
    }

    public Table getTableOfCurrentParaContainer()
    {
	if (isCurrentRowEmpty())
	    return null;
	final NodeImpl container = getCurrentParaContainer();
	if (container == null || container.type != Node.TABLE_CELL)
	    return null;
	if (container.parentNode == null || container.parentNode.parentNode == null)
	    return null;
	final NodeImpl tableNode = container.parentNode.parentNode;
	if (tableNode instanceof Table)
	    return (Table)tableNode;
	return null;
    }

    public boolean isCurrentParaContainerListItem()
    {
	if (isCurrentRowEmpty())
	    return false;
	final NodeImpl container = getCurrentParaContainer();
	return container.type == Node.LIST_ITEM &&
	container.parentNode != null &&
	(container.parentNode.type == Node.ORDERED_LIST || container.parentNode.type == Node.UNORDERED_LIST);
    }

    public int getListItemIndexOfCurrentParaContainer()
    {
	return getCurrentParaContainer().getIndexInParentSubnodes();
    }

    public boolean isListOfCurrentParaContainerOrdered()
    {
	return getCurrentParaContainer().parentNode.type == Node.ORDERED_LIST;
    }

    public boolean moveNext()
    {
	if (rowParts.length == 0 || current + 1 >= rowParts.length)
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
	return current + 1 < rowParts.length;
    }

    public boolean canMovePrev()
    {
	return current > 0;
    }
}

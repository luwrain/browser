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

public class NodeFactory
{
    static public NodeImpl create(int type)
    {
	switch(type)
	{
	case Node.PARAGRAPH:
	    throw new IllegalArgumentException("doctree.ParagraphImpl may not be created through NodeFactory.create(), use NodeFactory.createPara() instead");
	case Node.SECTION:
	    throw new IllegalArgumentException("doctree.Section may not be created through NodeFactory.create(), use NodeFactory.createPara() instead");
	case Node.TABLE:
	    return new Table();
	case Node.TABLE_ROW:
	    return new TableRow();
	case Node.TABLE_CELL:
	    return new TableCell();
	case Node.LIST_ITEM:
	    return new ListItem();
	default:
	return new NodeImpl(type);
	}
    }

    static public NodeImpl createSection(int level)
    {
	return new Section(level);
    }

    static public ParagraphImpl createPara()
    {
	return new ParagraphImpl();
    }

    static public ParagraphImpl createPara(String text)
    {
	final ParagraphImpl para = createPara();
	para.runs = new Run[]{new Run(text)};
	return para;
    }
}

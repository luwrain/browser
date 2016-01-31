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

public interface Visitor
{
    void visitNode(NodeImpl node);
    void visit(ListItem node);
    void visit(ParagraphImpl node);
    void visit(Section node);
    void visit(TableCell node);
    void visit(Table node);
    void visit(TableRow node);

    static public void walk(NodeImpl node, Visitor visitor)
    {
	NullCheck.notNull(node, "node");
	NullCheck.notNull(visitor, "visitor");
	if (node instanceof ListItem)
	    visitor.visit((ListItem)node); else
	if (node instanceof Section)
	    visitor.visit((Section)node); else
	if (node instanceof Table)
	    visitor.visit((Table)node); else
	if (node instanceof TableRow)
	    visitor.visit((TableRow)node); else
	if (node instanceof TableCell)
	    visitor.visit((TableCell)node); else
	if (node instanceof ParagraphImpl)
	    visitor.visit((ParagraphImpl)node); else
	    visitor.visitNode(node);
	if (node.subnodes != null)
	for(NodeImpl n: node.subnodes)
	    walk(n, visitor);
    }
}

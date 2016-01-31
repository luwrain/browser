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

package org.luwrain.doctree.filters;

import java.util.*;
import java.io.*;
import java.nio.file.*;

import org.luwrain.doctree.*;
import org.luwrain.util.*;
import org.luwrain.core.NullCheck;

public class TxtParaEachLine
{
    private String fileName;

    public TxtParaEachLine(String fileName)
    {
	this.fileName = fileName;
	NullCheck.notNull(fileName, "fileName");
    }

    public Document constructDocument(String encoding)
    {
	final Path path = Paths.get(fileName);
	final String[] lines = TxtParaIndent.read(path, encoding);
	if (lines == null)
	    return null;
	return format(lines);
    }

    private Document format(String[] lines)
    {
	final LinkedList<NodeImpl> nodes = new LinkedList<NodeImpl>();
	for(String line: lines)
	{
	    if (line.trim().isEmpty())
		continue;
	    nodes.add(NodeFactory.newPara(line.trim()));
	}
	final NodeImpl root = NodeFactory.newNode(Node.Type.ROOT); 
	root.subnodes = nodes.toArray(new NodeImpl[nodes.size()]);
	return new Document(root);
    }
}

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

package org.luwrain.doctree.filters;

import java.util.*;
import java.io.*;
import java.nio.file.*;

import org.luwrain.doctree.*;
import org.luwrain.util.*;
import org.luwrain.core.NullCheck;

public class TxtParaEmptyLine
{
    private String fileName;

    public TxtParaEmptyLine(String fileName)
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
	final LinkedList<String> paraLines = new LinkedList<String>();
	final LinkedList<NodeImpl> nodes = new LinkedList<NodeImpl>();
	for(String line: lines)
	{
	    if (line.trim().isEmpty())
	    {
		final ParagraphImpl para = createPara(paraLines);
		if (para != null)
		    nodes.add(para);
		continue;
	    }
	    paraLines.add(line.trim());
	}
	final ParagraphImpl para = createPara(paraLines);
	if (para != null)
	    nodes.add(para);
	final NodeImpl root = NodeFactory.create(Node.ROOT); 
	root.subnodes = nodes.toArray(new NodeImpl[nodes.size()]);
	return new Document(root);
    }

    private ParagraphImpl createPara(LinkedList<String> linesList)
    {
	final String[] lines = linesList.toArray(new String[linesList.size()]);
	linesList.clear();
	if (lines.length < 1)
	    return null;
	if (lines.length == 1)
	    return NodeFactory.createPara(lines[0]);
	final StringBuilder b = new StringBuilder();
	b.append(lines[0]);
	for(int i = 1;i < lines.length;++i)
	    b.append(" " + lines[i]);
	return NodeFactory.createPara(b.toString());
    }
}

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
import java.nio.charset.*;

import org.luwrain.doctree.*;
import org.luwrain.util.*;

public class Html
{
    private String fileName;
    private String src;

    public Html(boolean shouldRead, String arg)
    {
	if (arg == null)
	    throw new NullPointerException("arg may not be null");
	if (shouldRead)
	{
	    fileName = arg;
	    src = null;
	} else
	{
	    fileName = "";
	    src = arg;
	}
    }

    public Document constructDocument(int width)
    {
	if (src == null)
	read(StandardCharsets.UTF_8);
	if (src == null)
	    return null;
	HtmlParse parse = new HtmlParse();
	new MlReader(new HtmlConfig(), parse, src).read();
	final NodeImpl root = parse.constructRoot();
	return new Document(parse.getTitle(), root, width);
    }

    private void read(Charset encoding)
    {
	try {
	    readImpl(encoding);
	}
	catch (IOException e)
	{
		e.printStackTrace();
		src = null;
		return;
	}
    }

    private void readImpl(Charset encoding) throws IOException
    {
	StringBuilder b = new StringBuilder();
	Path path = Paths.get(fileName);
	try (Scanner scanner =  new Scanner(path, encoding.name())) {
		while (scanner.hasNextLine())
		{
		    b.append(scanner.nextLine());
		    b.append("\n");
		}
	    }
	src = b.toString();
    }
}

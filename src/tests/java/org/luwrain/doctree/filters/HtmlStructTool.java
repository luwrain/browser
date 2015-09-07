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

import org.luwrain.util.*;

public class HtmlStructTool
{
    static public void main(String[] args) throws Exception
    {
	if (args.length < 1)
	{
	    System.out.println("No file to read");
	    return;
	}
	final String text = read(args[0], StandardCharsets.UTF_8);
	final HtmlStructListener listener = new HtmlStructListener();
	new MlReader(new HtmlConfig(), listener, text).read();
    }

    static private String read(String fileName, Charset encoding) throws IOException
    {
	final StringBuilder b = new StringBuilder();
	final Path path = Paths.get(fileName);
	try (Scanner scanner =  new Scanner(path, encoding.name())) {
		while (scanner.hasNextLine())
		{
		    b.append(scanner.nextLine());
		    b.append("\n");
		}
	    }
	return b.toString();
    }
}

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

package org.luwrain.doctree.loading;

import java.io.*;
import java.nio.file.*;
import java.util.regex.*;

import org.luwrain.core.NullCheck;

class XmlEncoding
{
    static private final Pattern pattern1 = Pattern.compile("<?xml.*encoding\\s*=\\s*\"([^\"]*)\".*?>", Pattern.CASE_INSENSITIVE);
    static private final Pattern pattern2 = Pattern.compile("<?xml.*encoding\\s*=\\s*\'([^\']*)\'.*?>", Pattern.CASE_INSENSITIVE);

    static String getEncoding(InputStream s) throws IOException
    {
	NullCheck.notNull(s, "s");
	final BufferedReader r = new BufferedReader(new InputStreamReader(s));
	String line;
	while ( (line = r.readLine()) != null)
	{
	Matcher matcher = pattern1.matcher(line);
	if (matcher.find())
	    return matcher.group(1);
	matcher = pattern2.matcher(line);
	if (matcher.find())
	    return matcher.group(1);
	}
	return null;
    }

    static String getEncoding(Path path) throws IOException
    {
	NullCheck.notNull(path, "path");
	InputStream is = null;
	try {
	    is = Files.newInputStream(path);
	    return getEncoding(is);
	}
	finally
	{
	    if (is != null)
		is.close();
	}
    }
}

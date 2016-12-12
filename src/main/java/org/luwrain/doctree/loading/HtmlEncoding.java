/*
   Copyright 2012-2016 Michael Pozhidaev <michael.pozhidaev@gmail.com>
   Copyright 2015-2016 Roman Volovodov <gr.rPman@gmail.com>

   This file is part of LUWRAIN.

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

import java.util.*;
import java.io.*;
import java.nio.file.*;
import javax.activation.*;

import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

import org.luwrain.core.*;

class HtmlEncoding
{
    static String getEncoding(Path path) throws IOException
    {
	NullCheck.notNull(path, "path");
	//  html5 <meta charset="UTF-8">
        //  html4 <meta http-equiv="Content-Type"
	//content="text/html;charset=ISO-8859-1">
        //doc.updateMetaCharsetElement()
	final Document doc = Jsoup.parse(Files.newInputStream(path), "US-ASCII", path.toString());
        final Elements el = doc.getElementsByTag("meta");
        for(Element e: el)
	{
	    final String cs = e.attr("charset");
	    if(cs != null && !cs.isEmpty())
		return cs;
	    final String httpEquiv = e.attr("http-equiv");
	    if (httpEquiv == null || !httpEquiv.trim().toLowerCase().equals("content-type"))
		continue;
	    final String content = e.attr("content");
	    if(content == null || content.isEmpty())
		continue;
	    final MimeType mime;
	    try {
		mime = new MimeType(content);
	    }
	    catch(MimeTypeParseException ex)
	    {
		ex.printStackTrace();
		continue;
	    }
	    final String baseType = mime.getBaseType();
	    if (baseType == null || !baseType.trim().toLowerCase().equals("text/html"))
		continue;
	    final String res = mime.getParameter("charset");
	    if (res != null && !res.trim().isEmpty())
		return res.trim();
	}
	return "";
    }	
}

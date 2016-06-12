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

import java.net.*;
import java.io.*;
import java.nio.file.*;
import java.nio.charset.*;
import java.util.*;
import javax.activation.*;

import org.luwrain.core.*;
import org.luwrain.doctree.filters.*;
import org.luwrain.doctree.books.BookFactory;

public class LoadingPath
{
    static public Result load(Path path, String mimeType) throws MimeTypeParseException
    {
	NullCheck.notNull(path, "path");
	NullCheck.notNull(mimeType, "mimeType");
	Log.debug("doctree", "need to prepare a document by path " + path.toString() + " and mime type \'" + mimeType + "\'");
	final MimeType mime = !mimeType.isEmpty()?new MimeType(mimeType):Formats.suggest(path);
	final Result res = new Result(Result.Type.OK);
	res.setProperty("path", path.toString());
	res.setProperty("mimetype", mime.toString());
	try {
	    switch (mime.getBaseType())
	    {
	    case Formats.MIME_TYPE_TEXT:
		res.doc = new TxtParaIndent(path.toString()).constructDocument(mime.getParameter("charset") != null?mime.getParameter("charset"):"");
		return res;
	    case Formats.MIME_TYPE_DOC:
		res.doc = new Doc(path).constructDocument();
		return res;
	    case Formats.MIME_TYPE_DOCX:
		res.doc = DocX.read(path);
		return res;
	    case Formats.MIME_TYPE_HTML:
		if (mime.getParameter("charset") != null && mime.getParameter("charset").trim().isEmpty())
		    res.doc = new Html(path, extractCharsetInfo(path), path.toUri().toURL()).constructDocument(); else
		    res.doc = new Html(path, mime.getParameter("charset"), path.toUri().toURL()).constructDocument();
		if (path.getFileName().toString().toLowerCase().equals("ncc.html"))
		{
		    res.book = BookFactory.initDaisy2(res.doc);
		    res.doc = null;
		}
		return res;
	    case Formats.MIME_TYPE_ZIP:
		res.doc = new Zip(path.toString(), "", mime.getParameter("charset") != null?mime.getParameter("charset"):"", path.toUri().toURL()).createDoc();
		return res;
	    case Formats.MIME_TYPE_FB2:
		//		return new FictionBook2(fileName).constructDocument();
		return null;
	    default:
		res.type = Result.Type.UNRECOGNIZED_FORMAT;
		return res;
	    }
	}
	catch(Exception e)
	{
	    e.printStackTrace();
	    res.type = Result.Type.EXCEPTION;
	    res.setProperty("exception.class", e.getClass().getName());
	    res.setProperty("exception.message", e.getMessage());
	    return res;
	}
    }
    
    static private String extractCharsetInfo(Path path) throws IOException
    {
	Log.debug("doctree", "extracting charset info from the HTML file " + path.toString());
	final BufferedReader r = new BufferedReader(new InputStreamReader(Files.newInputStream(path), StandardCharsets.US_ASCII));
	final StringBuilder b = new StringBuilder();
	String line;
	while ( (line = r.readLine()) != null)
	    b.append(line + "\n");
	final String res = HtmlEncoding.getEncoding(new String(b));
	if (res != null)
	    Log.debug("doctree", "recognized charset is " + res); else
	    Log.debug("doctree", "charset isn\'t recognized");
	return res != null?res:"";
    }
   }

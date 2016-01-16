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

import java.net.*;
import java.io.*;
import java.nio.file.*;
import java.nio.charset.*;
import java.util.*;

import org.apache.poi.util.IOUtils;
import org.luwrain.core.*;
import org.luwrain.doctree.filters.*;

public class Factory
{
    static public final int UNRECOGNIZED = 0;
    static public final int TEXT_PARA_EMPTY_LINE = 1;
    static public final int TEXT_PARA_INDENT = 2;
    static public final int TEXT_PARA_EACH_LINE = 3;
    static public final int HTML = 4;
    static public final int DOC = 5;
    static public final int DOCX = 6;
    static public final int EPUB = 7;
	static public final int ZIPTEXT = 8;
	static public final int FICTIONBOOK2 = 9;

    static private final String USER_AGENT = "Mozilla/4.0";

    static public Document loadFromFile(int format, String fileName, String encoding)
    {
	NullCheck.notNull(fileName, "fileName");
	switch (format)
	{
	case TEXT_PARA_INDENT:
	    return new TxtParaIndent(fileName).constructDocument(encoding);
	case TEXT_PARA_EMPTY_LINE:
	    return new TxtParaEmptyLine(fileName).constructDocument(encoding);
	case TEXT_PARA_EACH_LINE:
	    return new TxtParaEachLine(fileName).constructDocument(encoding);
	case DOC:
	    return new Doc(fileName).constructDocument();
	case DOCX:
	    return new DocX(fileName).constructDocument();
	case HTML:
	    //	    return new Html(true, fileName).constructDocument(encoding);

	    try {
		new HtmlJsoup(Paths.get(fileName), encoding).constructDocument();
	    }
	    catch(Exception e)
	    {
		e.printStackTrace(); 
		return null;
	    }

	case EPUB:
		return new Epub(fileName).constructDocument();
	case ZIPTEXT:
		return new Zip(fileName).constructDocument();
	case FICTIONBOOK2 :
	    //		return new FictionBook2(fileName).constructDocument();
	    return null;
	default:
	    throw new IllegalArgumentException("unknown format " + format);
	}
    }

    static public Document fromUrl(URL url, int format, 
				   String charset)
    {
	NullCheck.notNull(url, "url");
	NullCheck.notNull(charset, "charset");
	URLConnection con = null;
	URL resultUrl = null;
	InputStream is = null;
	String contentTypeCharset = null;
	try {
	    con = url.openConnection();
	    con.setRequestProperty("User-Agent", USER_AGENT);
	    if (charset.isEmpty())
	    contentTypeCharset = con.getContentType(); else
		contentTypeCharset = charset;
	    is = con.getInputStream();
	    resultUrl = con.getURL();
	}
	catch(IOException e)
	{
	    //	    if (is != null)
	    //		is.close();
	    //	    if (tmpFile != null)
	    //		tmpFile.delete();
	    return null;
	}
	if (contentTypeCharset != null && !contentTypeCharset.trim().isEmpty())
	    switch(format)
	{
	case HTML:
	    try {
		final Document doc = new HtmlJsoup(is, contentTypeCharset, resultUrl.toString()).constructDocument();
is.close();
is = null;
return doc;
	    }
	    catch(Exception e)
	    {
		//		is.close();
		e.printStackTrace();
		return null;
	    }
	default:
	    //	    is.close();
	    Log.error("doctree", "unknown format:" + format );
	    return null;
	}
	Path path = null;
	try {
	    path = downloadToTmpFile(is);
	is.close();
	is = null;
	contentTypeCharset = extractCharsetInfo(path);
	}
	catch(IOException e)
	{
	    //	    if (is != null)
		//		is.close();
	    e.printStackTrace();
	    return null;
	}
	if (contentTypeCharset == null || contentTypeCharset.trim().isEmpty())
	{
	    Log.error("doctree", "unable to get a charset information for " + url.toString());
	    return null;
	}
	switch(format)
	{
	case HTML:
	    try {
		return new HtmlJsoup(path, contentTypeCharset).constructDocument();
	    }
	    catch(Exception e)
	    {
		e.printStackTrace(); 
		return null;
	    }
	default:
	    Log.error("doctree", "unknown format:" + format);
	    return null;
	}
    }


    static public Document loadFromStream(int format, InputStream stream, String charset)
    {
    	switch (format)
    	{
   		case TEXT_PARA_INDENT:
   		case TEXT_PARA_EMPTY_LINE:
   		case TEXT_PARA_EACH_LINE:
   		case DOC:
   		case DOCX:
   		case HTML:
   		case EPUB:
   		case ZIPTEXT:
			try
			{
				byte[] data;
				data=IOUtils.toByteArray(stream);
	    		return loadFromText(format,new String(data,"UTF-8"));
			} catch(IOException e)
			{
				e.printStackTrace();
				return null;
			}
    	case FICTIONBOOK2 :
	    try {
    		return new FictionBook2(stream,charset).constructDocument();
	    }
	    catch(Exception e)
	    {
		e.printStackTrace();
		return null;
	    }
    	default:
    	    throw new IllegalArgumentException("unknown format " + format);
    	}
    }

    static public Document loadFromText(int format, String text)
    {
	NullCheck.notNull(text, "text");
	switch (format)
	{
	case HTML:
	    //	    return new Html(false, text).constructDocument("");

	    try {
		return new HtmlJsoup(text).constructDocument();
	    }
	    catch(Exception e)
	    {
		e.printStackTrace(); 
		return null;
	    }

	default:
	    throw new IllegalArgumentException("unknown format " + format);
	}
    }

    static public int suggestFormat(String path)
    {
	NullCheck.notNull(path, "path");
	if (path.isEmpty())
	    throw new IllegalArgumentException("path may not be empty");
	String ext = FileTypes.getExtension(path);
	if (ext == null || path.isEmpty())
	    return UNRECOGNIZED;
	ext = ext.toLowerCase();
	switch(ext)
	{
	case "epub":
	    return EPUB;
	case "txt":
	    return TEXT_PARA_INDENT;
	case "doc":
	    return DOC;
	case "docx":
	    return DOCX;
	case "html":
	case "htm":
	    return HTML;
	case "zip":
	    return ZIPTEXT;
	case "fb2":
		return FICTIONBOOK2;
	default:
	    return UNRECOGNIZED;
	}
    }

    static private Path downloadToTmpFile(InputStream s) throws IOException
    {
	final Path path = Files.createTempFile("lwrdoctree-download", "");
	    Files.copy(s, path, StandardCopyOption.REPLACE_EXISTING);
	    return path;
    }

    static String extractCharsetInfo(Path path) throws IOException
    {
	final List<String> lines = Files.readAllLines(path, StandardCharsets.US_ASCII);
	final StringBuilder b = new StringBuilder();
	for(String s: lines)
	    b.append(s + "\n");
	final String res = HtmlEncoding.getEncoding(new String(b));
	return res != null?res:"";
    }
}

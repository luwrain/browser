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
import java.util.zip.*;
import javax.activation.*;

import org.apache.poi.util.IOUtils;
import org.luwrain.core.*;
import org.luwrain.doctree.filters.*;

public class Factory
{
    public enum Format {
	UNRECOGNIZED,
	TEXT_PARA_EMPTY_LINE, TEXT_PARA_INDENT, TEXT_PARA_EACH_LINE,
	HTML,
	DOC, DOCX,
	FB2,
	EPUB,
	ZIPTEXT,
	FB2_ZIP,
    };

    static private final String USER_AGENT = "Mozilla/5.0";
    //static private final String USER_AGENT =      "Emacs-w3m/1.4.466 w3m/0.5.2";

    static private final String DEFAULT_CHARSET = "UTF-8";

    static public Document fromPath(Path path, String contentType, String charset)
    {
	NullCheck.notNull(path, "path");
	NullCheck.notNull(contentType, "contentType");
	NullCheck.notNull(charset, "charset");
	Format filter = Format.UNRECOGNIZED;
	if (!contentType.trim().isEmpty())
	    filter = chooseFilterByContentType(contentType);
	if (filter == Format.UNRECOGNIZED)
	    filter = suggestFilterByExtension(path.toString());
	if (filter == Format.UNRECOGNIZED)
	{
	    Log.warning("doctree", "unable to find a suitable filter for a file, content type is \'" + contentType + "\', path is \'" + path.toString() + "\'");
	    return null;
	}
	return fromPath(path, filter, charset);
    }

    static public Document fromPath(Path path,
				    Format format, String encoding)
    {
	NullCheck.notNull(path, "path");
	try {
	    switch (format)
	    {
	    case TEXT_PARA_INDENT:
		return new TxtParaIndent(path.toString()).constructDocument(encoding);
	    case TEXT_PARA_EMPTY_LINE:
		return new TxtParaEmptyLine(path.toString()).constructDocument(encoding);
	    case TEXT_PARA_EACH_LINE:
		return new TxtParaEachLine(path.toString()).constructDocument(encoding);
	    case DOC:
		return new Doc(path.toString()).constructDocument();
	    case DOCX:
		return new DocX(path.toString()).constructDocument();
	    case HTML:
		return new Html(path, encoding).constructDocument();
	case EPUB:
	    return new Epub(path.toString()).constructDocument();
	case ZIPTEXT:
	    return new Zip(path.toString()).createDoc();
	case FB2:
	    //		return new FictionBook2(fileName).constructDocument();
	    return null;
	default:
	    throw new IllegalArgumentException("Unknown format " + format);
	}
	}
	catch(Exception e)
	{
	    e.printStackTrace();
	    return null;
	}
    }

    static public Document fromUrl(URL unpreparedUrl, 
String contentType, String charset)
    {
	NullCheck.notNull(unpreparedUrl, "unpreparedUrl");
	NullCheck.notNull(contentType, "contentType");
	NullCheck.notNull(charset, "charset");
	Log.debug("doctree", "fetching url " + unpreparedUrl.toString() + " (content type=" + contentType + ")");
	URL url = null;
	try {
	    url = new URL(unpreparedUrl.getProtocol(), IDN.toASCII(unpreparedUrl.getHost()),
			  unpreparedUrl.getPort(), unpreparedUrl.getFile());
	}
	catch(MalformedURLException e)
	{
	    e.printStackTrace();
	    return null;
	}
	try {
	    return fromUrlImpl(url, contentType, charset);
	}
	catch(Exception e)
	{
	    e.printStackTrace();
	    return null;
	}
    }

    static public Document fromUrlImpl(URL url,
String contentType, String charset) throws Exception
    {
	NullCheck.notNull(url, "url");
	NullCheck.notNull(charset, "charset");
	InputStream is = null;
	InputStream effectiveIs = null;
	URLConnection con;
	try {
	    con = url.openConnection();
	    con.setRequestProperty("User-Agent", USER_AGENT);
	    con.connect();
	    while(true)
	    {
		if (!(con instanceof HttpURLConnection))
		    break;
		final HttpURLConnection httpCon = (HttpURLConnection)con;
final int code = httpCon.getResponseCode();
if (code >= 400 && code < 200)
{
    Log.warning("doctree", "HTTP responce code is " + code);
    return null;
}
if (code >= 200 && code <= 299)
    break;
final String location = httpCon.getHeaderField("location");
if (location == null || location.isEmpty())
{
    Log.warning("doctree", "HTTP responce code is " + code + " but \'location\' field is empty");
    return null;
}
Log.debug("doctree", "trying to follow redirect to " + location);
final URL locationUrl = new URL(location);
	    con = locationUrl.openConnection();
	    con.setRequestProperty("User-Agent", USER_AGENT);
	    con.connect();
	    }
	    is = con.getInputStream();
	    final URL resultUrl = con.getURL();
	    final String effectiveContentType = (contentType == null || contentType.trim().isEmpty())?getBaseContentType(con.getContentType()):contentType;
final String effectiveCharset = (charset == null || charset.trim().isEmpty())?getCharset(con.getContentType()):charset;
final String encoding = con.getContentEncoding();
if (encoding != null && encoding.toLowerCase().trim().equals("gzip"))
    effectiveIs = new GZIPInputStream(is); else
    effectiveIs = is;
return fromInputStream(effectiveIs, effectiveContentType, effectiveCharset, resultUrl != null?resultUrl.toString():url.toString(), Format.HTML);
	}
	finally
	       {
		   if (effectiveIs != null)
		   {
		       effectiveIs.close();
effectiveIs = null;
		   }
		   if (is != null)
		   {
		       is.close();
		       is = null;
		   }
	       }
    }

    static public Document fromInputStream(InputStream stream, String contentType,
					   String charset, String baseUrl,
Format defaultFilter) throws Exception
    {
	NullCheck.notNull(stream, "stream");
	NullCheck.notNull(contentType, "contentType");
	NullCheck.notNull(charset, "charset");
	NullCheck.notNull(baseUrl, "baseUrl");
	Format filter = chooseFilterByContentType(contentType);
	if (filter == Format.UNRECOGNIZED)
	    filter = defaultFilter;
	if (filter == Format.UNRECOGNIZED)
	{
	    Log.warning("doctree", "unable to suggest a filter for content type \'" + contentType + "\'");
	    return null;
	}
	Log.debug("doctree", "reading input stream using " + filter + " filter");
	InputStream effectiveStream = stream;
	String effectiveCharset = null;
	Path tmpFile = null;
	try {
	if (charset.trim().isEmpty())
	{
	    switch(filter)
	    {
	    case FB2:
tmpFile = downloadToTmpFile(stream);
effectiveCharset = XmlEncoding.getEncoding(tmpFile);
Log.debug("doctree", "XML encoding of " + tmpFile.toString() + " is " + effectiveCharset);
effectiveStream = Files.newInputStream(tmpFile);
break;
	    case HTML:
tmpFile = downloadToTmpFile(stream);
effectiveCharset = extractCharsetInfo(tmpFile);
Log.debug("doctree", "HTML encoding of " + tmpFile.toString() + " is " + effectiveCharset);
effectiveStream = Files.newInputStream(tmpFile);
break;
	    }
	} else
	    effectiveCharset = charset;
	if (effectiveCharset == null || effectiveCharset.trim().isEmpty())
	    effectiveCharset = DEFAULT_CHARSET;
	//	System.out.println("effectiveCharset=" + effectiveCharset);
	switch(filter)
	{
	case HTML:
	    return new Html(effectiveStream, effectiveCharset, baseUrl).constructDocument();
	case FB2:
	    return new org.luwrain.doctree.filters.FictionBook2(effectiveStream, effectiveCharset).createDoc();
	case FB2_ZIP:
	    return new org.luwrain.doctree.filters.Zip(effectiveStream, "application/fb2", charset, baseUrl).createDoc();
	default:
	    return null;
	}
	}
	finally
	{
	    if (effectiveStream != stream)
		effectiveStream.close();
	    if (tmpFile != null)
	    {
		Log.debug("doctree", "deleting temporary file " + tmpFile.toString());
		Files.delete(tmpFile);
	    }
	}
    }

    static public Document loadFromStream(Format format, InputStream stream, String charset)
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
    	case FB2:
	    try {
    		return new FictionBook2(stream,charset).createDoc();
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


    static public Document loadFromText(Format format, String text)
    {
	NullCheck.notNull(text, "text");
	switch (format)
	{
	case HTML:
	    //	    return new Html(false, text).constructDocument("");
	    try {
		return new Html(text).constructDocument();
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

    static public Format suggestFilterByExtension(String path)
    {
	NullCheck.notNull(path, "path");
	if (path.isEmpty())
	    throw new IllegalArgumentException("path may not be empty");
	String ext = FileTypes.getExtension(path);
	if (ext == null || path.isEmpty())
	    return Format.UNRECOGNIZED;
	ext = ext.toLowerCase();
	switch(ext)
	{
	case "epub":
	    return Format.EPUB;
	case "txt":
	    return Format.TEXT_PARA_INDENT;
	case "doc":
	    return Format.DOC;
	case "docx":
	    return Format.DOCX;
	case "html":
	case "htm":
	    return Format.HTML;
	case "zip":
	    return Format.ZIPTEXT;
	case "fb2":
		return Format.FB2;
	default:
	    return Format.UNRECOGNIZED;
	}
    }

    static private Path downloadToTmpFile(InputStream s) throws IOException
    {
	final Path path = Files.createTempFile("lwrdoctree-download", "");
	Log.debug("doctree", "creating temporary file " + path.toString());
	    Files.copy(s, path, StandardCopyOption.REPLACE_EXISTING);
	    return path;
    }

    static String extractCharsetInfo(Path path) throws IOException
    {
	final List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
	final StringBuilder b = new StringBuilder();
	for(String s: lines)
	    b.append(s + "\n");
	final String res = HtmlEncoding.getEncoding(new String(b));
	return res != null?res:"";
    }

    static private Format chooseFilterByContentType(String contentType)
    {
	NullCheck.notNull(contentType, "contentType");
	switch(contentType.toLowerCase().trim())
	{
	case "text/html":
	    return Format.HTML;
	case "application/fb2":
	    return Format.FB2;
	case "application/fb2+zip":
	    return Format.FB2_ZIP;
	default:
	    return Format.UNRECOGNIZED;
	}
    }

    static private String getBaseContentType(String value)
    {
	if (value == null)
	    return "";
	    try {
		final MimeType mime = new MimeType(value);
		final String res = mime.getBaseType();
		return res != null?res:"";
	    }
	    catch(MimeTypeParseException e)
	    {
		e.printStackTrace();
		return "";
	    }
    }

    static private String getCharset(String value)
    {
	if (value == null)
	    return "";
	    try {
		final MimeType mime = new MimeType(value);
		final String res = mime.getParameter("charset");
		return res != null?res:"";
	    }
	    catch(MimeTypeParseException e)
	    {
		e.printStackTrace();
		return "";
	    }
    }
}

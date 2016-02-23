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
	TEXT_PARA_EMPTY_LINE, TEXT_PARA_INDENT, TEXT_PARA_EACH_LINE,
	HTML, DOC, DOCX,
	FB2, EPUB, SMIL,
	ZIP, FB2_ZIP,
    };

    static public final String USER_AGENT = "Mozilla/5.0";
    static private final String DEFAULT_CHARSET = "UTF-8";

    static public Result fromPath(Path path, String contentType, String charset)
    {
	NullCheck.notNull(path, "path");
	NullCheck.notNull(contentType, "contentType");
	NullCheck.notNull(charset, "charset");
	Format filter = null;
	if (!contentType.trim().isEmpty())
	    filter = chooseFilterByContentType(contentType);
	if (filter == null)
	    filter = chooseFilterByExtension(path.toString());
	if (filter == null)
	    return new Result(Result.Type.UNRECOGNIZED_FORMAT, path.toString());
	return fromPath(path, filter, charset);
    }

    static public Result fromPath(Path path,
				  Format format, String charset)
    {
	NullCheck.notNull(path, "path");
	NullCheck.notNull(format, "format");
	NullCheck.notNull(charset, "charset");
	final Result res = new Result(Result.Type.OK, path.toString());
	res.format = format.toString();
	try {
	    switch (format)
	    {
	    case TEXT_PARA_INDENT:
		res.doc = new TxtParaIndent(path.toString()).constructDocument(charset);
		return res;
	    case TEXT_PARA_EMPTY_LINE:
		res.doc = new TxtParaEmptyLine(path.toString()).constructDocument(charset);
		return res;
	    case TEXT_PARA_EACH_LINE:
		res.doc = new TxtParaEachLine(path.toString()).constructDocument(charset);
		return res;
	    case DOC:
		res.doc = new Doc(path.toString()).constructDocument();
		return res;
	    case DOCX:
		res.doc = new DocX(path.toString()).constructDocument();
		return res;
	    case HTML:
		res.doc = new Html(path, charset).constructDocument();
		return res;
	    case EPUB:
		res.doc = new Epub(path.toString()).constructDocument();
		return res;
	    case ZIP:
		res.doc = new Zip(path.toString(), "", charset, path.toString()).createDoc();
		return res;
	    case FB2:
		//		return new FictionBook2(fileName).constructDocument();
		return null;

	    case SMIL:
		org.luwrain.util.Smil.fromPath(path);
		return new Result(Result.Type.UNEXPECTED_ERROR);
	    default:
		return new Result(Result.Type.UNRECOGNIZED_FORMAT, path.toString());
	    }
	}
	catch(Exception e)
	{
	    e.printStackTrace();
	    return new Result(Result.Type.UNEXPECTED_ERROR, path.toString());
	}
    }

    static public Result fromUrl(URL unpreparedUrl, 
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
	    final Result res = new Result(Result.Type.INVALID_URL);
	    res.origAddr = unpreparedUrl.toString();
	    res.resultAddr = res.origAddr;
	    return res;
	}
	try {
	    final Result res = fromUrlImpl(url, contentType, charset);
	    res.origAddr = unpreparedUrl.toString();
	    if (res.resultAddr == null || res.resultAddr.isEmpty())
		res.resultAddr = res.origAddr;
	    return res;
	}
	catch(Exception e)
	{
	    e.printStackTrace();
	    final Result res = new Result(Result.Type.UNEXPECTED_ERROR);
	    res.origAddr = unpreparedUrl.toString();
	    res.resultAddr = res.origAddr;
	    return res;
	}
    }

    static public Result fromUrlImpl(URL url,
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
		if (code >= 400 || code < 200)
		{
		    Log.warning("doctree", "HTTP responce code is " + code);
		    return new Result(Result.Type.HTTP_ERROR, code);
		}
		if (code >= 200 && code <= 299)
		    break;
		final String location = httpCon.getHeaderField("location");
		if (location == null || location.isEmpty())
		{
		    Log.warning("doctree", "HTTP responce code is " + code + " but \'location\' field is empty");
		    return new Result(Result.Type.INVALID_HTTP_REDIRECT);
		}
		Log.debug("doctree", "trying to follow redirect to " + location);
		final URL locationUrl = new URL(location);
		con = locationUrl.openConnection();
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.connect();
	    }
	    is = con.getInputStream();
	    final URL resultUrl = con.getURL();
	    Log.debug("doctree", "content type in HTTP header is \'" + con.getContentType() + "\'");
	    final String effectiveContentType = (contentType == null || contentType.trim().isEmpty())?getBaseContentType(con.getContentType()):contentType;
	    Log.debug("doctree", "effective content type is \'" + effectiveContentType + "\'");
	    final String effectiveCharset = (charset == null || charset.trim().isEmpty())?getCharset(con.getContentType()):charset;
	    Log.debug("doctree", "effective charset is \'" + effectiveCharset + "\'");
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

    static public Result fromInputStream(InputStream stream, String contentType,
					 String charset, String baseUrl,
					 Format defaultFilter) throws Exception
    {
	NullCheck.notNull(stream, "stream");
	NullCheck.notNull(contentType, "contentType");
	NullCheck.notNull(charset, "charset");
	NullCheck.notNull(baseUrl, "baseUrl");
	Format filter = chooseFilterByContentType(contentType);
	if (filter == null)
	    filter = defaultFilter;
	if (filter == null)
	{
	    Log.warning("doctree", "unable to suggest a filter for content type \'" + contentType + "\'");
	    return new Result(Result.Type.UNRECOGNIZED_FORMAT);
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
	    final Result res = new Result(Result.Type.OK);
	    res.format = filter.toString();
	    res.charset = effectiveCharset;
	    res.resultAddr = baseUrl;
	    switch(filter)
	    {
	    case HTML:
		res.doc = new Html(effectiveStream, effectiveCharset, baseUrl).constructDocument();
		return res;
	    case FB2:
		Log.debug("proba", "1");
		res.doc = new FictionBook2(effectiveStream, effectiveCharset).createDoc();
		Log.debug("proba", "2");
		return res;
	    case ZIP:
		res.charset = charset;
		tmpFile = downloadToTmpFile(stream);
		Log.debug("doctree", "dealing with ZIP, so, downloading to tmp file " + tmpFile.toString());
		res.doc = new org.luwrain.doctree.filters.Zip(tmpFile.toString(), "", charset, baseUrl).createDoc();
		return res;
	    case FB2_ZIP:
		res.charset = charset;
		tmpFile = downloadToTmpFile(stream);
		Log.debug("doctree", "dealing with ZIP, so, downloading to tmp file " + tmpFile.toString());
		res.doc = new org.luwrain.doctree.filters.Zip(tmpFile.toString(), "application/fb2", charset, baseUrl).createDoc();
		return res;
	    default:
		return new Result(Result.Type.UNRECOGNIZED_FORMAT);
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

    static public Format chooseFilterByExtension(String path)
    {
	NullCheck.notNull(path, "path");
	if (path.isEmpty())
	    return null;
	String ext = FileTypes.getExtension(path);
	if (ext == null)
	    return null;
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
	    return Format.ZIP;
	case "fb2":
	    return Format.FB2;
	case "smil":
	    return Format.SMIL;
	default:
	    return null;
	}
    }

    static public Format chooseFilterByContentType(String contentType)
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
	case "application/zip":
	    return Format.ZIP;
	default:
	    return null;
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

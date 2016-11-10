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

package org.luwrain.util;

import java.util.*;
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.FileWriter;
import java.io.IOException;
//import java.io.InputStream;
import java.net.*;
import javax.activation.*;

import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;

import org.luwrain.core.NullCheck;

/**
 * OPDS (Open Publication Distribution System) parser. This class
 * contains a number of utilities to fetch and parse data provided by
 * OPDS resources (usually digital libraries). Basically, it just reads
 * XML and saves necessary data in corresponding classes for further
 * using in client applications.
 */
public class Opds
{
    final static private int BUFFER_SIZE=32*1024;

    static public class Link
    {
	static public final String PROFILE_CATALOG = "opds-catalog";
	static public final String BASE_TYPE_CATALOG = "application/atom+xml";
	static public final String PRIMARY_TYPE_IMAGE = "image";

	private final String url;
	private final String rel;
	private final String type;
	private final String profile;

	Link(String url, String rel,
	     String type, String profile)
	{
	    NullCheck.notNull(url, "url");
	    this.url = url;
	    this.rel = rel;
	    this.type = type;
	    this.profile = profile;
	}

	public boolean isCatalog()
	{
	    if (getTypeProfile().toLowerCase().equals(PROFILE_CATALOG))
		return true;
	    return getBaseType().equals(BASE_TYPE_CATALOG);
	}

	public boolean isImage()
	{
	    return getPrimaryType().toLowerCase().trim().equals(PRIMARY_TYPE_IMAGE);
	}

	//Never returns null
	public String getBaseType()
	{
	    if (type == null)
		return "";
	    try {
		final MimeType mime = new MimeType(type);
		final String value = mime.getBaseType();
		return value != null?value:"";
	    }
	    catch(MimeTypeParseException e)
	    {
		e.printStackTrace();
		return "";
	    }
	}

	//Never returns null
	public String getPrimaryType()
	{
	    if (type == null)
		return "";
	    try {
		final MimeType mime = new MimeType(type);
		final String value = mime.getPrimaryType();
		return value != null?value:"";
	    }
	    catch(MimeTypeParseException e)
	    {
		e.printStackTrace();
		return "";
	    }
	}

	//Never returns null
	public String getSubType()
	{
	    if (type == null)
		return "";
	    try {
		final MimeType mime = new MimeType(type);
		final String value = mime.getSubType();
		return value != null?value:"";
	    }
	    catch(MimeTypeParseException e)
	    {
		e.printStackTrace();
		return "";
	    }
	}

	//Never returns null
	public String getTypeProfile()
	{
	    if (type == null)
		return "";
	    try {
		final MimeType mime = new MimeType(type);
		final String value = mime.getParameter("profile");
		return value != null?value:"";
	    }
	    catch(MimeTypeParseException e)
	    {
		e.printStackTrace();
		return "";
	    }
	}

	@Override public String toString()
	{
	    return "url=" + url + ",rel=" + rel + ",type=" + type + ",profile=" + getTypeProfile();
	}

	public String getUrl(){return url;}
	public String getRel(){return rel;};
	public String getType(){return type;}
	public String getProfile(){return profile;}
    }

    static public class Entry 
    {
	private final String id;
	private final URL parentUrl;
	private final String title;
	private final Link[] links;

	Entry(String id, URL parentUrl,
	      String title, Link[] links)
	{
	    NullCheck.notNull(id, "id");
	    NullCheck.notNull(parentUrl, "parentUrl");
	    NullCheck.notNull(title, "title");
	    NullCheck.notNullItems(links, "links");
	    this.id = id;
	    this.parentUrl = parentUrl;;
	    this.title = title;
	    this.links = links != null?links:new Link[0];
	}

	public Link getCatalogLink()
	{
	    for(Link link: links)
		if (link.isCatalog())
		    return link;
	    return null;
	}

	public boolean isCatalogOnly()
	{
	    for(Link link: links)
		if (!link.isCatalog())
		    return false;
	    return true;
	}

	public boolean hasCatalogLinks()
	{
	    for(Link link: links)
		if (link.isCatalog())
		    return true;
	    return false;
	}

	public boolean hasBooks()
	{
	    for(Link link: links)
		if (!link.isCatalog() && !link.isImage())
		    return true;
	    return false;
	}

	@Override public String toString()
	{
	    return title;
	}

	public String getId(){return id;}
	public URL getParentUrl() {return parentUrl;}
	public String getTitle(){return title;}
	public Link[] getLinks(){return links;}
    }

    static public class Result
    {
	public enum Errors {FETCH, PARSE, NOERROR, NEEDPAY};

	private final Entry[] entries;
	private final Errors error;
	// file link if result is not a directory entry and mime type of it
	//	private String filename;
	//	private String mime;

	Result(Errors error)
	{
	    NullCheck.notNull(error, "error");
	    this.error = error;
	    this.entries = null;
	}

	Result(Entry[] entries)
	{
	    NullCheck.notNullItems(entries, "entries");
	    this.error = Errors.NOERROR;
	    this.entries = entries;
    	}

	public Entry[] getEntries()
	{
	    return entries;
	}

	public Errors getError(){return error;}

	public boolean hasEntries()
	{
	    return error == Errors.NOERROR && entries != null;
	}
    }

    static public Result fetch(URL url)
    {
	NullCheck.notNull(url, "url");
	final LinkedList<Entry> res = new LinkedList<Entry>();
	org.jsoup.nodes.Document doc = null;
	try {
	    final Connection con=Jsoup.connect(url.toString());
	    con.userAgent(org.luwrain.doctree.loading.UrlLoader.USER_AGENT);
	    con.timeout(30000);
	    doc = con.get();
	}
	catch(UnsupportedMimeTypeException e)
	{
	    e.printStackTrace(); 
	    return new Result(Result.Errors.FETCH);
	}
	catch(IOException e)
	{
	    e.printStackTrace(); 
	    return new Result(Result.Errors.FETCH);
	}
	for(org.jsoup.nodes.Element node:doc.getElementsByTag("entry"))
	    try {
		final Entry entry = parseEntry(url, node);
		if (entry != null)
		    res.add(entry);
	    }
	    catch (Exception e)
	    {
		e.printStackTrace();
	    }
	return new Result(res.toArray(new Entry[res.size()]));
    }

    static private Entry parseEntry(URL parentUrl, Element el) throws Exception
    {
	NullCheck.notNull(el, "el");
	String id = "";
	String title = "";
	final LinkedList<Link> links = new LinkedList<Link>();
	for(Element node:el.getElementsByTag("title"))
	    title = node.text();
	for(Element node:el.getElementsByTag("id"))
	    id = node.text();
	for(Element node:el.getElementsByTag("link"))
	    links.add(new Link(node.attributes().get("href"),
			       node.attributes().get("rel"),
			       node.attributes().get("type"),
			       node.attributes().get("profile")));
	if (id != null && title != null && !links.isEmpty())
	    return new Entry(id, parentUrl, title, links.toArray(new Link[links.size()]));
	return null;
    }
}

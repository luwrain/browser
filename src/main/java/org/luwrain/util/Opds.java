/*
   Copyright 2012-2015 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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
import java.net.*;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import org.xml.sax.*;

import org.luwrain.core.NullCheck;

public class Opds
{
    static public class Entry 
    {
	private String id;
	private String title;
	private String link;

	Entry(String id, String title, String link)
	{
	    this.id = id;
	    this.title = title;
	    this.link = link;
	    NullCheck.notNull(id, "id");
	    NullCheck.notNull(title, "title");
	    NullCheck.notNull(link, "link");
	}

	@Override public String toString()
	{
	    return title;
	}

	public String id()
	{
	    return id;
	}

	public String link()
	{
	    return link;
	}

	public String title()
	{
	    return title;
	}
    }

    static public class Directory
    {
	private Entry[] entries;

	Directory(Entry[] entries)
	{
	    this.entries = entries;
	    NullCheck.notNullItems(entries, "entries");
	}

	public Entry[] entries()
	{
	    return entries;
	}
    }

    static public class Result
    {
	public enum Errors {FETCH, PARSE, NOERROR};

	private Directory dir;
	private Errors error;

	Result(Errors error)
	{
	    this.error = error;
	    this.dir = null;
	}

	Result(Directory dir)
	{
	    NullCheck.notNull(dir, "dir");
	    this.error = Errors.NOERROR;
	    this.dir = dir;
	}

	public Directory directory()
	{
	    return dir;
	}

	public Errors error()
	{
	    return error;
	}
    }

    static public Result fetch(URL url)
    {
	NullCheck.notNull(url, "url");
	final LinkedList<Entry> res = new LinkedList<Entry>();
	Document doc = null;
	try {

	final URLConnection con = url.openConnection();
con.setRequestProperty("User-Agent", "Mozilla/4.0");
//	    inputStream = ;
	

	    final DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	    doc = builder.parse(new InputSource(con.getInputStream() ));
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	    return new Result(Result.Errors.FETCH);
	}
	try {
	    final NodeList nodes = doc.getElementsByTagName("entry");
	    for (int i = 0;i < nodes.getLength();++i)
	    {
		final Node node = nodes.item(i);
		if (node == null || node.getNodeType() != Node.ELEMENT_NODE)
		    continue;
		final Entry entry = parseEntry((Element)node);
		//		System.out.println(el.toString());
		if (entry != null)
		    res.add(entry);
		/*
		  final NamedNodeMap attr = el.getAttributes();
		  final Node title = attr.getNamedItem("title");
		  final Node snippet = attr.getNamedItem("snippet");
		  if (title != null)
		  res.add(new Page(lang, title.getTextContent(), snippet != null?MlTagStrip.run(snippet.getTextContent()):""));
		*/
	    }
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	    return new Result(Result.Errors.PARSE);
	}
	return new Result(new Directory(res.toArray(new Entry[res.size()])));
    }

    static private Entry parseEntry(Element el)
    {
	NullCheck.notNull(el, "el");
	try {
	    String id = "";
	    String title = "";
	    String link = "";

	    //Title
	    NodeList nodes = el.getElementsByTagName("title");
	    for (int i = 0;i < nodes.getLength();++i)
		title = nodes.item(i).getTextContent();

	    //ID
	    nodes = el.getElementsByTagName("id");
	    for (int i = 0;i < nodes.getLength();++i)
		id = nodes.item(i).getTextContent();

	    //Link
	    nodes = el.getElementsByTagName("link");
	    for (int i = 0;i < nodes.getLength();++i)
		link = nodes.item(i).getAttributes().getNamedItem("href").getTextContent();
	    if (id != null && title != null && link != null)
		return new Entry(id, title, link);
	    return null;
	}
	catch(Exception e)
	{
	    e.printStackTrace();
	    return null;
	}
    }
}

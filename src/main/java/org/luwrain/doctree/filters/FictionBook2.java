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

import java.io.*;
//import java.io.InputStream;
import java.util.*;
import java.util.function.Consumer;

//import org.apache.poi.util.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import org.luwrain.core.Log;
import org.luwrain.core.NullCheck;
import org.luwrain.doctree.*;

public class FictionBook2
{
	    private Document jdoc = null;

    public FictionBook2(InputStream stream,String charset) throws IOException
    {
	NullCheck.notNull(stream, "stream");
	NullCheck.notNull(charset, "charset");
	jdoc = Jsoup.parse(stream, charset, "", Parser.xmlParser());
    }

    public org.luwrain.doctree.Document createDoc()
    {
	try {
	    final NodeImpl root = NodeFactory.create(Node.ROOT);
	    final LinkedList<NodeImpl> subnodes = new LinkedList<NodeImpl>();
	    final Elements descr=jdoc.select("FictionBook > description");
	    if(!descr.isEmpty())
	    {

		// title
		Elements title=descr.first().getElementsByTag("book-title");
		if(!title.isEmpty())
		{
		    final NodeImpl h1=NodeFactory.createSection(1);
		    h1.subnodes=new NodeImpl[]{NodeFactory.createPara(title.first().text())};
		    subnodes.add(h1);
		}

		// genre
		final Elements genre=descr.first().getElementsByTag("genre");
		if(!genre.isEmpty())
		{
		    String str="Genre:";
		    for(org.jsoup.nodes.Element e:genre) str+=" "+e.text();
		    subnodes.add(NodeFactory.createPara(str));
		}

		// author, each per new line
		final Elements author=descr.first().getElementsByTag("author");
		if(!author.isEmpty())
		{
		    for(org.jsoup.nodes.Element e:author)
		    {
			String str="";
			for(org.jsoup.nodes.Element i:e.children())
			    if(i.hasText())
				str+=" "+i.text();
			subnodes.add(NodeFactory.createPara(str));
		    }
		}

		// annotation, as usual text
		final Elements annotation=descr.first().getElementsByTag("annotation");
		if(!annotation.isEmpty())
		{
		    for(org.jsoup.nodes.Element e:annotation)
			complexContent(subnodes,e);
		}
	    }
	    Elements body=jdoc.select("FictionBook > body");
	    if(!body.isEmpty())
	    {
		body.forEach((org.jsoup.nodes.Element e)->
			     { // enumeraty body esctions
				 if(e.hasAttr("name"))
				 { // body name as h2
				     NodeImpl h2=NodeFactory.createSection(2);
				     h2.subnodes=new NodeImpl[]{NodeFactory.createPara(e.attr("name"))};
				     subnodes.add(h2);
				 }
				 complexContent(subnodes,e);
			     });
	    }
	    root.subnodes = subnodes.toArray(new NodeImpl[subnodes.size()]);
	    return new org.luwrain.doctree.Document(root);
	} catch (Exception e)
	{
	    e.printStackTrace();
	    return null;
	}
    }
    
    private void complexContent(LinkedList<NodeImpl> subnodes,org.jsoup.nodes.Element element)
    {
    	for(org.jsoup.nodes.Element e:element.children())
    	{
	    switch(e.tagName())
	    {
	    case "title":
	    case "section":
	    case "epigraph":
	    case "subtitle":
		NodeImpl h2=NodeFactory.createSection(3);
	    final LinkedList<NodeImpl> sn = new LinkedList<NodeImpl>();
	    complexContent(sn,e);
	    h2.subnodes=sn.toArray(new NodeImpl[sn.size()]);
	    subnodes.add(h2);
	    break;
	    case "empty-line":
		subnodes.add(NodeFactory.createPara(" "));
		break;
	    case "p":
		subnodes.add(NodeFactory.createPara(paraContent(e)));
		break;
	    case "binary":
	    case "image":
		break;
	    default:
		System.out.println(e.tagName());//+": "+e.text());
		break;
	    }
    	}
    }

    private String paraContent(org.jsoup.nodes.Element element)
    {
    	String text="";
    	ListIterator<org.jsoup.nodes.Node> list=element.childNodes().listIterator();
    	while(list.hasNext())
    	{
	    org.jsoup.nodes.Node n=list.next();
	    switch(n.nodeName())
	    {
	    case "#text":
		text+=((org.jsoup.nodes.TextNode)n).text();
		break;
	    case "strong":
	    case "emphasis":
	    case "style":
	    case "strikethrough":
	    case "sub":
	    case "sup":
	    case "code":
		text+=paraContent((org.jsoup.nodes.Element)n);
	    break;
	    case "date":
		if(n.hasAttr("value"))
		    text+=n.attr("value");
		else
		    text+=((org.jsoup.nodes.TextNode)n).text();
		break;
	    case "a":
		break;
	    case "image":
		break;
	    default:
		System.out.println(n.nodeName());//+": "+n.text());
		break;
	    }
    	}
    	return text;
    }
}

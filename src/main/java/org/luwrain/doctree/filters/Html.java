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

package org.luwrain.doctree.filters;

import java.util.*;
import java.io.*;
import java.nio.file.*;
import java.nio.charset.*;
import java.net.*;

import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;


import org.luwrain.doctree.NodeImpl;
import org.luwrain.doctree.NodeFactory;
import org.luwrain.doctree.ExtraInfo;
import org.luwrain.core.NullCheck;
import org.luwrain.core.Log;

public class Html
{
    private Document jsoupDoc;
    private URL hrefBaseUrl = null;
    private final LinkedList<String> hrefStack = new LinkedList<String>();
    private final LinkedList<ExtraInfo> extraInfoStack = new LinkedList<ExtraInfo>();

    public Html(Path path, String charset) throws IOException
    {
	NullCheck.notNull(path, "path");
	NullCheck.notNull(charset, "charset");
	Log.debug("doctree-html", "reading " + path.toString() + " with charset " + charset);
jsoupDoc = Jsoup.parse(Files.newInputStream(path), charset, path.toString());
    }

    public Html(InputStream is, String charset,
		String baseUrl) throws IOException, MalformedURLException
    {
	NullCheck.notNull(is, "is");
	Log.debug("doctree-html", "reading input stream with charset " + charset);
	jsoupDoc = Jsoup.parse(is, charset, baseUrl);
	hrefBaseUrl = new URL(baseUrl);
    }

    public org.luwrain.doctree.Document constructDocument()
    {
	//	final org.luwrain.doctree.NodeImpl rootNode = onNode(jsoupDoc.body(), NodeImpl.ROOT);
	final org.luwrain.doctree.NodeImpl res = NodeFactory.newNode(org.luwrain.doctree.Node.Type.ROOT);
	res.subnodes = onNode(jsoupDoc.body());
return new org.luwrain.doctree.Document(jsoupDoc.title(), res);
    }

    private NodeImpl[] onNode(Node node)
    {
	//http://jsoup.org/apidocs/org/jsoup/nodes/Element.html
	NullCheck.notNull(node, "node");
	final LinkedList<org.luwrain.doctree.NodeImpl> resNodes = new LinkedList<org.luwrain.doctree.NodeImpl>();
	final LinkedList<org.luwrain.doctree.Run> runs = new LinkedList<org.luwrain.doctree.Run>();
	final List<Node> nodes = node.childNodes();
	//	System.out.println("" + nodes.size() + " nodes");
	for(Node n: nodes)
	{
	    //	    System.out.println(n.getClass().getName());
	    final String name = n.nodeName();
	    if (n instanceof TextNode)
	    {
		final TextNode textNode = (TextNode)n;
		final String text = textNode.text();
		if (text != null && !text.isEmpty())
		    runs.add(new org.luwrain.doctree.Run(text, !hrefStack.isEmpty()?hrefStack.getLast():""));
		continue;
	    }
	    if (n instanceof Element)
	    {
		final Element el = (Element)n;
		{
		    onElement((Element)n, resNodes, runs);
		    continue;
		}
	    }
	}
	commitPara(resNodes, runs);
	return resNodes.toArray(new NodeImpl[resNodes.size()]);
    }

    private void onElementInPara(Element el,
			      LinkedList<NodeImpl> nodes, LinkedList<org.luwrain.doctree.Run> runs)
    {
	NullCheck.notNull(el, "el");
	final String tagName = el.nodeName();
	String href = null;

	//img
if (tagName.toLowerCase().trim().equals("img"))
{
	    final String value = el.attr("alt");
	    if (value != null && !value.isEmpty())
	runs.add(new org.luwrain.doctree.Run("[" + value + "]", !hrefStack.isEmpty()?hrefStack.getLast():""));
		//Do nothing else here	    }
		return;
}

	//a
if (tagName.toLowerCase().trim().equals("a"))
	{
	    final String value = el.attr("href");
	    //	    System.out.println(value);
	    if (value != null)
	    {
		try {
		href = new URL(hrefBaseUrl, value).toString();
		}
		catch(MalformedURLException e)
		{
		    e.printStackTrace();
		    href = value;
		}
	    } else
		href = value;
	    //	    System.out.println("+" + href.toString());
	}
    //	System.out.println(tagName);


if (href != null)
    hrefStack.add(href);

try {
	final List<Node> nn = el.childNodes();
	for(Node n: nn)
	{
if (n instanceof TextNode)
{
    onTextNode((TextNode)n, runs);
    continue;
}
if (n instanceof Element)
{
    //    Log.debug("html-jsoup", "encountering  element \'" + n.nodeName() + "\' in place where expecting text nodes only");
	onElement((Element)n, nodes, runs);
	continue;
}
Log.warning("doctree-html", "encountering unexpected node of class " + n.getClass().getName());
	}
}
finally
{
    if (href != null)
	hrefStack.pollLast();
}
    }

    private void onElement(Element el,
			   LinkedList<NodeImpl> nodes, LinkedList<org.luwrain.doctree.Run> runs)
    {
	final String name = el.nodeName();
	if (name == null || name.trim().isEmpty())
	    return;
	if (name.toLowerCase().trim().startsWith("g:") ||
	    name.toLowerCase().trim().startsWith("fb:"))
	    return;
	switch(name.toLowerCase().trim())
	{
	case "script":
	case "style":
	case "hr":
	case "input":
	case "button":
	case "nobr":
	case "wbr":
	case "map":
	    return;
	}
	NodeImpl n = null;
	NodeImpl[] nn = null;
	switch(name.toLowerCase().trim())
	{
	case "br":
	    commitPara(nodes, runs);
	    break;

	case "p":
	case "div":
	case "noscript":
	case "header":
	case "footer":
	case "center":
	case "blockquote":
	case "tbody":
	case "figure":
	case "figcaption":
	case "address":
	case "nav":
	case "article":
	case "noindex":
	case "iframe":
	case "form":
	case "section":
	case "dl":
	case "dt":
	case "dd":
	case "aside":
	    commitPara(nodes, runs);
	nn = onNode(el);
	for(NodeImpl i: nn)
	    nodes.add(i);
	break;

	case "h1":
	case "h2":
	case "h3":
	case "h4":
	case "h5":
	case "h66":
	case "h7":
	case "h8":
	case "h9":
	    commitPara(nodes, runs);
	n = NodeFactory.newSection(name.trim().charAt(1) - '0');
	n.subnodes = onNode(el);
	nodes.add(n);
	break;

	case "ul":
	case "ol":
	case "li":
	case "table":
	case "th":
	case "tr":
	case "td":
	    commitPara(nodes, runs);
	n = NodeFactory.newNode(getNodeType(name));
	n.subnodes = onNode(el);
	nodes.add(n);
	break;

	case "img":
	case "a":
	case "b":
	case "s":
	case "ins":
	case "em":
	case "i":
	case "big":
	case "small":
	case "strong":
	case "span":
	case "cite":
	case "font":
	case "sup":
	case "label":
	    onElementInPara(el, nodes, runs);
	break;

	default:
	    Log.warning("doctree-html", "unprocessed tag:" + name);
	}
    }

    private void onTextNode(TextNode textNode, LinkedList<org.luwrain.doctree.Run> runs)
    {
    final String text = textNode.text();
    if (text != null && !text.isEmpty())
	runs.add(new org.luwrain.doctree.Run(text, !hrefStack.isEmpty()?hrefStack.getLast():""));
    }

    private void commitPara(LinkedList<NodeImpl> nodes, LinkedList<org.luwrain.doctree.Run> runs)
    {
	if (runs.isEmpty())
	    return;
	final org.luwrain.doctree.ParagraphImpl para = NodeFactory.newPara();
	para.runs = runs.toArray(new org.luwrain.doctree.Run[runs.size()]);
	nodes.add(para);
	runs.clear();
    }

    private org.luwrain.doctree.Node.Type getNodeType(String tagName)
    {
	switch(tagName)
	{
	case "ul":
	    return org.luwrain.doctree.Node.Type.UNORDERED_LIST;
	case "ol":
	    return org.luwrain.doctree.Node.Type.ORDERED_LIST;
	case "li":
	    return org.luwrain.doctree.Node.Type.LIST_ITEM;
	case "table":
	    return org.luwrain.doctree.Node.Type.TABLE;
	case "th":
	case "tr":
	    return org.luwrain.doctree.Node.Type.TABLE_ROW;
	case "td":
	    return org.luwrain.doctree.Node.Type.TABLE_CELL;
	default:
	    Log.warning("doctree-html", "unable to create the node for tag \'" + tagName + "\'");
	    return null;
	}
    }

    private void addExtraInfoItem(Element el)
    {
	NullCheck.notNull(el, "el");
	final ExtraInfo info = new ExtraInfo();
	info.name = el.nodeName();
	final Attributes attrs = el.attributes();
	if (attrs != null)
	    for(Attribute a: attrs.asList())
	    {
		final String key = a.getKey();
		final String value = a.getValue();
		if (key != null && value != null)
		    info.attrs.put(key, value);
	    }
	if (!extraInfoStack.isEmpty())
	    info.parent = extraInfoStack.getLast(); else
	    info.parent = null;
	extraInfoStack.add(info);
    }

    private void releaseExtraInfo()
    {
	if (!extraInfoStack.isEmpty())
	    extraInfoStack.pollLast();
    }
}

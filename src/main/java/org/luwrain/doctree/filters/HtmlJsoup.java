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
//import org.luwrain.util.*;
import org.luwrain.core.NullCheck;
import org.luwrain.core.Log;

public class HtmlJsoup
{
    private Document jsoupDoc;

    public HtmlJsoup(URL url) throws IOException
    {
	NullCheck.notNull(url, "url");
jsoupDoc = Jsoup.parse(url, 60000);
    }

    public HtmlJsoup(Path path, String charset) throws IOException
    {
	//	NullCheck.notNull(url, "url");
jsoupDoc = Jsoup.parse(Files.newInputStream(path), charset, path.toString());
    }

    public HtmlJsoup(InputStream is, String charset,
String baseUrl) throws IOException
    {
	NullCheck.notNull(is, "is");
	jsoupDoc = Jsoup.parse(is, charset, baseUrl);
    }


    public HtmlJsoup(String text) throws IOException
    {
	NullCheck.notNull(text, "text");
jsoupDoc = Jsoup.parse(text);
    }

    public org.luwrain.doctree.Document constructDocument()
    {
	//	final org.luwrain.doctree.NodeImpl rootNode = onNode(jsoupDoc.body(), NodeImpl.ROOT);
	final org.luwrain.doctree.NodeImpl res = NodeFactory.create(NodeImpl.ROOT);
	res.subnodes = onNode(jsoupDoc.body());
	System.out.println("" + res.subnodes.length + " root subnodes");
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
    //stem.out.println(text);
    if (text != null && !text.isEmpty())
    runs.add(new org.luwrain.doctree.Run(text));
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

    private void onNodeInPara(Node node,
			      LinkedList<NodeImpl> nodes, LinkedList<org.luwrain.doctree.Run> runs)
    {
	NullCheck.notNull(node, "node");
	final List<Node> nn = node.childNodes();
	for(Node n: nn)
	{
if (n instanceof TextNode)
{
    onTextNode((TextNode)n, runs);
    continue;
}
if (n instanceof Element)
{
    Log.debug("html-jsoup", "encountering  element \'" + n.nodeName() + "\' in place where expecting text nodes only");
	onElement((Element)n, nodes, runs);
	continue;
}
	}
    }

    private void onElement(Element el,
			   LinkedList<NodeImpl> nodes, LinkedList<org.luwrain.doctree.Run> runs)
    {
final String name = el.nodeName();
if (name == null || name.trim().isEmpty())
    return;
switch(name.toLowerCase().trim())
{
case "script":
    return;
}
//Log.debug("jsoup", "processing tag:" + name);

NodeImpl n = null;
NodeImpl[] nn = null;

switch(name.toLowerCase().trim())
{

case "p":
case "div":
case "noscript":
    //case "header":
case "tbody":
    commitPara(nodes, runs);
    nn = onNode(el);
    for(NodeImpl i: nn)
	nodes.add(i);
    break;

case "h1":
case "h2":
case "h3":
    commitPara(nodes, runs);
n = NodeFactory.createSection(1);
    n.subnodes = onNode(el);
	nodes.add(n);
    break;

case "ul":
case "ol":
    commitPara(nodes, runs);
n = NodeFactory.create(name.equals("ul")?NodeImpl.UNORDERED_LIST:NodeImpl.ORDERED_LIST);
    n.subnodes = onNode(el);
	nodes.add(n);
    break;
case "li":
    commitPara(nodes, runs);
n = NodeFactory.create(NodeImpl.LIST_ITEM);
    n.subnodes = onNode(el);
	nodes.add(n);
    break;

case "table":
    commitPara(nodes, runs);
n = NodeFactory.create(NodeImpl.TABLE);
    n.subnodes = onNode(el);
	nodes.add(n);
    break;

case "tr":
    commitPara(nodes, runs);
n = NodeFactory.create(NodeImpl.TABLE_ROW);
    n.subnodes = onNode(el);
	nodes.add(n);
    break;

case "td":
    commitPara(nodes, runs);
n = NodeFactory.create(NodeImpl.TABLE_CELL);
    n.subnodes = onNode(el);
	nodes.add(n);
    break;





case "a":
case "b":
case "i":
case "span":
    onNodeInPara(el, nodes, runs);
    break;



default:
    Log.debug("html-jsoup", "unprocessed tag:" + name);
}
    }

    private void onTextNode(TextNode textNode, LinkedList<org.luwrain.doctree.Run> runs)
    {
    final String text = textNode.text();
    if (text != null && !text.isEmpty())
    runs.add(new org.luwrain.doctree.Run(text));
    }

    private void commitPara(LinkedList<NodeImpl> nodes, LinkedList<org.luwrain.doctree.Run> runs)
    {
	if (runs.isEmpty())
	    return;
	final org.luwrain.doctree.ParagraphImpl para = NodeFactory.createPara();
	para.runs = runs.toArray(new org.luwrain.doctree.Run[runs.size()]);
	nodes.add(para);
	runs.clear();
    }

}

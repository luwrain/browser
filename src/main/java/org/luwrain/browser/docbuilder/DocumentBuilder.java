/*
   Copyright 2012-2017 Michael Pozhidaev <michael.pozhidaev@gmail.com>
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

package org.luwrain.browser.docbuilder;

import java.awt.Rectangle;
import java.net.*;
import java.util.*;

import org.luwrain.core.*;
import org.luwrain.doctree.*;
import org.luwrain.browser.*;
import org.luwrain.browser.selectors.*;

public class DocumentBuilder
{
    static final String LOG_COMPONENT = "browser-builder";

    private final Browser browser;
    private final URL baseUrl;
    private final Prenode tempRoot = new Prenode();
    private final Prenode[] nodes;

    LinkedList<Integer> watch = new LinkedList<Integer>();

    public DocumentBuilder(Browser browser)
    {
	NullCheck.notNull(browser, "browser");
	this.browser = browser;
	this.baseUrl = prepareBaseUrl(browser);
	Log.debug(LOG_COMPONENT, "starting building of doctree document for " + (baseUrl != null?baseUrl.toString():""));
	nodes = new NodeInfoTreeBuilder(browser, tempRoot).build();
	Log.debug(LOG_COMPONENT, "" + nodes.length + " nodes saved for document construction");
    }

    public Document build()
    {
	watch.clear();
	while(Cleaning.clean(tempRoot) != 0){}
	Cleaning.mergeSingleChildrenNodes(tempRoot);
	return makeDocument();
    }

    private Document makeDocument()
    {
	final Node root=NodeFactory.newNode(Node.Type.ROOT);
	final Node[] subnodes = makeNodes(tempRoot);
	root.setSubnodes(subnodes);
	Log.debug(LOG_COMPONENT, "finalizing");
final org.luwrain.doctree.Document res = new Document(root);
Log.debug(LOG_COMPONENT, "document construction finished");
return res;
    }

    private Node[] makeNodes(Prenode nodeInfo)
    {
	NullCheck.notNull(nodeInfo, "nodeInfo");
	final LinkedList<Node> res = new LinkedList<Node>();
	// TODO: search elements with same X screen (equals for x pos) and different Y - same group
	// TODO: search elements with different Y and same X (intersect intervals! not equal like for X)
	final LinkedList<Run> subruns = new LinkedList<Run>();
	Rectangle rect = null;
	for(ItemWrapper runInfo: makeWrappers(nodeInfo))
	{
	    if(runInfo.isNode())
	    {
		res.add(createPara(subruns));
		res.add(runInfo.node);
		continue;
	    }

	    // first rect compare with itself
	    if(rect == null)
		rect = runInfo.nodeInfo.browserIt.getRect();

	    // check, if next r in the same Y interval like previous
	    final Rectangle curRect = runInfo.nodeInfo.browserIt.getRect();
	    if(!((curRect.y>=rect.y&&curRect.y<rect.y+rect.height)
		 ||(rect.y>=curRect.y&&rect.y<curRect.y+curRect.height)))
		res.add(createPara(subruns));
	    subruns.add(runInfo.run);
	    rect = curRect;
	} //for();
	res.add(createPara(subruns));
	return res.toArray(new Node[res.size()]);
    }

    private Paragraph createPara(LinkedList<Run> runs)
    {
	NullCheck.notNull(runs, "run");
	final Paragraph para = NodeFactory.newPara();
	para.runs = runs.toArray(new Run[runs.size()]);
	runs.clear();
	return para;
    }

    private ItemWrapper[] makeWrappers(Prenode node)
    {
	NullCheck.notNull(node, "node");
	if(node.children.isEmpty())
	    return new ItemWrapper[]{makeWrapperForLeaf(node)};
	final LinkedList<ItemWrapper> res = new LinkedList<ItemWrapper>();
	final BrowserIterator it = node.browserIt;
	final String tagName = it.getHtmlTagName().toLowerCase();
	switch(tagName)
	{
	    // list
	case "ol":
	case "ul": // li element can be mixed with contents, but each child of node is a li
	    Node listNode=NodeFactory.newNode(tagName.equals("ol")?Node.Type.ORDERED_LIST:Node.Type.UNORDERED_LIST);
	LinkedList<Node> listItems=new LinkedList<Node>();
	for(Prenode child:node.children)
	{
	    Node listItem=NodeFactory.newNode(Node.Type.LIST_ITEM);
	    final Node[] listItemNodes = makeNodes(child);
	    listItem.setSubnodes(listItemNodes);
	    listItems.add(listItem);
	}
	listNode.setSubnodes(listItems.toArray(new Node[listItems.size()]));
	res.add(new ItemWrapper(listNode,node));
	break;
	// table
	case "table": // table can be mixed with any other element, for example parent form
	case "tbody": // but if tbody not exist, table would exist as single, because tr/td/th can't be mixed
	    res.add(createRunInfoForTable(node));
	break;
	default:
	    // unknown group mixed to run list, it would be splited to paragraphs later
	    for(Prenode child:node.children)
		for (ItemWrapper childToAdd: makeWrappers(child))
		    res.add(childToAdd);
	    break;
	}
	return res.toArray(new ItemWrapper[res.size()]);
    }

    private ItemWrapper createRunInfoForTable(Prenode tableNodeInfo)
    {
	NullCheck.notNull(tableNodeInfo, "tableNodeInfo");
	final LinkedList<LinkedList<Node>> table = new LinkedList<LinkedList<Node>>();
	//All children are rows, no additional checking is required 
	for(Prenode rowNodeInfo: tableNodeInfo.children)
	{ // each rows contains a table cell or header cell, but also we can see tbody, tfoor, thead, we must enter into
	    final LinkedList<Node> row = new LinkedList<Node>();
	    // detect thead, tbody, tfoot
Prenode child_ = rowNodeInfo;
	    final String tagName = rowNodeInfo.browserIt.getHtmlTagName().toLowerCase();
	    switch(tagName)
	    {
	    case "thead":
	    case "tfoot":
	    case "tbody":
		// check child exist, if not, skip this row at all
		if(!rowNodeInfo.children.isEmpty())
		    child_ = rowNodeInfo.children.firstElement();
	    // we must go out here but we can pass this alone child next without errors 
	    break;
	    }
	    //Cells
	    for(Prenode cellChild: child_.children)
	    {
		//collspan detection
		final String tagName2 = cellChild.browserIt.getHtmlTagName().toLowerCase();
		String collSpanStr = null;
		switch(tagName2)
		{
		case "td":
		case "th":
		    collSpanStr = cellChild.browserIt.getAttribute("colspan");
		//break; // no we can skip this check becouse we don't known what to do if we detect errors here
		default:
		    Integer collSpan=null;
		    if(collSpanStr != null) 
			try {
					      collSpan=Integer.parseInt(collSpanStr);
					  } 
catch(NumberFormatException e)
			{} // we can skip this error
		    // add node
		    final Node cellNode=NodeFactory.newNode(Node.Type.TABLE_CELL);
		    //Make a recursive call of makeNodes()
		    final Node[] cellNodes = makeNodes(cellChild);
		    for(Node nn: cellNodes)
		    row.add(nn);
		    // emulate collspan FIXME: make Document table element usable with it
		    if(collSpan != null)
		    { // we have colspan, add empty colls to table row
			for(;collSpan>0;collSpan--)
			{
			    Node emptyCellNode=NodeFactory.newNode(Node.Type.TABLE_CELL);
			    Paragraph emptyPar=NodeFactory.newPara("");
			    emptyCellNode.setSubnodes(new Node[]{emptyPar});
			    row.add(emptyCellNode);
			}
		    }
		    break;	
		}
	    }
	    table.add(row);
	}
	// add empty cells to make table balanced by equal numbers of colls each row
	// move multiple colls to section inside of it
	// [col1,col2,col3,col4,col5,col6] -> [{col1,col2},{col3,col4},{col5,col6}]
	// call setSubnodes each one row
	Node tableNode = NodeFactory.newNode(Node.Type.TABLE);
	final LinkedList<Node> tableRowNodes = new LinkedList<Node>();
	for(LinkedList<Node> rows: table)
	{
	    final Node tableRowNode = NodeFactory.newNode(Node.Type.TABLE_ROW);
	    tableRowNode.setSubnodes(rows.toArray(new Node[rows.size()]));
	    tableRowNodes.add(tableRowNode);
	}
	tableNode.setSubnodes(tableRowNodes.toArray(new Node[tableRowNodes.size()]));
	return new ItemWrapper(tableNode, tableNodeInfo);
    }

    private ItemWrapper makeWrapperForLeaf(Prenode nodeInfo)
    {
	NullCheck.notNull(nodeInfo, "nodeInfo");
	WebInfo webInfo = null;
	final BrowserIterator it = nodeInfo.browserIt;
	final String tagName = it.getHtmlTagName().toLowerCase();

	if (tagName.toLowerCase().trim().equals("video"))
	{
	    Log.debug(LOG_COMPONENT, "video found");
	}
	String txt = "";
	switch(tagName)
	{
	case "img":
	    txt = "[картинка]";
	    break;

	case "video":
	    txt = "[Видео ]";
	    break;


	case "input":
String type = it.getAttribute("type");
	    if(type == null)
type = "";
	    switch(type)
	    {
	    case "image":
	    case "button":
	    case "submit":
		txt = "[кнопка " + it.getText() + "]";
	    webInfo = new WebInfo(WebInfo.ActionType.CLICK, nodeInfo.browserIt);
	    break;
	    case "radio":
		txt = "Radio: " + it.getText();
		webInfo = new WebInfo(WebInfo.ActionType.CLICK, nodeInfo.browserIt);
		break;
	    case "checkbox":
		txt = "Checkbox: " + it.getText();
		webInfo = new WebInfo(WebInfo.ActionType.CLICK, nodeInfo.browserIt);
		break;
	    case "text":
	    default:
		txt = "Edit: " + it.getText();
	    webInfo = new WebInfo(WebInfo.ActionType.EDIT, nodeInfo.browserIt);
	    break;
	    }
	    break;
	case "button":
	    txt = "Button: " + it.getText();
webInfo = new WebInfo(WebInfo.ActionType.CLICK, nodeInfo.browserIt);
	    break;
	case "select":
	    txt = "Select: " + it.getText();
	    webInfo = new WebInfo(WebInfo.ActionType.SELECT, nodeInfo.browserIt);
	    break;
	default:
	    txt = it.getText();
	    break;
	}
	if(!nodeInfo.mixed.isEmpty())
	{ // check for A tag inside mixed
	    for(BrowserIterator e: nodeInfo.getMixedinfo())
	    {
		final String etag = e.getHtmlTagName().toLowerCase();

		if(etag.equals("video"))
		{
		    Log.debug(LOG_COMPONENT, "video found");
		}

		if(etag.equals("a"))
		{
		    final String url;
		    final String urlSrc = e.getAttribute("href");
		    if (urlSrc != null && !urlSrc.trim().isEmpty())
			url = constructUrl(urlSrc); else
			url = null;
		    if (url != null)
			Log.debug("href", url);
		    //		    txt = txt;
		    webInfo = new WebInfo(WebInfo.ActionType.CLICK, nodeInfo.browserIt);
		    break;
		} else
		    if(etag.equals("button"))
		    {
			txt = "Button: "+txt;
			webInfo = new WebInfo(WebInfo.ActionType.CLICK, nodeInfo.browserIt);
			break;
		    }
	    }
	}
	// any non edit elements was UNKNOWN
	if(webInfo == null)
webInfo = new WebInfo(WebInfo.ActionType.UNKNOWN, nodeInfo.browserIt);
	txt = cleanupText(txt);
	txt += " "+cleanupText(it.getAltText());
	final TextRun run = new TextRun(txt.trim()+" ");
	if(webInfo != null)
	    run.setAssociatedObject(webInfo);
	watch.add(nodeInfo.browserIt.getPos());
	return new ItemWrapper(run, nodeInfo);
    }

    private String constructUrl(String url)
    {
	NullCheck.notNull(url, "url");
	if (baseUrl == null)
	    return url;
	try {
	    return new URL(baseUrl, url).toString();
	}
	catch(MalformedURLException e)
	{
	    return url;
	}
    }

    static private URL prepareBaseUrl(Browser browser)
    {
	NullCheck.notNull(browser, "browser");
	final String openedUrl = browser.getUrl();
	if (openedUrl == null || openedUrl.trim().isEmpty())
	    return null;
	try {
	    return new URL(openedUrl);
	    }
	catch(MalformedURLException e)
	{
	    return null;
	}
    }

    static private String cleanupText(String txt)
    {
	return txt.replace("/\\s+/g"," ").trim();
    }
}

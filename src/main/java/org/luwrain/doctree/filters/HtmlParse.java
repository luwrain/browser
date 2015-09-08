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
import org.luwrain.core.NullCheck;
import org.luwrain.util.*;
import org.luwrain.doctree.*;

class HtmlParse implements MlReaderListener
{
    static private class Level
    {
	public NodeImpl node;
	public final LinkedList<NodeImpl> subnodes = new LinkedList<NodeImpl>();

	Level(NodeImpl node)
	{
	    this.node = node;
	    NullCheck.notNull(node, "node");
	}

	void saveSubnodes()
	{
	    node.subnodes = subnodes.toArray(new NodeImpl[subnodes.size()]);
	}
    }

    private final LinkedList<String> errors = new LinkedList<String>();
    private final LinkedList<Level> levels = new LinkedList<Level>();
    private final LinkedList<Run> runs = new LinkedList<Run>();
    private String title = "";
    private boolean printErrors = true;

    public HtmlParse()
    {
	levels.add(new Level(NodeFactory.create(Node.ROOT)));
    }

    @Override public void onMlTagOpen(String tagName, Map<String, String> attrs)
    {
	switch (tagName)
	{
	case "html":
	case "body":
	case "head":
	case "link":
	case "meta":
	case "style":
	case "script":
	case "form":
	case "input":
	case "button":
	case "noscript":
	    return;
	case "span":
	case "sup":
	case "a":
	case "center":
	case "u":
	case "font":
	case "b":
	case "i":
	    return;
	case "p":
	case "div":
	    savePara();
	    return;
	case "img":
	    runs.add(new Run("[img]"));
	    return;
	case "br":
	    savePara();
	    return;
	case "table":
	    startLevel(Node.TABLE);
	    return;
	case "tr":
	    startLevel(Node.TABLE_ROW);
	    return;
	case "th":
	    startLevel(Node.TABLE_CELL);
	    return;
	case "td":
	    startLevel(Node.TABLE_CELL);
	    return;
	case "ul":
	    startLevel(Node.UNORDERED_LIST);
	    return;
	case "ol":
	    startLevel(Node.ORDERED_LIST);
	    return;
	case "li":
	    startLevel(Node.LIST_ITEM);
	    return;
	default:
	    savePara();
	    error("unhandled tag:" + tagName);
	}
    }

    @Override public void onMlTagClose(String tagName)
    {
	final String adjusted = tagName.toLowerCase().trim();
	switch(adjusted)
	{
	case "span":
	case "a":
	case "center":
	case "u":
	case "sup":
	case "font":
	case "b":
	case "i":
	    return;
	case "th":
	case "td":
	case 	    "tr":
	case "table":
	case "ol":
	case "ul":
	case "li":
	    commitLevel();
	return;
	default:
	    savePara();
	    return;
	}
    }

    @Override public void onMlText(String text, LinkedList<String> tagsStack)
    {
	if (text == null)
	    return;
	if (tagsStack.contains("script") ||
	    tagsStack.contains("style") ||
	    tagsStack.contains("form"))
	    return;
	if (!tagsStack.isEmpty() && tagsStack.getLast().equals("title"))
	{
	    title = text.trim();
	    return;
	}
	addText(text);
    }

    private void addText(String text)
    {
	final StringBuilder b = new StringBuilder();
	for(int i = 0;i < text.length();++i)
	{
	    final char c = text.charAt(i);
	    if (Character.isISOControl(c))
		b.append(" "); else
		b.append(c);
	}
	final String text2 = b.toString();
	if (text2.trim().isEmpty())
	{
	    if (!text2.isEmpty() && !runs.isEmpty())
		runs.add(new Run(" "));
	    return;
	}
	runs.add(new Run(text2.trim()));
    }

    NodeImpl constructRoot()
    {
	final Level firstLevel = levels.getFirst();
	firstLevel.saveSubnodes();
	return firstLevel.node;
    }

    String getTitle()
    {
	return title != null?title:"";
    }

    private void startLevel(int type)
    {
	savePara();
	final Level lastLevel = levels.getLast();
	final NodeImpl node = NodeFactory.create(type);
	lastLevel.subnodes.add(node);
	levels.add(new Level(node));
    }

    private void commitLevel()
    {
	savePara();
	final Level lastLevel = levels.pollLast();
	lastLevel.saveSubnodes();
    }

    private void savePara()
    {
	if (runs.isEmpty())
	    return;
	final ParagraphImpl para = NodeFactory.createPara();
	para.runs = runs.toArray(new Run[runs.size()]);
	runs.clear();
	final int lastLevelType = levels.getLast().node.type;
	if (lastLevelType == Node.TABLE || lastLevelType == Node.TABLE_ROW ||
	    lastLevelType == Node.ORDERED_LIST || lastLevelType == Node.UNORDERED_LIST)
	{
	    error("unable to save a paragraph because last level type is " + lastLevelType);
	    return;
	}
	levels.getLast().subnodes.add(para);
    }

    private void error(String msg)
    {
	errors.add(msg);
	if (printErrors)
	    System.out.println(msg);
    }
}

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
    private String href = null;
    private boolean printErrors = true;

    public HtmlParse()
    {
	levels.add(new Level(NodeFactory.create(Node.ROOT)));
    }

    @Override public void onMlTagOpen(String tagName, Map<String, String> attrs)
    {
	switch (tagName.toLowerCase().trim())
	{
	case "a":
	if (attrs != null && attrs.containsKey("href"))
href = attrs.get("href");
	return;
	case "img":
	    if (attrs != null && attrs.containsKey("alt"))
	    {
		final String value = attrs.get("alt");
		if (value != null && !value.trim().isEmpty())
		    runs.add(new Run(value.trim(), href));
	    }
	    return;

	case "html":
	case "body":
	case "head":
	case "title":
	case "link":
	case "meta":
	case "style":
	case "script":
	case "noscript":

	case "form":
	case "input":
	case "button":
	case "label":

	case "span":
	case "sup":
	case "center":
	case "big":
	case "small":
	case "strong":
	case "u":
	case "font":
	case "b":
	case "i":
	case "em":
	case "code":
	case "tt":
	case "dt":
	case "dd":
	    return;

	case "p":
	case "blockquote":
	case "cite":
	case "div":
	case "dl":
	case "pre":
	case "hr":
	    savePara();
	    return;

	case "br":
	    savePara();
	    return;

	case "h1":
	    startSection(1);
	    return;
	case "h2":
	    startSection(2);
	    return;
	case "h3":
	    startSection(3);
	    return;
	case "h4":
	    startSection(4);
	    return;
	case "h5":
	    startSection(5);
	    return;
	case "h6":
	    startSection(6);
	    return;
	case "h7":
	    startSection(7);
	    return;
	case "h8":
	    startSection(8);
	    return;
	case "h9":
	    startSection(9);
	    return;

	case "table":
	    startLevel(Node.TABLE);
	    return;
	case "caption":
	    //	    expectingCurrentLevel(Node.TABLE, Node.TABLE_ROW);
	    startLevel(Node.TABLE_ROW);
	    startLevel(Node.TABLE_CELL);
	    return;
	case "tr":
	    //	    expectingCurrentLevel(Node.TABLE, Node.TABLE_ROW);
	    startLevel(Node.TABLE_ROW);
	    return;
	case "th":
	    //	    expectingCurrentLevel(Node.TABLE_ROW, Node.TABLE_CELL);
	    startLevel(Node.TABLE_CELL);
	    return;
	case "td":
	    //	    expectingCurrentLevel(Node.TABLE_ROW, Node.TABLE_CELL);
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
	switch(tagName.toLowerCase().trim())
	{
	case "a":
	    href = null;
	    return;
	    case "title":
	case "style":
	case "script":
	case "noscript":
	case "head":
	case "body":
	case "html":

	case "form":
	case "input":
	case "button":
	case "label":

	case "span":
	case "center":
	case "big":
	case "small":
	case "strong":
	case "u":
	case "sup":
	case "font":
	case "b":
	case "i":
	case "em":
	case "code":
	case "tt":
	case "dt":
	case "dd":
	    return;

	case "p":
	case "blockquote":
	case "cite":
	case "div":
	case "pre":
	case "dl":
	    savePara();
	return;

	case "h1":
	case "h2":
	case "h3":
	case "h4":
	case "h5":
	case "h6":
	case "h7":
	case "h8":
	case "h9":
	    commitLevel();
	return;

	case "caption":
	    commitLevel();
	    commitLevel();
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
	    return;
	}
    }

    @Override public boolean isMlAutoClosingNeededOnTagOpen(String newTagName, LinkedList<String> tagsStack)
    {
	return tagsAutoClosingPolicy(newTagName, tagsStack);
    }

    @Override public boolean mayMlAnticipatoryTagClose(String tagName,
						   LinkedList<String> anticipatoryTags, LinkedList<String> tagsStack)
    {
	return anticipatoryTagsPolicy(tagName, anticipatoryTags, tagsStack);
    }

    @Override public void onMlText(String text, LinkedList<String> tagsStack)
    {
	if (text == null)
	    return;
	if (tagsStack.contains("script") ||//FIXME:not effective checking
	    tagsStack.contains("style") ||
	    tagsStack.contains("form"))
	    return;
	if (!tagsStack.isEmpty() && tagsStack.getLast().equals("table") &&
	    !text.trim().isEmpty())
	    return;
    if (!tagsStack.isEmpty() && tagsStack.getLast().equals("ul") &&
	!text.trim().isEmpty())
	return;
    if (!tagsStack.isEmpty() && tagsStack.getLast().equals("ol") &&
	!text.trim().isEmpty())
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
	boolean lastSpace = runs.isEmpty();
	for(int i = 0;i < text.length();++i)
	{
	    final char c = text.charAt(i);
	    final char r = Character.isISOControl(c)?' ':c;
	    if (lastSpace && Character.isSpace(r))
		continue;
	    b.append(r);
	    lastSpace = Character.isSpace(r);
	}
	final String text2 = b.toString();
	if (text2.isEmpty())
	    return;
	runs.add(new Run(text2, href));
    }

    NodeImpl constructRoot()
    {
	while (levels.size() > 1)
	    commitLevel();
	final Level firstLevel = levels.getFirst();
	firstLevel.saveSubnodes();
	return firstLevel.node;
    }

    String getTitle()
    {
	return title != null?title:"";
    }

    private int lastLevelType()
    {
	if (levels.isEmpty())
	    return -1;
	return levels.getLast().node.type;
    }

    private void expectingCurrentLevel(int levelType, int expectingFor)
    {
	if (levels.isEmpty())
	{
	    error("Expecting current level to be of type " + NodeImpl.typeStr(levelType) + ", but there are no levels at all");
	    return;
	}
	final Level lastLevel = levels.getLast();
	if (lastLevel.node.type != levelType)
	{
	    error("Expecting last level to be of type " + NodeImpl.typeStr(levelType) + " (trying to open " + NodeImpl.typeStr(expectingFor) + "), but it is of type " + NodeImpl.typeStr(lastLevel.node.type));
	    return;
	}
    }

    private void startLevel(int type)
    {
	savePara();
	final Level lastLevel = levels.getLast();
	final NodeImpl node = NodeFactory.create(type);
	lastLevel.subnodes.add(node);
	levels.add(new Level(node));
    }

    private void startSection(int level)
    {
	savePara();
	final Level lastLevel = levels.getLast();
	final NodeImpl node = NodeFactory.createSection(level);
	lastLevel.subnodes.add(node);
	levels.add(new Level(node));
    }

    private void commitLevel()
    {
	savePara();
	levels.pollLast().saveSubnodes();
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
	    error("unable to save a paragraph because last level type is " + lastLevelType + ", lost text:" + para.toString());
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


    static boolean tagsAutoClosingPolicy(String newTagName, LinkedList<String> tagsStack)
    {
	if (tagsStack.isEmpty())//Actually, never happens
	    return false;
	final String adjusted1 = newTagName.toLowerCase().trim();
	final String adjusted2 = tagsStack.getLast().toLowerCase().trim();
	if (adjusted1.equals("p") && adjusted2.equals("p"))
	    return true;
	if (adjusted1.equals("li") && adjusted2.equals("li"))
	    return true;
	if (adjusted1.equals("option") && adjusted2.equals("option"))
	    return true;
	return false;
    }

    static     boolean anticipatoryTagsPolicy(String tagName,
					      LinkedList<String> anticipatoryTags, LinkedList<String> tagsStack)
    {
	final String adjusted1 = tagName.toLowerCase().trim();
	if (adjusted1.equals("html"))
	    return true;


	if (adjusted1.equals("form") ||
adjusted1.equals("table") ||
	    adjusted1.equals("div"))
	    return true;
	if (anticipatoryTags.size() != 1)
	    return false;
	final String adjusted2 = anticipatoryTags.getLast().toLowerCase().trim();
	if (adjusted2.equals("p"))
	    return true;
	if (/*adjusted1.equals("ul") &&*/ adjusted2.equals("li"))
	    return true;
	if (/*adjusted1.equals("ol") &&*/ adjusted2.equals("li"))
	    return true;
	if (adjusted2.equals("option"))
	    return true;
	return false;
    }
}

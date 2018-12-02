/*
   Copyright 2012-2018 Michael Pozhidaev <michael.pozhidaev@gmail.com>
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

package org.luwrain.controls.web;

import java.util.*;
import java.awt.Rectangle;

import org.luwrain.core.*;
import org.luwrain.browser.*;

final class ModelBuilder
{
    static final String LOG_COMPONENT = "web";

    public Model build(Browser browser)
    {
	NullCheck.notNull(browser, "browser");
	final int count = browser.numElements();
	final Item[] items = new Item[count];
Item root = null;
	final BrowserIterator it = browser.createIterator();
	for(int i = 0;i < count;++i)
	{
	    it.setPos(i);
	    items[i] = new Item(it.clone());
	}
	for(Item i: items)
	    {
		if (i.className.equals(Classes.DOCUMENT_TYPE))
		continue;
		final BrowserIterator parentIt = i.it.getParent();
		if (parentIt == null)
		{
		    if (root == null)
			root = i; else
		    Log.warning(LOG_COMPONENT, "the node without a parent");
		    continue;
		}
		final int parentPos = parentIt.getPos();
		items[parentPos].children.add(i);
		if (i.content && i.visible)
		    items[parentPos].contentItems.add(i);
		i.parent = items[parentPos];
	    }
	if (root != null)
	    Log.debug(LOG_COMPONENT, "root tag <" + root.tagName + ">"); else
	    Log.warning(LOG_COMPONENT, "no root item");
	if (root != null)
	    setHrefs(root, "");
	return new Model(createContainers(items, root));
    }

    private void setHrefs(Item item, String href)
    {
	NullCheck.notNull(item, "item");
	NullCheck.notNull(href, "href");
	final String current;
	if (item.className.equals(Classes.ANCHOR))
	{
	final String hrefAttr = item.it.getAttr("href");
	if (hrefAttr != null)
	    current = hrefAttr; else
	    current = "";
	} else
	    current = href;
	item.href = current;
	for(Item i: item.children)
	    setHrefs(i, current);
    }

    private Container[] createContainers(Item[] items, Item root)
    {
	NullCheck.notNullItems(items, "items");
	//NullCheck.notNull(root, "root");
	final List<Container> res = new LinkedList();
	for(Item i: items)
	{
	    if (i.contentItems.isEmpty() || i.content)
		continue;
	    switch(i.className.toLowerCase())//FIXME:
	    {
	    case "title":
	    case "script":
			    case "noscript":
	    case "style":
		continue;
	    }
	    res.add(new Container(i.it, i, i.createContentItem().children));
	}
	return res.toArray(new Container[res.size()]);
    }

    static private final class Item implements TreeItem
    {
	final BrowserIterator it;
	final boolean content;
	final boolean visible;
	final String tagName;
	final String className;

	String href = "";
	Item parent = null;
	final List<Item> children = new LinkedList();
	final List<Item> contentItems = new LinkedList();

	Item(BrowserIterator it)
	{
	    NullCheck.notNull(it, "it");
	    this.it = it;
	    this.content = isContentNode(it);
	    if (content)
	    this.visible = isVisible(it); else
		this.visible = true;
	    this.className = it.getClassName();
	    this.tagName = it.getTagName();
	}

	@Override public TreeItem getParentItem()
	{
	    return parent;
	}

	@Override public TreeItem[] getChildren()
	{
	    return children.toArray(new TreeItem[children.size()]);
	}

	@Override public Map<String, String> getItemAttrs()
	{
	    return it.getAttrs();
	}

	ContentItem createContentItem()
	{
	    final List<ContentItem> c = new LinkedList();
	    for(Item i: contentItems)
		c.add(i.createContentItem());
	    return new ContentItem(it, c.toArray(new ContentItem[c.size()]), href);
	}

	    static private boolean isContentNode(BrowserIterator it)
    {
	NullCheck.notNull(it, "it");
	final String role = it.getAttr("role");
	if (role != null && role.toLowerCase().equals("img"))
	    return true;
	final String className = it.getClassName();
	switch(className)
	{
	case Classes.BUTTON:
	case Classes.INPUT:
	case Classes.IMAGE:
	case Classes.BR:
	case Classes.ANCHOR:
	case Classes.TEXT:
	    return true;
	case "":
	    {
		final String tagName = it.getTagName();
		if (tagName == null)
		    return false;
		switch(tagName.toLowerCase())
		{
		case "em":
		case "strong":
		case "b":
		case "span":
		case "svg":
		    return true;
		default:
		    return false;
		}
	    }
	}
	return false;
    }

	    static private boolean isVisible(BrowserIterator it)
    {
	if (it.getClassName().equals(Classes.BR))
	    return true;
	if (it.getComputedStyle("visibility").toLowerCase().equals("hidden"))
	    return false;
	final Rectangle rect = it.getRect();
	if (rect == null)
	    return false;
	return rect.width > 0 && rect.height > 0;
    }


	@Override public String toString()
	{
	    if (className.equals(Classes.TEXT))
		return it.getText();
	    final Map<String, String> attrs = it.getAttrs();
	    final StringBuilder b = new StringBuilder();
		b.append("<").append(tagName);
		for(Map.Entry<String, String> e: attrs.entrySet())
		    b.append (System.lineSeparator()).append("  ").append(e.getKey()).append("=").append(e.getValue());
		b.append(">").append(System.lineSeparator());
		for(Item c: children)
		    b.append(c.toString()).append(System.lineSeparator());
		b.append("</").append(tagName).append(">");
		return new String(b);
	}
    }
}

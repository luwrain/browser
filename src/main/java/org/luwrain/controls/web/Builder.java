

package org.luwrain.controls.web;

import java.util.*;
import java.awt.Rectangle;

import org.luwrain.core.*;
import org.luwrain.browser.*;

public final class Builder
{
    static final String LOG_COMPONENT = "web";

    private Container[] containers = new Container[0];

    public void build(Browser browser)
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
	    if (isContentNode(i.it))
	    {
		final BrowserIterator parentIt = i.it.getParent();
		if (parentIt == null)
		{
		    if (root == null)
			root = i; else
		    Log.warning(LOG_COMPONENT, "the node without a parent");
		}
		final int parentPos = parentIt.getPos();
		items[parentPos].children.add(i);
		if (isContentNode(i.it))
		    items[parentPos].contentItems.add(i);
		i.parent = items[parentPos];
	    }
	this.containers = createContainers(items, root);
	printToLog();
    }

    private boolean isContentNode(BrowserIterator it)
    {
	NullCheck.notNull(it, "it");
	final String className = it.getClassName();
	switch(className)
	{
	case "Br":
	case "Anchor":
	case "Text":
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
		    return true;
		default:
		    return false;
		}
	    }
	}
	return false;
    }

    static boolean isVisible(BrowserIterator it)
    {
	final Rectangle rect = it.getRect();
	if (rect == null)
	    return false;
	return rect.width > 0 && rect.height > 0;
    }

    private Container[] createContainers(Item[] items, Item root)
    {
	NullCheck.notNullItems(items, "items");
	//NullCheck.notNull(root, "root");
	final List<Container> res = new LinkedList();
	for(Item i: items)
	    if (!i.contentItems.isEmpty() && !isContentNode(i.it))
		res.add(new Container(i.it, i.createContentItem().children));
	return res.toArray(new Container[res.size()]);
    }

    public void printToLog()
    {
	for(int i = 0;i < containers.length;++i)
	    if (containers[i] != null)
	    {
		Log.debug(LOG_COMPONENT, containers[i].toString());
	    }
    }

    static private final class Item
    {
	final BrowserIterator it;
	final String tagName;
	final String className;

	Item parent = null;
	final List<Item> children = new LinkedList();
	
	final List<Item> contentItems = new LinkedList();

	Item(BrowserIterator it)
	{
	    NullCheck.notNull(it, "it");
	    this.it = it;
	    this.className = it.getClassName();
	    this.tagName = it.getTagName();
	}

	ContentItem createContentItem()
	{
	    final List<ContentItem> c = new LinkedList();
	    for(Item i: contentItems)
		c.add(i.createContentItem());
	    return new ContentItem(it, c.toArray(new ContentItem[c.size()]));
	}
    }
}
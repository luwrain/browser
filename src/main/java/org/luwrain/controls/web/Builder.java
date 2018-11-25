

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
    }

    private boolean isContentNode(BrowserIterator it)
    {
	NullCheck.notNull(it, "it");
	final String className = it.getClassName();
	switch(className)
	{
	case "Anchor":
	case "Text":
	    return true;
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

    public void printToLog()
    {
	for(int i = 0;i < containers.length;++i)
	    if (containers[i] != null)
	    {
		Log.debug(LOG_COMPONENT, containers[i].toString());
		/*
	for(BrowserIterator it: containers[i].content)
	    Log.debug(LOG_COMPONENT, "+ " + it.getClassName() + ": " + it.getText());
		*/
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
    }
}



package org.luwrain.controls.web;

import java.awt.Rectangle;

import org.luwrain.core.*;
import org.luwrain.browser.*;

public final class Builder
{
    static final String LOG_COMPONENT = "web";


    public void build(Browser browser)
    {
	NullCheck.notNull(browser, "browser");
	final int count = browser.numElements();
	final BrowserIterator[] nodes = new BrowserIterator[count];
	final BrowserIterator it = browser.createIterator();
	for(int i = 0;i < count;++i)
	{
	    it.setPos(i);
	    nodes[i] = it.clone();
	}
	final Container[] containers = new Container[count];
	for(BrowserIterator i: nodes)
	    if (isContentNode(i))
	    {
		final BrowserIterator parentIt = i.getParent();
		if (parentIt == null)
		{
		    Log.warning(LOG_COMPONENT, "the parent node without a parent");
		    continue;
		}
		final int parentPos = parentIt.getPos();
		if (containers[parentPos] == null)
		    containers[parentPos] = new Container(nodes[parentPos]);
		containers[parentPos].content.add(i);
	    }
    }

    private boolean isContentNode(BrowserIterator it)
    {
	NullCheck.notNull(it, "it");
	return false;
    }

    static boolean isVisible(BrowserIterator it)
    {
	final Rectangle rect = it.getRect();
	if (rect == null)
	    return false;
	return rect.width > 0 && rect.height > 0;
    }
}

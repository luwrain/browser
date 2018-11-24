
package org.luwrain.controls.web;

import java.util.*;
import java.awt.Rectangle;

import org.luwrain.core.*;
import org.luwrain.browser.*;

public final class Container
{
    final BrowserIterator it;
    final int x;
    final int y;
    final int width;
    final int height;
    final Vector<BrowserIterator> content = new Vector();

    public Container(BrowserIterator it)
    {
	NullCheck.notNull(it, "it");
	this.it = it;
	final Rectangle rect = it.getRect();
	if (rect == null)
	{
	    this.x = 0;
	    this.y = 0;
	    this.width = 0;
	    this.height = 0;
	    return;
	}
	this.x = rect.x;
	this.y = rect.y;
	this.width = rect.width;
	this.height = rect.height;
    }
}

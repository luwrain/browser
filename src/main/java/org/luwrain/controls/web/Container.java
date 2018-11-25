
package org.luwrain.controls.web;

import java.util.*;
import java.awt.Rectangle;

import org.luwrain.core.*;
import org.luwrain.browser.*;

final class Container
{
    final BrowserIterator it;
    final int x;
    final int y;
    final int width;
    final int height;
    final ContentItem[] content;

    public Container(BrowserIterator it, ContentItem[] content)
    {
	NullCheck.notNull(it, "it");
	NullCheck.notNullItems(content, "content");
	this.it = it;
	this.content = content;
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

    String getText()
    {
	final StringBuilder b = new StringBuilder();
	for(ContentItem i: content)
	    b.append(i.getText());
	return new String(b);
    }

    @Override public String toString()
    {
			final StringBuilder b = new StringBuilder();
			b.append(it.getClassName()).append(" <").append(it.getTagName()).append(">: ");
	b.append("(").append(String.format("%d,%d,%d,%d", x, y, width, height)).append(")");
	b.append(": ").append(getText());
	return new String(b);
    }

    
}

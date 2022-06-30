
package org.luwrain.controls.web;

import java.util.*;
import java.awt.Rectangle;

import org.luwrain.core.*;
import org.luwrain.controls.block.*;
import org.luwrain.browser.*;

import static org.luwrain.util.RangeUtils.*;

final class Container extends Block
{
    enum Type {LIST_ITEM, PARA, HEADING};

    final BrowserIterator it;
    final TreeItem treeItem;
    final String className;
    final String tagName;
    final Type type;
    final int x;
    final int y;
    final int width;
    final int height;

    final ContentItem[] content;

    public Container(BrowserIterator it, TreeItem treeItem, ContentItem[] content)
    {
	super(content);
	NullCheck.notNull(it, "it");
	NullCheck.notNull(treeItem, "treeItem");
	this.it = it;
	this.treeItem = treeItem;
	this.className = it.getClassName();
	this.tagName = it.getTagName();
	this.type = getType(className.trim().toLowerCase(), tagName.trim().toLowerCase());
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

    private Type getType(String className, String tagName)
    {
	NullCheck.notNull(className, "className");
	NullCheck.notNull(tagName, "tagName");
	switch(className)
	{
	case "li":
	    return Type.LIST_ITEM;
	case "paragraph":
	    return Type.PARA;
	case "heading":
	    return Type.HEADING;
	}
	return Type.PARA;
    }

    ContentItem[] getContent()
    {
	return content.clone();
    }

    boolean intersectsGraphically(Container c)
    {
	NullCheck.notNull(c, "c");
	final int sq1 = getGraphicalSquare();
	final int sq2 = c.getGraphicalSquare();
	if (sq1 == 0 && sq2 == 0)
	    return x == c.x && y == c.y;
	if (sq1 == 0)
	    return between(x, c.x, c.x + c.width) && between(y, c.y, c.y + c.height);
	if (sq2 == 0)
	    return between(c.x, x, x + width) && between(c.y, y, y + height);
	return intersects(x, width, c.x, c.width) &&
	intersects(y, height, c.y, c.height);
    }

    int getGraphicalSquare()
    {
	return width * height;
    }

    @Override public String toString()
    {
	final StringBuilder b = new StringBuilder();
	b.append(" <").append(it.getTagName()).append("> ");
	b.append("(gr:").append(String.format("%d,%d,%d,%d", x, y, x + width, y + height)).append(")");
	return new String(b);
    }
}

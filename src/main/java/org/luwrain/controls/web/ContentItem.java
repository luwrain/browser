
package org.luwrain.controls.web;

import java.util.*;

import  org.luwrain.core.*;
import org.luwrain.browser.*;

final class ContentItem
{
    final BrowserIterator it;
    final String className;
    final String tagName;
    final String text;
    final ContentItem[] children;

    ContentItem(BrowserIterator it, ContentItem[] children)
    {
	NullCheck.notNull(it, "it");
	NullCheck.notNullItems(children, "children");
	this.it = it;
	this.className = it.getClassName();
	this.tagName = it.getTagName();
	this.text = it.getText();
	this.children = children;
    }

    String getText()
    {
	if (className.equals("Text"))
	    return text;
	final StringBuilder b = new StringBuilder();
	for(ContentItem i: children)
	    b.append(i.getText());
	return new String(b);
    }
}

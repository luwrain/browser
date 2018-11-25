
package org.luwrain.controls.web;

import java.util.*;

import  org.luwrain.core.*;
import org.luwrain.browser.*;

final class ContentItem
{
    final BrowserIterator it;
    final ContentItem[] children;

    ContentItem(BrowserIterator it, ContentItem[] children)
    {
	NullCheck.notNull(it, "it");
	NullCheck.notNullItems(children, "children");
	this.it = it;
	this.children = children;
    }

    String getText()
    {
	final StringBuilder b = new StringBuilder();
	b.append(it.getText());
	for(ContentItem i: children)
	    b.append(i.getText());
	return new String(b);
    }
}

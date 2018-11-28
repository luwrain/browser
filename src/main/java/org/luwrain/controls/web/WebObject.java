
package org.luwrain.controls.web;

import org.luwrain.core.*;

class WebObject
{
    final ContentItem contentItem;

    WebObject(ContentItem contentItem)
    {
	NullCheck.notNull(contentItem, "contentItem");
	this.contentItem = contentItem;
    }
}

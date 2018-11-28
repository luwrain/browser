
package org.luwrain.controls.web;

import org.luwrain.core.*;

final class WebText extends WebObject
{
    final String text;
    final int posFrom;
    final int posTo;

    
    WebText(ContentItem contentItem, int posFrom, int posTo)
    {
	super(contentItem);
	this.text = contentItem.getText();
	this.posFrom = posFrom;
	this.posTo = posTo;
	if (posFrom < 0 || posTo < 0)
	    throw new IllegalArgumentException("posFrom (" + posFrom + ") and posTo (" + posTo + ") may not be negative");
	if (posFrom > posTo)
	    throw new IllegalArgumentException("posFrom (" + posFrom + ") must be equal or less than posTo (" + posTo + ")");
	if (posTo > text.length())
	    throw new IllegalArgumentException("posTo may not be greater than the lenth of the text (" + text.length() + ")");
    }

    String getText()
    {
	return text.substring(posFrom, posTo);
    }
}

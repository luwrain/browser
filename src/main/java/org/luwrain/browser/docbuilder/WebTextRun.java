
package org.luwrain.browser.docbuilder;

import org.luwrain.core.*;
import org.luwrain.doctree.*;
import org.luwrain.browser.*;

public class WebTextRun extends WebRun
{
    protected final String text;
    protected final String href;

    WebTextRun(BrowserIterator it, String href)
    {
	super(it);
	NullCheck.notNull(it, "it");
	NullCheck.notNull(href, "href");
	this.href = href;
	final String s = it.getText();
	this.text = s != null?s:"";
    }

    @Override public String href()
    {
	return href;
    }

    @Override public String text()
    {
	return text;
    }

    @Override public boolean isEmpty()
    {
	return text.isEmpty();
    }
}

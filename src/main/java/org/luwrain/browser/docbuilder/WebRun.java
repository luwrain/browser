
package org.luwrain.browser.docbuilder;

import org.luwrain.core.*;
import org.luwrain.doctree.*;
import org.luwrain.browser.*;

abstract public class WebRun implements Run
{
    protected final BrowserIterator it;
    protected final TextAttr textAttr = new TextAttr();
    protected final ExtraInfo extraInfo = new ExtraInfo();
    protected Node parentNode = null;

    WebRun(BrowserIterator it)
    {
	NullCheck.notNull(it, "it");
	this.it = it;
    }

    @Override public Node getParentNode()
    {
	return parentNode;
    }

    @Override public void setParentNode(Node node)
    {
	NullCheck.notNull(node, "node");
	this.parentNode = node;
    }

        @Override public String href()
    {
	return "";
    }

    @Override public TextAttr textAttr()
    {
	return textAttr;
    }

        @Override public ExtraInfo extraInfo()
    {
	return extraInfo;
    }

    @Override public void prepareText()
    {
    }

    @Override public boolean isEmpty()
    {
	return false;
    }


    //May be called in browser thread only
    public void emulateClick()
    {
	it.emulateClick();
    }
}

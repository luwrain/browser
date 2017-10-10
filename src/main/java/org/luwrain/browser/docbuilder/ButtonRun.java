
package org.luwrain.browser.docbuilder;

import org.luwrain.core.*;
import org.luwrain.doctree.*;
import org.luwrain.browser.*;

public class ButtonRun implements Run
{
    protected final BrowserIterator it;
    protected final String text;

    protected final TextAttr textAttr = new TextAttr();
    protected final ExtraInfo extraInfo = new ExtraInfo();
    protected Node parentNode = null;

    ButtonRun(BrowserIterator it)
    {
	NullCheck.notNull(it, "it");
	this.it = it;
	final String s = it.getText();
	this.text = s != null?s:"";
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

    @Override public TextAttr textAttr()
    {
	return textAttr;
    }

    @Override public void prepareText()
    {
    }

    @Override public ExtraInfo extraInfo()
    {
	return extraInfo;
    }

    @Override public String href()
    {
	return "";
    }

    @Override public boolean isEmpty()
    {
	return false;
    }

    @Override public String text()
    {
	return "Кнопка " + text;
    }
}

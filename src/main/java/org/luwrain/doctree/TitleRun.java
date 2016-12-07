
package org.luwrain.doctree;

import org.luwrain.core.NullCheck;

public class TitleRun implements Run
{
    protected Node parentNode;
    protected final ExtraInfo extraInfo = new ExtraInfo();
    protected String parentClassName;

    TitleRun(String parentClassName)
    {
	NullCheck.notNull(parentClassName, "parentClassName");
	this.parentClassName = parentClassName;
    }

    @Override public String text()
    {
	return "";
    }

    @Override public boolean isEmpty()
    {
	return false;
    }

    @Override public String href()
    {
	return "";
    }

    @Override public TextAttr textAttr()
    {
	return new TextAttr();
    }

    @Override public String toString()
    {
	//	return text();
	return parentClassName;
    }

    @Override public void prepareText()
    {
    }

    public ExtraInfo extraInfo()
    {
	return extraInfo;
    }

@Override public void setParentNode(Node node)
    {
	NullCheck.notNull(node, "node");
	parentNode = node;
    }

    public Node getParentNode()
    {
	return parentNode;
    }
}

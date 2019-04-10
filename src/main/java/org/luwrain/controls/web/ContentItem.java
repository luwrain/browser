
package org.luwrain.controls.web;

import java.util.*;

import  org.luwrain.core.*;
import org.luwrain.controls.block.*;
import org.luwrain.browser.*;

final class ContentItem implements BlockObject
{
    final BrowserIterator it;
    final String href;
    final String className;
    final String tagName;
    final String text;
    final String role;
    final String ariaLabel;
    private final Map<String, String> attrs;
    final ContentItem[] children;

    ContentItem(BrowserIterator it, ContentItem[] children, String href)
    {
	NullCheck.notNull(it, "it");
	NullCheck.notNullItems(children, "children");
	NullCheck.notNull(href, "href");
	this.it = it;
		this.children = children;
	this.className = it.getClassName();
	this.tagName = it.getTagName();
	this.href = href.trim();
	this.text = prepareText(it.getText());
	this.attrs = it.getAttrs();
	this.role = attrs.containsKey("role")?attrs.get("role"):"";
		this.ariaLabel = attrs.containsKey("aria-label")?attrs.get("aria-label"):"";
    }

    boolean isBr()
    {
	return className.equals(Classes.BR);
    }

    boolean isText()
    {
	return className.equals(Classes.TEXT);
    }

        @Override public String getText()
    {
	    return text;
}

    boolean isImage()
    {
	if (className.equals(Classes.IMAGE))
	    return true;
	if (tagName.toLowerCase().equals("svg"))
	    return true;
	if (role.toLowerCase().equals("img"))
	    return true;
	return false;
    }

    String getImageComment()
    {
	if (ariaLabel.isEmpty())
	    return ariaLabel;
	if (attrs.containsKey("alt"))
	{
	    final String value = attrs.get("alt");
	    if (value != null)
		return value;
	}
	return "";
    }

    boolean isTextInput()
    {
	return tagName.toLowerCase().equals("input");
    }

    boolean isButton()
    {
	if (className.equals(Classes.BUTTON))
	    return true;
	if (attrs.containsKey("role"))
	{
	    final String role = attrs.get("role");
	    return role.equals("button");
	}
	return false;
    }

    String getButtonTitle()
    {
	if (attrs.containsKey("aria-label"))
	{
	    final String value = attrs.get("aria-label");
	    if (value != null && !value.isEmpty())
		return value;
	}
	return "FIXME:NO_TITLE";
    }


    ContentItem[] getChildren()
    {
	return children.clone();
    }

    @Override public String toString()
    {
	if (isText())
	    return getText();
	final StringBuilder b = new StringBuilder();
	b.append("<" + tagName + ">");
	for(ContentItem c: children)
	    b.append(c.toString());
	b.append("</" + tagName + ">");
	return new String(b);
    }

    static private String  prepareText(String text)
    {
	NullCheck.notNull(text, "text");
	String res = "";
	boolean wasSpace = false;
	for(int i = 0;i < text.length();++i)
	{
char c = text.charAt(i);
	    if (Character.isISOControl(c))
		c = ' ';
	    if (wasSpace && Character.isSpace(c))
		continue;
	    		res += c;
			wasSpace = Character.isSpace(c);
	}
	return res;
    }
}

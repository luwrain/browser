// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>
// Copyright 2015-2016 Roman Volovodov <gr.rPman@gmail.com>

package org.luwrain.app.webinspector;

import java.util.*;
import java.util.concurrent.atomic.*;

import javafx.scene.web.WebEngine;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.w3c.dom.css.CSSStyleDeclaration;
import com.sun.webkit.dom.TextImpl;
import com.sun.webkit.dom.ElementImpl;

import org.luwrain.core.*;

import static org.luwrain.graphical.FxThread.*;
import static org.luwrain.core.NullCheck.*;

final class WebObject
{
    final WebTree tree;
    final Node node;
    final Element el;
    final String tagName;
    final String text;

    WebObject(WebTree tree, Node node)
    {
		notNull(tree, "tree");
		notNull(node, "node");
		ensure();
		this.tree = tree;
		this.node = node;
		if (node instanceof Element)
		{
			this.el = (Element)node;
			this.tagName = this.el.getTagName();
			this.text = null;
		} else
			if (node instanceof TextImpl)
			{
			final TextImpl textObj = (TextImpl)node;
			this.el = null;
			this.tagName = null;
			this.text = textObj.getWholeText();
			} else 
		{
			this.el = null;
			this.tagName = null;
			this.text = node.getClass().getName();
		}
    }

    WebObject[] getChildren()
    {
	final AtomicBoolean isNull = new AtomicBoolean(false);	
	final List<WebObject> res = new ArrayList<>();
	runSync(()->{
	    	final NodeList items =node.getChildNodes();
	if (items == null)
	{
	    isNull.set(true);
	    return;
	}
	for(int i = 0;i < items.getLength();i++)
	{
	    final WebObject newObj = new WebObject(tree, items.item(i));
	    if (newObj.text != null && newObj.text.trim().isEmpty())
		continue;
	    res.add(newObj);
	}
	});
    if (isNull.get())
	return null;
	return res.toArray(new WebObject[res.size()]);
    }

    boolean hasChildren()
    {
	return node.hasChildNodes();
    }

    CSSStyleDeclaration getStyle()
    {
		if (el == null)
			return null;
		return tree.getStyle(el);
    }

    String getStyleAsText()
    {
	/*
		final CSSStyleDeclaration style = getStyle();
		if (style == null)
			return null;
		final WebKitGeomScanner.Item geom = tree.geom.nodes.get(node);
		final StringBuilder b = new StringBuilder();
		if (el != null && geom != null)
		{
					final ElementImpl e = (ElementImpl)el;
			b.append("lwr-geom: true;");
			b.append("lwr-x: ").append(String.valueOf(geom.x)).append("px;");
			b.append("lwr-x2: ").append(String.format("%.2f", e.getOffsetLeft())).append("px;");
			b.append("lwr-y: ").append(String.valueOf(geom.y)).append("px;");
					b.append("lwr-y2: ").append(String.format("%.2f", e.getOffsetTop())).append("px;");
			b.append("lwr-width: ").append(String.valueOf(geom.width)).append("px;");
			b.append("lwr-height: ").append(String.valueOf(geom.height)).append("px;");
		} else
		{
			b.append("lwr-geom: false;");
		}
		return new String(b) + style.getCssText();
	*/
	return "";
    }

    @Override public String toString()
    {
	if (tagName != null)
	    return tagName;
	return text;
    }
}

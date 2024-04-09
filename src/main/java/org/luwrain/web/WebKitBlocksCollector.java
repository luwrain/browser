/*
   Copyright 2012-2024 Michael Pozhidaev <msp@luwrain.org>
   Copyright 2015-2016 Roman Volovodov <gr.rPman@gmail.com>

   This file is part of LUWRAIN.

   LUWRAIN is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   LUWRAIN is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

package org.luwrain.web;

import java.util.*;
import java.util.concurrent.atomic.*;

import javafx.scene.web.WebEngine;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.w3c.dom.html.HTMLDocument;
import org.w3c.dom.html.HTMLBodyElement;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.views.DocumentView;

import com.sun.webkit.dom.DOMWindowImpl;
import netscape.javascript.*;

import org.luwrain.core.*;

import static org.luwrain.graphical.FxThread.*;
import static org.luwrain.web.WebKitGeomInjection.*;

public final class WebKitBlocksCollector extends BlocksCollector<Node, WebKitBlock>
{
    public final WebEngine engine;
    public final HTMLDocument doc;
    public final DOMWindowImpl window;
    public final HTMLBodyElement body;
    public final WebKitGeomInfo geom;

    public WebKitBlocksCollector(WebEngine engine)
    {
        ensure();
        this.engine = engine;
        this.doc = (HTMLDocument)engine.documentProperty().getValue();
        this.window = (DOMWindowImpl)((DocumentView)doc).getDefaultView();
        this.body = (HTMLBodyElement)doc.getBody();
        this.geom = new WebKitGeomInjection(engine).scan();
    }

        @Override public List<Node> getChildNodes(Node node)
    {
	final var res = new ArrayList<Node>();
	    	final NodeList items =node.getChildNodes();
	if (items != null)
	for(int i = 0;i < items.getLength();i++)
	    res.add(items.item(i));
	return res;
    }

    @Override public boolean isMarkupNode(Node node)
    {
	return false;
    }

    @Override public WebKitBlock createBlock(Node node)
    {
	return null;
    }

    public 	CSSStyleDeclaration getStyle(Element el)
    {
return window.getComputedStyle(el, "");
    }
}

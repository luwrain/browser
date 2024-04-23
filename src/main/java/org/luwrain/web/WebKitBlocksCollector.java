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

import org.w3c.dom.*;
import org.w3c.dom.html.*;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.views.DocumentView;

import com.sun.webkit.dom.*;//DOMWindowImpl;
import netscape.javascript.*;

import org.luwrain.core.*;

import static org.luwrain.graphical.FxThread.*;

import static org.luwrain.app.webinspector.App.log;

public final class WebKitBlocksCollector extends BlocksCollector<Node, WebKitBlock>
{
    public final WebEngine engine;
    public final HTMLDocument doc;
    public final DOMWindowImpl window;
    public final HTMLBodyElement body;
    final WebKitGeom geom;

    public WebKitBlocksCollector(WebEngine engine)
    {
        ensure();
        this.engine = engine;
        this.doc = (HTMLDocument)engine.documentProperty().getValue();
        this.window = (DOMWindowImpl)((DocumentView)doc).getDefaultView();
        this.body = (HTMLBodyElement)doc.getBody();
	final Node n = this.body;
        this.geom = new WebKitGeom(engine);
	Log.debug("proba", "new");
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

	//true
	if (node instanceof HTMLAnchorElementImpl) return true;

	//false
	if (node instanceof CommentImpl) return false;
	if (node instanceof HTMLLIElementImpl) return false;
	if (node instanceof HTMLUListElementImpl) return false;
	if (node instanceof  HTMLButtonElementImpl) return false;
	if (node instanceof HTMLDivElementImpl) return false;
	if (node instanceof HTMLBRElementImpl) return false;


if (!(node instanceof HTMLElementImpl) &&
    !(node instanceof ElementImpl))
	log("Unclassified: " + node.getClass().getSimpleName());
	return false;
    }

        @Override public boolean isTextNode(Node node)
    {
	return node instanceof TextImpl;
    }

    @Override public void addTextToBlock(Node node, WebKitBlock block)
    {
	final TextImpl t = (TextImpl)node;
	//	block.textBuilder.append(t.getWholeText());
	block.runs.add(new WebKitBlock.Run(t.getWholeText()));
	//	log(t.getWholeText());
    }

    @Override public WebKitBlock createBlock(Node node)
    {
	return new WebKitBlock(window, geom, (NodeImpl)node);
    }

    @Override public boolean saveBlock(WebKitBlock block)
    {
	final var b = new StringBuilder();
	for(var r: block.runs)
	    b.append(r.text);
	block.text = new String(b);
	return !block.text.trim().isEmpty();
    }
}

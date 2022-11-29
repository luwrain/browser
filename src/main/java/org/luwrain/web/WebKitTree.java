/*
   Copyright 2012-2022 Michael Pozhidaev <msp@luwrain.org>
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
import org.w3c.dom.Element;
import org.w3c.dom.html.HTMLDocument;
import org.w3c.dom.html.HTMLBodyElement;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.views.DocumentView;

import com.sun.webkit.dom.DOMWindowImpl;
import netscape.javascript.*;

import org.luwrain.core.*;

import static org.luwrain.graphical.FxThread.*;
import static org.luwrain.web.WebKitGeom.*;

public final class WebKitTree
{
    final WebEngine engine;
    final HTMLDocument doc;
    final DOMWindowImpl window;
    final HTMLBodyElement body;
    final WebKitGeomInfo geom;

    public WebKitTree(WebEngine engine)
    {
	ensure();
	this.engine = engine;
	//	this.doc = (HTMLDocument)engine.getDocument();
	this.doc = (HTMLDocument)engine.documentProperty().getValue();
	this.window = (DOMWindowImpl)((DocumentView)doc).getDefaultView();
	this.body = (HTMLBodyElement)doc.getBody();
	this.geom = new WebKitGeom(engine).scan();
    }

        public WebObject getBody()
    {
	final AtomicReference<WebObject> res = new AtomicReference<>();
	runSync(()->res.set(new WebObject(this, body)));
	return res.get();
    }

    public 	CSSStyleDeclaration getStyle(Element el)
    {
	final AtomicReference<CSSStyleDeclaration> res = new AtomicReference<>();
	runSync(()->res.set(window.getComputedStyle(el, "")));
		return res.get();
    }
}

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

import javafx.scene.web.WebEngine;

import org.w3c.dom.Node;
import org.w3c.dom.html.HTMLDocument;
import org.w3c.dom.html.HTMLBodyElement;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.views.DocumentView;

import com.sun.webkit.dom.DOMWindowImpl;
import netscape.javascript.*;

import org.luwrain.core.*;
import static org.luwrain.web.WebKitScan.*;

public final class WebKitTree
{
    final WebEngine engine;
    //    final JSObject src, root;
    final HTMLDocument doc;
    final DOMWindowImpl window;

    int count = 0;
    final List<WebObject> objs = new ArrayList<>();

    public WebKitTree(WebEngine engine)
    {
	this.engine = engine;
	//	this.src = src;
	this.doc = (HTMLDocument)engine.getDocument();
	this.window = (DOMWindowImpl)((DocumentView)doc).getDefaultView();
	//	this.root = (JSObject)src.getMember("dom");

		final HTMLBodyElement body = (HTMLBodyElement)doc.getBody();

//DOMWindowImpl wnd = (DOMWindowImpl)((DocumentView)htmlDoc).getDefaultView();

	CSSStyleDeclaration style = window.getComputedStyle(body, "");
	Log.debug("proba", "" + style.getLength());
	Log.debug("proba", "" + style.getPropertyValue("height"));

    }

    /*
    static int intValue(Object o)
    {
	if(o == null) 
	    return 0;
	if(o instanceof Number)
	    return ((Number)o).intValue();
	return Double.valueOf(Double.parseDouble(o.toString())).intValue();
    }
    */
}

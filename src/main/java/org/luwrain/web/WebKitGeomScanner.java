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
import org.w3c.dom.views.DocumentView;
import com.sun.webkit.dom.DOMWindowImpl;
import netscape.javascript.*;

import org.luwrain.core.*;

import static org.luwrain.graphical.FxThread.*;
import static org.luwrain.web.WebKitGeomInjection.*;

public final class WebKitGeomScanner
{
    public final Map<Node, Item> nodes = new HashMap<>();

    WebKitGeomInfoScanner(WebEngine engine, JSObject src)
    {
	ensure();
	final var doc = (HTMLDocument)engine.getDocument();
	final var window = (DOMWindowImpl)((DocumentView)doc).getDefaultView();
	final var root = (JSObject)src.getMember("dom");
	Object o;
	for(int i = 0;!(o = root.getSlot(i)).getClass().equals(String.class);i++)
	{
	    final JSObject jsObj = (JSObject)o;
	    final JSObject rect = (JSObject)jsObj.getMember("rect");
	    final String text = (String)jsObj.getMember("text");
	    final Node node = (Node)jsObj.getMember("node");
	    int x = -1, y = -1, width = -1, height = -1;
	    if (rect != null)
	    {
		x = intValue(rect.getMember("left"));
		y = intValue(rect.getMember("top"));
		width = intValue(rect.getMember("width"));
		height = intValue(rect.getMember("height"));
	    }
	    nodes.put(node, new Item(x, y, width, height, String.valueOf(text)));
	}
	Log.debug(LOG_COMPONENT, "geom scanning completed: " + nodes.size());
    }

    static int intValue(Object o)
    {
	if(o == null) 
	    return 0;
	if(o instanceof Number)
	    return ((Number)o).intValue();
	return Double.valueOf(Double.parseDouble(o.toString())).intValue();
    }

    static public final class Item
    {
	public final int x, y, width, height;
	public final String text;
	Item(int x, int y, int width, int height, String text)
	{
	    this.x = x;
	    this.y = y;
	    this.width = width;
	    this.height = height;
	    this.text = text;
	}
    }
}

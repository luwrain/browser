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
import java.io.*;
//import java.util.logging.FileHandler;
//import java.util.logging.Logger;
//import java.util.logging.SimpleFormatter;

import org.w3c.dom.*;
import org.w3c.dom.html.*;
import org.w3c.dom.views.*;
import com.sun.webkit.dom.*;
import netscape.javascript.*;
import javafx.scene.web.WebEngine;


import org.luwrain.core.*;

import static org.luwrain.graphical.FxThread.*;
import static org.luwrain.util.ResourceUtils.*;
import static org.luwrain.app.webinspector.App.log;

public final class WebKitGeomInjection
{
    static final String
	LOG_COMPONENT = "web",
	INJECTION_NAME = "injection.js";

    static private String injection = null;
    final WebEngine engine;

    public WebKitGeomInjection(WebEngine engine)
    {
		this.engine = engine;
		try {
			if (injection == null)
			{
				injection = getStringResource(getClass(), INJECTION_NAME);
				Log.debug(LOG_COMPONENT, "the web injection loaded");
				log("The injection loaded");
			}
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
    }

    public WebKitGeomScanner scan()
    {
		ensure();
		final Object res = engine.executeScript(injection);
		if (res == null)
			throw new RuntimeException("The result of web scanning is null");
		if (!(res instanceof JSObject))
			throw new RuntimeException("The result of web scanning is not an instance of JSObject");
		Log.debug(LOG_COMPONENT, "Performing scanning of the web page");
		final JSObject jsRes = (JSObject)res;
		return new WebKitGeomScanner(engine, jsRes);
    }

    public final class Scanner
{
    public final Map<Node, GeomEntry> nodes = new HashMap<>();

    Scanner(WebEngine engine, JSObject src)
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
	    nodes.put(node, new GeomEntry(x, y, width, height, String.valueOf(text)));
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
    }
}

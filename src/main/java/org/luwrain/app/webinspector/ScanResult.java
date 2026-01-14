// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>
// Copyright 2015-2016 Roman Volovodov <gr.rPman@gmail.com>

package org.luwrain.app.webinspector;

import java.util.*;

import javafx.scene.web.WebEngine;

import org.w3c.dom.Node;
import org.w3c.dom.html.HTMLDocument;
import org.w3c.dom.views.DocumentView;

import com.sun.webkit.dom.DOMWindowImpl;
import netscape.javascript.*;

final class ScanResult
{
    final WebEngine engine;
    final JSObject src, root;
    final HTMLDocument doc;
    final DOMWindowImpl window;
	final Map<Node, Item> nodes = new HashMap<>();

    int count = 0;

    ScanResult(WebEngine engine, JSObject src)
    {
		this.engine = engine;
		this.src = src;
		this.doc = (HTMLDocument)engine.getDocument();
		this.window = (DOMWindowImpl)((DocumentView)doc).getDefaultView();
		this.root = (JSObject)src.getMember("dom");
		Object o;
		for(int i = 0;!(o = root.getSlot(i)).getClass().equals(String.class);i++)
		{
			final JSObject jsObj = (JSObject)o;
			count++;
			final JSObject rect = (JSObject)jsObj.getMember("rect");
			final String text = (String) jsObj.getMember("text");
			final Node node = (Node) jsObj.getMember("node");
			int x = -1, y = -1, width = -1, height = -1;
			if (rect != null)
			{
				x = intValue(rect.getMember("left"));
				y = intValue(rect.getMember("top"));
				width = intValue(rect.getMember("width"));
				height = intValue(rect.getMember("height"));
			}

			if (text.equals("123") == false && text.isBlank() == false) {

				nodes.put(node, new Item(x, y, width, height, String.valueOf(text)));
				// logg.info("Got text = " + String.valueOf(text) + " and x = " + x + " y = " +
				// y + " width = " + width + " height = " + height);
			}
		}
    }

    static int intValue(Object o)
    {
	if(o == null) 
	    return 0;
	if(o instanceof Number)
	    return ((Number)o).intValue();
	return Double.valueOf(Double.parseDouble(o.toString())).intValue();
    }

	public Map<Node, Item> getNodes() {
		return nodes;
	}

	static public final class Item {
		public final int x, y, width, height;
		public final String text;

		Item(int x, int y, int width, int height, String text) {
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
			this.text = text;
		}
	}
}

/*
   Copyright 2012-2024 Michael Pozhidaev <msp@luwrain.org>

   This file is part of LUWRAIN.

пт   LUWRAIN is free software; you can redistribute it and/or
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
import org.w3c.dom.*;

import com.sun.webkit.dom.*;

import static org.luwrain.core.NullCheck.*;
import static org.luwrain.graphical.FxThread.*;
import static org.luwrain.app.webinspector.App.log;
import static java.lang.Character.*;

public final class WebKitBlock extends WebKitBlockBase
{
    public final NodeImpl node;
        public final DOMWindowImpl window;
    public String text = null;
    final boolean visible;

    public WebKitBlock(DOMWindowImpl window, WebKitGeom geom, NodeImpl node)
    {
	notNull(window, "window");
	notNull(geom, "geom");
		notNull(node, "node");
	this.window = window;
	this.node = node;
	final GeomEntry entry = geom.getEntry(node);
	if (entry != null)
	{
	this.left = entry.x;
	this.right = entry.x + entry.width;
	this.top = entry.y;
    } else
      {
	  log("No geom for the node " + node.getClass().getSimpleName());
	  this.left = 0;
	  this.right = 0;
	  this.top = 0;
      }
	this.visible = (this.right - this.left) > 0;
    }

    public boolean isVisible()
    {
	return this.visible;
    }

        public String getStyle()
    {
	if (node instanceof Element el)
	{
        final var res = new AtomicReference<String>();
        runSync(()->{
		final var css = window.getComputedStyle(el, "");
		if (css != null)
		    res.set(css.getCssText());
		});
        return res.get();
	}
	return null;
    }
}

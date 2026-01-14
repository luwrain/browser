// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>
// Copyright 2015-2016 Roman Volovodov <gr.rPman@gmail.com>

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
static private final int
    MIN_BLOCK_WIDTH = 5;

    public final String className, tagName;
        public final int srcLeft, srcRight, srcTop, srcBottom;
        public final boolean visible;
    final NodeImpl node;
        final DOMWindowImpl window;
    public String text = null;



    public WebKitBlock(DOMWindowImpl window, WebKitGeom geom, NodeImpl node)
    {
	notNull(window, "window");
	notNull(geom, "geom");
		notNull(node, "node");
		ensure();
	this.window = window;
	this.node = node;
	this.className = node.getClass().getSimpleName();
	if (node instanceof HTMLElementImpl el)
	    tagName = el.getTagName(); else
	    tagName = null;
	final GeomEntry entry = geom.getEntry(node);
	if (entry != null)
	{
	this.srcLeft = entry.x;
	this.srcRight = entry.x + entry.width;
	this.srcTop = entry.y;
	this.srcBottom = entry.y + entry.height;
    } else
      {
	  log("No geom for the node " + node.getClass().getSimpleName());
	  this.srcLeft = 0;
	  this.srcRight = 0;
	  this.srcTop = 0;
	  this.srcBottom = 0;
      }
	this.left = this.srcLeft;
	this.right = this.srcRight;
	this.top = this.srcTop;
	this.visible = (this.right - this.left) > 0;
    }

    public boolean isVisible()
    {
	return this.visible;
    }

    void rescale(float scale)
    {
	left = Float.valueOf(scale * left).intValue();
	right = Math.max(Float.valueOf(scale * right).intValue(), left + MIN_BLOCK_WIDTH );
				top = Float.valueOf(scale * top).intValue();
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

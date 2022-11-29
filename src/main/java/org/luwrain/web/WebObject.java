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

import javafx.scene.web.WebEngine;

import org.w3c.dom.Node;
import org.w3c.dom.Element;

import org.luwrain.core.*;

public final class WebObject
{
final Node node;
    public final int x, y, width, height;
    final Element el;
    public String tagName;

    WebObject(Node node, int x, int y, int width, int height)
    {
	NullCheck.notNull(node, "node");
	this.node = node;
	if (node instanceof Element)
	{
	    this.el = (Element)node;
	    this.tagName = this.el.getTagName();
	}
	else
	{
	    this.el = null;
	    this.tagName = null;
	}
	this.x = x;
	this.y = y;
	this.width = width;
	this.height = height;
    }
}

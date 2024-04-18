/*
   Copyright 2012-2024 Michael Pozhidaev <msp@luwrain.org>

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

import com.sun.webkit.dom.*;

import static org.luwrain.core.NullCheck.*;

public final class WebKitBlock extends BlockGeom.Block
{
    public final NodeImpl node;
    public String text = null;
    StringBuilder textBuilder = new StringBuilder();

    public WebKitBlock(NodeImpl node, int left, int right, int top)
    {
	notNull(node, "node");
	this.node = node;
	this.left = left;
	this.right = right;
	this.top = top;
    }
}

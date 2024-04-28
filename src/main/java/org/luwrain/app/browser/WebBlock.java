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

package org.luwrain.app.browser;

import java.util.*;

import org.luwrain.controls.block.*;
import org.luwrain.web.*;

import static org.luwrain.core.NullCheck.*;

final class WebBlock implements Block
{
    final int x, y, width;
    final ArrayList<WebLine> lines = new ArrayList<>();
    final WebKitBlock srcBlock;

    WebBlock(int x, int y, int width, List<WebLine> lines)
    {
	if (x < 0)
	    throw new IllegalArgumentException("x (" + x + ") can't be negative");
	if (y < 0)
	    throw new IllegalArgumentException("y (" + y + ") can't be negative");
	if (width < 0)
	    throw new IllegalArgumentException("width (" + width + ") can't be negative");
	this.x = x;
	this.y = y;
	this.width = width;
	this.lines.addAll(lines);
	this.srcBlock = null;
    }

    WebBlock(WebKitBlock srcBlock)
    {
	notNull(srcBlock, "srcBlock");
	this.x = srcBlock.getLeft();
	this.y = srcBlock.getTop();
	this.width = srcBlock.getWidth();
	lines.ensureCapacity(srcBlock.lines.size());
	srcBlock.lines.forEach(b->lines.add(new WebLine(b)));
	this.srcBlock = srcBlock;
    }

    @Override public int getWidth()
    {
	return width;
    }

    @Override public int getX()
    {
	return x;
    }

    @Override public int getY()
    {
	return y;
    }

    @Override public int getLineCount()
    {
	return lines.size();
    }

    @Override public BlockLine getLine(int index)
    {
	return lines.get(index);
    }
}

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

package org.luwrain.controls.block;

import java.util.*;
import java.io.*;

import org.luwrain.core.*;

import static org.luwrain.core.NullCheck.*;

public final class View implements Lines
{
    private final BlockArea.Appearance appearance;
    private final ArrayList<Block> blocks = new ArrayList<>();
    private final ArrayList<String> lines = new ArrayList<>();

    public View(BlockArea.Appearance appearance, List<Block> blocks)
    {
	notNull(appearance, "appearance");
	notNull(blocks, "blocks");
	this.appearance = appearance;
	this.blocks.addAll(blocks);
	buildLines();
    }

    @Override public int getLineCount() {
	return Math.max(lines.size(), 1);
    }

    @Override public String getLine(int index)
    {
	if (index < 0)
	    throw new IllegalArgumentException("index (" + index + ") can't be negative");
	if (index >= lines.size())
	    return "";
	return lines.get(index);
    }

    private void buildLines()
    {
	int width = 0, height = 0;
	for(var b: blocks)
	{
	    width = Math.max(width, b.getX() + b.getWidth());
	    height = Math.max(height, b.getY() + b.getLineCount());
	}
	if (width == 0)
	    return;
	String emptyLine = " ";
	while (emptyLine.length() < width)
	    if (emptyLine.length() * 2 < width)
		emptyLine = emptyLine + emptyLine; else
		emptyLine += " ";
	lines.ensureCapacity(height);
	while(lines.size() < height)
	    lines.add(emptyLine);
	for(var b: blocks)
	    for(int i = 0;i < b.getLineCount();i++)
	    {
		String line = appearance.getBlockLineTextAppearance(b, b.getLine(i));
		if (line.length() > b.getWidth())
		    line = line.substring(0, b.getWidth());
		if (line.length() < b.getWidth())
		    line = line + emptyLine.substring(0, b.getWidth() - line.length());
		final int lineIndex = b.getY() + i;
		final String origLine = lines.get(lineIndex);
		lines.set(lineIndex, origLine.substring(0, b.getX()) + line + origLine.substring(b.getX() + b.getWidth()));
	    }
		    }
}

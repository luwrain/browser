// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>
// Copyright 2015-2016 Roman Volovodov <gr.rPman@gmail.com>

package org.luwrain.controls.block;

import java.util.*;
import java.io.*;

import org.luwrain.core.*;

import static org.luwrain.core.NullCheck.*;

public final class BlockIterator
    {
	private final BlockArea blockArea;
	int blockIndex, lineIndex;

	BlockIterator(BlockArea blockArea, int blockIndex, int lineIndex)
	{
	    notNull(blockArea, "blockArea");
	    if (blockIndex < 0)
		throw new IllegalArgumentException("blockIndex (" + blockIndex + ") may not be negative");
	    	    if (lineIndex < 0)
		throw new IllegalArgumentException("lineIndex (" + lineIndex + ") may not be negative");
		    this.blockArea = blockArea;
		    this.blockIndex = blockIndex;
		    this.lineIndex = lineIndex;
	}

	public BlockIterator(BlockArea blockArea)
	{
	    this(blockArea, 0, 0);
	}

	boolean movePrev()
	{
	    if (lineIndex > 0)
	    {
		lineIndex--;
		return true;
	    }
	    if (blockIndex > 0)
	    {
		blockIndex--;
		lineIndex = getBlock().getLineCount() - 1;
		return true;
	    }
	    return false;
	}

	boolean moveNext()
	{
	    if (lineIndex + 1 < getBlock().getLineCount())
	    {
		lineIndex++;
		return true;
	    }
	    if (blockIndex + 1 < blockArea.blocks.size())
	    {
		blockIndex++;
		lineIndex = 0;
		return true;
	    }
	    return false;
	}

	public Block getBlock()
	{
	    return blockArea.blocks.get(blockIndex);
	}

	public int getX()
	{
	    return getBlock().getX();
	}

	public int getY()
	{
	    return getBlock().getY() + lineIndex;
	}

	public BlockLine getLine()
	{
	    return getBlock().getLine(lineIndex);
	}

	public String getLineText(BlockArea.Appearance appearance)
	{
	    return appearance.getBlockLineTextAppearance(getBlock(), getLine());
	}
}

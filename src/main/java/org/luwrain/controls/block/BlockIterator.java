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

public final class BlockIterator
    {
	private final BlockArea area;
	private int blockIndex, lineIndex;
	
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
		lineIndex = getBlock().getLIneCount() - 1;
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
}

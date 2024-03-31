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

import static org.luwrain.util.RangeUtils.*;

public final class BlockGeom
{
    private final Block[] blocks;
    public BlockGeom(Block[] blocks)
    {
	this.blocks = blocks;
    }

    	public void process()
	{
	    Arrays.sort(blocks);
	}

    static public class Block implements Comparable<Block>
    {
	public int left, right, top, height;


	@Override public  int compareTo(Block b)
	{
	    if (top != b.top)
	    return Integer.valueOf(top).compareTo(Integer.valueOf(b.top));
	    return Integer.valueOf(left).compareTo(Integer.valueOf(b.left));
	}
    }
}

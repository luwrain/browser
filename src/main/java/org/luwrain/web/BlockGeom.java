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
    public final Block[] blocks;
    private final int count;
    private int[][] matrix = null;
    public BlockGeom(Block[] blocks)
    {
	this.blocks = blocks;
	this.count = blocks.length;
    }

    	public void process()
	{
	    Arrays.sort(blocks);
	    buildMatrix();
	    for(int i = 0;i < count;i++)
		for(int j = i + 1;j < count;j++)
		{
		    final Block b1 = blocks[i], b2 = blocks[j];
		    final int space = matrix[i][j];
		    if (space < 0)//Completely independent blocks
			continue;
		    b2.top = Math.max(b2.top, b1.top + b1.height + space - 1);
		}
	}

    private void buildMatrix()
    {
    this.matrix = new int[count][count];
    for(int i = 0;i < count;i++)
	for(int j = 0;j < count;j++)
	    matrix[i][j] = -1;
    for(int i = 0;i < count;i++)
	for(int j = i + 1;j < count;j++)
	{
	    final Block b1 = blocks[i], b2 = blocks[j];
	    if (!intersects(b1.left, b1.right - b1.left, b2.left, b2.right - b1.left))
		continue;
	    if (b1.top == b2.top)
		matrix[i][j] = 1; else
		matrix[i][j] = b2.top - b1.top;
	    assert matrix[i][j] >= 0;
	}
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

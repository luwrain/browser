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
import static java.util.Arrays.*;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import org.luwrain.web.BlockGeom.Block;

public class BlockGeomTest
{
    @Test public void main()
    {
	final var b = new BlockGeom(asList(
				     new Block(1, 4, 3, 5),
				     new Block(3, 5, 1, 5),
				     new Block(1, 5, 1, 5)
					   ));
	b.process();
	assertNotNull(b.blocks);
	assertEquals(3, b.blocks.length);

	final var b1 = b.blocks[0];
		final var b2 = b.blocks[1];
			final var b3 = b.blocks[2];

	//The first block
	assertEquals(1, b1.left);
	assertEquals(5, b1.right);
		assertEquals(1, b1.top);
				assertEquals(5, b1.height);

				//The second block
					assertEquals(3, b2.left);
	assertEquals(5, b2.right);
		assertEquals(6, b2.top);
				assertEquals(5, b2.height);

								//The third block
					assertEquals(1, b3.left);
	assertEquals(4, b3.right);
			assertEquals(7, b3.top);
				assertEquals(5, b3.height);
    }

    static final class Block extends BlockGeom.Block
    {
	Block(int left, int right, int top, int height)
	{
	    this.left = left;
	    this.right = right;
	    this.top = top;
	    this.height = height;
	}
    }
}

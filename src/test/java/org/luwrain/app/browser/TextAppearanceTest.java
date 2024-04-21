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

package org.luwrain.app.browser;

import java.util.*;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import org.luwrain.controls.block.*;

import static org.luwrain.app.browser.TestBlocks.*;

public class TextAppearanceTest
{
    @Test public void main()
    {
	final var view = new View(new Appearance(), Arrays.asList(getTestBlocks()));
	assertEquals(9, view.getLineCount());
	for(int i = 0;i < view.getLineCount();i++)
	    assertEquals(25, view.getLine(i).length());
	assertEquals("                         ", view.getLine(0));
	assertEquals(" Строка 1                ", view.getLine(1));
	assertEquals(" Строка 2      Блок 3    ", view.getLine(2));
	assertEquals("               Строка 2  ", view.getLine(3));
	assertEquals(" Блок 2        Строка 3  ", view.getLine(4));
	assertEquals(" Строка 2                ", view.getLine(5));
	assertEquals("               Блок 4    ", view.getLine(6));
	assertEquals("               Строка 2  ", view.getLine(7));
	assertEquals("               Строка 3  ", view.getLine(8));
    }

    static final class Appearance implements BlockArea.Appearance
    {
	@Override public void announceFirstBlockLine(Block block, BlockLine blockLine) {}
	@Override public void announceBlockLine(Block block, BlockLine blockLine) {}
	@Override public String getBlockLineTextAppearance(Block block, BlockLine blockLine)
	{
	    final var webLine = (WebLine)blockLine;
	    return webLine.text;
	}

    }
}

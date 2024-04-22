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

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

//import org.luwrain.controls.block.*;

import static org.luwrain.web.WebKitBlockBase.*;

public class WebKitBlockBaseTest 
{
    @Test public void singleLongRun()
    {
	final var b = new WebKitBlockBase();
	b.runs.add(new Run("Какие вкусные пирожки были сегодня к обеду, но особенно интересно, что будет завтра."));
	b.left = 0;
	b.right = 15;
	b.buildLines();
	assertEquals(7, b.lines.size());
	for(final var l: b.lines)
	    assertTrue(l.text.length() <= b.right - b.left);
	assertEquals("Какие вкусные ", b.lines.get(0).text);
	assertEquals("пирожки были ", b.lines.get(1).text);
	assertEquals("сегодня к ", b.lines.get(2).text);
	assertEquals("обеду, но ", b.lines.get(3).text);
	assertEquals("особенно ", b.lines.get(4).text);
	assertEquals("будет завтра.", b.lines.get(6).text);
    }
}

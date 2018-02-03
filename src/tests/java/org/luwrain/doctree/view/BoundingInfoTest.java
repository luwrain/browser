/*
   Copyright 2012-2018 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

package org.luwrain.doctree.view;

import org.junit.*;

import org.luwrain.core.*;
import org.luwrain.doctree.*;

public class BoundingInfoTest extends Assert
{
    @Test public void everything()
    {
	final Run[] runs = new Run[3];
	runs[0] = new TextRun("123");
	runs[1] = new TextRun("456");
	runs[2] = new TextRun("789");
	BoundingInfo info = new BoundingInfo(runs[0], 0, runs[2], runs[2].text().length());
	final StringBuilder b = new StringBuilder();
	info.filter(runs, (run,posFrom,posTo)->{
		assertTrue(posFrom >= 0);
		assertTrue(posTo >= 0);
		b.append(run.text().substring(posFrom, posTo));
	    });
	assertTrue("123456789".equals(new String(b)));
    }
    }

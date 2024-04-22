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
    @Test public void main()
    {
	final var b = new WebKitBlockBase();
	b.runs.add(new Run("Какие вкусные пирожки были сегодня к обеду, но особенно интересно, что будет завтра."));
	b.left = 0;
	b.right = 15;
	b.buildLines();
		
    }
}

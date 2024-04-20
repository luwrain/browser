/*
   Copyright 2012-2024 Michael Pozhidaev <msp@luwrain.org>
   Copyright 2015-2016 Roman Volovodov <gr.rPman@gmail.com>

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

final class TestBlocks
{
    static WebBlock[] getTestBlocks()
    {
	return new WebBlock[]{
	    
	    new WebBlock(1, 1, 10, Arrays.asList(
						 new WebLine("Строка 1"),
												new WebLine("Строка 2")
)),

	    	    new WebBlock(1, 4, 10, Arrays.asList(
						 new WebLine("Блок 2"),
												new WebLine("Строка 2")
)),

		    	    new WebBlock(15, 2, 10, Arrays.asList(
						 new WebLine("Блок 3"),
						 new WebLine("Строка 2"),
						 												new WebLine("Строка 3")
)),

	    	    new WebBlock(15, 6, 10, Arrays.asList(
						 new WebLine("Блок 4"),
						 new WebLine("Строка 2"),
						 												new WebLine("Строка 3")
)),


		    
	};
    }
}

// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>
// Copyright 2015-2016 Roman Volovodov <gr.rPman@gmail.com>

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

/*
   Copyright 2012-2017 Michael Pozhidaev <michael.pozhidaev@gmail.com>
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

package org.luwrain.browser.weight;

import java.util.TreeSet;

/** SortedSet of WebElements ordered by weight with reversed order */
class WeightSortedSet extends TreeSet<WebElement>
{
	private static final long serialVersionUID=1L;
	public WeightSortedSet()
	{
		super((o1, o2)->
		{
			if(o1 == o2 || o1.getWeight() == o2.getWeight()) return 0;
			// reversed order
			return o1.getWeight() < o2.getWeight()?1:-1;
	    });
	}
	
}

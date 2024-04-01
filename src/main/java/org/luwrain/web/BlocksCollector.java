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

public abstract class BlocksCollector<N>
{
    final LinkedList<N> markupStack = new LinkedList<>();

    public abstract List<N> getChildNodes(N node);
    public abstract boolean isMarkupNode(N node);

    public void process(N node)
    {
	if (isMarkupNode(node))
	{
	    markupStack.addLast(node);
	    final var children = getChildNodes(node);
	    for(final var c: children)
		process(c);
	    markupStack.pollLast();
	}
    }

}

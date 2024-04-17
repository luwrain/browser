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

import static org.luwrain.app.webinspector.App.log;

public abstract class BlocksCollector<N, B>
{
    final LinkedList<B> blocksStack = new LinkedList<>();
    final LinkedList<N> markupStack = new LinkedList<>();
    final ArrayList<B> blocks = new ArrayList<>();

    public abstract List<N> getChildNodes(N node);
    public abstract boolean isMarkupNode(N node);
    public abstract boolean isTextNode(N node);
    public abstract void addTextToBlock(N node, B block);
    public abstract B createBlock(N node);
    public abstract boolean saveBlock(B block);

    public void process(N node)
    {
	try {
	if (isTextNode(node))
	{
	    if (!blocksStack.isEmpty())
	    addTextToBlock(node, blocksStack.getLast());
	    return;
	}
		    final var children = getChildNodes(node);
	if (isMarkupNode(node))
	{
	    markupStack.addLast(node);
	    for(final var c: children)
		process(c);
	    markupStack.pollLast();
	    return;
	}
	blocksStack.addLast(createBlock(node));
		    for(final var c: children)
		process(c);
		    final B block = blocksStack.pollLast();
		    if (saveBlock(block))
			this.blocks.add(block);
	}
	catch(Throwable e)
	{
	    log(e.getMessage());
	}
    }

}

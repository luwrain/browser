// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>
// Copyright 2015-2016 Roman Volovodov <gr.rPman@gmail.com>

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

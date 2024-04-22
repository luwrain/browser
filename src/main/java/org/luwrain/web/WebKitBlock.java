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
import java.util.concurrent.atomic.*;
import org.w3c.dom.*;

import com.sun.webkit.dom.*;

import static org.luwrain.core.NullCheck.*;
import static org.luwrain.graphical.FxThread.*;
import static org.luwrain.app.webinspector.App.log;
import static java.lang.Character.*;

public final class WebKitBlock extends WebKitBlockBase
{
    public final NodeImpl node;
        public final DOMWindowImpl window;
    public String text = null;

    public WebKitBlock(DOMWindowImpl window, WebKitGeom geom, NodeImpl node)
    {
	notNull(window, "window");
	notNull(geom, "geom");
		notNull(node, "node");
	this.window = window;
	this.node = node;
	final GeomEntry entry = geom.getEntry(node);
	if (entry != null)
	{
	this.left = entry.x;
	this.right = entry.x + entry.width;
	this.top = entry.y;
    } else
      {
	  log("No geom for the node " + node.getClass().getSimpleName());
	  this.left = 0;
	  this.right = 0;
	  this.top = 0;
      }
    }

    void buildLines()
    {
	final int availableWidth = this.right - this.left;
	int spaceLeft = availableWidth;
	for(final var r: runs)
	{
	    final var len = r.text.length();
	    final var breaks = r.getBreaks();
	    int continueFrom = 0;
	    while(continueFrom < len)
	    {
		int newBreak = findNextBreak(breaks, continueFrom, len, spaceLeft);
		//New fragment continueFrom to break
		spaceLeft -= newBreak - continueFrom;
	    }
	}
    }

    int findNextBreak(int[]   breaks, int continueFrom, int wholeLen, int availableSpace)
    {
	if (wholeLen - continueFrom <= availableSpace)
	    return -1;
	int left;
	//Searching the closest break to continueWith located on the left (there are no breaks more to continueWith).
	//If continueWith has the same location as one of the breaks, we have to choose the corresponding break.
	for(left = 0;left + 1 < breaks.length && breaks[left + 1] <= continueFrom;left++);
	int right = breaks.length - 1;
	while(right > left && breaks[right] - continueFrom > availableSpace)
	      right--;
	      return right > left?breaks[right]:continueFrom + availableSpace;
    }

        public String getStyle()
    {
	if (node instanceof Element el)
	{
        final var res = new AtomicReference<String>();
        runSync(()->{
		final var css = window.getComputedStyle(el, "");
		if (css != null)
		    res.set(css.getCssText());
		});
        return res.get();
	}
	return null;
    }

    static public final class Run
    {
	public final String text;
	Run(String text)
	{
	    notNull(text, "text");
	    this.text = text;
	}
	int[] getBreaks()
	{
	    final var res = new ArrayList<Integer>();
	    for(int i = 1;i < text.length();i++)
	    {
		final char ch = text.charAt(i), prevCh = text.charAt(i - 1);
		if (!isSpace(ch) && isSpace(prevCh))
		    res.add(Integer.valueOf(i));
	    }
	    final int[] intRes = new int[res.size()];
	    for(int i = 0;i < intRes.length;i++)
		intRes[i] = res.get(i).intValue();
	    return intRes;
    }
    }
	
	static public final class Fragment
	{
	    public final Run run;
	    public final int fromPos, toPos;
	    Fragment(Run run, int fromPos, int toPos)
	    {
		notNull(run, "run");
		if (fromPos < 0 || toPos < 0)
		    throw new IllegalArgumentException("fromPos (" + fromPos + ") and toPos (" + toPos + ") can't be negative");
		if (fromPos > toPos)
		    throw new IllegalArgumentException("fromPos ( " + fromPos + ") must be less than toPos (" + toPos + ")");
		this.run = run;
		this.fromPos = fromPos;
		this.toPos = toPos;
	    }
	}

	static public final class Line
	{
	    public final Fragment[] fragments;
	    Line(List<Fragment> fragments)
	    {
		notNull(fragments, "fragments");
		this.fragments = fragments.toArray(new Fragment[fragments.size()]);
	    }
	}
	    
}

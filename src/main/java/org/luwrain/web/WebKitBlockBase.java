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
//import java.util.concurrent.atomic.*;
//import org.w3c.dom.*;

//import com.sun.webkit.dom.*;

import static org.luwrain.core.NullCheck.*;
import static org.luwrain.graphical.FxThread.*;
import static org.luwrain.app.webinspector.App.log;
import static java.lang.Character.*;

public final class WebKitBlockBase extends BlockGeom.Block
{
    public String text = null;
    public final List<Run> runs = new ArrayList<>();
    public final List<Line> lines = new ArrayList<>();

    void buildLines()
    {
	this.lines.clear();
	final int availableWidth = this.right - this.left;
	int spaceLeft = availableWidth;
	final var fragments = new ArrayList<Fragment>();
	for(final var r: runs)
	{
	    final int len = r.text.length();
	    final var breaks = r.getBreaks();
	    System.out.println(Arrays.toString(breaks));
	    int continueFrom = 0;
	    while(continueFrom < len)
	    {
		//No need to break the run any more
		if (len - continueFrom <= spaceLeft)
		{
		    fragments.add(new Fragment(r, continueFrom, len));
		    continueFrom = len;
		    continue;
		}
		int newBreak = findNextBreak(breaks, continueFrom, len, spaceLeft);
		//		System.out.println("Break " + newBreak);
		//No room for breaking even on the closest break
		if (newBreak < 0)
		{
		    this.lines.add(new Line(fragments));
		    fragments.clear();
		    //FIXME:spaceLeft = availableSpace
		    spaceLeft = availableWidth;
		    continue;
		}
		//		stop();
		fragments.add(new Fragment(r, continueFrom, newBreak));
		spaceLeft -= newBreak - continueFrom;
		continueFrom = newBreak;

		//				System.out.println("continueFrom=" + continueFrom);
		//		System.out.println("spaceLeft=" + spaceLeft);


		
		//		stop();
	    }
	}
	if (!fragments.isEmpty())
	    lines.add(new Line(fragments));
    }

    int findNextBreak(int[]   breaks, int continueFrom, int wholeLen, int availableSpace)
    {
	//	if (wholeLen - continueFrom <= availableSpace)
	//	    return -1;
	int left;
	//Searching the closest break to continueWith located on the left (there are no breaks more to continueWith).
	//If continueWith has the same location as one of the breaks, we have to choose the corresponding break.
	for(left = 0;left + 1 < breaks.length && breaks[left + 1] <= continueFrom;left++);
	int right = breaks.length - 1;
	while(right > left && breaks[right] - continueFrom > availableSpace)
	      right--;
	      return right > left?breaks[right]:-1;
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
		if (toPos > run.text.length())
		    throw new IllegalArgumentException("toPos (" + toPos + ") igreater than the length of the run text (" + run.text + ")");
		this.run = run;
		this.fromPos = fromPos;
		this.toPos = toPos;
	    }
	    public String text()
	    {
		return run.text.substring(fromPos, toPos);
	    }
	}

	static public final class Line
	{
	    public final Fragment[] fragments;
	    public final String text;
	    Line(List<Fragment> fragments)
	    {
		notNull(fragments, "fragments");
		this.fragments = fragments.toArray(new Fragment[fragments.size()]);
		final var b = new StringBuilder();
		for(var f: fragments)
		    b.append(f.text());
		this.text = new String(b);
	    }
	}

    void stop()
    {
	throw new RuntimeException("Debug stop");
    }
}

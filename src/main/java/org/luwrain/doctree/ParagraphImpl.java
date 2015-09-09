/*
   Copyright 2012-2015 Michael Pozhidaev <michael.pozhidaev@gmail.com>
   Copyright 2015 Roman Volovodov <gr.rPman@gmail.com>

   This file is part of the LUWRAIN.

   LUWRAIN is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   LUWRAIN is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

package org.luwrain.doctree;

public class ParagraphImpl extends NodeImpl
{
    public Run[] runs = new Run[0];
    RowPart[] rowParts = new RowPart[0];

    /** Position of the first row in a document*/
    int topRowIndex = -1;

    ParagraphImpl()
    {
	super(Node.PARAGRAPH);
    }

    public boolean hasSingleLineOnly()
    {
	return height == 1;
    }

    @Override void commit()
    {
	subnodes = null;
	if (runs == null)
	    return;
	for(Run r: runs)
	    r.parentParagraph = this;
    }

    @Override void setEmptyMark()
    {
	empty = true;
	if (runs == null || runs.length < 1)
	    return;
	for(Run r: runs)
	    if (r.text != null && !r.text.isEmpty())
		empty = false;
    }

    @Override void removeEmpty()
    {
	if (runs == null)
	    return;
	int k = 0;
	for(int i = 0;i < runs.length;++i)
	    if (runs[i].text.isEmpty() )
		++k; else
		runs[i - k] = runs[i];
	if (k > 0)
	{
	    final int count = runs.length - k;
	    Run[] newRuns = new Run[count];
	    for(int i = 0;i < count;++i)
		newRuns[i] = runs[i];
	    runs = newRuns;
	}
    }

    @Override void saveStatistics(Statistics stat)
    {
	++stat.numNodes;
	++stat.numParagraphs;
	stat.numRuns += (runs != null?runs.length:0);
    }

    @Override void calcWidth(int recommended)
    {
	width = recommended;
    }

    @Override public void calcHeight()
    {
	if (rowParts == null || rowParts.length < 1)
	{
	    height = 0;
	    return;
	}
	int maxRelRowNum = 0;
	for(RowPart p: rowParts)
	    if (p.relRowNum > maxRelRowNum)
		maxRelRowNum = p.relRowNum;
	height = maxRelRowNum + 1;
    }

    @Override void calcPosition()
    {
    }

    int getParaIndex()
    {
	return getIndexInParentSubnodes();
    }

    @Override public String toString()
    {
	if (runs == null)
	    return "";
	String s = "";
	for(Run r: runs)
	{
	    if (!s.isEmpty())
		s += " ";
	    s += r.toString();
	}
	return s;
    }
}

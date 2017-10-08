/*
   Copyright 2012-2016 Michael Pozhidaev <michael.pozhidaev@gmail.com>
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

package org.luwrain.doctree.view;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.doctree.*;

class RowPartsBuilder
{
    static private final String LOG_COMPONENT = "doctree";

    private final List<RowPart> parts = new LinkedList<RowPart>();
    private final List<RowPart> currentParaParts = new LinkedList<RowPart>();
    private final List<Paragraph> paragraphs = new LinkedList<Paragraph>();

    /** The index of the next row to be added to the current paragraph*/
    private int index = 0;
    /** Number of characters in the current (incomplete) row*/
    private int offset = 0;

    private final BoundingInfo boundingInfo;

    RowPartsBuilder()
    {
	this.boundingInfo = null;
    }

    RowPartsBuilder(BoundingInfo boundingInfo)
    {
	NullCheck.notNull(boundingInfo, "boundingInfo");
	this.boundingInfo = boundingInfo;
    }

void onNode(Node node)
    {
	NullCheck.notNull(node, "node"); 
	onNode(node, 0);
    }

    void onNode(Node node, int width)
    {
	NullCheck.notNull(node, "node");
	if (node instanceof EmptyLine)
	{
	    final Paragraph para = (Paragraph)node;
	    final RowPart part = new RowPart(para.runs[0]);
	    para.setRowParts(new RowPart[]{part});
	    parts.add(part);
	    return;
	}
   	if (node instanceof Paragraph)
	{
	    offset = 0;
	    index = 0;
	    final Paragraph para = (Paragraph)node;
	    currentParaParts.clear();
	    for(Run r: para.runs())
		    onRun(r, width > 0?width:para.width);
	    if (!currentParaParts.isEmpty())
	    {
		para.setRowParts(currentParaParts.toArray(new RowPart[currentParaParts.size()]));
		paragraphs.add(para);
		for(RowPart p: currentParaParts)
		    parts.add(p);
	    }
	    return;
	}
	if (NodeGeom.hasTitleRun(node))
	parts.add(makeTitlePart(node.getTitleRun()));
	for(Node n: node.getSubnodes())
		onNode(n);
    }

    //Removes spaces only on row breaks and only if after the break there are non-spacing chars;
    private void onRun(Run run, int maxRowLen)
    {
	final String text = run.text();
	final int boundTo = text.length();
	NullCheck.notNull(text, "text");
	if (text.isEmpty())
	    return;
	int stepFrom = 0;
	while (stepFrom < boundTo)
	{
	    final int roomOnLine = maxRowLen - offset;//Available space on current line
	    if (roomOnLine <= 0)
	    {
		//Try again on the next line
		++index;
		offset = 0;
		continue;
	    }
	    final int remains = boundTo - stepFrom;
	    //Both remains and roomOnLine are greater than zero
	    if (remains <= roomOnLine)
	    {
		//Everything fits on the current line
		currentParaParts.add(makeTextPart(run, stepFrom, boundTo));
		offset += remains;
		return;
	    }
	    int stepTo = findWordsFittingOnLIne(text, stepFrom, boundTo, roomOnLine);
	    if (stepTo == stepFrom)//No word ends before the end of the row
	    {
		if (offset > 0)
		{
		    //Trying to do the same once again from the beginning of the next line in hope a whole line is enough
		    offset = 0;
		    ++index;
		    continue;
		}
		//The only thing we can do is split the line in the middle of the word, no another way
		stepTo = stepFrom + roomOnLine;
	    } //no fitting words
	    	    if (stepTo <= stepFrom)
			throw new RuntimeException("stepTo (" + stepTo + ") == stepFrom (" + stepFrom + ")");
	    	    if (stepTo - stepFrom > roomOnLine)
			throw new RuntimeException("Exceeding room on line (" + roomOnLine + "), stepFrom=" + stepFrom + ", stepTo=" + stepTo);
	    currentParaParts.add(makeTextPart(run, stepFrom, stepTo));
	    ++index;
	    offset = 0;
	    stepFrom = findNextWord(stepTo, text, boundTo);
	} //main loop;
    }

    private int findWordsFittingOnLIne(String text, int posFrom, int boundTo, int roomOnLine)
    {
	int pos = posFrom;
		    int nextWordEnd = posFrom;
	    while (nextWordEnd - posFrom <= roomOnLine && nextWordEnd > pos)
	    {
		pos = nextWordEnd;//It is definitely before the row end
		while (nextWordEnd < boundTo && Character.isSpace(text.charAt(nextWordEnd)))//FIXME:nbsp
		    ++nextWordEnd;
		while (nextWordEnd < boundTo && !Character.isSpace(text.charAt(nextWordEnd)))//FIXME:nbsp
		    ++nextWordEnd;
			    }
	    return pos;
    }

    private int findNextWord(int pos, String text, int boundTo)
    {
	NullCheck.notNull(text, "text");
	int i = pos;
	    while (i < boundTo && Character.isSpace(text.charAt(i)))
		++i;
	    if (i >= boundTo)
		return pos;
	    return i;
    }

    private RowPart makeTextPart(Run run, int posFrom, int posTo)
    {
	NullCheck.notNull(run, "run");
return new RowPart(run, posFrom, posTo, index);
    }

    static private RowPart makeTitlePart(Run run)
    {
	NullCheck.notNull(run, "run");
return new RowPart(run);
    }

    public RowPart[] getRowParts()
    {
	return parts.toArray(new RowPart[parts.size()]);
    }

    public Paragraph[] getParagraphs()
    {
	return paragraphs.toArray(new Paragraph[paragraphs.size()]);
    }
}

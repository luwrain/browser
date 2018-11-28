
package org.luwrain.controls.web;

import java.util.*;

import org.luwrain.core.*;

final class ViewBuilder
{
    static private final String LOG_COMPONENT = WebArea.LOG_COMPONENT;

    final List<WebObject> res = new LinkedList();

    /** The index of the next row to be added to the current paragraph*/
    private int index = 0;
    /** Number of characters in the current (incomplete) row*/
    private int offset = 0;

    //Removes spaces only on row breaks and only if after the break there are non-spacing chars
    void onContentItem(ContentItem item, String text, int boundFrom, int boundTo, int maxRowLen)
    {
	NullCheck.notNull(item, "item");
	NullCheck.notNull(text, "text");
	if (boundFrom < 0 || boundTo < 0)
	    throw new IllegalArgumentException("boundFrom (" + boundFrom + ") and boundTo (" + boundTo + ") may not be negative");
	if (boundFrom > text.length() || boundTo > text.length())
	    throw new IllegalArgumentException("boundFrom (" + boundFrom + ") and boundTo (" + boundTo + ") may not be greater than length of the text (" + text.length() + ")");
	if (boundFrom > boundTo)
	    throw new IllegalArgumentException("boundFrom (" + boundFrom + ") may not be greater than boundTo (" + boundTo + ")");
	if (offset > maxRowLen)
	    throw new RuntimeException("offset (" + offset + ") may not be greater than maxRowLen (" + maxRowLen + ")");
	if (boundFrom == boundTo)
	    return;
	int nextStepFrom = boundFrom;
	while (nextStepFrom < boundTo)
	{
	    final int stepFrom = nextStepFrom;
	    final int roomOnLine = maxRowLen - offset;//Available space on current line
	    if (roomOnLine == 0)
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
		res.add(new WebText(item, stepFrom, boundTo));
		offset += remains;
		return;
	    }
	    int stepTo = findWordsFittingOnLIne(text, stepFrom, boundTo, roomOnLine);
	    if (stepTo == stepFrom)//No word ends before the end of the row
	    {
		if (offset > 0)
		{
		    //Trying to do the same once again from the beginning of the next line in hope a whole line will be enough
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
	    res.add(new WebText(item, stepFrom, stepTo));
	    ++index;
	    offset = 0;
	    nextStepFrom = findNextWord(stepTo, text, boundTo);
	} //main loop;
    }

    private int findWordsFittingOnLIne(String text, int posFrom, int boundTo, int lenRestriction)
    {
	int pos = 0;
		    int nextWordEnd = posFrom;
	    while (nextWordEnd - posFrom <= lenRestriction)
	    {
		pos = nextWordEnd;//It is definitely before the row end
		while (nextWordEnd < boundTo && Character.isSpace(text.charAt(nextWordEnd)))//FIXME:nbsp
		    ++nextWordEnd;
		while (nextWordEnd < boundTo && !Character.isSpace(text.charAt(nextWordEnd)))//FIXME:nbsp
		    ++nextWordEnd;
		if (nextWordEnd == pos)
		    return pos;
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
    }

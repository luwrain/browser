/*
   Copyright 2012-2015 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.core.queries.*;
import org.luwrain.controls.*;
import org.luwrain.util.*;

public class DocTreeArea implements Area
{
    private ControlEnvironment environment;
    private final Region region = new Region(new LinesRegionProvider(this));
    private RowIntroduction introduction;
    private String areaName = null;//FIXME:No corresponding constructor;
    private Document document;
    private org.luwrain.doctree.Iterator iterator;
    private int hotPointX = 0;

    public DocTreeArea(ControlEnvironment environment, 
		       RowIntroduction introduction, 
		       Document document)
    {
	this.environment = environment;
	this.introduction = introduction;

	this.document = document;
	NullCheck.notNull(environment, "environment");
	NullCheck.notNull(introduction, "introduction");
	if (document != null)
	    iterator = document.getIterator(); else
	    iterator = null;
    }

    public void setDocument(Document document)
    {
	NullCheck.notNull(document, "document");
	this.document = document;
	iterator = document.getIterator();
	hotPointX = 0;
	environment.onAreaNewContent(this);
	environment.onAreaNewHotPoint(this);
    }

    public boolean hasDocument()
    {
	return document != null && iterator != null;
    }

    public Document getDocument()
    {
	return document;
    }

    public String getHref()
    {
	if (document == null || iterator == null)
	    return null;
	if (iterator.isEmptyRow())
	    return null;
	return iterator.getHref(hotPointX);
    }

    @Override public int getLineCount()
    {
	return document != null?document.getLineCount() + 1:1;
    }

    @Override public String getLine(int index)
    {
	if (document == null)
	    return index == 0?noContentStr():"";
	return index < document.getLineCount()?document.getLine(index):"";
    }

    @Override public boolean onKeyboardEvent(KeyboardEvent event) 
    {
	NullCheck.notNull(event, "event");
	if (event.isCommand() && !event.isModified())
	    switch(event.getCommand())
	    {
	    case KeyboardEvent.TAB:
		return onTab(event, false);
	    case KeyboardEvent.ARROW_DOWN:
		return onArrowDown(event, false);
	    case KeyboardEvent.ARROW_UP:
		return onArrowUp(event, false);
	    case KeyboardEvent.ALTERNATIVE_ARROW_DOWN:
		return onArrowDown(event, true);
	    case KeyboardEvent.ALTERNATIVE_ARROW_UP:
		return onArrowUp(event, true);
	    case KeyboardEvent.ARROW_LEFT:
		return onArrowLeft(event);
	    case KeyboardEvent.ARROW_RIGHT:
		return onArrowRight(event);
	    case KeyboardEvent.ALTERNATIVE_ARROW_LEFT:
		return onAltLeft(event);
	    case KeyboardEvent.ALTERNATIVE_ARROW_RIGHT:
		return onAltRight(event);
	    case KeyboardEvent.HOME:
		return onHome(event);
	    case KeyboardEvent .END:
		return onEnd(event);
	    case KeyboardEvent.ALTERNATIVE_HOME:
		return onAltHome(event);
	    case KeyboardEvent .ALTERNATIVE_END:
		return onAltEnd(event);
	    case KeyboardEvent.PAGE_UP:
		return onPageUp(event, false);
	    case KeyboardEvent.PAGE_DOWN:
		return onPageDown(event, false);
	    case KeyboardEvent.ALTERNATIVE_PAGE_UP:
		return onPageUp(event, true);
	    case KeyboardEvent.ALTERNATIVE_PAGE_DOWN:
		return onPageDown(event, true);
	    default:
		return false;
	    }
	return false;
    }

    @Override public boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	NullCheck.notNull(event, "event");
	switch(event.getCode())
	{
	case EnvironmentEvent.READING_POINT:
	case EnvironmentEvent.MOVE_HOT_POINT:
	    if (event instanceof MoveHotPointEvent)
		return onMoveHotPoint((MoveHotPointEvent)event);
	    return false;
	default:
	    return region.onEnvironmentEvent(event, getHotPointX(), getHotPointY());
	}
    }

    @Override public Action[] getAreaActions()
    {
	return null;
    }

    @Override public boolean onAreaQuery(AreaQuery query)
    {
	NullCheck.notNull(query, "query");
	switch(query.getQueryCode())
	{
	case AreaQuery.VOICED_FRAGMENT:
	    return onVoicedFragmentQuery((VoicedFragmentQuery)query);
	default:
	return region.onAreaQuery(query, getHotPointX(), getHotPointY());
	}
    }

    @Override public int getHotPointX()
    {
	if (isEmpty())
	    return 0;
	return iterator.getRow().getRowX() + hotPointX;
    }

    @Override public int getHotPointY()
    {
	if (isEmpty())
	    return 0;
	return iterator.getRow().getRowY();
    }

    @Override public String getAreaName()
    {
	if (areaName != null)
	    return areaName;
	if (document != null)
	{
	    final String title = document.getTitle();
	    return title != null?title:"";
	}
	return "";
    }

    private boolean onVoicedFragmentQuery(VoicedFragmentQuery query)
    {
	return false;
	/*
	NullCheck.notNull(query, "query");
	if (noContentCheck())
	    return false;
	final Iterator it2 = (Iterator)iterator.clone();
	String text = it2.getText();
	int endPos = endOfSentence(text, hotPointX);
	if (endPos > hotPointX)
	{

	}
	while(it2.moveNext())
	{
	    text = it2.getText();
	    endPost = getEndOfSentence(text, 0);
	    if (endPoss >= 0)
	}
	return false;
	*/
    }

    private boolean onMoveHotPoint(MoveHotPointEvent event)
    {
	if (document == null)
	    return false;
	final Iterator it2 = (Iterator)iterator.clone();
	final int x = event.getNewHotPointX();
	final int y = event.getNewHotPointY();
	while (it2.canMoveNext() && !it2.coversPos(x, y))
	    it2.moveNext();
	if (!it2.canMoveNext())
	    return false;
	iterator = it2;
	hotPointX = x - iterator.getRow().getRowX();
	return true;
    }

    private boolean onTab(KeyboardEvent event, boolean briefIntroduction)
    {
	if (noContentCheck())
	    return true;
	final SmartJump jump = new SmartJump((Iterator)iterator.clone(), true);
	if (!jump.jumpForward())
	{
	    environment.hint(Hints.NO_LINES_BELOW);
	    return true;
	}
	iterator = jump.it;
	announceFragment((Iterator)iterator.clone(), jump.speakToIt);
	environment.onAreaNewHotPoint(this);
	//onNewHotPointY() should not be called
	return true;
    }

    private boolean onArrowDown(KeyboardEvent event, boolean briefIntroduction)
    {
	if (noContentCheck())
	    return true;
	if (!iterator.moveNext())
	{
	    environment.hint(Hints.NO_LINES_BELOW);
	    return true;
	}
	onNewHotPointY( briefIntroduction );
	return true;
    }

    private boolean onArrowUp(KeyboardEvent event, boolean briefIntroduction)
    {
	if (noContentCheck())
	    return true;
	if (!iterator.movePrev())
	{
	    environment.hint(Hints.NO_LINES_ABOVE);
	    return true;
	}
	onNewHotPointY( briefIntroduction);
	return true;
    }

    private boolean onAltEnd(KeyboardEvent event)
    {
	if (noContentCheck())
	    return true;
	iterator.moveEnd();
	onNewHotPointY( false);
	return true;
    }

    private boolean onAltHome(KeyboardEvent event)
    {
	if (noContentCheck())
	    return true;
	iterator.moveHome();
	onNewHotPointY( false);
	return true;
    }

    private boolean onPageDown(KeyboardEvent event, boolean briefIntroduction)
    {
	if (noContentCheck())
	    return true;
	if (!iterator.moveNext())
	{
	    environment.hint(Hints.NO_LINES_BELOW);
	    return true;
	}
	while(!iterator.isContainerSection() && iterator.moveNext());
	onNewHotPointY( briefIntroduction );
	return true;
    }

    private boolean onPageUp(KeyboardEvent event, boolean briefIntroduction)
    {
	if (noContentCheck())
	    return true;
	if (!iterator.movePrev())
	{
	    environment.hint(Hints.NO_LINES_ABOVE);
	    return true;
	}
	while(!iterator.isContainerSection() && iterator.movePrev());
	onNewHotPointY( briefIntroduction );
	return true;
    }

    private boolean onArrowLeft(KeyboardEvent event)
    {
	if (noContentCheck())
	    return true;
	if (!iterator.isEmptyRow())
	{
	final String text = iterator.getText();
	if (hotPointX > text.length())
	    hotPointX = text.length();
	if (hotPointX > 0)
	{
	    --hotPointX;
	    environment.sayLetter(text.charAt(hotPointX));
	    environment.onAreaNewHotPoint(this);
	    return true;
	}
    }
	if (!iterator.canMovePrev())
	{
	    environment.hint(Hints.BEGIN_OF_TEXT);
	    return true;
	}
	iterator.movePrev();
	final String prevRowText = iterator.getText();
	hotPointX = prevRowText.length();
	environment.hint(Hints.END_OF_LINE);
	return true;
    }

    private boolean onArrowRight(KeyboardEvent event)
    {
	if (noContentCheck())
	    return true;
	if (!iterator.isEmptyRow())
	{
	final String text = iterator.getText();
	if (hotPointX < text.length())
	{
	    ++hotPointX;
	    if (hotPointX < text.length())
		environment.sayLetter(text.charAt(hotPointX)); else
		environment.hint(Hints.END_OF_LINE);
	    environment.onAreaNewContent(this);
	    return true;
	}
}
	if (!iterator.canMoveNext())
	{
	    environment.hint(Hints.END_OF_TEXT);
	    return true;
	}
	iterator.moveNext();
	final String nextRowText = iterator.getText();
	hotPointX = 0;
	if (nextRowText.isEmpty())
	    environment.hint(Hints.END_OF_LINE); else
	    environment.sayLetter(nextRowText.charAt(0));
	environment.onAreaNewHotPoint(this);
	return true;
    }

    private boolean onAltLeft(KeyboardEvent event)
    {
	if (noContentCheck())
	    return true;
	if (iterator.isEmptyRow())
	{
	    environment.hint(Hints.EMPTY_LINE);
	    return true;
	}
	final String text = iterator.getText();
	final WordIterator it = new WordIterator(text, hotPointX);
	if (!it.stepBackward())
	{
	    environment.hint(Hints.BEGIN_OF_LINE);
	    return true;
	}
	hotPointX = it.pos();
	environment.say(it.announce());
	environment.onAreaNewHotPoint(this);
	return true;
    }

    private boolean onAltRight(KeyboardEvent event)
    {
	if (noContentCheck())
	    return true;
	if (iterator.isEmptyRow())
	{
	    environment.hint(Hints.EMPTY_LINE);
	    return true;
	}
	final String text = iterator.getText();
	final WordIterator it = new WordIterator(text, hotPointX);
	if (!it.stepForward())
	{
	    environment.hint(Hints.END_OF_LINE);
	    return true;
	}
	hotPointX = it.pos();
	if (it.announce().length() > 0)
	    environment.say(it.announce()); else
	    environment.hint(Hints.END_OF_LINE);
	environment.onAreaNewHotPoint(this);
	return true;
    }

    private boolean onHome(KeyboardEvent event)
    {
	if (noContentCheck())
	    return true;
	if (iterator.isEmptyRow())
	{
	    environment.hint(Hints.EMPTY_LINE);
	    return true;
	}
	final String text = iterator.getText();
	hotPointX = 0;
	if (!text.isEmpty())
	    environment.sayLetter(text.charAt(0)); else
	    environment.hint(Hints.EMPTY_LINE);
	environment.onAreaNewHotPoint(this);
	return true;
    }

    private boolean onEnd(KeyboardEvent event)
    {
	if (noContentCheck())
	    return true;
	if (iterator.isEmptyRow())
	{
	    environment.hint(Hints.EMPTY_LINE);
	    return true;
	}
	final String text = iterator.getText();
	hotPointX = text.length();
	environment.hint(Hints.END_OF_LINE);
	environment.onAreaNewHotPoint(this);
	return true;
    }

    private void onNewHotPointY(boolean briefIntroduction)
    {
	hotPointX = 0;
	if (iterator.isEmptyRow())
	    environment.hint(Hints.EMPTY_LINE); else
	    introduction.introduce(iterator, briefIntroduction);
	environment.onAreaNewHotPoint(this);
    }

    protected 	void announceFragment(Iterator itFrom, Iterator itTo)
    {
	final StringBuilder b = new StringBuilder();
	Iterator it = itFrom;
	while(!it.equals(itTo))
	{
	    if (!it.isEmptyRow())
		b.append(it.getText());
	    if (!it.moveNext())
		break;
	}
	environment.say(b.toString());

    }

    public boolean isEmpty()
    {
	if (document ==null || iterator == null)
	    return true;
	return iterator.noContent();
    }

    protected String noContentStr()
    {
	return environment.staticStr(LangStatic.DOCUMENT_NO_CONTENT);
    }

    private boolean noContentCheck()
    {
	if (isEmpty())
	{
	    environment.hint(noContentStr(), Hints.NO_CONTENT);
	    return true;
	}
	return false;
    }
}

/*
   Copyright 2012-2016 Michael Pozhidaev <michael.pozhidaev@gmail.com>
   Copyright 2015-2016 Roman Volovodov <gr.rPman@gmail.com>

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

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.core.queries.*;
import org.luwrain.controls.*;
import org.luwrain.util.*;

public class DoctreeArea implements Area
{
    protected ControlEnvironment environment;
    protected final RegionTranslator region = new RegionTranslator(new LinesRegionProvider(this));
    private String areaName = null;//FIXME:No corresponding constructor;
    protected Document document;
    protected org.luwrain.doctree.Iterator iterator;
    protected int hotPointX = 0;

    public DoctreeArea(ControlEnvironment environment, Document document)
    {
	NullCheck.notNull(environment, "environment");
	this.environment = environment;
	this.document = null;
	    this.iterator = null;
	    if (document != null)
		setDocument(document);
    }

    public void setDocument(Document document)
    {
	NullCheck.notNull(document, "document");
	this.document = document;
	int defaultIndex = -1;
	if (!document.getProperty(Document.DEFAULT_ITERATOR_INDEX_PROPERTY).isEmpty())
	    try {
		defaultIndex = Integer.parseInt(document.getProperty("defaultiteratorindex"));
	    }
	    catch (NumberFormatException e)
	    {
	    }
	if (defaultIndex >= 0)
	iterator = document.getIterator(defaultIndex); else
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

    public Run getCurrentRun()
    {
	if (isEmpty())
	    return null;
	return iterator.getRunUnderPos(hotPointX);
    }

    public boolean findRun(Run run)
    {
	NullCheck.notNull(run, "run");
	if (isEmpty())
	    return false;
	final Iterator newIt = document.getIterator();
	while(newIt.canMoveNext() && !newIt.hasRunOnRow(run))
	    newIt.moveNext();
	if (!newIt.hasRunOnRow(run))
	    return false;
	final int pos = newIt.runBeginsAt(run);
	if (pos < 0)
	    return false;
	iterator = newIt;
	hotPointX = pos;
	environment.onAreaNewHotPoint(this);
	return true;
    }

    public int getCurrentRowIndex()
    {
	return !isEmpty()?iterator.getRowAbsIndex():-1;
    }

    public boolean setCurrentRowIndex(int index)
    {
	if (isEmpty())
		      return false;
	final Iterator newIt;
	try {
	    newIt = document.getIterator(index);
	}
	catch(IllegalArgumentException e)
	{
	    return false;
	}
	this.iterator = newIt;
	hotPointX = 0;
	environment.onAreaNewHotPoint(this);
	return true;
    }


    public java.net.URL getUrl()
    {
	return !isEmpty()?document.getUrl():null;
    }

    public boolean reBuildView(int width)
    {
	if (isEmpty())
	    return false;
	final Run currentRun = getCurrentRun();
	document.buildView(width);
	if (currentRun != null)
	    findRun(currentRun);
	return true;
    }

    @Override public int getLineCount()
    {
	return !isEmpty()?document.getLineCount() + 1:1;
    }

    @Override public String getLine(int index)
    {
	if (isEmpty())
	    return index == 0?noContentStr():"";
	return index < document.getLineCount()?document.getLine(index):"";
    }

    @Override public boolean onKeyboardEvent(KeyboardEvent event) 
    {
	NullCheck.notNull(event, "event");
	if (!event.isSpecial() && !event.isModified())
	    switch(event.getChar())
	    {
	    case ' ':
		return onSpace(event);
	    case '[':
		return onLeftSquareBracket(event);
	    case ']':
		return onRightSquareBracket(event);
	    }
	if (event.isSpecial() && !event.isModified())
	    switch(event.getSpecial())
	    {
	    case TAB:
		return onTab(event, false);
	    case ARROW_DOWN:
		return onArrowDown(event, false);
	    case ARROW_UP:
		return onArrowUp(event, false);
	    case ALTERNATIVE_ARROW_DOWN:
		return onArrowDown(event, true);
	    case ALTERNATIVE_ARROW_UP:
		return onArrowUp(event, true);
	    case ARROW_LEFT:
		return onArrowLeft(event);
	    case ARROW_RIGHT:
		return onArrowRight(event);
	    case ALTERNATIVE_ARROW_LEFT:
		return onAltLeft(event);
	    case ALTERNATIVE_ARROW_RIGHT:
		return onAltRight(event);
	    case HOME:
		return onHome(event);
	    case END:
		return onEnd(event);
	    case ALTERNATIVE_HOME:
		return onAltHome(event);
	    case ALTERNATIVE_END:
		return onAltEnd(event);
	    case PAGE_UP:
		return onPageUp(event, false);
	    case PAGE_DOWN:
		return onPageDown(event, false);
	    case ALTERNATIVE_PAGE_UP:
		return onPageUp(event, true);
	    case ALTERNATIVE_PAGE_DOWN:
		return onPageDown(event, true);
	    }
	return false;
    }

    @Override public boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	NullCheck.notNull(event, "event");
	switch(event.getCode())
	{
	case READING_POINT:
	case MOVE_HOT_POINT:
	    if (event instanceof MoveHotPointEvent)
		return onMoveHotPoint((MoveHotPointEvent)event);
	    return false;
	default:
	    return region.onEnvironmentEvent(event, getHotPointX(), getHotPointY());
	}
    }

    @Override public Action[] getAreaActions()
    {
	return new Action[0];
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
	NullCheck.notNull(query, "query");
	if (noContentCheck())
	    return false;
	final Iterator it2 = (Iterator)iterator.clone();
	String text = it2.getText();
	int pos = endOfSentence(text, hotPointX);
	if (pos > hotPointX)
	{
	    text = text.substring(hotPointX, pos + 1);
	    pos = findNextSentence(it2, pos);
	    if (pos < 0)
		return false;
	    query.answer(text, it2.getRow().getRowX() + pos, it2.getRow().getRowY());
	    return true;
	}
	final StringBuilder b = new StringBuilder();
	b.append(text.substring(hotPointX) + " ");
	while(it2.moveNext())
	{
	    text = it2.getText();
pos = endOfSentence(text, 0);
if (pos < 0)
{
    b.append(text + " ");
    continue;
}
b.append(text.substring(0, pos + 1));
	    pos = findNextSentence(it2, pos);
	    if (pos < 0)
		return false;
	    query.answer(new String(b), it2.getRow().getRowX() + pos, it2.getRow().getRowY());
	}
	return false;
    }

    private boolean onMoveHotPoint(MoveHotPointEvent event)
    {
	NullCheck.notNull(event, "event");
	if (document == null)
	    return false;
	final Iterator it2 = document.getIterator();
	final int x = event.getNewHotPointX();
	final int y = event.getNewHotPointY();
	if (x < 0 || y < 0)
	    return false;
	Log.debug("doctree", "area requested to move hot point at " + x + "," + y);
	while (it2.canMoveNext() && !it2.coversPos(x, y))
	    it2.moveNext();
	if (!it2.canMoveNext())
	    return false;
	iterator = it2;
	hotPointX = x - iterator.getRow().getRowX();
	environment.onAreaNewHotPoint(this);
	return true;
    }

    private boolean onTab(KeyboardEvent event, boolean briefAnnouncement)
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

    private boolean onArrowDown(KeyboardEvent event, boolean quickNav)
    {
	if (noContentCheck())
	    return true;
	if (!iterator.moveNext())
	{
	    environment.hint(Hints.NO_LINES_BELOW);
	    return true;
	}
	if (quickNav)
	    while(iterator.canMoveNext() && iterator.isTitleRow())
		iterator.moveNext();
	onNewHotPointY( quickNav);
	return true;
    }

    private boolean onArrowUp(KeyboardEvent event, boolean briefAnnouncement)
    {
	if (noContentCheck())
	    return true;
	if (!iterator.movePrev())
	{
	    environment.hint(Hints.NO_LINES_ABOVE);
	    return true;
	}
	onNewHotPointY( briefAnnouncement);
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

    protected boolean onPageDown(KeyboardEvent event, boolean quickNav)
    {
	if (noContentCheck())
	    return true;
	if (!quickNav)
	{
	    final Node current = iterator.getParaContainer();//Will be null, if is at title row
	    while(iterator.canMoveNext() &&
		  (iterator.getNode().getType() != Node.Type.SECTION  ||
		   iterator.getNode() == current))
		iterator.moveNext();
	    if (iterator.getNode().getType() != Node.Type.SECTION)
	    {
		environment.hint(Hints.NO_LINES_BELOW);
		return true;
	    }
	} else
	{
	    //FIXME:
	    return false;
	}
	onNewHotPointY( quickNav, quickNav);
	return true;
    }

    protected boolean onPageUp(KeyboardEvent event, boolean quickNav)
    {
	if (noContentCheck())
	    return true;
	if (!quickNav)
	{
	    final Node current = iterator.getParaContainer();//Will be null, if is at title row
	    while(iterator.canMovePrev() &&
		  (iterator.getNode().getType() != Node.Type.SECTION  ||
		   iterator.getNode() == current))
		iterator.movePrev();
	    if (iterator.getNode().getType() != Node.Type.SECTION)
	    {
		environment.hint(Hints.NO_LINES_ABOVE);
		return true;
	    }
	} else
	{
	    //FIXME:
	    return false;
	}
	onNewHotPointY( quickNav, quickNav);
	return true;
    }


    private boolean onRightSquareBracket(KeyboardEvent event)
    {
	if (noContentCheck())
	    return true;
	if (!iterator.moveNext())
	{
	    environment.hint(Hints.NO_LINES_BELOW);
	    return true;
	}
	while(!iterator.isFirstRow() && iterator.moveNext());
	onNewHotPointY(false);
	return true;
    }

    private boolean onLeftSquareBracket(KeyboardEvent event)
    {
	if (noContentCheck())
	    return true;
	if (!iterator.movePrev())
	{
	    environment.hint(Hints.NO_LINES_ABOVE);
	    return true;
	}
	while(!iterator.isFirstRow() && iterator.movePrev());
	onNewHotPointY( false);
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

    protected boolean onSpace(KeyboardEvent event)
    {
	/*
	if (noContentCheck())
	    return true;
	int pos = iterator.findNextHref(hotPointX);
	if (pos >= 0)
	{
	    hotPointX = pos;
	    environment.say(getHrefText());
	    environment.onAreaNewHotPoint(this);
	    return true;
	}
	while (iterator.moveNext())
	{
	    if (iterator.hasHrefUnderPos(0))
	    {
		hotPointX = 0;
		environment.say(getHrefText());
		environment.onAreaNewHotPoint(this);
		return true;
	    }
	    pos = iterator.findNextHref(0);
	    if (pos >= 0)
	    {
		hotPointX = pos;
		environment.say(getHrefText());
		environment.onAreaNewHotPoint(this);
		return true;
	    }
	}
	*/
	return false;
    }

    private void onNewHotPointY(boolean briefAnnouncement, boolean alwaysSpeakTitleText)
    {
	onNewHotPointY(briefAnnouncement);
    }

    private void onNewHotPointY(boolean briefAnnouncement)
    {
	hotPointX = 0;
	if (iterator.isEmptyRow())
	    environment.hint(Hints.EMPTY_LINE); else
	    announceRow(iterator, briefAnnouncement);
	environment.onAreaNewHotPoint(this);
    }

    protected void announceRow(Iterator it, boolean briefAnnouncement)
    {
	NullCheck.notNull(it, "it");
	if (it.isEmptyRow())
	{
	    environment.hint(Hints.EMPTY_LINE);
	    return;
	}
	if (it.isTitleRow())
	{
	    final Node node = it.getTitleParentNode();
	    if (node instanceof Table)
	    {
		final Table table = (Table)node;
		environment.say("Table, " + table.getRowCount() + " rows, " + table.getColCount() + " columns, level " + table.getTableLevel());
	    } else

	    if (node instanceof TableCell)
	    {
		final TableCell cell = (TableCell)node;
		environment.say("Cell, row " + cell.getRowIndex() + ", column " + cell.getColIndex());
	    } else



	    environment.say(node.getClass().getName());
	    return;
	}
	    environment.say(it.getText());
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

    public String[] getHtmlIds()
    {
	if (isEmpty() || iterator.isEmptyRow())
	    return new String[0];
	final LinkedList<String> res = new LinkedList<String>();
	final Run run = iterator.getRunUnderPos(hotPointX);
	if (run == null)
	    return new String[0];
	ExtraInfo info = run.extraInfo();
	while (info != null)
	{
	    if (info.attrs.containsKey("id"))
	    {
		final String value = info.attrs.get("id");
		if (!value.isEmpty())
		    res.add(value);
	    }
	    info = info.parent;
	}
	return res.toArray(new String[res.size()]);

    }

    //Iterator may return true on isEmptyRow() even if this method returns false
    public boolean isEmpty()
    {
	if (document ==null || iterator == null)
	    return true;
	return iterator.noContent();
    }

    protected String noContentStr()
    {
	return environment.getStaticStr("DocumentNoContent");
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

    static private int endOfSentence(String text, int startFrom)
    {
	for(int i = startFrom;i < text.length();++i)
	    if (text.charAt(i) == '.' ||
		text.charAt(i) == '?' ||
		text.charAt(i) == '!')
		return i;
	return -1;
    }

    static private int findNextSentence(Iterator it, int pos)
    {
	int start = pos;
	do {
	    final String text = it.getText();
	    int i = start;
	    start = 0;
	    while (i < text.length() &&(
					text.charAt(i) == '.' || text.charAt(i) == '?' || text.charAt(i) == '!'))
		++i;
	    if (i < text.length())
		return i;
	} while(it.moveNext());
	return pos;
    }
}

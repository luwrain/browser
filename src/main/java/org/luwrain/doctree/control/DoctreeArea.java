
package org.luwrain.doctree.control;

import java.util.LinkedList;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.core.queries.*;
import org.luwrain.controls.*;
import org.luwrain.util.WordIterator;
import org.luwrain.doctree.*;
import org.luwrain.doctree.view.*;

public class DoctreeArea implements Area
{
    protected final ControlEnvironment environment;
    protected final RegionTranslator region = new RegionTranslator(new LinesRegionProvider(this));
    private String areaName = null;//FIXME:No corresponding constructor;
    protected final Announcement announcement;

    protected Document document;
    protected View view = null;
    protected org.luwrain.doctree.view.Iterator iterator;
    protected int hotPointX = 0;

    public DoctreeArea(ControlEnvironment environment, Announcement announcement)
    {
	NullCheck.notNull(environment, "environment");
	NullCheck.notNull(announcement, "announcement");
	this.environment = environment;
	this.announcement = announcement;
	this.document = null;
	    this.iterator = null;
    }

    public DoctreeArea(ControlEnvironment environment, Announcement announcement,
		       Document document, int width)
    {
	NullCheck.notNull(environment, "environment");
	NullCheck.notNull(announcement, "announcement");
	this.environment = environment;
	this.announcement = announcement;
	this.document = null;
	    this.iterator = null;
	    if (document != null)
		setDocument(document, width);
    }

    public void setDocument(Document document, int width)
    {
	NullCheck.notNull(document, "document");
	this.document = document;
	this.view = new View(document);
	this.view.build(width);
	int defaultIndex = -1;
	if (!document.getProperty(Document.DEFAULT_ITERATOR_INDEX_PROPERTY).isEmpty())
	    try {
		defaultIndex = Integer.parseInt(document.getProperty("defaultiteratorindex"));
	    }
	    catch (NumberFormatException e)
	    {
	    }
	if (defaultIndex >= 0)
	{
	    try {
		iterator = view.getIterator(defaultIndex);
	    }
	    catch(IllegalArgumentException e)
	    {
		iterator = view.getIterator();
	    }
	} else
	    iterator = view.getIterator();
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
	final Iterator newIt = view.getIterator();
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
	return !isEmpty()?iterator.getIndex():-1;
    }

    public boolean setCurrentRowIndex(int index)
    {
	if (isEmpty())
		      return false;
	final Iterator newIt;
	try {
	    newIt = view.getIterator(index);
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

    public boolean rebuildView(int width)
    {
	if (isEmpty())
	    return false;
	final Run currentRun = getCurrentRun();
	view = new View(document);
	view.build(width);
	if (currentRun != null)
	    findRun(currentRun);
	hotPointX = Math.min(hotPointX, iterator.getText().length());
	environment.onAreaNewContent(this);
	environment.onAreaNewHotPoint(this);
	return true;
    }

    @Override public int getLineCount()
    {
	return !isEmpty()?view.getLineCount() + 1:1;
    }

    @Override public String getLine(int index)
    {
	if (isEmpty())
	    return index == 0?noContentStr():"";
	return index < view.getLineCount()?view.getLine(index):"";
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
	case LISTENING_FINISHED:
	    return onListeningFinishedEvent((ListeningFinishedEvent)event);
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
	case AreaQuery.OBJECT_UNIREF:
	    if (isEmpty() || document.getUrl() == null)
		return false;
	    ((ObjectUniRefQuery)query).answer("url:" + document.getUrl().toString());
	    return true;
	case AreaQuery.BEGIN_LISTENING:
	    return onBeginListeningQuery((BeginListeningQuery)query);
	default:
	return region.onAreaQuery(query, getHotPointX(), getHotPointY());
	}
    }

    @Override public int getHotPointX()
    {
	if (isEmpty())
	    return 0;
	return iterator.getX() + hotPointX;
    }

    @Override public int getHotPointY()
    {
	if (isEmpty())
	    return 0;
	return iterator.getY();
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

protected boolean onBeginListeningQuery(BeginListeningQuery query)
    {
	NullCheck.notNull(query, "query");
	if (isEmpty())
	    return false;
	//Checking if there is the end of sentence on the current row
	String text = iterator.getText();
	int pos = findEndOfSentence(text, hotPointX);
	if (pos >= hotPointX)
	{
	    while (pos < text.length() && charOfSentenceEnd(text.charAt(pos)))
		++pos;
	    query.answer(new BeginListeningQuery.Answer(text.substring(hotPointX, pos), new ListeningInfo(iterator, pos)));
	    return true;
	}
	final Iterator newIt = (Iterator)iterator.clone();
	final Node origNode = newIt.getNode();
	if (origNode == null)
	    return false;
	final StringBuilder b = new StringBuilder();
	b.append(text.substring(hotPointX));
	Iterator lastIt = (Iterator)newIt.clone();
	//Continuing until the end of the entire document or the end of the node.
	// In second case we must have what to speak right now anyway, otherwise going on.
	while(newIt.moveNext() &&
	      (b.length() == 0 || newIt.getNode() == origNode))
	{
	    text = newIt.getText();
pos = findEndOfSentence(text, 0);
if (pos < 0)
{
    b.append(" " + text);
    lastIt = (Iterator)newIt.clone();
    continue;
}
//Yes, the end of sentence on the current row
while (pos < text.length() && charOfSentenceEnd(text.charAt(pos)))
    ++pos;
b.append(" " + text.substring(0, pos));
query.answer(new BeginListeningQuery.Answer(new String(b), new ListeningInfo(newIt, pos)));
return true;
	}
	if (b.length() <= 0)//No text to listen at all
	    return false;
	query.answer(new BeginListeningQuery.Answer(new String(b), new ListeningInfo(lastIt, lastIt.getText().length())));
	return true;
    }

    protected boolean onListeningFinishedEvent(ListeningFinishedEvent event)
    {
	NullCheck.notNull(event, "event");
	if (!(event.getExtraInfo() instanceof ListeningInfo))
	    return false;
	final ListeningInfo info = (ListeningInfo)event.getExtraInfo();
	iterator = info.it;
	hotPointX = info.pos;
	environment.onAreaNewHotPoint(this);
	return true;
    }

protected boolean onMoveHotPoint(MoveHotPointEvent event)
    {
	NullCheck.notNull(event, "event");
	if (isEmpty())
	    return false;
	final Iterator it2 = view.getIterator();
	final int x = event.getNewHotPointX();
	final int y = event.getNewHotPointY();
	if (x < 0 || y < 0)
	    return false;
	Iterator nearest = null;
	while (it2.canMoveNext() && !it2.coversPos(x, y))
	{
	    if (it2.getY() == y)
		nearest = (Iterator)it2.clone();
	    it2.moveNext();
	}
	if (it2.coversPos(x, y) &&
	    x >= it2.getX())
	{
	iterator = it2;
	hotPointX = x - iterator.getX();
	environment.onAreaNewHotPoint(this);
	return true;
	}
	if (event.precisely())
	    return false;
	if (nearest != null)
	{
	    iterator = nearest;
	    hotPointX = 0;
	    return true;
	}
	return false;
    }

    protected boolean onTab(KeyboardEvent event, boolean briefAnnouncement)
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

    protected boolean onArrowDown(KeyboardEvent event, boolean quickNav)
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

    protected boolean onArrowUp(KeyboardEvent event, boolean briefAnnouncement)
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

    protected boolean onAltEnd(KeyboardEvent event)
    {
	if (noContentCheck())
	    return true;
	iterator.moveEnd();
	onNewHotPointY( false);
	return true;
    }

    protected boolean onAltHome(KeyboardEvent event)
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

protected boolean onRightSquareBracket(KeyboardEvent event)
    {
	if (noContentCheck())
	    return true;
	if (!iterator.moveNext())
	{
	    environment.hint(Hints.NO_LINES_BELOW);
	    return true;
	}
	while(!iterator.isParagraphBeginning() && iterator.moveNext());
	onNewHotPointY(false);
	return true;
    }

    protected boolean onLeftSquareBracket(KeyboardEvent event)
    {
	if (noContentCheck())
	    return true;
	if (!iterator.movePrev())
	{
	    environment.hint(Hints.NO_LINES_ABOVE);
	    return true;
	}
	while(!iterator.isParagraphBeginning() && iterator.movePrev());
	onNewHotPointY( false);
	return true;
    }

    protected boolean onArrowLeft(KeyboardEvent event)
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

    protected boolean onArrowRight(KeyboardEvent event)
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

    protected boolean onAltLeft(KeyboardEvent event)
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

    protected boolean onAltRight(KeyboardEvent event)
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

    protected boolean onHome(KeyboardEvent event)
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

    protected boolean onEnd(KeyboardEvent event)
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

    protected void onNewHotPointY(boolean briefAnnouncement, boolean alwaysSpeakTitleText)
    {
	onNewHotPointY(briefAnnouncement);
    }

    protected void onNewHotPointY(boolean briefAnnouncement)
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
	announcement.announce(it, briefAnnouncement);
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

    static protected int findEndOfSentence(String text, int startFrom)
    {
	NullCheck.notNull(text, "text");
	for(int i = startFrom;i < text.length();++i)
	    if (charOfSentenceEnd(text.charAt(i)))
		return i;
	return -1;
    }

    static protected boolean charOfSentenceEnd(char ch)
    {
	switch(ch)
	{
	case '.':
	case '!':
	case '?':
	    return true;
	default:
	    return false;
	}
    }

    static protected class ListeningInfo
    {
	final Iterator it;
	final int pos;

	ListeningInfo(Iterator it, int pos)
	{
	    NullCheck.notNull(it, "it");
	    this.it = it;
	    this.pos = pos;
	}
    }
}

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

package org.luwrain.util;

import java.util.*;
import java.io.*;
import java.net.URL;

import org.luwrain.core.NullCheck;

public class MlReader
{
    static private final int MAX_ENTITY_LEN = 10;
    static private final String ENTITIES_RESOURCE = "org/luwrain/util/ml-entities.properties";

    private MlReaderConfig config;
    private MlReaderListener listener;
    private Properties entities = null;

    private String text = "";
    private int pos;

    private final LinkedList<String> openedTagStack = new LinkedList<String>();

    public MlReader(MlReaderConfig config, MlReaderListener listener,
		    String text)
    {
	this.config = config;
	this.listener = listener;
	this.text = text;
	NullCheck.notNull(config, "config");
	NullCheck.notNull(listener, "listener");
	NullCheck.notNull(text, "text");
    }

    public void read()
    {
	pos = 0;
	while (pos < text.length())
	{
	    final char c = text.charAt(pos);
	    int newPos = expecting(pos, "<![cdata[");
	    if (newPos > pos)
	    {
		pos = newPos;
		onCdata();
		continue;
	    }

	    newPos = expecting(pos, "<!doctype");
	    if (newPos > pos)
	    {
		pos = newPos;
		onDoctype();
		continue;
	    }

	    newPos = expecting(pos, "<!--");
	    if (newPos > pos)
	    {
		pos = newPos;
		onComments();
		continue;
	    }

	    if (text.charAt(pos) == '<')
	    {
		if (onOpenTag())
		    continue;
		if (!openedTagStack.isEmpty() && onClosingTag())
		    continue;
	    }

	    if (c == '&')
	    {
		++pos;
		onEntity();
		continue;
	    }

	    onText();
	} //while();
    }

    public boolean isTagOpened(String tag)
    {
	final String adjusted = tag.toLowerCase().trim();
	return openedTagStack.contains(adjusted);
    }

    private boolean  onOpenTag()
    {
	try {
	    StringIterator it = new StringIterator(text, pos);
	    it.moveNext();
	    it.skipBlank();
	    final String tagName = it.getUntilBlankOr(">/").toLowerCase();
	    if (!config.mlAdmissibleTag(tagName, openedTagStack))
		return false;
	    it.skipBlank();
	    if (it.currentChar() == '/' || it.currentChar() == '>')
	    {
		//No attributes
		if (it.currentChar() == '>')
		{
		    performAutoClosing(tagName);
		    if (config.mlTagMustBeClosed(tagName))
			openedTagStack.add(tagName);
		    listener.onMlTagOpen(tagName, null);
		    pos = it.pos() + 1;
		    return true;
		}
		if (it.isStringHere("/>"))
		{
		    performAutoClosing(tagName);
		    listener.onMlTagOpen(tagName, null);
		    pos = it.pos() + 2;
		    return true;
		}
		return false;
	    } //No attributes;
	    TreeMap<String, String> attr = new TreeMap<String, String>();
	    while(it.currentChar() != '>' && it.currentChar() != '/')
	    {
		if (!onOpenTagAttr(it, attr))
		    return false;
		it.skipBlank();
	    } //No attributes;
	    if (it.currentChar() == '>')
	    {
		performAutoClosing(tagName);
		if (config.mlTagMustBeClosed(tagName))
		    openedTagStack.add(tagName);
		listener.onMlTagOpen(tagName, attr);
		pos = it.pos() + 1;
		return true;
	    }
	    if (it.isStringHere("/>"))
	    {
		performAutoClosing(tagName);
		listener.onMlTagOpen(tagName, attr);
		pos = it.pos() + 2;
		return true;
	    }
	    return false;
	}
	catch(StringIterator.OutOfBoundsException e)
	{
	    return false;
	}
    }

    private void performAutoClosing(String newTagName)
    {
	while(!openedTagStack.isEmpty() && listener.isMlAutoClosingNeededOnTagOpen(newTagName, openedTagStack))
	{
	    final String lastTag = openedTagStack.pollLast();
	    listener.onMlTagClose(lastTag);
	}
    }

    private boolean onOpenTagAttr(StringIterator it, TreeMap<String, String> attr) throws StringIterator.OutOfBoundsException
    {
	final String attrName = it.getUntilBlankOr("=>/");
	it.skipBlank();
	if (it.currentChar() != '=')
	{
	    attr.put(attrName, "");
	    return true;
	}
	it.moveNext();
	it.skipBlank();
	String value = "";
	while (!it.isCurrentBlank() &&
	       it.currentChar() != '/' && 
	       it.currentChar() != '>')
	{
	    if (it.currentChar() == '\'')
	    {
		it.moveNext();
		value += it.getUntil("\'");
		it.moveNext();
		continue;
	    }
	    if (it.currentChar() == '\"')
	    {
		it.moveNext();
		value += it.getUntil("\"");
		it.moveNext();
		continue;
	    }
	    value += it.getUntilBlankOr(">/\'\"");
	}
	//FIXME:entity processing;
	attr.put(attrName, value);
	return true;
    }

    private boolean onClosingTag()
    {
	if (openedTagStack.isEmpty())
	    return false;
	int newPos = expecting(pos, constructClosingTag(openedTagStack.getLast()));
	if (newPos > pos)
	{
	    listener.onMlTagClose(openedTagStack.pollLast());
	    pos = newPos;
	    return true;
	}
	if (openedTagStack.size() < 2)
	    return false;
	//Trying anticipatory tags closing (usually for </p>, </li>, etc)
	final LinkedList<String> tagsToClose = new LinkedList<String>();
	final Iterator it = openedTagStack.descendingIterator();
	if (!it.hasNext())
	    return false;
	tagsToClose.add((String)it.next());
	while (it.hasNext())
	{
	    final String tagName = (String)it.next();
	    newPos = expecting(pos, constructClosingTag(tagName));
	    if (newPos <= pos)
	    {
		tagsToClose.addFirst(tagName);
		continue;
	    }
	    if (!listener.mayMlAnticipatoryTagClose(tagName, tagsToClose, openedTagStack))
	    {
		tagsToClose.addFirst(tagName);
		continue;
	    }
	    tagsToClose.addFirst(tagName);
	    for(int i = 0;i < tagsToClose.size();++i)
	    {
		final String removed = 		openedTagStack.pollLast();
		listener.onMlTagClose(removed);
	    }
	    pos = newPos;
	    return true;
	}
	return false;
    }

    private String constructClosingTag(String tagName)
    {
	return "</" + tagName + ">";
    }

    private void onCdata()
    {
	if (!validState())
	    return;
	final StringBuilder value = new StringBuilder();
	while (pos < text.length())
	{
	    if (text.charAt(pos) == ']')
	    {
		final int newPos = expecting(pos, "]]>");
		if (newPos > pos)
		{
		    listener.onMlText(value.toString(), openedTagStack);
		    pos = newPos;
		    return;
		}
	    }
	    value.append(text.charAt(pos++));
	}
	listener.onMlText(value.toString(), openedTagStack);
    }

    private void onComments()
    {
	while (pos < text.length())
	{
	    if (text.charAt(pos) == '-')
	    {
		final int newPos = expecting(pos, "-->");
		if (newPos > pos)
		{
		    pos = newPos;
		    return;
		}
	    }
	    ++pos;
	}
    }

    private void onDoctype()
    {
	while (pos < text.length() && text.charAt(pos) != '>')
	    ++pos;
	if (pos + 1 < text.length())
	    ++pos;
    }

    private void onEntity()
    {
	if (!validState())
	    return;
	int ending = pos;
	while (ending < text.length() && ending - pos <= MAX_ENTITY_LEN &&
	       text.charAt(ending) != ';')
	    ++ending;
	if (ending > text.length() || text.charAt(ending) != ';')
	    return;
	parseEntity(text.substring(pos, ending));
	pos = ending + 1;
    }

    private void onText()
    {
	final int oldPos = pos;
	++pos;
	String res = "";
	while (pos < text.length())
	{
	    final char current = text.charAt(pos);
	    if (current == '<')
		break;
	    if (current == '&')
		break;
	    ++pos;
	}
	listener.onMlText(text.substring(oldPos, pos), openedTagStack);
    }

    /**
     * Checks if a substring presents at the specified position.
     *
     * @param posFrom The position to start checking from
     * @param substr A substring to check
     * @return The position immediately after the encountered substring
     */
    private int expecting(int posFrom, String substr)
    {
	if (substr.isEmpty())
	    throw new IllegalArgumentException("substr may not be empty");
	int posInText = posFrom;
	for(int i = 0;i < substr.length();++i)
	{
	    final char c = substr.charAt(i);
	    //Skipping all spaces if there are any
	    while (posInText < text.length() && StringIterator.blankChar(text.charAt(posInText)))
		++posInText;
	    if (posInText >= text.length())
		return posFrom;
	    if (Character.toLowerCase(text.charAt(posInText)) != Character.toLowerCase(c))
		return posFrom;
	    ++posInText;
	}
	return posInText;
    }

    private void parseEntity(String entity)
    {
	final String name = entity.trim().toLowerCase();
	if (name.isEmpty())
	    return;
	if (name.charAt(0) == '#')
	{
	    parseNumEntity(name);
	    return;
	}
	listener.onMlText("" + (char)getCodeOfEntity(name), openedTagStack);
    }

    private void parseNumEntity(String name)
    {
	if (name.length() < 2)
	    return;
	if (name.charAt(1) == 'x')
	{
	    parseHexEntity(name.substring(1));
	    return;
	}
	int value;
	try {
	    value = Integer.parseInt(name.substring(1));
	}
	catch(NumberFormatException ee)
	{
	    listener.onMlText("&" + name + ";", openedTagStack);
	    return;
	}
	listener.onMlText("" + (char)value, openedTagStack);
    } 

    private void parseHexEntity(String name)
    {
	//FIXME:
	listener.onMlText("&#" + name + ";", openedTagStack);
    }

    private int getCodeOfEntity(String name)
    {
	if (!isEntitiesReady())
	    return 32;
	final String value = entities.getProperty("entity." + name);
	if (value == null)
	    return 32;
	try {
	    return Integer.parseInt(value);
	}
	catch (NumberFormatException e)
	{
	    e.printStackTrace();
	    return 32;
	}
    }

    private boolean isEntitiesReady()
    {
	if (entities != null)
	    return true;
	final URL url = ClassLoader.getSystemResource(ENTITIES_RESOURCE);
	if (url == null)
	    return false;
	entities = new Properties();
	try {
	    entities.load(url.openStream());
	}
	catch (IOException e)
	{
	    e.printStackTrace();
	    entities = null;
	    return false;
	}
	return true;
    }

    private boolean validState()
    {
	return pos < text.length();
    }
}

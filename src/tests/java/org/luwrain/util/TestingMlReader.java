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

class TestingMlReader
{
    static private final String[] nonClosingTags = new String[]{
	"!doctype",
	"input",
	"br",
	"hr",
	"link",
	"img",
	"meta"
    }; 

    static private class Strip implements MlReaderConfig, MlReaderListener
    {
	final StringBuilder builder = new StringBuilder();

	@Override public boolean mlTagMustBeClosed(String tag)
	{
	    final String adjusted = tag.toLowerCase().trim();
	    for(String s: nonClosingTags)
		if (s.equals(adjusted))
		    return false;
	    return true;
	}

	@Override public boolean mlAdmissibleTag(String tagName, LinkedList<String> tagsStack)
	{
	    //May not open a tag inside of a script
	    if (!tagsStack.isEmpty() &&tagsStack.getLast().toLowerCase().trim().equals("script"))
		return false;
	    final String adjusted = tagName.toLowerCase().trim();
	    for(int i = 0;i < adjusted.length();++i)
	    {
		final char c = adjusted.charAt(i);
		if (!Character.isLetter(c) && !Character.isDigit(c) &&
		    c != '_' && c != '-')
		    return false;
	    }
	    return true;
	}

	@Override public void onMlTagOpen(String tagName, Map<String, String> attrs)
	{
	}

	@Override public void onMlText(String text, LinkedList<String> tagsStack)
	{
	    builder.append(text);
	}

	@Override public void onMlTagClose(String tagName)
	{
	}

	@Override public boolean isMlAutoClosingNeededOnTagOpen(String newTagName, LinkedList<String> tagsStack)
	{
	    return false;
	}

    @Override public boolean mayMlAnticipatoryTagClose(String tagName,
						       LinkedList<String> anticipatoryTags, LinkedList<String> tagsStack)
	{
	    return false;
	}

	@Override public String toString()
	{
	    return builder.toString();
	}
    }

    final Strip s = new Strip();

    void run(String text)
    {
	new MlReader(s, s, text).read();
    }

    String result()
    {
	return s.toString();
    }
}

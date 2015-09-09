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

package org.luwrain.doctree.filters;

import java.util.*;
import org.luwrain.util.*;

class HtmlStructListener implements MlReaderListener
{
    @Override public void onMlTagOpen(String tagName, Map<String, String> attrs)
    {
	System.out.println("* OPEN " + tagName);
    }

    @Override public void onMlTagClose(String tagName)
    {
	System.out.println("* CLOSE " + tagName);
    }

    @Override public boolean isMlAutoClosingNeededOnTagOpen(String newTagName, LinkedList<String> tagsStack)
    {
	if (tagsStack.isEmpty())//Actually, never happens
	    return false;
	final String adjusted1 = newTagName.toLowerCase().trim();
	final String adjusted2 = tagsStack.getLast().toLowerCase().trim();
	if (adjusted1.equals("p") && adjusted2.equals("p"))
	    return true;
	if (adjusted1.equals("li") && adjusted2.equals("li"))
	    return true;
	return false;
    }

    @Override public boolean mayMlAnticipatoryTagClose(String tagName,
						   LinkedList<String> anticipatoryTags, LinkedList<String> tagsStack)
    {
	System.out.println("anticipatory: " + tagName + ", " + anticipatoryTags);
	if (anticipatoryTags.size() != 1)
	    return false;
	final String adjusted1 = tagName.toLowerCase().trim();
	final String adjusted2 = anticipatoryTags.getLast().toLowerCase().trim();
	//	System.out.println("anticipatory: " + adjusted1 + ", " + adjusted2);
	if (adjusted2.equals("p"))
	    return true;
	if (adjusted1.equals("ul") && adjusted2.equals("li"))
	    return true;
	if (adjusted1.equals("ol") && adjusted2.equals("li"))
	    return true;
	return false;
    }

    @Override public void onMlText(String text, LinkedList<String> tagsStack)
    {
	final String trimmed = text.trim();
	if (trimmed.isEmpty())
	    return;
	System.out.println(trimmed);
    }
}

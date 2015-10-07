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
	return HtmlParse.tagsAutoClosingPolicy(newTagName, tagsStack);
    }

    @Override public boolean mayMlAnticipatoryTagClose(String tagName,
						   LinkedList<String> anticipatoryTags, LinkedList<String> tagsStack)
    {
	final boolean res = HtmlParse.anticipatoryTagsPolicy(tagName, anticipatoryTags, tagsStack);
	if (res)
	System.out.println("Accepted anticipatory: " + tagName + ", " + anticipatoryTags); else
	System.out.println("Rejected anticipatory: " + tagName + ", " + anticipatoryTags);
	return res;
    }

    @Override public void onMlText(String text, LinkedList<String> tagsStack)
    {
	final String adjusted = text.replaceAll("\\n", " ").trim();
	if (!adjusted.isEmpty())
	    System.out.println(adjusted);
    }
}

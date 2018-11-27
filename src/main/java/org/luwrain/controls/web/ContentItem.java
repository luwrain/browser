/*
   Copyright 2012-2018 Michael Pozhidaev <michael.pozhidaev@gmail.com>
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

package org.luwrain.controls.web;

import java.util.*;

import  org.luwrain.core.*;
import org.luwrain.browser.*;

final class ContentItem
{
    final BrowserIterator it;
    final String className;
    final String tagName;
    final String text;
    final ContentItem[] children;

    ContentItem(BrowserIterator it, ContentItem[] children)
    {
	NullCheck.notNull(it, "it");
	NullCheck.notNullItems(children, "children");
	this.it = it;
	this.className = it.getClassName();
	this.tagName = it.getTagName();
	this.text = it.getText();
	this.children = children;
    }

    String getText()
    {
	if (className.equals("Text"))
	    return text;
	final StringBuilder b = new StringBuilder();
	for(ContentItem i: children)
	    b.append(i.getText());
	return new String(b);
    }
}

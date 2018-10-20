/*
   Copyright 2012-2018 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

package org.luwrain.app.webinspector;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.browser.*;

final class Item
{
    final BrowserIterator it;
    final String tagName;
    final String text;

    Item(BrowserIterator it)
    {
	NullCheck.notNull(it, "it");
	this.it = it.clone();
	this.tagName = it.getHtmlTagName();
	this.text = it.getText();
    }

    @Override public String toString()
    {
	final StringBuilder b = new StringBuilder();
	b.append(tagName != null?tagName:"null").append(" ").append(text != null?text:"null");
	return new String(b);
	}
}

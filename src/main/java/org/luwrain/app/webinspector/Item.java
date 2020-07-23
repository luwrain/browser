/*
   Copyright 2012-2020 Michael Pozhidaev <msp@luwrain.org>
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

package org.luwrain.app.webinspector;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.browser.*;

final class Item
{
    final BrowserIterator it;
    final String className;
    final String inputType;
    final String tagName;
    final String text;

    Item(BrowserIterator it)
    {
	NullCheck.notNull(it, "it");
	this.it = it.clone();
	this.className = it.getClassName();
	if (it.isInput())
	    this.inputType = it.getInputType(); else
	    this.inputType = "";
	this.tagName = it.getTagName();
	this.text = it.getText();
    }

    @Override public String toString()
    {
	String className = this.className;
	if (className.startsWith("HTML"))
	    className = className.substring(4);
	if (className.endsWith("Impl"))
	    className = className.substring(0, className.length() - 4);
	final StringBuilder b = new StringBuilder();
	b.append(className);
	if (!inputType.isEmpty())
	    b.append("(").append(inputType).append(")");
	b.append(" ").append(text != null?text:"null");
	return new String(b);
    }
}

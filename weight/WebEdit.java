/*
   Copyright 2012-2017 Michael Pozhidaev <michael.pozhidaev@gmail.com>
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

package org.luwrain.browser.weight;

import org.luwrain.browser.*;

class WebEdit extends WebText
{
    WebEdit(WebElement parent, BrowserIterator element)
    {
	super(parent,element);
	super.needBeginLine = false;
	super.needEndLine = true;
    }

    @Override public Type getType()
    {
	return Type.Edit;
    }

    @Override public String getText()
    {
	return "[" + nodeIt.getText() + "]";
    }
}

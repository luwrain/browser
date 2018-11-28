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

import org.luwrain.core.*;

final class WebText extends WebObject
{
    final String text;
    final int posFrom;
    final int posTo;

    
    WebText(ContentItem contentItem, int posFrom, int posTo)
    {
	super(contentItem);
	this.text = contentItem.getText();
	this.posFrom = posFrom;
	this.posTo = posTo;
	if (posFrom < 0 || posTo < 0)
	    throw new IllegalArgumentException("posFrom (" + posFrom + ") and posTo (" + posTo + ") may not be negative");
	if (posFrom > posTo)
	    throw new IllegalArgumentException("posFrom (" + posFrom + ") must be equal or less than posTo (" + posTo + ")");
	if (posTo > text.length())
	    throw new IllegalArgumentException("posTo may not be greater than the lenth of the text (" + text.length() + ")");
    }

    String getText()
    {
	return text.substring(posFrom, posTo);
    }
}

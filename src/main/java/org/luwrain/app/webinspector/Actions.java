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

import java.net.*;
import java.util.Date;

import org.luwrain.core.*;
import org.luwrain.browser.*;

final class Actions
{
    private final Luwrain luwrain;
    private final Base base;
    private final Strings strings;
    final Conversations conv;

    Actions(Luwrain luwrain, Base base, Strings strings)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(base, "base");
	NullCheck.notNull(strings, "strings");
	this.luwrain = luwrain;
	this.base = base;
	this.conv = new Conversations(luwrain);
	this.strings = strings;
    }

    boolean onClick(Item item)
    {
	NullCheck.notNull(item, "item");
	if (item.className.equals("HTMLButtonElementImpl") ||
	    item.inputType.equals("submit"))
	{
	    base.browser.runSafely(()->{
		    item.it.emulateSubmit();
		    return null;
		});
	    return true;
	}
	if (item.inputType.equals("text") ||
	    item.inputType.equals("password") ||
	    item.inputType.equals("email"))
	{
	    final String text = conv.formText("");
	    if (text == null)
		return true;
	    base.browser.runSafely(()->{
		    item.it.setInputText(text);
		    base.updateItems();
		    return null;
		});
	    return true;
	}
	return false;
    }
}

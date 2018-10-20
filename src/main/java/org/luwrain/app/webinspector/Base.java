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
import java.util.concurrent.*;

import org.luwrain.core.*;
import org.luwrain.browser.*;
import org.luwrain.doctree.*;
import org.luwrain.controls.browser.*;

final class Base implements ClientThread
{
    static final String LOG_COMPONENT = "browser";

    private final Luwrain luwrain;
    final Browser browser;

    Base(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	this.luwrain = luwrain;
	this.browser = luwrain.createBrowser();
    }

    @Override public Object runSync(Callable callable)
    {
	//FIXME:
	return null;
    }

    @Override public void runAsync(Runnable runnable)
    {
	luwrain.runUiSafely(runnable);
    }
}

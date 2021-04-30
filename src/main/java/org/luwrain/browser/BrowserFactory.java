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

//LWR_API 1.0

package org.luwrain.browser;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

import org.luwrain.core.*;

public final class BrowserFactory
{
    static public final String GRAPHICAL_MODE_NAME = "browser";

    static public Browser newBrowser(Luwrain luwrain, BrowserEvents events)
    {
	NullCheck.notNull(events, "events");
	final BrowserParams params = new BrowserParams();
	final org.luwrain.settings.browser.Settings sett = org.luwrain.settings.browser.Settings.create(luwrain.getRegistry());
	params.userAgent = sett.getUserAgent(params.userAgent);
	params.javaScriptEnabled = sett.getJavaScriptEnabled(true);
	final File baseDir = luwrain.getAppDataDir("luwrain.browser").toFile();
	final UUID uuid = UUID.randomUUID();
	params.userDataDir = new File(baseDir, uuid.toString().replaceAll("-", ""));
	params.userDataDir.mkdir();
	params.events = events;
	return new Browser(params);
    }
}

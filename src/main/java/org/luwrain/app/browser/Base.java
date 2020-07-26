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

package org.luwrain.app.browser;

import java.net.*;
import java.util.concurrent.*;

import org.luwrain.core.*;
import org.luwrain.base.*;
import org.luwrain.browser.*;
import org.luwrain.controls.web.*;

class Base implements WebArea.ClientThread
{
    static final String LOG_COMPONENT = "web";

    private final Luwrain luwrain;
    final org.luwrain.settings.browser.Settings sett;
    final Browser browser;

    Base(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	this.luwrain = luwrain;
	this.sett = org.luwrain.settings.browser.Settings.create(luwrain.getRegistry());
	this.browser = (Browser)luwrain.openGraphicalMode("browser", new GraphicalMode.Params());
    }

    String makeHref(WebArea area, String href)
    {
	NullCheck.notNull(area, "area");
	NullCheck.notNull(href, "href");
	if (href.isEmpty())
	    return "";
	final String pageUrl = area.getUrl();
	if (pageUrl == null || pageUrl.isEmpty())
	    return href;
	    try {
			    URL urlObj = null;
urlObj = new URL(pageUrl);
urlObj = new URL(urlObj, href);
return urlObj.toString();
	    }
	    catch(MalformedURLException e)
	    {
		return href;
	    }
    }

    void closeApp()
    {
	browser.close();
	luwrain.closeApp();
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

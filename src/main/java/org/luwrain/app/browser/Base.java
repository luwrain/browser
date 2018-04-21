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

package org.luwrain.app.browser;

import java.net.*;
import java.util.concurrent.*;

import org.luwrain.core.*;
import org.luwrain.browser.*;
import org.luwrain.doctree.*;
import org.luwrain.controls.browser.*;

class Base implements DocumentBuilder, ClientThread
{
    static final String LOG_COMPONENT = "browser";

    private final Luwrain luwrain;
    final Settings sett;
    final Browser browser;

    Base(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	this.luwrain = luwrain;
	this.sett = Settings.create(luwrain.getRegistry());
	this.browser = luwrain.createBrowser();
    }

    String makeHref(BrowserArea area, String href)
    {
	NullCheck.notNull(area, "area");
	NullCheck.notNull(href, "href");
	if (href.isEmpty())
	    return "";
	final String pageUrl = area.getBrowserUrl();
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

    @Override public Document build(Browser browser)
    {
	NullCheck.notNull(browser, "browser");
	final Object res = this.browser.runSafely(()->{
		try {
		    return new org.luwrain.browser.docbuilder.DocumentBuilder(browser).build();
		}
		catch(Throwable e)
		{
		    Log.error(LOG_COMPONENT, "unable to construct a doctree document:" + e.getClass().getName() + ":" + e.getMessage());
		    return null;
		}
	    });
	if (res == null || !(res instanceof org.luwrain.doctree.Document))
	    return null;
	return (Document)res;
    }

    @Override public Object runSync(Callable callable)
    {
	//FIXME:
	return null;
    }

    @Override public void runAsync(Runnable runnable)
    {
	luwrain.runInMainThread(runnable);
    }
}

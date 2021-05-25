/*
   Copyright 2012-2021 Michael Pozhidaev <msp@luwrain.org>
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

import org.luwrain.core.*;
import org.luwrain.browser.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.web.*;
import org.luwrain.controls.*;
import org.luwrain.app.base.*;

final class MainLayout extends LayoutBase
{
    private final WebArea webArea;

    MainLayout(App app)
    {
	super(app);
	{
	    final WebArea.Params params = new WebArea.Params();
	    params.context = getControlContext();
	    params.appearance = new DefaultAppearance(params.context);
	    //FIXME:	params.clickHandler = (area,rowIndex,webObj)->actions.onClick(area, webObj, rowIndex);
	    params.browserFactory = (events)->{
		NullCheck.notNull(events, "events");
		return BrowserFactory.newBrowser(getLuwrain(), events);
	    };
	    params.callback = new Callback();
	    	params.clientThread = app;
	    webArea = new WebArea(params);
	}
	setAreaLayout(webArea, actions());
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



final class Callback implements org.luwrain.controls.web.WebArea.Callback
{
    private final Conversations conv = null;
    private final Browser browser = null;
    private final Strings strings = null;

    boolean onClick(WebArea area, WebObject webObj, int rowIndex)
    {
	NullCheck.notNull(area, "area");
	NullCheck.notNull(webObj, "webObj");
	if (webObj instanceof WebTextInput)
	{
	    final WebTextInput webTextInput = (WebTextInput)webObj;
	    final String value = conv.formTextEdit(webTextInput.getText());
	    if (value == null)
		return true;
	    browser.runSafely(()->{
		    webTextInput.setText(value);
		    return null;
		});
	    area.updateView(getLuwrain().getAreaVisibleWidth(area));
	    return true;
	}
	browser.runSafely(()->{
		webObj.getIterator().emulateClick();
		return null;
	    });
	return true;
    }

    boolean onOpenUrl(WebArea area)
    {
	NullCheck.notNull(area, "area");
	final String current = area.getUrl();
	final String addr = conv.openUrl(current != null?current:"");
	if (addr == null || addr.isEmpty())
	    return true;
	area.open(addr);
	return true;
    }

    @Override public void onBrowserRunning()
    {
	getLuwrain().speak(strings.loading());
    }

    @Override public void onBrowserSuccess(String title)
    {
	NullCheck.notNull(title, "title");
	if (!title.trim().isEmpty())
	    app.message(title, Luwrain.MessageType.DONE); else
	    getLuwrain().playSound(Sounds.CLICK);
    }

    @Override public void onBrowserFailed()
    {
	app.getLuwrain().message("Страница не может быть загружена", Luwrain.MessageType.ERROR);
    }

    @Override public void message(String text, MessageType type)
    {
	NullCheck.notNull(text, "text");
	NullCheck.notNull(type, "type");
	app.getLuwrain().message(text);
    }

    @Override public String prompt(String message, String value)
    {
	NullCheck.notNull(message, "message");
	NullCheck.notNull(value, "value");
	return "";
    }

    @Override public boolean confirm(String message)
    {
	NullCheck.notNull(message, "message");
	return false;
    }
}

}

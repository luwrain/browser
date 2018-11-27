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
import java.util.Date;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.core.queries.*;
import org.luwrain.doctree.*;
import org.luwrain.controls.web.*;
import org.luwrain.controls.doc.*;
import org.luwrain.browser.*;

import org.luwrain.popups.*;

class Actions implements org.luwrain.controls.web.WebArea.Callback
{
    static private final String UNIREF_TYPE = "url";//FIXME:change to web: in the future

    private final Luwrain luwrain;
    private final Base base;
    private final Settings sett;
    private final Browser browser;
    private final Strings strings;
    private final Conversations conv;

    Actions(Luwrain luwrain, Base base,
	    Settings sett, Browser browser, Strings strings)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(base, "base");
	NullCheck.notNull(sett, "sett");
	NullCheck.notNull(browser, "browser");
	NullCheck.notNull(strings, "strings");
	this.luwrain = luwrain;
	this.base = base;
	this.sett = sett;
	this.browser = browser;
	this.conv = new Conversations(luwrain, sett);
	this.strings = strings;
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

    boolean onCopyUrl(WebArea area)
    {
	NullCheck.notNull(area, "area");
	final String value = area.getUrl();
	if (value == null || value.trim().isEmpty())
	    return false;
	luwrain.message(value, Luwrain.MessageType.OK);
	return true;
    }

    boolean onCopyRef(WebArea area)
    {
	/*
	NullCheck.notNull(area, "area");
	final Run run = area.getCurrentRun();
	if (run == null || !(run instanceof  WebTextRun))
	    return false;
	final String value = run.href();
	if (value == null || value.isEmpty())
	    return false;
	final String res = base.makeHref(area, value);
	//FIXME:clipboard
	luwrain.message(res, Luwrain.MessageType.OK);
	return true;
	*/
	return false;
    }

    boolean onUniRefQuery(WebArea area, AreaQuery query)
    {
	NullCheck.notNull(area, "area");
	NullCheck.notNull(query, "query");
	if (!(query instanceof UniRefAreaQuery))
	    return false;
	final UniRefAreaQuery q = (UniRefAreaQuery)query;
	final String title = area.getTitle();
	final String url = area.getUrl();
	if (url == null || url.trim().isEmpty())
	    return false;
	if (title != null && !title.isEmpty())
	    q.answer("link:" + title.trim().replaceAll(":", "\\:") + ":" + UNIREF_TYPE + ":" + url); else
	    q.answer(UNIREF_TYPE + ":" + url);
	return true;
    }

    boolean onEnter(WebArea area)
    {
	/*
	NullCheck.notNull(area, "area");
	final Run run = area.getCurrentRun();
	if (run == null)
	    return false;
	if (run instanceof WebTextRun)
	    //	    run instanceof ButtonRun)
	{
	    final WebRun webTextRun = (WebRun)run;
	    browser.runSafely(()->{
	    webTextRun.click();
	    return null;
		});
	    luwrain.playSound(Sounds.INTRO_REGULAR);
	    return true;
	}

	//button
		if (run instanceof ButtonRun)
	{
	    final ButtonRun buttonRun = (ButtonRun)run;
	    browser.runSafely(()->{
		    buttonRun.submit();
	    return null;
		});
	    luwrain.playSound(Sounds.INTRO_REGULAR);
	    return true;
	}


	//edits
	if (run instanceof EditRun)
	{
	    final EditRun editRun = (EditRun)run;
	    final Object oldValue = browser.runSafely(()->{
		    return editRun.getText();
		});
	    final String newValue = conv.formTextEdit(oldValue != null?oldValue.toString():"");
	    if (newValue == null)
		return false;
	    browser.runSafely(()->{
		    editRun.setText(newValue);
		    return null;
		});
	    //FIXME:area.refresh();
	    return true;
	}
	*/
	//FIXME:
    /*
	final String[] items = el.getMultipleText();
	el.setText(res);
    */
	return false;
    }

    boolean onOk(WebArea area)
    {
	NullCheck.notNull(area, "area");
	/*
	final Run run = area.getCurrentRun();
	if (run == null || !(run instanceof WebRun))
	    return false;
	final WebRun webRun = (WebRun)run;
	browser.runSafely(()->{
		webRun.click();
		return null;
	    });
	return true;
	*/
	return false;
    }

    boolean onHistoryPrev(WebArea area)
    {
	NullCheck.notNull(area, "area");
	//FIXME:
	return true;
    }

    boolean onShowGraphical()
    {
	browser.setVisibility(!browser.getVisibility());
	return true;
    }

    @Override public void onBrowserRunning()
    {
	luwrain.say("Идёт загрузка страницы. Пожалуйста, подождите...");//FIXME:some kind message is better
    }

    @Override public void onBrowserSuccess(String title)
    {
	NullCheck.notNull(title, "title");
	if (!title.trim().isEmpty())
	    luwrain.message(title, Sounds.CLICK); else
	    luwrain.playSound(Sounds.CLICK);
    }

    @Override public void onBrowserFailed()
    {
	luwrain.message("Страница не может быть загружена", Luwrain.MessageType.ERROR);
    }

    @Override public void message(String text, MessageType type)
    {
	NullCheck.notNull(text, "text");
	NullCheck.notNull(type, "type");
	luwrain.message(text);
    }

    @Override public int getAreaVisibleWidth(Area area)
    {
	NullCheck.notNull(area, "area");
	return luwrain.getAreaVisibleWidth(area);
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
    

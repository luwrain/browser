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
import org.luwrain.core.events.*;
import org.luwrain.core.queries.*;
import org.luwrain.doctree.*;
import org.luwrain.controls.browser.*;
import org.luwrain.controls.doc.*;
import org.luwrain.browser.*;
import org.luwrain.browser.docbuilder.*;

import org.luwrain.popups.*;

final class Actions implements org.luwrain.controls.browser.Callback
{
    static private final String UNIREF_TYPE = "url";//FIXME:change to web: in the future

    private final Luwrain luwrain;
    private final Base base;
    private final Browser browser;
    private final Strings strings;
    private final Conversations conv;

    Actions(Luwrain luwrain, Base base,
Browser browser, Strings strings)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(base, "base");
	NullCheck.notNull(browser, "browser");
	NullCheck.notNull(strings, "strings");
	this.luwrain = luwrain;
	this.base = base;
	this.browser = browser;
	this.conv = new Conversations(luwrain);
	this.strings = strings;
    }

    boolean onOpenUrl(BrowserArea area)
    {
	NullCheck.notNull(area, "area");
	final String current = area.getBrowserUrl();
	final String addr = conv.openUrl(current != null?current:"");
	if (addr == null || addr.isEmpty())
	    return true;
	area.open(addr);
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
	if (title != null)
	    luwrain.message(title, Sounds.INTRO_REGULAR); else //FIXME:strings
	    luwrain.message("Готово"); //FIXME:strings
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
    

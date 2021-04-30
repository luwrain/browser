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

import org.luwrain.core.*;
import org.luwrain.browser.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.web.*;
import org.luwrain.controls.*;

final class App implements Application
{
    private Luwrain luwrain = null;
    private Strings strings = null;
    private Base base = null;
    private Actions actions = null;
    private ActionLists actionLists = null;
    private WebArea area = null;

    private final String arg;

    public App()
    {
	arg = null;
    }

    public App(String arg)
    {
	NullCheck.notNull(arg, "arg");
	this.arg = arg;
    }

    @Override public InitResult onLaunchApp(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	final Object o = luwrain.i18n().getStrings(Strings.NAME);
	if (o == null || !(o instanceof Strings))
	    return new InitResult(InitResult.Type.NO_STRINGS_OBJ, Strings.NAME);
	strings = (Strings)o;
	this.luwrain = luwrain;
	this.base = new Base(luwrain);
	this.actionLists = new ActionLists(luwrain, strings);
	this.actions = new Actions(luwrain, base, base.sett, base.browser, strings);
	createArea();
	if (arg != null && !arg.trim().isEmpty())
	    area.open(arg); else
	{
	    final String startingUrl = base.sett.getHomePage("");
	    if (!startingUrl.trim().isEmpty())
		area.open(startingUrl);
	}
	return new InitResult();
    }

    private void createArea()
    {
	//final org.luwrain.controls.doc.Strings announcementStrings = (org.luwrain.controls.doc.Strings)luwrain.i18n().getStrings("luwrain.doc");
	final WebArea.Params params = new WebArea.Params();
	params.context = new DefaultControlContext(luwrain);
	params.appearance = new DefaultAppearance(params.context);
	//FIXME:	params.clickHandler = (area,rowIndex,webObj)->actions.onClick(area, webObj, rowIndex);
	params.browserFactory = (events)->{
	    NullCheck.notNull(events, "events");
	    return null;//Browser.create(luwrain, events);
	};
	params.callback = actions;
	params.clientThread = base;
    	area = new WebArea(params){
		@Override public boolean onInputEvent(InputEvent event)
		{
		    NullCheck.notNull(event, "event");
		    NullCheck.notNull(event, "event");
		    if (event.isSpecial())
			switch(event.getSpecial())
			{
			default:
			    break;
			}
		    return super.onInputEvent(event);
		}
		@Override public boolean onSystemEvent(SystemEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (event.getType() != SystemEvent.Type.REGULAR)
			return super.onSystemEvent(event);
		    switch(event.getCode())
		    {
		    case ACTION:
			if (ActionEvent.isAction(event, "open-url"))
			    return actions.onOpenUrl(area);
			if (ActionEvent.isAction(event, "show-graphical"))
			{
			    base.browser.showGraphical();
			    return true;
			}
			if (ActionEvent.isAction(event, "history-prev"))
			    return area.goHistoryPrev();
			return false;
		    case CLOSE:
			closeApp();
			return true;
		    default:
			return super.onSystemEvent(event);
		    }
		}
		@Override public Action[] getAreaActions()
		{
		    return actionLists.getBrowserActions();
		}
	    };
    }

    @Override public void closeApp()
    {
	base.closeApp();
    }

    @Override public String getAppName()
    {
	return strings.appName();
    }

    @Override public AreaLayout getAreaLayout()
    {
	return new AreaLayout(area);
    }
}

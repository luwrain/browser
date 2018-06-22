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

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.browser.*;
import org.luwrain.controls.*;
import org.luwrain.controls.doctree.*;

class BrowserApp implements Application
{
    private Luwrain luwrain = null;
    private Strings strings = null;
    private Base base = null;
    private Actions actions = null;
    private ActionLists actionLists = null;
    private BrowserArea area = null;

    private final String arg;

    BrowserApp()
    {
	arg = null;
    }

    BrowserApp(String arg)
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
	final String startingUrl = base.sett.getHomePage("");
	if (!startingUrl.trim().isEmpty())
	    area.open(startingUrl.trim());
	return new InitResult();
    }

    private void createArea()
    {
	final org.luwrain.controls.doctree.Strings announcementStrings = (org.luwrain.controls.doctree.Strings)luwrain.i18n().getStrings("luwrain.doctree");
	final Announcement announcement = new Announcement(new DefaultControlEnvironment(luwrain), announcementStrings);

    	area = new BrowserArea(
			       new DefaultControlEnvironment(luwrain),
			       base.browser,
			       actions, 
			       base,
			       base,
			       announcement){

		@Override public boolean onKeyboardEvent(KeyboardEvent event)
		{
		    NullCheck.notNull(event, "event");
		    NullCheck.notNull(event, "event");
		    if (event.isSpecial())
			switch(event.getSpecial())
			{
			case ENTER:
			    return actions.onEnter(this);
			default:
			    break;
			}
		    return super.onKeyboardEvent(event);
		}

		@Override public boolean onSystemEvent(EnvironmentEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (event.getType() != EnvironmentEvent.Type.REGULAR)
			return super.onSystemEvent(event);
		    switch(event.getCode())
		    {
		    case ACTION:
			return onBrowserAction(event);
		    case CLOSE:
			closeApp();
			return true;
		    case OK:
			return actions.onOk(area);
		    default:
			return super.onSystemEvent(event);
		    }
		}

		@Override public boolean onAreaQuery(AreaQuery query)
		{
		    NullCheck.notNull(query, "query");
		    switch(query.getQueryCode())
		    {
					    case AreaQuery.OBJECT_UNIREF:
			return actions.onUniRefQuery(area, query);
		    default:
			return super.onAreaQuery(query);
		    }
		}

		@Override public Action[] getAreaActions()
		{
		    return actionLists.getBrowserActions();
		}
	    };
    }

    private boolean onBrowserAction(EnvironmentEvent event)
    {
	NullCheck.notNull(event, "event");
	if (ActionEvent.isAction(event, "open-url"))
	    return actions.onOpenUrl(area);
	if (ActionEvent.isAction(event, "show-graphical"))
	    return actions.onShowGraphical();
	if (ActionEvent.isAction(event, "history-prev"))
	    return actions.onHistoryPrev(area);
		if (ActionEvent.isAction(event, "copy-url"))
	    return actions.onCopyUrl(area);
				if (ActionEvent.isAction(event, "copy-ref"))
	    return actions.onCopyRef(area);
	return false;
    }

    @Override public void closeApp()
    {
	//FIXME:browser.close();
	luwrain.closeApp();
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

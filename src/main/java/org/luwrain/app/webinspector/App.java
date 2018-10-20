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

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.browser.*;
import org.luwrain.controls.*;
import org.luwrain.controls.doc.*;

public final class App implements Application
{
    private Luwrain luwrain = null;
    private Strings strings = null;
    private Base base = null;
    private Actions actions = null;
    private ActionLists actionLists = null;
    private ListArea area = null;

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
	this.actions = new Actions(luwrain, base, base.browser, strings);
	createArea();
	final String startingUrl = "http://luwrain.org";
	return new InitResult();
    }

    private void createArea()
    {
	final ListArea.Params params = new ListArea.Params();
	params.context = new DefaultControlEnvironment(luwrain);
	params.name = strings.appName();
	params.model = base.getModel();
	params.appearance = new ListUtils.DefaultAppearance(params.context);
	params.clickHandler = (area,index,obj)->{
	    if (obj == null || !(obj instanceof Item))
		return false;
	    return actions.onClick((Item)obj);
	};
    	this.area = new ListArea(params) {
		@Override public boolean onInputEvent(KeyboardEvent event)
		{
		    NullCheck.notNull(event, "event");
		    NullCheck.notNull(event, "event");
		    if (event.isSpecial())
			switch(event.getSpecial())
			{
			case ESCAPE:
			    closeApp();
			    return true;
			}
		    return super.onInputEvent(event);
		}

		@Override public boolean onSystemEvent(EnvironmentEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (event.getType() != EnvironmentEvent.Type.REGULAR)
			return super.onSystemEvent(event);
		    switch(event.getCode())
		    {
		    case ACTION:
				if (ActionEvent.isAction(event, "open-url"))
	    return actions.onOpenUrl();
	if (ActionEvent.isAction(event, "show-graphical"))
	    return actions.onShowGraphical();
	if (ActionEvent.isAction(event, "show-graphical"))
	    return actions.onShowGraphical();
	return false;
		    case REFRESH:
			base.updateItems();
			super.refresh();
			return true;
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

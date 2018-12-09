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
import org.luwrain.controls.*;

public final class App implements Application
{
    private Luwrain luwrain = null;
    private Strings strings = null;
    private Base base = null;
    private Actions actions = null;
    private ActionLists actionLists = null;
    private ListArea elementsArea = null;
    private ListArea attrsArea = null;

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
	this.strings = (Strings)o;
	this.luwrain = luwrain;
	this.base = new Base(luwrain);
	this.actionLists = new ActionLists(luwrain, strings);
	this.actions = new Actions(luwrain, base, strings);
	createArea();
	if (arg != null && !arg.trim().isEmpty())
	{
	    final String url;
	    if (!arg.toLowerCase().startsWith("http://") && !arg.toLowerCase().startsWith("https://"))
		url = "http://" + arg; else
		url = arg;
	    base.browser.loadByUrl(url);
	}
	return new InitResult();
    }

    private void createArea()
    {
	final ListArea.Params elementsParams = new ListArea.Params();
	elementsParams.context = new DefaultControlEnvironment(luwrain);
	elementsParams.name = strings.appName();
	elementsParams.model = base.getItemsModel();
	elementsParams.appearance = new ListUtils.DefaultAppearance(elementsParams.context);
	elementsParams.clickHandler = (area,index,obj)->{
	    if (obj == null || !(obj instanceof Item))
		return false;
	    base.fillAttrs((Item)obj);
	    attrsArea.refresh();
	    luwrain.setActiveArea(attrsArea);
	    return true;
	};
    	this.elementsArea = new ListArea(elementsParams) {
		@Override public boolean onInputEvent(KeyboardEvent event)
		{
		    NullCheck.notNull(event, "event");
		    NullCheck.notNull(event, "event");
		    if (event.isSpecial())
			switch(event.getSpecial())
			{
			case TAB:
			    luwrain.setActiveArea(attrsArea);
			    return true;
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
	if (ActionEvent.isAction(event, "show-graphical"))
	{
base.browser.setVisibility(true);
return true;
	}
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

		final ListArea.Params attrsParams = new ListArea.Params();
	attrsParams.context = new DefaultControlEnvironment(luwrain);
	attrsParams.name = strings.appName();
	attrsParams.model = base.getAttrsModel();
	attrsParams.appearance = new ListUtils.DefaultAppearance(attrsParams.context);
	attrsParams.clickHandler = (area,index,obj)->{
	    if (obj == null || !(obj instanceof Item))
		return false;
	    //FIXME:
	    return false;
	};
    	this.attrsArea = new ListArea(attrsParams) {
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
			case TAB:
			    luwrain.setActiveArea(elementsArea);
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
		    return new Action[0];
		}
	    };

	
    }

    @Override public void closeApp()
    {
	base.browser.close();
	luwrain.closeApp();
    }

    @Override public String getAppName()
    {
	return strings.appName();
    }

    @Override public AreaLayout getAreaLayout()
    {
	return new AreaLayout(AreaLayout.TOP_BOTTOM, elementsArea, attrsArea);
    }
}

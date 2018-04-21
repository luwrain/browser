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

import java.util.*;

import org.luwrain.core.*;

public class Extension extends org.luwrain.core.extensions.EmptyExtension
{
    @Override public Command[] getCommands(Luwrain luwrain)
    {
	return new Command[]{
	    new Command(){
		@Override public String getName()
		{
		    return "browser";
		}
		@Override public void onCommand(Luwrain luwrain)
		{
		    luwrain.launchApp("browser");
		}
	    }};
    }

    @Override public Shortcut[] getShortcuts(Luwrain luwrain)
    {
	return new Shortcut[]{
	    new Shortcut() {
		@Override public String getExtObjName()
		{
		    return "browser";
		}
		@Override public Application[] prepareApp(String[] args)
		{
		    if (args == null || args.length < 1)
			return new Application[]{new BrowserApp()};
		    LinkedList<Application> v = new LinkedList<Application>();
		    for(String s: args)
			if (s != null)
			    v.add(new BrowserApp(s));
		    if (v.isEmpty())
			return new Application[]{new BrowserApp()};
		    return v.toArray(new Application[v.size()]);
		}
	    }};
    }

    @Override public org.luwrain.cpanel.Factory[] getControlPanelFactories(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	return new org.luwrain.cpanel.Factory[]{new SettingsFactory(luwrain)};
    }
}

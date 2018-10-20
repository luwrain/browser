/*
   Copyright 2012-2018 Michael Pozhidaev <michael.pozhidaev@gmail.com>
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

package org.luwrain.browser;

import java.util.*;
import org.luwrain.base.*;

import org.luwrain.core.*;

public final class Extension extends org.luwrain.core.extensions.EmptyExtension
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
	    },

	    new Command(){
		@Override public String getName()
		{
		    return "web-inspector";
		}
		@Override public void onCommand(Luwrain luwrain)
		{
		    luwrain.launchApp("web-inspector");
		}
	    },

	};
    }

    @Override public ExtensionObject[] getExtObjects(Luwrain luwrain)
    {
	return new ExtensionObject[]{

	    new Shortcut() {
		@Override public String getExtObjName()
		{
		    return "browser";
		}
		@Override public Application[] prepareApp(String[] args)
		{
		    NullCheck.notNullItems(args, "args");
		    if (args.length == 0)
			return new Application[]{new org.luwrain.app.browser.App()};
		    final List<Application> v = new LinkedList();
		    for(String s: args)
			if (!s.isEmpty())
			    v.add(new org.luwrain.app.browser.App(s));
		    if (v.isEmpty())
			return new Application[]{new org.luwrain.app.browser.App()};
		    return v.toArray(new Application[v.size()]);
		}
	    },

	    new Shortcut() {
		@Override public String getExtObjName()
		{
		    return "web-inspector";
		}
		@Override public Application[] prepareApp(String[] args)
		{
		    NullCheck.notNullItems(args, "args");
		    if (args.length == 0)
			return new Application[]{new org.luwrain.app.webinspector.App()};
		    final List<Application> v = new LinkedList();
		    for(String s: args)
			if (!s.isEmpty())
			    v.add(new org.luwrain.app.webinspector.App(s));
		    if (v.isEmpty())
			return new Application[]{new org.luwrain.app.webinspector.App()};
		    return v.toArray(new Application[v.size()]);
		}
	    },

	};
    }

    @Override public org.luwrain.cpanel.Factory[] getControlPanelFactories(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	//	return new org.luwrain.cpanel.Factory[]{new SettingsFactory(luwrain)};
	return new org.luwrain.cpanel.Factory[0];
    }
}

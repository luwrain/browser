/*
   Copyright 2012-2022 Michael Pozhidaev <msp@luwrain.org>
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
import org.luwrain.settings.browser.SettingsFactory;
import org.luwrain.i18n.*;

public final class Extension extends EmptyExtension
{
    @Override public Command[] getCommands(Luwrain luwrain)
    {
	return new Command[]{
	    new SimpleShortcutCommand("browser"),
	    new SimpleShortcutCommand("web-ins"),
	    new SimpleShortcutCommand("chromite"),
	};
    }

    @Override public ExtensionObject[] getExtObjects(Luwrain luwrain)
    {
	return new ExtensionObject[]{
	    new SimpleShortcut("chromite", org.luwrain.app.chromite.App.class),
	    new Shortcut() {
		@Override public String getExtObjName() { return "browser"; }
		@Override public Application[] prepareApp(String[] args)
		{
		    if (args.length == 0)
			return new Application[]{new org.luwrain.app.browser.App()};
		    final List<Application> v = new ArrayList<>();
		    for(String s: args)
			if (!s.isEmpty())
			    v.add(new org.luwrain.app.browser.App(s));
		    if (v.isEmpty())
			return new Application[]{new org.luwrain.app.browser.App()};
		    return v.toArray(new Application[v.size()]);
		}
	    },
	    new SimpleShortcut("web-ins", org.luwrain.app.webinspector.App.class)
	};
    }

    @Override public void i18nExtension(Luwrain luwrain, org.luwrain.i18n.I18nExtension i18nExt)
    {
	i18nExt.addCommandTitle(Lang.EN, "browser", "Internet");
	i18nExt.addCommandTitle(Lang.RU, "browser", "Интернет");
	i18nExt.addCommandTitle(Lang.EN, "web-ins", "Web inspector");
	i18nExt.addCommandTitle(Lang.RU, "web-ins", "Веб-инспектор");
	try {
	    i18nExt.addStrings(Lang.EN, Strings.NAME, new ResourceStringsObj(luwrain, getClass().getClassLoader(), getClass(), "strings-main.properties").create(Lang.EN, Strings.class));
	    i18nExt.addStrings(Lang.RU, Strings.NAME, new ResourceStringsObj(luwrain, getClass().getClassLoader(), getClass(), "strings-main.properties").create(Lang.RU, Strings.class));
	}
	catch(java.io.IOException e)
	{
	    throw new RuntimeException(e);
	}
    }

    @Override public org.luwrain.cpanel.Factory[] getControlPanelFactories(Luwrain luwrain)
    {
	return new org.luwrain.cpanel.Factory[]{new SettingsFactory(luwrain)};
    }
}

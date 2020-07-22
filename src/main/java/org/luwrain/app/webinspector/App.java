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

package org.luwrain.app.webinspector;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;

import org.luwrain.template.*;

public final class App extends AppBase <Strings>implements Application
{
    private final String arg;

    public App(String arg)
    {
	super(Strings.NAME, Strings.class);
	this.arg = arg;
    }

    public App()
    {
	this(null);
    }

    @Override public boolean onAppInit()
    {
	return true;
    }

    @Override public AreaLayout getDefaultAreaLayout()
    {
	return null;
    }

}

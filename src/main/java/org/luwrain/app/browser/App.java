/*
   Copyright 2012-2021 Michael Pozhidaev <msp@luwrain.org>
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

import java.util.concurrent.*;

import org.luwrain.core.*;
import org.luwrain.controls.web.*;
import org.luwrain.app.base.*;

final class App extends AppBase<Strings> implements WebArea.ClientThread
{
    private final String arg;
    private Conversations conv = null;
    private MainLayout mainLayout = null;

    public App(String arg)
    {
	super(Strings.NAME, Strings.class);
	this.arg = arg != null?arg:"";
    }

    public App()
    {
	this(null);
    }

    @Override protected AreaLayout onAppInit()
    {
	this.conv = new Conversations(this);
	this.mainLayout = new MainLayout(this);
	return mainLayout.getAreaLayout();
    }

    @Override public boolean onEscape()
    {
	closeApp();
	return true;
    }

    @Override public Object runSync(Callable callable)
    {
	NullCheck.notNull(callable, "callable");
	return getLuwrain().callUiSafely(callable);
    }

    @Override public void runAsync(Runnable runnable)
    {
	NullCheck.notNull(runnable, "runnable");
	getLuwrain().runUiSafely(runnable);
    }

    Conversations getConv()
    {
	return this.conv;
    }
}

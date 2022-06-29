/*
   Copyright 2012-2022 Michael Pozhidaev <msp@luwrain.org>

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

package org.luwrain.app.chromite;

import java.util.concurrent.*;

import org.luwrain.core.*;
import org.luwrain.app.base.*;
import org.luwrain.web.chromite.*;

public final class App extends AppBase<Strings>
{
    private Conv conv = null;
    private final Chromite chromite = new Chromite();
    private MainLayout mainLayout = null;

    public App()
    {
	super(Strings.NAME, Strings.class);
    }

    @Override protected AreaLayout onAppInit()
    {
	this.conv = new Conv(this);
	this.mainLayout = new MainLayout(this);
	return mainLayout.getAreaLayout();
    }

    @Override public boolean onEscape()
    {
	closeApp();
	return true;
    }

    @Override public void closeApp()
    {
	this.chromite.close();
	super.closeApp();
    }

    Conv getConv() { return this.conv; }
    Chromite getChromite(){ return this.chromite; }
}

// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>
// Copyright 2015-2016 Roman Volovodov <gr.rPman@gmail.com>

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

/*
   Copyright 2012-2024 Michael Pozhidaev <msp@luwrain.org>
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

import java.net.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.app.base.*;
import org.luwrain.graphical.*;

final class MainLayout extends LayoutBase
{
    final App app;
    final NavigationArea webArea;

    MainLayout(App app)
    {
	super(app);
	this.app = app;
	this.webArea = new NavigationArea(getControlContext()){
		@Override public String getLine(int index)
		{
		    return "";
		}
		@Override public int getLineCount()
		{
		    return 1;
		}
		@Override public String getAreaName()
		{
		    return "web";
		}
	    };
	setAreaLayout(webArea, actions(
				       action("open-url", app.getStrings().actionOpenUrl(), new InputEvent(InputEvent.Special.F6), this::actOpenUrl)
));
    }

    private boolean actOpenUrl()
    {
	final String url = app.getConv().openUrl("https://");
	if (url == null)
	    return false;
	FxThread.runSync(()->app.getEngine().load(url));
	return true;
    }
}

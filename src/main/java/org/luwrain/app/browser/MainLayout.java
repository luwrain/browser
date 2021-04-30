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

package org.luwrain.app.browser;

import org.luwrain.core.*;
import org.luwrain.browser.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.web.*;
import org.luwrain.controls.*;
import org.luwrain.app.base.*;

final class MainLayout extends LayoutBase
{
        private final WebArea area;

    MainLayout(AppBase app)
    {
	super(app);
    	area = new WebArea(createWebAreaParams()){
		@Override public boolean onInputEvent(InputEvent event)
		{
		    NullCheck.notNull(event, "event");
		    return super.onInputEvent(event);
		}
		@Override public boolean onSystemEvent(SystemEvent event)
		{
		    NullCheck.notNull(event, "event");
			return super.onSystemEvent(event);
		}
		@Override public Action[] getAreaActions()
		{
		    return new Action[0];
		}
	    };
    }


    WebArea.Params createWebAreaParams()
    {
		final WebArea.Params params = new WebArea.Params();
		params.context = getControlContext();
	params.appearance = new DefaultAppearance(params.context);
	//FIXME:	params.clickHandler = (area,rowIndex,webObj)->actions.onClick(area, webObj, rowIndex);
	params.browserFactory = (events)->{
	    NullCheck.notNull(events, "events");
	    return BrowserFactory.newBrowser(app.getLuwrain(), events);
	};
	//	params.callback = actions;
	//	params.clientThread = base;
	return params;
    }
}

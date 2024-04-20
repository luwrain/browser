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
import org.luwrain.controls.block.*;
import org.luwrain.app.base.*;
import org.luwrain.graphical.*;

import static org.luwrain.core.DefaultEventResponse.*;
import static org.luwrain.app.browser.TestBlocks.*;

final class MainLayout extends LayoutBase
{
    final App app;
    final BlockArea webArea;

    MainLayout(App app)
    {
	super(app);
	this.app = app;

	final var params = new BlockArea.Params();
	params.context = getControlContext();
	params.appearance = new Appearance();
	this.webArea = new BlockArea(params){
	    };
	webArea.setBlocks(getTestBlocks());
	
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

        private final class Appearance implements BlockArea.Appearance
    {
	@Override public void announceFirstBlockLine(Block block, BlockLine blockLine)
	{
	    	    final var webLine = (WebLine)blockLine;
		    app.setEventResponse(text(webLine.text));
	}
	@Override public void announceBlockLine(Block block, BlockLine blockLine)
	{
	    	    final var webLine = (WebLine)blockLine;
		    		    app.setEventResponse(text(webLine.text));
	}
	@Override public String getBlockLineTextAppearance(Block block, BlockLine blockLine)
	{
	    final var webLine = (WebLine)blockLine;
	    return webLine.text;
	}
    }

}

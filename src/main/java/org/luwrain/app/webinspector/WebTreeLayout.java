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

package org.luwrain.app.webinspector;

import java.util.*;

import netscape.javascript.JSObject;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.app.base.*;
import org.luwrain.graphical.*;
import org.luwrain.web.*;

import org.w3c.dom.Node;

import static org.luwrain.core.DefaultEventResponse.*;


final class WebTreeLayout extends LayoutBase
{
    private final App app;
    final TreeListArea<WebObject> elementsArea;
    final SimpleArea stylesArea;

    WebTreeLayout(App app)
    {
		super(app);
		this.app = app;

		final TreeListArea.Params<WebObject> treeParams = new TreeListArea.Params<>();
		treeParams.context = getControlContext();
		treeParams.name = "Elements";
		treeParams.model = new ElementsModel(app);
		//	treeParams.leafClickHandler = this;

		this.elementsArea = new TreeListArea<WebObject>(treeParams){
			@Override public boolean onSystemEvent(SystemEvent event)
			{
				if (event.getType() == SystemEvent.Type.REGULAR)
				switch(event.getCode())
				{
					case REFRESH:
						app.update();
						app.getLuwrain().playSound(Sounds.OK);
						return true;
					case OK:
						return onShowStyles();
				}
				return super.onSystemEvent(event);
			}
		};
		this.stylesArea = new SimpleArea(getControlContext(), "Styles");
		setAreaLayout(AreaLayout.LEFT_RIGHT, elementsArea, null,
				stylesArea, null);
    }

    private boolean onShowStyles()
    {
		 stylesArea.update((lines)->{
		 lines.clear();
		 lines.add("Test styles box");
		 });
		//final WebObject obj = elementsArea.selected();
		//if (obj == null)
		//	return false;
		//final String text = obj.getStyleAsText();
		//if (text == null)
		//	return false;
		//stylesArea.update((lines)->{
		//	lines.clear();
		//	final String[] ll = text.split(";", -1);
		//	for(String l: ll)
		//		lines.add(l);
		//	});
		//setActiveArea(stylesArea);
		return true;
    }
}

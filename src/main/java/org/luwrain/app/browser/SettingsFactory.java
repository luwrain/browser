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

package org.luwrain.app.browser;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.popups.Popups;
import org.luwrain.cpanel.*;

class SettingsFactory implements org.luwrain.cpanel.Factory
{
    private final Luwrain luwrain;
    private SimpleElement browserElement = new SimpleElement(StandardElements.APPLICATIONS, this.getClass().getName());

    SettingsFactory(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	this.luwrain = luwrain;
    }

    @Override public Element[] getElements()
    {
	return new Element[]{browserElement};
    }

    @Override public Element[] getOnDemandElements(Element parent)
    {
	NullCheck.notNull(parent, "parent");
	return new Element[0];
    }

    @Override public Section createSection(Element el)
    {
	NullCheck.notNull(el, "el");
	if (el.equals(browserElement))
	    return new SimpleSection(el, "Интернет", (controlPanel)->{return new SettingsForm(controlPanel);});
	return null;
    }
}

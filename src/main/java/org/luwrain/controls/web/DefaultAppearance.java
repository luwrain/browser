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

package org.luwrain.controls.web;

import org.luwrain.core.*;
import org.luwrain.controls.*;
import org.luwrain.browser.*;

public class DefaultAppearance implements WebArea.Appearance
{
    protected ControlEnvironment context;

    public DefaultAppearance(ControlEnvironment context)
    {
	NullCheck.notNull(context, "context");
	this.context = context;
    }

    @Override public void announceFirstRow(Container.Type type, WebObject[] objs)
    {
	NullCheck.notNull(type, "type");
	NullCheck.notNullItems(objs, "objs");
	final Sounds sound;
	switch(type)
	{
	case PARA:
	    sound = Sounds.PARAGRAPH;
	    break;
	case LIST_ITEM:
	    sound = Sounds.LIST_ITEM;
	    break;
	case HEADING:
	    sound = Sounds.DOC_SECTION;
	    break;
	default:
	    sound = null;
	}
	final StringBuilder b = new StringBuilder();
	for(WebObject obj: objs)
	    b.append(obj.getText());
	context.setEventResponse(DefaultEventResponse.text(sound, new String(b)));
    }

    @Override public void announceRow(WebObject[] objs)
    {
	NullCheck.notNullItems(objs, "objs");
	final StringBuilder b = new StringBuilder();
	for(WebObject obj: objs)
	    b.append(obj.getText());
	context.setEventResponse(DefaultEventResponse.text(new String(b)));
    }
}

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
	context.setEventResponse(DefaultEventResponse.text(sound, makeResponseText(objs)));
    }

    @Override public void announceRow(WebObject[] objs)
    {
	NullCheck.notNullItems(objs, "objs");
	context.setEventResponse(DefaultEventResponse.text(makeResponseText(objs)));
    }

    @Override public String getRowTextAppearance(WebObject[] objs)
    {
	NullCheck.notNullItems(objs, "objs");
	final StringBuilder b = new StringBuilder();
	for(WebObject o: objs)
	{
	    if (o instanceof WebText)
	    {
		final WebText webText = (WebText)o;
		b.append(webText.getText());
		continue;
	    }
	    if (o instanceof WebTextInput)
	    {
		final WebTextInput webTextInput = (WebTextInput)o;
		//FIXME:width
		b.append("[").append(webTextInput.getText()).append("]");
		continue;
	    }
	    if (o instanceof WebButton)
	    {
		final WebButton webButton = (WebButton)o;
		//FIXME:width
		b.append("[").append(webButton.getTitle()).append("]");
		continue;
	    }
	    if (o instanceof WebImage)
	    {
		final WebImage webImage = (WebImage)o;
		//FIXME:
		b.append("[").append(webImage.getComment()).append("]");
		continue;
	    }
	}
	return new String(b);
    }

    protected String makeResponseText(WebObject[] objs)
    {
	NullCheck.notNullItems(objs, "objs");
	final StringBuilder b = new StringBuilder();
	for(WebObject o: objs)
	{
	    if (o instanceof WebText)
	    {
		final WebText webText = (WebText)o;
		b.append(webText.getText());
		continue;
	    }
	    if (o instanceof WebTextInput)
	    {
		final WebTextInput webTextInput = (WebTextInput)o;
		b.append(" поле для ввода текста ").append(webTextInput.getText()).append(" ");
		continue;
	    }
	    if (o instanceof WebButton)
	    {
		final WebButton webButton = (WebButton)o;
		b.append(" кнопка ").append(webButton.getTitle()).append(" ");
		continue;
	    }
	    if (o instanceof WebImage)
	    {
		final WebImage webImage = (WebImage)o;
		b.append(" изображение ").append(webImage.getComment()).append(" ");
		continue;
	    }
	}
	return new String(b);
    }
}

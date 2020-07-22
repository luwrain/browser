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

package org.luwrain.app.webinspector;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;

import org.luwrain.template.*;

final class MainLayout extends LayoutBase
{
    private final App app;
    private ListArea elementsArea;
    private ListArea attrsArea;

    MainLayout(App app)
    {
	NullCheck.notNull(app, "app");
	this.app = app;
	this.elementsArea = new ListArea(createElementsParams()) {
		@Override public boolean onInputEvent(InputEvent event)
		{
		    NullCheck.notNull(event, "event");
		    return super.onInputEvent(event);
		}
		@Override public boolean onSystemEvent(SystemEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (event.getType() != SystemEvent.Type.REGULAR)
			return super.onSystemEvent(event);
		    switch(event.getCode())
		    {
		    default:
			return super.onSystemEvent(event);
		    }
		}
		@Override public Action[] getAreaActions()
		{
		    return new Action[0];
		}
	    };

    	this.attrsArea = new ListArea(createAttrsParams()) {
		@Override public boolean onInputEvent(InputEvent event)
		{
		    NullCheck.notNull(event, "event");
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

        boolean onClick(Item item)
    {
	NullCheck.notNull(item, "item");
	if (item.className.equals("HTMLButtonElementImpl") ||
	    item.inputType.equals("submit"))
	{
	    app.getBrowser().runSafely(()->{
		    item.it.emulateSubmit();
		    return null;
		});
	    return true;
	}
	if (item.inputType.equals("text") ||
	    item.inputType.equals("password") ||
	    item.inputType.equals("email"))
	{
	    final String text = app.getConv().formText("");
	    if (text == null)
		return true;
	    app.getBrowser().runSafely(()->{
		    item.it.setInputText(text);
		    app.updateItems();
		    return null;
		});
	    return true;
	}
	return false;
    }


    private ListArea.Params createElementsParams()
    {
		final ListArea.Params elementsParams = new ListArea.Params();
		elementsParams.context = new DefaultControlContext(app.getLuwrain());
		elementsParams.name = app.getStrings().appName();
	//	elementsParams.model = base.getItemsModel();
	elementsParams.appearance = new ListUtils.DefaultAppearance(elementsParams.context);
	elementsParams.clickHandler = (area,index,obj)->{
	    if (obj == null || !(obj instanceof Item))
		return false;
	    //	    base.fillAttrs((Item)obj);
	    attrsArea.refresh();
	    app.getLuwrain().setActiveArea(attrsArea);
	    return true;
	};
	return elementsParams;
    }

    private ListArea.Params createAttrsParams()
    {
			final ListArea.Params attrsParams = new ListArea.Params();
			attrsParams.context = new DefaultControlContext(app.getLuwrain());
			attrsParams.name = app.getStrings().appName();
	//	attrsParams.model = base.getAttrsModel();
	attrsParams.appearance = new ListUtils.DefaultAppearance(attrsParams.context);
	attrsParams.clickHandler = (area,index,obj)->{
	    if (obj == null || !(obj instanceof Item))
		return false;
	    //FIXME:
	    return false;
	};
	return attrsParams;
    }

        private final class ItemsModel implements org.luwrain.controls.ListArea.Model
    {
	@Override public int getItemCount()
	{
	    return app.items.length;
	}
	@Override public Object getItem(int index)
	{
	    return app.items[index];
	}
	@Override public void refresh()
	{
	}
    }

        private final class AttrsModel implements org.luwrain.controls.ListArea.Model
    {
	@Override public int getItemCount()
	{
	    return app.attrs.length;
	}
	@Override public Object getItem(int index)
	{
	    return app.attrs[index];
	}
	@Override public void refresh()
	{
	}
    }

}

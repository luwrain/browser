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

final class MainLayout extends LayoutBase implements ConsoleArea.InputHandler
{
    private final App app;
    private ConsoleArea elementsArea;
    private ListArea attrsArea;

    MainLayout(App app)
    {
	NullCheck.notNull(app, "app");
	this.app = app;
	this.elementsArea = new ConsoleArea(createElementsParams()) {
		private final Actions actions = actions();
		@Override public boolean onInputEvent(InputEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (app.onInputEvent(this, event))
			return true;
		    return super.onInputEvent(event);
		}
		@Override public boolean onSystemEvent(SystemEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (app.onSystemEvent(this, event))
			return true;
		    if (event.getType() != SystemEvent.Type.REGULAR)
			return super.onSystemEvent(event);
		    switch(event.getCode())
		    {
		    default:
			return super.onSystemEvent(event);
		    }
		}
		@Override public boolean onAreaQuery(AreaQuery query)
		{
		    NullCheck.notNull(query, "query");
		    if (app.onAreaQuery(this, query))
			return true;
		    return super.onAreaQuery(query);
		}
		@Override public Action[] getAreaActions()
		{
		    return actions.getAreaActions();
		}
	    };
    	this.attrsArea = new ListArea(createAttrsParams()) {
		private final Actions actions = actions();
		@Override public boolean onInputEvent(InputEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (app.onInputEvent(this, event))
			return true;
		    return super.onInputEvent(event);
		}
		@Override public boolean onSystemEvent(SystemEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (app.onSystemEvent(this, event))
			return true;
		    return super.onSystemEvent(event);
		}
		@Override public boolean onAreaQuery(AreaQuery query)
		{
		    NullCheck.notNull(query, "query");
		    if (app.onAreaQuery(this, query))
			return true;
		    return super.onAreaQuery(query);
		}
		@Override public Action[] getAreaActions()
		{
		    return new Action[0];
		}
	    };
    }

    @Override public ConsoleArea.InputHandler.Result onConsoleInput(ConsoleArea area, String text)
    {
	return null;
    }

    private boolean onClick(Item item)
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

    private ConsoleArea.Params createElementsParams()
    {
	final ConsoleArea.Params params = new ConsoleArea.Params();
	params.context = new DefaultControlContext(app.getLuwrain());
	params.name = app.getStrings().appName();
	params.model = new ConsoleModel();
	params.appearance = new ElementsAppearance();
	params.inputHandler = this;
	params.inputPrefix = "WebKit>";
	params.clickHandler = (area,index,obj)->{
	    if (obj == null || !(obj instanceof Item))
		return false;
	    	    app.fillAttrs((Item)obj);
	    attrsArea.refresh();
	    app.getLuwrain().setActiveArea(attrsArea);
	    return true;
	};
	return params;
    }

    private ListArea.Params createAttrsParams()
    {
	final ListArea.Params attrsParams = new ListArea.Params();
	attrsParams.context = new DefaultControlContext(app.getLuwrain());
	attrsParams.name = app.getStrings().appName();
	attrsParams.model = new AttrsModel();
	attrsParams.appearance = new ListUtils.DefaultAppearance(attrsParams.context);
	attrsParams.clickHandler = (area,index,obj)->{
	    if (obj == null || !(obj instanceof Item))
		return false;
	    //FIXME:
	    return false;
	};
	return attrsParams;
    }

    AreaLayout getLayout()
    {
	return new AreaLayout(AreaLayout.TOP_BOTTOM, elementsArea, attrsArea);
    }

    private final class ConsoleModel implements org.luwrain.controls.ConsoleArea.Model
    {
	@Override public int getItemCount()
	{
	    return app.items.length;
	}
	@Override public Object getItem(int index)
	{
	    return app.items[index];
	}
    }

        private final class ElementsAppearance implements ConsoleArea.Appearance
    {
	@Override public void announceItem(Object item)
	{
	    NullCheck.notNull(item, "item");
	    app.getLuwrain().setEventResponse(DefaultEventResponse.text(item.toString()));
	    return;
	}
	@Override public String getTextAppearance(Object item)
	{
	    NullCheck.notNull(item, "item");
	    return item.toString();
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

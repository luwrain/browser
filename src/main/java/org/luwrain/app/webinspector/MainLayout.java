/*
   Copyright 2012-2022 Michael Pozhidaev <msp@luwrain.org>
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

import netscape.javascript.JSObject;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.app.base.*;
import org.luwrain.graphical.*;
import org.luwrain.web.*;

import static org.luwrain.core.DefaultEventResponse.*;


final class MainLayout extends LayoutBase implements ConsoleArea.InputHandler, ConsoleArea.ClickHandler<Item>
{
    private final App app;
    final ConsoleArea<Item> consoleArea;
    final TreeListArea<WebObject> elementsArea;
    final SimpleArea stylesArea;

    private JSObject jsRes = null;
    private ScanResult scanResult = null;

    MainLayout(App app)
    {
	super(app);
	this.app = app;
	    this.consoleArea = new ConsoleArea<Item>(consoleParams((params)->{
	    params.name = app.getStrings().appName();
	    params.model = new ConsoleUtils.ListModel(app.messages);
	    params.appearance = new ConsoleAppearance();
	    params.inputHandler = this;
	    params.clickHandler = this;
	    params.inputPrefix = "WebKit>";
		    }));
		final TreeListArea.Params<WebObject> treeParams = new TreeListArea.Params<>();
        treeParams.context = getControlContext();
	treeParams.name = "fixme";
	treeParams.model = new ElementsModel(app);
	//	treeParams.leafClickHandler = this;
	this.elementsArea = new TreeListArea<WebObject>(treeParams){
		@Override public boolean onSystemEvent(SystemEvent event)
		{
		    if (event.getType() == SystemEvent.Type.REGULAR)
			switch(event.getCode())
			{
			case REFRESH:
			app.updateTree();
			app.getLuwrain().playSound(Sounds.OK);
			return true;
			case OK:
			return onShowStyles();
			}
		    return super.onSystemEvent(event);
		}
	    };
	this.stylesArea = new SimpleArea(getControlContext(), "fixme");
	setAreaLayout(AreaLayout.LEFT_TOP_BOTTOM, consoleArea, actions(
						action("show-graphical", app.getStrings().actionShowGraphical(), new InputEvent(InputEvent.Special.F10), MainLayout.this::actShowGraphical)
								  ),
		      elementsArea, null,
		      stylesArea, null);
    }

    @Override public ConsoleArea.InputHandler.Result onConsoleInput(ConsoleArea area, String text)
    {
	if (text.trim().isEmpty())
	    return ConsoleArea.InputHandler.Result.REJECTED;
	if (text.trim().equals("dump"))
	    return dump()?ConsoleArea.InputHandler.Result.CLEAR_INPUT:ConsoleArea.InputHandler.Result.REJECTED;
	FxThread.runSync(()->app.getWebEngine().load(text));
	return ConsoleArea.InputHandler.Result.CLEAR_INPUT;
    }

    private boolean dump()
    {
	FxThread.runSync(()->{
		final Object res = app.getWebEngine().executeScript(app.injection);
		if (res == null)
		{
		    app.print("null");
		    return;
		}
		if (!(res instanceof JSObject))
		{
		    app.print("Instance of " + res.getClass().getName());
		    return;
		}
		this.jsRes = (JSObject)res;
		this.scanResult = new ScanResult(app.getWebEngine(), jsRes);
		app.print("Finished, " + scanResult.count + " items");
	    });
	getLuwrain().playSound(Sounds.OK);
	consoleArea.refresh();
	return true;
    }

    @Override public boolean onConsoleClick(ConsoleArea area, int index, Item item)
    {
	//			    setActiveArea(attrsArea);
		return false;
	    };

    private boolean actShowGraphical()
    {
	app.getLuwrain().showGraphical((graphicalModeControl)->{
		app.getWebView().setOnKeyReleased((event)->{
			switch(event.getCode())
			{
			case ESCAPE:
			    app.getLuwrain().runUiSafely(()->app.getLuwrain().playSound(Sounds.OK));
			    graphicalModeControl.close();
			    break;
			}
		    });
		app.getWebView().setVisible(true);
		return app.getWebView();
	    });
	return true;
    }

    private boolean onShowStyles()
    {
	final WebObject obj = elementsArea.selected();
	if (obj == null)
	    return false;
	final String text = obj.getStyleAsText();
	if (text == null)
	    return false;
	stylesArea.update((lines)->{
		lines.clear();
		final String[] ll = text.split(";", -1);
		for(String l: ll)
		    lines.addLine(l);
	    });
	setActiveArea(stylesArea);
	return true;
    }

    private final class ConsoleAppearance implements ConsoleArea.Appearance
    {
	@Override public void announceItem(Object item)
	{
	    app.getLuwrain().setEventResponse(text(item.toString()));
	    return;
	}
	@Override public String getTextAppearance(Object item)
	{
	    return item.toString();
	}
    }
}

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
import org.w3c.dom.*;
import org.w3c.dom.css.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.app.base.*;
import org.luwrain.graphical.*;
import org.luwrain.web.*;

import static org.luwrain.core.DefaultEventResponse.*;

final class MainLayout extends LayoutBase implements ConsoleArea.InputHandler
{
    private final App app;
    final ConsoleArea<String> consoleArea;
    final ListArea<WebKitBlock> blocksArea;
    final SimpleArea stylesArea;

    //    private JSObject jsRes = null;
    //    private ScanResult scanResult = null;

    MainLayout(App app)
    {
		super(app);
		this.app = app;

		this.consoleArea = new ConsoleArea<String>(consoleParams(params->{
		params.name = app.getStrings().appName();
		params.model = new ConsoleUtils.ListModel(app.messages);
		params.appearance = new ConsoleAppearance();
		params.inputHandler = this;
		params.inputPrefix = "WebKit>";
			}));

		this.blocksArea = new ListArea<WebKitBlock>(listParams(params->{
		params.name = "Блоки";
		params.model = new ListUtils.ListModel(app.blocks);
		params.appearance = new BlocksAppearance();
		params.clickHandler = MainLayout.this::onBlocksClick;
			})){
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
		this.stylesArea = new SimpleArea(getControlContext(), "Styles"){
			@Override public void announceLine(int index, String line)
			{
			    if (line.trim().isEmpty())
			    {
				app.setEventResponse(hint(Hint.EMPTY_LINE));
				return;
			    }
			    app.setEventResponse(text(getLuwrain().getSpeakableText(line, Luwrain.SpeakableTextType.PROGRAMMING)));
			}
		    };
		setAreaLayout(AreaLayout.LEFT_TOP_BOTTOM, consoleArea, actions(
							action("show-graphical", app.getStrings().actionShowGraphical(), new InputEvent(InputEvent.Special.F10), MainLayout.this::actShowGraphical)
									),
				blocksArea, null,
				stylesArea, null);
    }

    @Override public ConsoleArea.InputHandler.Result onConsoleInput(ConsoleArea area, String text)
    {
	if (text.trim().isEmpty())
	    return ConsoleArea.InputHandler.Result.REJECTED;

	if (text.trim().equals("test"))
	    return onTest()?ConsoleArea.InputHandler.Result.CLEAR_INPUT:ConsoleArea.InputHandler.Result.REJECTED;;

		if (text.trim().equals("dump")) //Error
	    return dump()?ConsoleArea.InputHandler.Result.CLEAR_INPUT:ConsoleArea.InputHandler.Result.REJECTED;

	//app.print("Opening -> " + text);
	FxThread.runSync(()->app.getEngine().load(text));
	return ConsoleArea.InputHandler.Result.CLEAR_INPUT;
    }

	//Injection??
    private boolean dump()
    {
	/*
		FxThread.runSync(()->{
			final Object res = app.getEngine().executeScript(app.injection);
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
			this.scanResult = new ScanResult(app.getEngine(), jsRes);
			app.print("Finished, " + scanResult.count + " items");
			app.print("Printing elements:");
			for (Map.Entry<Node, ScanResult.Item> entry : scanResult.getNodes().entrySet()) {
				app.print("name = " + entry.getKey().getNodeName());
				app.print("text = " + entry.getValue().text);
				app.print("X = " + entry.getValue().x);
				app.print("Y = " + entry.getValue().y);
				app.print("Width = " + entry.getValue().width);
				app.print("Height = " + entry.getValue().height);
				app.print("===============");
			}
			});
		getLuwrain().playSound(Sounds.OK);
		consoleArea.refresh();
	*/
		return true;
    }

    private boolean onTest()
    {
		FxThread.runSync(()->app.getEngine().load("https://marigostra.ru"));
		return true;
		    }

    private boolean onBlocksClick(ListArea<WebKitBlock> area, int index, WebKitBlock block)
    {
			 stylesArea.update((lines)->{
				 lines.clear();
				 lines.addLine("");
				 lines.addLine("Класс: " + block.node.getClass().getSimpleName());

				 final var style = block.getStyle();
				 if (style != null)
				     for(var l: style.split(";", -1))
				     lines.addLine(l);
			     });
			 stylesArea.setHotPoint(0, 0);
			 setActiveArea(stylesArea);
	return true;
    }

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
		 stylesArea.update((lines)->{
		 lines.clear();
		 lines.addLine("Test styles box");
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
		//		lines.addLine(l);
		//	});
		//setActiveArea(stylesArea);
		return true;
    }

    private final class ConsoleAppearance implements ConsoleArea.Appearance<String>
    {
		@Override public void announceItem(String text) { getLuwrain().setEventResponse(listItem(text)); }
		@Override public String getTextAppearance(String text) { return text; }
    }

    private final class BlocksAppearance extends ListUtils.AbstractAppearance<WebKitBlock>
    {
	@Override public void announceItem(WebKitBlock block, Set<Flags> flags) { getLuwrain().setEventResponse(listItem(Sounds.LIST_ITEM, block.text, Suggestions.LIST_ITEM)); }
	@Override public String getScreenAppearance(WebKitBlock block, Set<Flags> flags) { return block.text; }
    }
}

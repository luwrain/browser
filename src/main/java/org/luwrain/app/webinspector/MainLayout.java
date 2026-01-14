// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>
// Copyright 2015-2016 Roman Volovodov <gr.rPman@gmail.com>

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
						app.update();
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
		FxThread.runSync(()->app.getEngine().load(text));
		/*
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
		app.getEngine().load(text);
		return app.getWebView();
	    });
		*/
	return ConsoleArea.InputHandler.Result.CLEAR_INPUT;
    }

    private boolean onTest()
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
		app.getEngine().load("https://marigostra.ru");
				return app.getWebView();
	    });
				return true;
		    }

    private boolean onBlocksClick(ListArea<WebKitBlock> area, int index, WebKitBlock block)
    {
			 stylesArea.update((lines)->{
				 lines.clear();
				 lines.add("");
				 lines.add("Класс: " + block.className);
				 lines.add("Тег: " + block.tagName);
				 lines.add("Текстовые размеры: " + block.getLeft() + ", " + block.getRight() + ", " + block.getTop() + ", " + block.getBottom());
				 lines.add("Исходные размеры: " + block.srcLeft + ", " + block.srcRight + ", " + block.srcTop + ", " + block.srcBottom);
				 lines.add("Видимый: " + (block.visible?"да":"нет"));
				 lines.add("");
				 lines.add("" + block.runs.size() + " runs, " + block.lines.size() + " lines");
				 lines.add("Runs:");
				 for(var r: block.runs)
				     lines.add(r.toString().replaceAll("\u00a0", " "));

				 lines.add("");
				 lines.add("Строки:");
				 for(var l: block.lines)
				 {
				     final var b = new StringBuilder();
				     for(var f: l.fragments)
					 b.append("'").append(f.text()).append("',");
					 final var s = new String(b);
				     if (!s.isEmpty())
					 lines.add(s.substring(0, s.length() - 1).replaceAll("\u00a0", " "));
					 }

				 lines.add("");
				 lines.add("Стиль:");
				 final var style = block.getStyle();
				 if (style != null)
				     for(var l: style.split(";", -1))
					 lines.add(l);

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
		 lines.add("Test styles box");
		 });
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

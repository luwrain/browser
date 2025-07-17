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

import java.io.*;
import java.util.*;

import org.w3c.dom.css.DocumentCSS;

import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.concurrent.Worker.State;
import javafx.beans.value.ObservableValue;

import org.luwrain.core.*;
import org.luwrain.core.annotations.*;
import org.luwrain.app.base.*;
import org.luwrain.graphical.*;
import org.luwrain.web.*;

import org.w3c.dom.Node;

import static org.luwrain.graphical.FxThread.*;

@AppNoArgs(name = "web-ins", title = { "en=Web-inspector", "ru=Веб-инспектор" })
public final class App extends AppBase <Strings>implements Application
{
    static final String
	LOG_COMPONENT = "webins";

    static App instance = null;

    final List<String> messages = new ArrayList<>();
    private MainLayout mainLayout = null;
    private WebTreeLayout treeLayout = null;
    private Conv conv = null;

    private WebEngine webEngine = null;
    private WebView webView = null;
    private Runnable firstSwitching = null;
    final List<WebKitBlock> blocks = new ArrayList<>();
        private WebTree tree = null;

    public App()
    {
		super(Strings.class, "luwrain.webinspector");
		this.instance = this;
    }

    @Override public AreaLayout onAppInit()
    {
		this.conv = new Conv(this);
		runSync(()->{
			this.webView = new WebView();
			this.webEngine = webView.getEngine();
			this.webEngine.setUserDataDirectory(getLuwrain().getAppDataDir("luwrain.webins").toFile());
			this.webEngine.getLoadWorker().stateProperty().addListener((ov,oldState,newState)->onStateChanged(ov, oldState, newState));
					this.webEngine.setOnError(event->print("ERROR: " + event.getMessage()));
					this.webEngine.getLoadWorker().progressProperty().addListener((ov,o,n)->print("Progress " + String.valueOf(n)));
							this.webEngine.setOnAlert(event->print("ALERT: " + event.getData()));
		/*
		this.webEngine.setPromptHandler((event)->events.onPrompt(event.getMessage(),event.getDefaultValue()));
		this.webEngine.setConfirmHandler((param)->events.onConfirm(param));
		*/
							com.sun.javafx.webkit.WebConsoleListener.setDefaultListener((view, message, lineNum, source)->{
					print("Console message");
					print(message);
				    });
			this.webView.setVisible(false);
			});
		this.mainLayout = new MainLayout(this);
		this.treeLayout = new WebTreeLayout(this);
		setAppName(getStrings().appName());
				getLuwrain().showGraphical((graphicalModeControl)->{
		getWebView().setOnKeyReleased((event)->{
			switch(event.getCode())
			{
			case ESCAPE:
			     getLuwrain().runUiSafely(()->getLuwrain().playSound(Sounds.OK));
			    graphicalModeControl.close();
			    break;
			}});
		firstSwitching = ()->graphicalModeControl.close();
		getWebView().setVisible(true);
		getEngine().load("https://luwrain.org");
		return getWebView();
	    });
		return mainLayout.getAreaLayout();
    }

    @Override public boolean onEscape()
    {
		closeApp();
		return true;
    }

    void update()
    {
	runSync(()->{
		final var b = new org.luwrain.web.WebKitBlocks(webEngine);
b.process(100);
this.blocks.clear();
this.blocks.addAll(b.blocks);
	    });
		mainLayout.blocksArea.refresh();
	    }

    private void onStateChanged(ObservableValue<? extends State> ov, State oldState, State newState)
    {
		if (newState == null)
			return;
		print("New state: " + newState.toString());
		switch(newState)
		{
		case SUCCEEDED: {
final var b = new org.luwrain.web.WebKitBlocks(webEngine);
b.process(100);
this.blocks.clear();
this.blocks.addAll(b.blocks);
						getLuwrain().runUiSafely(()->{
mainLayout.blocksArea.refresh();
												getLuwrain().playSound(Sounds.MESSAGE);
					});
						print("The page is loaded");
						if (firstSwitching != null)
{
firstSwitching.run();
	firstSwitching = null;
}
				break;
		}
					case FAILED:
				getLuwrain().runUiSafely(()->getLuwrain().playSound(Sounds.ERROR));

				break;
		}
    }

    void print(String message)
    {
		messages.add(0, message);
		if (mainLayout != null)
		    getLuwrain().runUiSafely(()->mainLayout.consoleArea.refresh());
    }

    Conv getConv() { return this.conv; }
    WebView getWebView() { return webView; }
    WebEngine getEngine() { return webEngine; }
    WebTree getTree() { return tree; }

    static public void log(String msg)
    {
	if (instance != null)
	    instance.print(msg);
    }
}

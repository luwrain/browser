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

//import org.w3c.dom.Document;
import org.w3c.dom.css.DocumentCSS;

import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.concurrent.Worker.State;
import javafx.beans.value.ObservableValue;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.app.base.*;
import org.luwrain.graphical.*;
import org.luwrain.web.*;

import org.w3c.dom.Node;

import static org.luwrain.util.ResourceUtils.*;

public final class App extends AppBase <Strings>implements Application
{
    static final String
	LOG_COMPONENT = "webins";

    private final String arg;
    final String injection;
    final List<String> messages = new ArrayList<>();
    Item[] items = new Item[0];
    String[] attrs = new String[0];
    private MainLayout mainLayout = null;
    private Conv conv = null;

    private WebEngine webEngine = null;
    private WebView webView = null;
    private WebKitTree tree = null;

    public App() { this(null); }
    public App(String arg)
    {
		super(Strings.NAME, Strings.class, "luwrain.webinspector");
		this.arg = arg;
		try {
			this.injection = getStringResource(getClass(), "injection.js");
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
    }

	//Инициализация вьюшки (и веб движка), главногол лайаута, настройка директории для веб движка и назначение события на смену состояния (загрузка)
    @Override public AreaLayout onAppInit()
    {
		this.conv = new Conv(this);
		FxThread.runSync(()->{
			this.webView = new WebView();
			this.webEngine = webView.getEngine();
			this.webEngine.setUserDataDirectory(getLuwrain().getAppDataDir("luwrain.webins").toFile());
			this.webEngine.getLoadWorker().stateProperty().addListener((ov,oldState,newState)->onStateChanged(ov, oldState, newState));
		/*
		this.webEngine.getLoadWorker().progressProperty().addListener((ov,o,n)->events.onProgress(n));
		this.webEngine.setOnAlert((event)->events.onAlert(event.getData()));
		this.webEngine.setPromptHandler((event)->events.onPrompt(event.getMessage(),event.getDefaultValue()));
		this.webEngine.setConfirmHandler((param)->events.onConfirm(param));
		this.webEngine.setOnError((event)->events.onError(event.getMessage()));
		this.webView.setOnKeyReleased((event)->onKeyReleased(event));
		*/
			this.webView.setVisible(false);
			});
		this.mainLayout = new MainLayout(this);
		setAppName(getStrings().appName());
		return mainLayout.getAreaLayout();
    }

    @Override public boolean onEscape()
    {
		closeApp();
		return true;
    }

    @Override public void closeApp()
    {
		super.closeApp();
    }

    void updateTree()
    {
		FxThread.runSync(()->{
			this.tree = new WebKitTree(webEngine);
			//printTreeContent();
			});
		mainLayout.elementsArea.clear();
		mainLayout.elementsArea.requery();
		
    }

	void printTreeContent(){
		print("Printing elements:");
		/*
				print("Elements amount = " + tree.getGeomInfo().nodes.size());
		for(Map.Entry<Node,WebKitGeomInfo.Item> entry : tree.getGeomInfo().nodes.entrySet()) {
			print(entry.getKey().getNodeName());
			print(entry.getValue().text);
			print("X = " + entry.getValue().x);
			print("Y = " + entry.getValue().y);
			print("Width = " + entry.getValue().width);
			print("Height = " + entry.getValue().height);
		}
		*/
	}


    private void onStateChanged(ObservableValue<? extends State> ov, State oldState, State newState)
    {
		if (newState == null)
			return;

		print(newState.toString());
		switch(newState)
		{
			case SUCCEEDED:
			    new org.luwrain.web.WebKitBlocksCollector(webEngine);
						this.tree = new WebKitTree(webEngine);
						//printTreeContent();
						print("Has views 2.0: " + webEngine.getDocument().getImplementation().hasFeature("Views", "2.0"));
						getLuwrain().runUiSafely(()->{
						mainLayout.elementsArea.clear();
						mainLayout.elementsArea.requery();
						getLuwrain().playSound(Sounds.OK);
						
					});
				break;
					case FAILED:
				getLuwrain().runUiSafely(()->getLuwrain().playSound(Sounds.ERROR));
				break;
		}
    }

    public void print(String message)
    {
		messages.add(0, message);
    }

	public static void suspecting(){

	}

    Conv getConv() { return this.conv; }
    WebView getWebView() { return webView; }
    WebEngine getEngine() { return webEngine; }
    WebKitTree getTree() { return tree; } 
}

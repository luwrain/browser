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

package org.luwrain.app.browser;

import java.util.*;
import java.io.*;


import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.concurrent.Worker.State;
import javafx.beans.value.ObservableValue;

import org.luwrain.core.*;
import org.luwrain.app.base.*;
import org.luwrain.graphical.*;
import org.luwrain.web.*;

final class App extends AppBase<Strings>
{
static final String
    LOG_COMPONENT = "browser";

    private final String arg;
    private Conv conv = null;
    private MainLayout mainLayout = null;

        private     WebEngine webEngine = null;
    private WebView webView = null;
    private WebKitGeomInfo scanRes = null;

    public App() { this(null); }
    public App(String arg)
    {
	super(Strings.NAME, Strings.class);
	this.arg = arg != null?arg:"";
    }


    @Override protected AreaLayout onAppInit()
    {
	this.conv = new Conv(this);
		FxThread.runSync(()->{
		this.webView = new WebView();
		this.webEngine = webView.getEngine();
		this.webEngine.setUserDataDirectory(getLuwrain().getAppDataDir("luwrain.browser").toFile());
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

        private void onStateChanged(ObservableValue<? extends State> ov, State oldState, State newState)
    {
	if (newState == null)
	    return;
	Log .debug(LOG_COMPONENT, "browser state changed to " + newState.toString());
	switch(newState)
	{
	case SUCCEEDED:
	    getLuwrain().runUiSafely(()->getLuwrain().playSound(Sounds.OK));
	    FxThread.runSync(()->{
		    try {
			new WebKitGeomInjection(webEngine).scan();
		    }
		    catch(Throwable e)
		    {
			crash(e);
		    }
		});
	    break;
	    	case FAILED:
	    getLuwrain().runUiSafely(()->getLuwrain().playSound(Sounds.ERROR));
	    break;
	}
    }

    Conv getConv() { return this.conv; }
    WebEngine getEngine() { return webEngine; }
}

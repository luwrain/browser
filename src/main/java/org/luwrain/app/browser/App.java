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

import java.util.concurrent.*;

import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.concurrent.Worker.State;
import javafx.beans.value.ObservableValue;

import org.luwrain.core.*;
import org.luwrain.app.base.*;
import org.luwrain.graphical.*;

final class App extends AppBase<Strings>
{
    private final String arg;
    private Conversations conv = null;
    private MainLayout mainLayout = null;
        final String injection;

        private     WebEngine webEngine = null;
    private WebView webView = null;

    public App(String arg)
    {
	super(Strings.NAME, Strings.class);
	this.arg = arg != null?arg:"";
		try {
	    this.injection = getStringResource(getClass(), "injection.js");
	}
	catch(IOException e)
	{
	    throw new RuntimeException(e);
	}
    }
    public App() { this(null); }

    @Override protected AreaLayout onAppInit()
    {
	this.conv = new Conversations(this);

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
	//	print(newState.toString());
	switch(newState)
	{
	case SUCCEEDED:
	    getLuwrain().runUiSafely(()->getLuwrain().playSound(Sounds.OK));
	    break;
	    	case FAILED:
	    getLuwrain().runUiSafely(()->getLuwrain().playSound(Sounds.ERROR));
	    break;
	}
    }


    Conversations getConv() { return this.conv; }
}

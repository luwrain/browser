// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>
// Copyright 2015-2016 Roman Volovodov <gr.rPman@gmail.com>

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

import static org.luwrain.graphical.FxThread.*;

public final class App extends AppBase<Strings>
{
static final String
    LOG_COMPONENT = "browser";

    static private final boolean LOAD_INITIAL = true;

    private final String arg;
    private Conv conv = null;
    private MainLayout mainLayout = null;

        private     WebEngine webEngine = null;
    private WebView webView = null;
    private Runnable firstSwitching = null;

    public App() { this(null); }
    public App(String arg)
    {
	super(Strings.class);
	this.arg = arg != null?arg:"";
    }

    @Override protected AreaLayout onAppInit()
    {
	this.conv = new Conv(this);
		runSync(()->{
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
		if (LOAD_INITIAL)
				getLuwrain().showGraphical((graphicalModeControl)->{
		webView.setOnKeyReleased((event)->{
			switch(event.getCode())
			{
			case ESCAPE:
			     getLuwrain().runUiSafely(()->getLuwrain().playSound(Sounds.OK));
			    graphicalModeControl.close();
			    break;
			}});
		firstSwitching = ()->graphicalModeControl.close();
		webView.setVisible(true);
		getEngine().load("https://luwrain.org");
		return webView;
	    });
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
	    if (firstSwitching != null)
	    {
		firstSwitching.run();
		firstSwitching = null;
	    }
	    final var title = webEngine.getTitle();
	    final var blocks = new WebKitBlocks(webEngine).process(100);
	    getLuwrain().runUiSafely(()->{
		    final var b = new ArrayList<WebBlock>();
		    b.ensureCapacity(blocks.size());
		    blocks.forEach(i->{ if (i.visible) b.add(new WebBlock(i)); });
		    mainLayout.webArea.setBlocks(b.toArray(new WebBlock[b.size()]));
		    setAppName(title);
		    getLuwrain().playSound(Sounds.OK);
		});
	    break;
	    	case FAILED:
	    getLuwrain().runUiSafely(()->getLuwrain().playSound(Sounds.ERROR));
	    break;
	}
    }

    Conv getConv() { return this.conv; }
    public WebEngine getEngine() { return webEngine; }
    public WebView getView() { return webView; }
}

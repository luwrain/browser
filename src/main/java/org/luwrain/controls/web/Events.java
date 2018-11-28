/*
   Copyright 2012-2018 Michael Pozhidaev <michael.pozhidaev@gmail.com>
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

package org.luwrain.controls.web;

import org.luwrain.core.*;
import org.luwrain.browser.*;
import org.luwrain.controls.web.WebArea.Callback.MessageType;

final class Events implements org.luwrain.browser.BrowserEvents
{
    private final WebArea.ClientThread clientThread;
    private final WebArea area;
    private final WebArea.Callback callback;

    Events(WebArea.ClientThread clientThread, WebArea area, WebArea.Callback callback)
    {
	NullCheck.notNull(clientThread, "clientThread");
	NullCheck.notNull(area, "area");
	this.clientThread = clientThread;
	this.area = area;
	this.callback = callback;
    }

    @Override public void onChangeState(State state)
    {
	if (state == null)
	    return;
	clientThread.runAsync(()->area.onNewState(state));
    }

    @Override public void onProgress(Number progress)
    {
	if (progress == null)
	    return;
	clientThread.runAsync(()->area.onProgress(progress.intValue()));
    }

    @Override public void onAlert(String message)
    {
	if (message == null)
	    return;
	clientThread.runAsync(()->callback.message(message, MessageType.ALERT));
    }

    @Override public String onPrompt(String message, String value)
    {
	if (message == null || value == null)
	    return "";
	return (String)clientThread.runSync(()->callback.prompt(message, value));
    }

    @Override public void onError(String message)
    {
	if (message == null)
	    return;
	NullCheck.notNull(message, "message");
	clientThread.runAsync(()->callback.message(message, MessageType.ERROR));
    }

    @Override public boolean onDownloadStart(String url)
    {
	if (url == null)
	    return true;
	clientThread.runAsync(()->area.onDownloadStart(url));
    	return true;
    }

    @Override public Boolean onConfirm(String message)
    {
	if (message == null)
	    return false;
	return (boolean)clientThread.runSync(()->callback.confirm(message));
    }
};

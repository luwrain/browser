
package org.luwrain.controls.web;

import org.luwrain.core.*;
import org.luwrain.browser.*;
import org.luwrain.controls.web.WebArea.Callback.MessageType;

class Events implements org.luwrain.browser.BrowserEvents
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
	clientThread.runAsync(()->area.onPageChangeState(state));
    }

    @Override public void onProgress(Number progress)
    {
	if (progress == null)
	    return;
	clientThread.runAsync(()->area.onProgress(progress));
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

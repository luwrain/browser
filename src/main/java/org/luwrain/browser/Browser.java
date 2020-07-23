/*
   Copyright 2012-2020 Michael Pozhidaev <msp@luwrain.org>
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

//LWR_API 1.0

package org.luwrain.browser;

import java.util.Vector;
import java.util.concurrent.Callable;

import org.luwrain.core.*;

/**
 * An interface to web-browser engine. This interface provides
 * web-browser functionality with JavaScript support. The corresponding
 * back-end is based on WebKit implementation included in JavaFX.
 *
 * The methods of this interface don't cover any features related to the
 * view of the loaded page in any form. However, the client application
 * can request showing traditional graphical view of the loaded page.
 * The content of the loaded page is represented by a set of elements
 * which can be enumerated with {@link BrowserIterator} class.
 *
 * @see BrowserIterator Events
 */
public interface Browser extends org.luwrain.base.GraphicalMode
{
    static public final String GRAPHICAL_MODE_NAME = "browser";

    void close();
    void loadByUrl(String url);
    void loadByText(String text);
    Object executeScript(String script);
    BrowserIterator createIterator();
    String getTitle();
    String getUrl();
    boolean getVisibility();
    void setVisibility(boolean visible);
    int getElementCount();
    void update();
    Object runSafely(Callable callable);
    void stop();
    boolean goHistoryPrev();

    static public Browser create(Luwrain luwrain, BrowserEvents events)
    {
	NullCheck.notNull(events, "events");
	final BrowserParams params = new BrowserParams();
	params.events = events;
	return (Browser)luwrain.openGraphicalMode(GRAPHICAL_MODE_NAME, params);
    }
}

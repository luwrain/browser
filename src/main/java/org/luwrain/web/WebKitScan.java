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

package org.luwrain.web;

import javafx.scene.web.WebEngine;
import netscape.javascript.*;

public final class WebKitScan
{
    	static private String injection = "";
    final WebEngine engine;

    public WebKitScan(WebEngine engine)
    {
	this.engine = engine;
	    }

    public WebKitScanResult scan()
    {
			final Object res = engine.executeScript(injection);
		if (res == null)
		    throw new RuntimeException("The result of web scanning is null");
		if (!(res instanceof JSObject))
		    throw new RuntimeException("The result of web scanning is not an instance of JSObject");
		final JSObject jsRes = (JSObject)res;
return new WebKitScanResult(engine, jsRes);
    }
    }

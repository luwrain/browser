/*
   Copyright 2012-2024 Michael Pozhidaev <msp@luwrain.org>

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

import java.util.*;

import javafx.scene.web.WebEngine;
import org.w3c.dom.html.*;

public final class WebKitBlocks
{
    final WebEngine engine;
    final HTMLDocument doc;
    final HTMLBodyElement body;

    public WebKitBlocks(WebEngine engine)
    {
	this.engine = engine;
	        this.doc = (HTMLDocument)engine.documentProperty().getValue();
        this.body = (HTMLBodyElement)doc.getBody();
    }

    public void process()
    {
	final var c = new WebKitBlocksCollector(engine);
	c.process(body);
    }
}

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

import java.io.IOException;
import java.util.*;

import javafx.concurrent.Worker;
import javafx.scene.web.WebEngine;
import netscape.javascript.JSObject;
import org.w3c.dom.html.*;

import static org.luwrain.app.webinspector.App.log;
import static org.luwrain.util.ResourceUtils.getStringResource;

public final class WebKitBlocks
{
	static private final String MUTATION_OBSERVER_INJECTION = "mutation_observer_injection.js";
	static private String mutationObserverInjection = null;

    public final List<WebKitBlock> blocks = new ArrayList<>();

    final WebEngine engine;
    final HTMLDocument doc;
    final HTMLBodyElement body;

	private boolean needsToBeUpdated = false;

    public WebKitBlocks(WebEngine engine)
    {
		this.engine = engine;
		this.doc = (HTMLDocument)engine.documentProperty().getValue();
        this.body = (HTMLBodyElement)doc.getBody();
		enableMutationObserver();
    }

    public List<WebKitBlock> process(int desiredWidth)
    {
	try {
	    if (desiredWidth <= 0)
		throw new IllegalArgumentException("desiredWidth (" + desiredWidth + ") must be a positive number");
	    blocks.clear();
	    final var c = new WebKitBlocksCollector(engine);
	    c.process(body);
	    blocks.addAll(c.blocks);
	    int maxWidth = 0;
	    for(var b: blocks)
		maxWidth = Math.max(maxWidth, b.right);
	    final float scale = Float.valueOf(desiredWidth) / maxWidth;
	    log("Scale is " + String.format("%.2f", scale));
	    blocks.parallelStream().forEach(b->b.rescale(scale));
	    log("Building lines");
	    blocks.forEach(b->b.buildLines());//FIXME:parallelStream
	    log("Building of lines completed");
	    new BlockGeom(blocks).process();
	    return blocks;
	}
    catch(Throwable e)
    {
	log("Exception: " + e.getClass().getSimpleName());
	log("Message: " + e.getMessage());
	return Arrays.asList();
    }
    }

	public void enableMutationObserver()
	{
		try {
			if (mutationObserverInjection == null)
			{
				mutationObserverInjection = getStringResource(getClass(), MUTATION_OBSERVER_INJECTION);
				engine.getLoadWorker().stateProperty().addListener((observableValue, oldState, newState) ->
				{
					if (newState == Worker.State.SUCCEEDED)
					{
						JSObject jsObject = (JSObject) engine.executeScript("window");
						jsObject.setMember("webKitBlocks", this);

						engine.executeScript(mutationObserverInjection);
					}
				});
			}
		}
		catch (IOException e)
		{
			log("Can not enable mutation observer injection: " + e.getMessage());
		}
		catch (NullPointerException e)
		{
			log("Can not find mutation observer injection resource " + MUTATION_OBSERVER_INJECTION);
		}
	}

	public boolean isNeedsToBeUpdated()
	{
		return needsToBeUpdated;
	}

	public void setNeedsToBeUpdated(boolean needsToBeUpdated)
	{
		this.needsToBeUpdated = needsToBeUpdated;
	}
}

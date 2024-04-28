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

package org.luwrain.app.browser.layouts;

import java.util.*;
import java.util.concurrent.atomic.*;

import org.luwrain.controls.*;
import org.luwrain.app.base.*;
import org.luwrain.app.browser.*;

import static org.luwrain.graphical.FxThread.*;

public final class InfoLayout extends LayoutBase
{
    final App app;
    final SimpleArea textArea;

    public InfoLayout(App app, ActionHandler closing)
    {
	super(app);
	this.app = app;
	final var title = new AtomicReference<String>(null);
	final var location = new AtomicReference<String>(null);
	runSync(()->{
		title.set(app.getEngine().getTitle());
				location.set(app.getEngine().getLocation());
	    });
	textArea = new SimpleArea(getControlContext(), "Информация о странице", new String[]{
		"",
		location.get(),
		title.get(),
		""
	    });

	setAreaLayout(textArea, null);
	setCloseHandler(closing);
    }
}

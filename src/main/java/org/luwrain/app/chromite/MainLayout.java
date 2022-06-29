/*
   Copyright 2012-2022 Michael Pozhidaev <msp@luwrain.org>

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

package org.luwrain.app.chromite;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.app.base.*;

import static org.luwrain.util.TextUtils.*;

final class MainLayout extends LayoutBase
{
    private final App app;
    private final SimpleArea webArea;

    MainLayout(App app)
    {
	super(app);
	this.app = app;
	webArea = new SimpleArea(getControlContext(), "Chromite");
	setAreaLayout(webArea, actions(
				       action("test", "Открыть", new InputEvent(InputEvent.Special.F6), this::actOpen),
				       action("refresh", "Обновить", new InputEvent(InputEvent.Special.F5), this::actRefresh)
				       ));
    }

    private boolean actOpen()
    {
	final String url = app.getConv().openUrl("https://");
	if (url == null || url.trim().isEmpty())
	    return true;
	app.getChromite().navigate(url.trim());
	return true;
    }

    private boolean actRefresh()
    {
	final String html = app.getChromite().getHtml();
	webArea.update(lines->lines.setLines(splitLinesAnySeparator(html)));
	return true;
    }
}

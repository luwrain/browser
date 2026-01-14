// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>
// Copyright 2015-2016 Roman Volovodov <gr.rPman@gmail.com>

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

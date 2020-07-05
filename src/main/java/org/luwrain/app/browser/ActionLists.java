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

package org.luwrain.app.browser;

import org.luwrain.core.*;
import org.luwrain.core.events.*;

final class ActionLists
{
    private final Luwrain luwrain;
    private final Strings strings;

    ActionLists(Luwrain luwrain, Strings strings)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(strings, "strings");
	this.luwrain = luwrain;
	this.strings = strings;
    }

    Action[] getBrowserActions()
    {
	return new Action[]{
	    new Action("open-url", strings.actionOpenUrl(), new InputEvent(InputEvent.Special.F6)),
	    new Action("refresh", strings.actionRefresh(), new InputEvent(InputEvent.Special.F5)),
	    new Action("stop", strings.actionStop(), new InputEvent(InputEvent.Special.ESCAPE)),
	    new Action("history-prev", strings.actionHistoryPrev(), new InputEvent(InputEvent.Special.BACKSPACE)),
	    new Action("show-graphical", strings.actionShowGraphical(), new InputEvent(InputEvent.Special.F10)),
	};
    }
}

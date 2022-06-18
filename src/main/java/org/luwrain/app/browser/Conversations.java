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

package org.luwrain.app.browser;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.popups.*;
import org.luwrain.settings.browser.Settings;

class Conversations
{
    private final Luwrain luwrain;
    private final Settings sett;
    private final Set<String> openUrlHistory = new HashSet<String>();

    Conversations(App app)
    {
	NullCheck.notNull(app, "app");
	this.luwrain = app.getLuwrain();
	this.sett = Settings.create(luwrain.getRegistry());
    }

    String openUrl(String initialValue)
    {
	NullCheck.notNull(initialValue, "initialValue");
	final String res = Popups.editWithHistory(luwrain, "Открытие страницы", "Адрес:", initialValue, openUrlHistory, Popups.DEFAULT_POPUP_FLAGS);
	if (res == null || res.trim().isEmpty())
	    return null;
	return res;
    }

        String formTextEdit(String prevValue)
    {
	NullCheck.notNull(prevValue, "prevValue");
	return Popups.text(luwrain, "Редактирования формы", "Текст в поле:", prevValue);
    }
    }

/*
   Copyright 2012-2018 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

package org.luwrain.app.webinspector;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.popups.*;

final class Conversations
{
    private final Luwrain luwrain;
    private final Set<String> openUrlHistory = new HashSet<String>();

    Conversations(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	this.luwrain = luwrain;
    }

    String openUrl(String initialValue)
    {
	NullCheck.notNull(initialValue, "initialValue");
	final String res = Popups.editWithHistory(luwrain, "Открытие страницы", "Адрес:", initialValue, openUrlHistory, Popups.DEFAULT_POPUP_FLAGS);
	if (res == null || res.trim().isEmpty())
	    return null;
	return res;
    }

        String formText(String prevValue)
    {
	NullCheck.notNull(prevValue, "prevValue");
	return Popups.simple(luwrain, "Редактирования формы", "Текст в поле:", prevValue);
    }
    }

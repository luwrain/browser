// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>
// Copyright 2015-2016 Roman Volovodov <gr.rPman@gmail.com>

package org.luwrain.app.browser;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.popups.*;
import org.luwrain.settings.browser.Settings;

import static org.luwrain.popups.Popups.*;

final class Conv
{
    private final Luwrain luwrain;
    private final Settings sett;
    private final Set<String> openUrlHistory = new HashSet<String>();

    Conv(App app)
    {
	this.luwrain = app.getLuwrain();
	this.sett = null;//FIXME:newreg Settings.create(luwrain.getRegistry());
    }

    String openUrl(String initialValue)
    {
	final String res = editWithHistory(luwrain, "Открытие страницы", "Адрес:", initialValue, openUrlHistory, Popups.DEFAULT_POPUP_FLAGS);
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

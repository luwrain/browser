// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>
// Copyright 2015-2016 Roman Volovodov <gr.rPman@gmail.com>

package org.luwrain.app.chromite;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.popups.*;

final class Conv
{
    private final Luwrain luwrain;
    private final Strings strings;
    private final Set<String> openUrlHistory = new HashSet<>();

    Conv(App app)
    {
	this.luwrain = app.getLuwrain();
	this.strings = app.getStrings();
    }

    String openUrl(String initialValue)
    {
	final String res = Popups.editWithHistory(luwrain, "Открытие страницы", "Адрес:", initialValue, openUrlHistory, Popups.DEFAULT_POPUP_FLAGS);
	if (res == null || res.trim().isEmpty())
	    return null;
	return res;
    }
}

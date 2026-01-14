// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>
// Copyright 2015-2016 Roman Volovodov <gr.rPman@gmail.com>

package org.luwrain.app.webinspector;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.popups.*;

final class Conv
{
    private final Luwrain luwrain;
    private final Set<String> openUrlHistory = new HashSet<String>();

    Conv(App app)
    {
	NullCheck.notNull(app, "app");
	this.luwrain = app.getLuwrain();
    }

        String formText(String prevValue)
    {
	NullCheck.notNull(prevValue, "prevValue");
	return Popups.text(luwrain, "Редактирования формы", "Текст в поле:", prevValue);
    }
    }

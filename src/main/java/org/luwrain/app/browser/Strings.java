// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>
// Copyright 2015-2016 Roman Volovodov <gr.rPman@gmail.com>

package org.luwrain.app.browser;

import org.luwrain.core.annotations.*;

@ResourceStrings(langs = { "en", "ru" })
public interface Strings
{
    String actionHistoryPrev();
    String actionOpenUrl();
    String actionRefresh();
    String actionShowGraphical();
    String actionStop();
    String appName();
    String loading();
    String settHomePage();
    String settRunJavaScript();
    String settSectionName();
    String settUserAgent();
}

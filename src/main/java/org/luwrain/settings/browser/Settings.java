// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>
// Copyright 2015-2016 Roman Volovodov <gr.rPman@gmail.com>

package org.luwrain.settings.browser;

import org.luwrain.core.*;

public interface Settings
{
    static final String PATH = "/org/luwrain/browser";

    String getHomePage(String defValue);
    void setHomePage(String value);
    String getUserAgent(String defValue);
    void setUserAgent(String value);
    boolean getJavaScriptEnabled(boolean defValue);
    void setJavaScriptEnabled(boolean value);

    static public Settings create(Registry registry)
    {
	NullCheck.notNull(registry, "registry");
	registry.addDirectory(PATH);
	return RegistryProxy.create(registry, PATH, Settings.class);
    }
}

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

public interface Strings
{
	static final String NAME = "luwrain.browser";

        String appName();
        String actionOpenUrl();
        String actionRefresh();
        String actionStop();
        String actionHistoryPrev();
        String actionShowGraphical();
    String loading();
    String settHomePage();
    String settUserAgent();
    String settRunJavaScript();
    String settSectionName();
}

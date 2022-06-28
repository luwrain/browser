/*
   Copyright 2012-2022 Michael Pozhidaev <msp@luwrain.org>

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

package org.luwrain.app.chromite;

import com.github.kklisura.cdt.launch.ChromeLauncher;
import com.github.kklisura.cdt.protocol.commands.Page;
import com.github.kklisura.cdt.services.ChromeDevToolsService;
import com.github.kklisura.cdt.services.ChromeService;
import com.github.kklisura.cdt.services.types.ChromeTab;
import com.github.kklisura.cdt.services.impl.*;


import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.app.base.*;

final class MainLayout extends LayoutBase
{
    private final SimpleArea webArea;

    MainLayout(App app)
    {
	super(app);
	webArea = new SimpleArea(getControlContext(), "Chromite");
		setAreaLayout(webArea, actions(
					       action("Тест", "test", this::actTest)
));
    }

    private boolean actTest()
    {
	final ChromeService chromeService = new ChromeServiceImpl("localhost", 9222);
      final ChromeTab tab = chromeService.createTab();
      try (final ChromeDevToolsService devToolsService = chromeService.createDevToolsService(tab)) {
        final Page page = devToolsService.getPage();
        page.navigate("http://github.com");
      }
      chromeService.closeTab(tab);
    return true;
    }

    static public void main(String[] args)
    {

	    // Create chrome launcher.
	//    try (final ChromeLauncher launcher = new ChromeLauncher()) {
      // Launch chrome either as headless (true) or regular (false).
	//      final ChromeService chromeService = launcher.launch(false);
	final ChromeService chromeService = new ChromeServiceImpl(9222);

      // Create empty tab ie about:blank.
      final ChromeTab tab = chromeService.createTab();

      // Get DevTools service to this tab
      try (final ChromeDevToolsService devToolsService = chromeService.createDevToolsService(tab)) {
        final Page page = devToolsService.getPage();

        // Navigate to github.com.
        page.navigate("http://github.com");

        // Wait a while...
	//        Thread.sleep(2000);

        // Navigate to twitter.com.
	//        page.navigate("http://twitter.com");

        // Wait a while...
	//        Thread.sleep(2000);
      }

      // Close the tab.
      chromeService.closeTab(tab);

	
      //    }
    }



}

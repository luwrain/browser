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

package org.luwrain.web.chromite;

import com.github.kklisura.cdt.launch.ChromeLauncher;
import com.github.kklisura.cdt.protocol.commands.Page;
import com.github.kklisura.cdt.services.ChromeDevToolsService;
import com.github.kklisura.cdt.services.ChromeService;
import com.github.kklisura.cdt.services.types.ChromeTab;
import com.github.kklisura.cdt.services.impl.*;
import com.github.kklisura.cdt.protocol.commands.Runtime;
import com.github.kklisura.cdt.protocol.types.runtime.Evaluate;

import org.luwrain.core.*;


public class Chromite
{
        private boolean actTest()
    {
	final ChromeService chromeService = new ChromeServiceImpl("localhost", 9222);
      final ChromeTab tab = chromeService.createTab();
      try (final ChromeDevToolsService devToolsService = chromeService.createDevToolsService(tab)) {
        final Page page = devToolsService.getPage();
    final Runtime runtime = devToolsService.getRuntime();
    page.onLoadEventFired((event)->{
	    			  Log.debug("proba", "on load");
				  /*
          final Evaluate evaluation = runtime.evaluate("document.documentElement.outerHTML");
          Log.debug("proba", evaluation.getResult().getValue().toString());
	  //	            devToolsService.close();
	  */
        });
    page.enable();
    Log.debug("proba", "navigating");
        page.navigate("https://luwrain.org");
	try {
	Thread.sleep(10000);
	}
	catch(Exception e)
	{
	}
	          final Evaluate evaluation = runtime.evaluate("document.documentElement.outerHTML");
          Log.debug("proba", evaluation.getResult().getValue().toString());

      }
      chromeService.closeTab(tab);
    return true;
    }
    }

// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>
// Copyright 2015-2016 Roman Volovodov <gr.rPman@gmail.com>

package org.luwrain.web.chromite;

import java.util.*;
import java.io.*;
import com.google.gson.*;

import com.github.kklisura.cdt.launch.ChromeLauncher;
import com.github.kklisura.cdt.protocol.commands.Page;
import com.github.kklisura.cdt.services.ChromeDevToolsService;
import com.github.kklisura.cdt.services.ChromeService;
import com.github.kklisura.cdt.services.types.ChromeTab;
import com.github.kklisura.cdt.services.impl.*;
import com.github.kklisura.cdt.protocol.commands.Runtime;
import com.github.kklisura.cdt.protocol.types.runtime.Evaluate;

import org.luwrain.core.*;

public class Chromite implements AutoCloseable
{
    final ChromeService chromeService;
    final ChromeTab tab;
    final ChromeDevToolsService devToolsService;
    final Page page;
    final Runtime runtime;
    final Gson gson = new Gson();

    public Chromite()
    {
	this.chromeService = new ChromeServiceImpl("localhost", 9222);
	this.tab = this.chromeService.createTab();
	this.devToolsService = this.chromeService.createDevToolsService(tab);
	this.page = devToolsService.getPage();
	this.runtime = this.devToolsService.getRuntime();
    }

    @Override public void close()
    {
	this.devToolsService.close();
	this.chromeService.closeTab(tab);
    }

    public void navigate(String url)
    {
	this.page.navigate(url);
    }

    public String getHtml()
    {
		final Evaluate evaluation = runtime.evaluate("document.documentElement.outerHTML");
	return evaluation.getResult().getValue().toString();
    }

        public Elements getPage()
    {
final String script = concatLines(readResource("page.js"));
	final Evaluate evaluation = runtime.evaluate(script);
	return gson.fromJson(evaluation.getResult().getValue().toString(), Elements.class);
    }





    private void setOnLoadEvent()
    {
	page.onLoadEventFired((event)->{
	    });
    }

        String[] readResource(String resName)
    {
	try {
	final List<String> res = new ArrayList<>();
	try (final BufferedReader r = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(resName), "UTF-8"))) {
	    String line = r.readLine();
	    while(line != null)
	    {
		res.add(line);
		line = r.readLine();
	    }
	}
	return res.toArray(new String[res.size()]);
	}
	catch(IOException e)
	{
	    throw new RuntimeException(e);
	}
    }

    static String concatLines(String[] lines)
    {
	final StringBuilder b = new StringBuilder();
	for(String l: lines)
	    b.append(l).append(" ");
	return new String(b);
    }
}

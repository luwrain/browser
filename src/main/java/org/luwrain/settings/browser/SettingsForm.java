// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>
// Copyright 2015-2016 Roman Volovodov <gr.rPman@gmail.com>

package org.luwrain.settings.browser;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.cpanel.*;

final class SettingsForm extends FormArea implements SectionArea
{
    private final ControlPanel controlPanel;
    private final Luwrain luwrain;
    private final Registry registry;
    private final Settings sett;
    private final Strings strings;

    SettingsForm(ControlPanel controlPanel, Strings strings)
    {
	super(new DefaultControlContext(controlPanel.getCoreInterface()), strings.sectionName());
	NullCheck.notNull(controlPanel, "controlPanel");
	NullCheck.notNull(strings, "strings");
	this.controlPanel = controlPanel;
	this.luwrain = controlPanel.getCoreInterface();
	this.registry = null;//FIXME:newreg luwrain.getRegistry();
	this.sett = Settings.create(registry);
	this.strings = strings;
	fillForm();
    }

    private void fillForm()
    {
	addEdit("home-page", strings.homePage(), sett.getHomePage(""));
	addEdit("user-agent", strings.userAgent(), sett.getUserAgent(""));
	addCheckbox("java-script-enabled", strings.javaScriptEnabled(), sett.getJavaScriptEnabled(true));
    }

    @Override public boolean saveSectionData()
    {
	sett.setHomePage(getEnteredText("home-page"));
	sett.setUserAgent(getEnteredText("user-agent"));
	sett.setJavaScriptEnabled(getCheckboxState("java-script-enabled"));
	return true;
    }

    @Override public boolean onInputEvent(InputEvent event)
    {
	NullCheck.notNull(event, "event");
	if (controlPanel.onInputEvent(event))
	    return true;
	return super.onInputEvent(event);
    }

    @Override public boolean onSystemEvent(SystemEvent event)
    {
	NullCheck.notNull(event, "event");
	if (controlPanel.onSystemEvent(event))
	    return true;
	return super.onSystemEvent(event);
    }
}

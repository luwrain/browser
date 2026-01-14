// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>
// Copyright 2015-2016 Roman Volovodov <gr.rPman@gmail.com>

package org.luwrain.settings.browser;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.popups.Popups;
import org.luwrain.cpanel.*;

public final class SettingsFactory implements org.luwrain.cpanel.Factory
{
    private final Luwrain luwrain;
    private final SimpleElement browserElement = new SimpleElement(StandardElements.APPLICATIONS, this.getClass().getName());

    public SettingsFactory(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	this.luwrain = luwrain;
    }

    @Override public Element[] getElements()
    {
	return new Element[]{browserElement};
    }

    @Override public Element[] getOnDemandElements(Element parent)
    {
	NullCheck.notNull(parent, "parent");
	return new Element[0];
    }

    @Override public Section createSection(Element el)
    {
	NullCheck.notNull(el, "el");
	final Object o = luwrain.i18n().getStrings(Strings.NAME);
	if (o == null || !(o instanceof Strings))
	    return null;
	final Strings strings = (Strings)o;
	if (el.equals(browserElement))
	    return new SimpleSection(el, strings.sectionName(), (controlPanel)->{return new SettingsForm(controlPanel, strings);});
	return null;
    }
}

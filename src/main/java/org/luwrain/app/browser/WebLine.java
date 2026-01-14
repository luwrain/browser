// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>
// Copyright 2015-2016 Roman Volovodov <gr.rPman@gmail.com>

package org.luwrain.app.browser;

import java.util.*;

import org.luwrain.controls.block.*;
import org.luwrain.web.WebKitBlockBase.*;

import static org.luwrain.core.NullCheck.*;

final class WebLine implements BlockLine
{
    final String text;
    final Line srcLine;

    WebLine(String text)
    {
	notNull(text, "text");
	this.text = text;
	this.srcLine = null;
    }

    WebLine(Line srcLine)
    {
	notNull(srcLine, "srcLine");
	this.text = srcLine.text;
	this.srcLine = srcLine;
    }
}

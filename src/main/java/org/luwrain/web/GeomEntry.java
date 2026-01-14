// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>
// Copyright 2015-2016 Roman Volovodov <gr.rPman@gmail.com>

package org.luwrain.web;

public final class GeomEntry
{
    public final int x, y, width, height;
    public final String text;

    GeomEntry(int x, int y, int width, int height, String text)
    {
	this.x = x;
	this.y = y;
	this.width = width;
	this.height = height;
	this.text = text;
    }
}

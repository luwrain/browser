// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>
// Copyright 2015-2016 Roman Volovodov <gr.rPman@gmail.com>

package org.luwrain.web.chromite;

import java.util.*;

public final class Element
{
    private String
	tagName = null;

    private Integer
	x = null, y = null,
	width = null, height = null;

    private List<Element> children = null;

    public String getTagName() { return tagName != null?tagName:""; }
    public int getX() { return x != null?x.intValue():0; }
    public int getY() { return y != null?y.intValue():0; }
    public int getWidth() { return width != null?width.intValue():0; }
    public int getHeight() { return height != null?height.intValue():0; }
    public Element[] getChildren() { return children != null?children.toArray(new Element[children.size()]):new Element[0]; }
    @Override public String toString() { return getTagName(); }
}

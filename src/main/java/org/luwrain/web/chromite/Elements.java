// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>
// Copyright 2015-2016 Roman Volovodov <gr.rPman@gmail.com>

package org.luwrain.web.chromite;

import java.util.*;

public final class Elements
{
private List<Element> children = null;

    public Element[] getChildren() { return children != null?children.toArray(new Element[children.size()]):new Element[0]; } 
}

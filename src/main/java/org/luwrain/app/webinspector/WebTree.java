// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>
// Copyright 2015-2016 Roman Volovodov <gr.rPman@gmail.com>

package org.luwrain.app.webinspector;

import java.util.*;
import java.util.concurrent.atomic.*;

import javafx.scene.web.WebEngine;

import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.html.HTMLDocument;
import org.w3c.dom.html.HTMLBodyElement;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.views.DocumentView;

import com.sun.webkit.dom.DOMWindowImpl;
import netscape.javascript.*;

import org.luwrain.core.*;

import static org.luwrain.graphical.FxThread.*;
import static org.luwrain.web.WebKitGeom.*;

final class WebTree
{
    final WebEngine engine;
    final HTMLDocument doc;
    final DOMWindowImpl window;
    final HTMLBodyElement body;
    final WebObject root;

WebTree(WebEngine engine)
    {
        ensure();
        this.engine = engine;
        this.doc = (HTMLDocument)engine.documentProperty().getValue();
        this.window = (DOMWindowImpl)((DocumentView)doc).getDefaultView();
        this.body = (HTMLBodyElement)doc.getBody();
	this.root = new WebObject(this, this.body);
    }

    CSSStyleDeclaration getStyle(Element el)
    {
        final AtomicReference<CSSStyleDeclaration> res = new AtomicReference<>();
        runSync(()->res.set(window.getComputedStyle(el, "")));
        return res.get();
    }
}

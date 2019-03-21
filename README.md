
# WebKit-based browser engine for the blind

This repository contains the code responsible of transforming the structures of the [WebKit](https://en.wikipedia.org/wiki/WebKit) engine
to the the form suitable for reading and navigating by blind people.
The code itself doesn't deal with low-level information and uses only the things provided
by the [org.luwrain.browser.Browser](http://luwrain.org/api/org/luwrain/browser/Browser.html) interface,
which serves as a bridge to the WebKit internals .
You can find its implementation in [interaction-javafx](https://github.com/luwrain/interaction-javafx/) repository.

The general aim is to have an accessible to blind people web-browser with proper support of JavaScript.
The WebKit implementation is used as it comes in the [JavaFX](https://en.wikipedia.org/wiki/JavaFX) platform.

In addition, this repository provides a structure suitable for reading structural text documents.
Besides the browser, it is very useful for viewing office documents.

This repository is a part of the LUWRAIN project.
LUWRAIN is a platform for the creation of apps for the blind and partially-sighted.
Please visit [luwrain.org](http://luwrain.org/?lang=en) for further information.


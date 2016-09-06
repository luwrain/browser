
package org.luwrain.doctree.loading;

import java.net.*;

public interface UrlLoaderFactory
{
    UrlLoader newUrlLoader(URL url) throws MalformedURLException;
}

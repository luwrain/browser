
package org.luwrain.doctree;

import java.util.*;
import java.net.*;

public interface Book
{
    Document[] getDocuments();
    Map<URL, Document> getDocumentsWithUrls();
    Document getFirstDocument();
}

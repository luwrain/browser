
package org.luwrain.util;

import java.net.*;

public class Opds
{
    static public class Entry 
{
    public String id;
    public String title;
    public String link;
    }

static public class Directory
{
    public Entry[] entries;
}

    static public Entry[] fetch(URL url)
    {
	return new Entry[0];
    }
}

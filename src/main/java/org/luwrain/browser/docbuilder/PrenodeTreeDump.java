
package org.luwrain.browser.docbuilder;

import java.io.*;

import org.luwrain.core.*;
import org.luwrain.browser.*;

class PrenodeTreeDump
{
    static private final String LOG_COMPONENT = "browser-docbuilder-dumps";
    
    private final Browser browser;
    private final Prenode root;
    private String destDir;
    private final File rootDir;

    PrenodeTreeDump(Browser browser, Prenode root, String destDir)
    {
	NullCheck.notNull(browser, "browser");
	NullCheck.notNull(root, "root");
	NullCheck.notEmpty(destDir, "destDir");
	this.browser = browser;
	this.root = root;
	this.destDir = destDir;

	final File destDirFile = new File(destDir);
	final String url = browser.getUrl();
	if (url != null && !url.isEmpty())
	{
	final StringBuilder b = new StringBuilder();
	for(int i = 0;i < url.length();++i)
	{
	    final char c = url.charAt(i);
	    if (Character.isLetter(c) || Character.isDigit(c))
		b.append("" + c); else
		b.append("-");
	}
	this.rootDir = new File(destDirFile, new String(b));
	} else
	    this.rootDir = new File(destDirFile, "NO-URL");
	    }

    void dump()
    {
	Log.debug(LOG_COMPONENT, "Starting dump in " + rootDir.getAbsolutePath());
	try {
	    if (!(new File(destDir).isDirectory()))
		return;
		dumpPrenode(rootDir, root);
		return;
	}
	catch(IOException e)
	{
	    Log.error(LOG_COMPONENT, "unable to make a prenode tree dump:" + e.getClass().getName() + ":" + e.getMessage());
	}
    }

    private void dumpPrenode(File dir, Prenode prenode) throws IOException
    {
	NullCheck.notNull(dir, "dir");
	NullCheck.notNull(prenode, "prenode");
	if (dir.exists())
	    return;
	dir.mkdir();
	int counter = 1;
	for(Prenode n: prenode.children)
	{
	    final String subdir = formatInt(counter) + n.tagName;
	    ++counter;
	    dumpPrenode(new File(dir, subdir), n);
	}
    }

    private String formatInt(int value)
    {
	if (value < 10)
	    return "00" + value;
	if (value < 100)
	    return "0" + value;
	return "" + value;
    }
}

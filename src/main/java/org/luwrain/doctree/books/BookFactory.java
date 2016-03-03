
package org.luwrain.doctree.books;

import java.net.*;
import java.io.*;
import java.nio.file.*;

import org.luwrain.core.NullCheck;
import org.luwrain.core.Log;
import org.luwrain.doctree.*;
import org.luwrain.util.*;

public class BookFactory
{
    static public Book initDaisyBookBySmil(Path smilFile, URL urlBase)
    {
	final Daisy2 book = new Daisy2();
	book.loadSmil(smilFile, urlBase);
	return book;
    }
}

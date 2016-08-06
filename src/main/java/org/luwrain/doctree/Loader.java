
package org.luwrain.doctree;

import java.net.*;
import java.io.*;
import java.nio.file.*;
import java.nio.charset.*;
import java.util.*;
import java.util.zip.*;
import javax.activation.*;

import org.apache.poi.util.IOUtils;
import org.luwrain.core.*;
import org.luwrain.doctree.filters.*;
import org.luwrain.doctree.books.BookFactory;

public class Loader
{
    public enum Format {
	TEXT_PARA_EMPTY_LINE, TEXT_PARA_INDENT, TEXT_PARA_EACH_LINE,
	HTML, XML, DOC, DOCX,
	FB2, EPUB, SMIL,
	ZIP, FB2_ZIP,
    };

    static public final String USER_AGENT = "Mozilla/5.0";
    static private final String DEFAULT_CHARSET = "UTF-8";

    private URL requestedUrl;
    private String requestedContentType;
    private URL responseUrl;
    private String responseContentType;
    private String responseContentEncoding;
    private int httpCode;
    private Path tmpFile;
    private String selectedContentType;
    private String selectedCharset;

    public Loader(URL url) throws MalformedURLException
    {
	NullCheck.notNull(url, "url");
requestedUrl = new URL(url.getProtocol(), IDN.toASCII(url.getHost()),
url.getPort(), url.getFile());
requestedContentType = "";
    }

    public Loader(URL url, String contentType) throws MalformedURLException
    {
	NullCheck.notNull(url, "url");
	NullCheck.notNull(contentType, "contentType");
requestedUrl = new URL(url.getProtocol(), IDN.toASCII(url.getHost()),
url.getPort(), url.getFile());
requestedContentType = contentType;
    }

    public Result load() throws IOException
    {
	fetch();
	selectedContentType = requestedContentType.isEmpty()?responseContentType:requestedContentType;
	if (selectedContentType.isEmpty())
	    return new Result(Result.Type.UNRECOGNIZED_FORMAT);
	final Format format = chooseFilterByContentType(selectedContentType);
	if (format == null)
	    return new Result(Result.Type.UNRECOGNIZED_FORMAT);
	selectCharset(format);
final Result res = parse(format);
//	    res.setProperty("format", filter.toString());
//	    res.setProperty("charset", effectiveCharset);
//	    res.setProperty("url", baseUrl.toString());
return res;
    }

    private boolean fetch() throws IOException
    {
	//	InputStream is = null;
	//	InputStream effectiveIs = null;
	InputStream responseStream = null;
	try {
	URLConnection con;
	    Log.debug("doctree", "opening connection for " + requestedUrl.toString());
	    con = requestedUrl.openConnection();
	    while(true)
	    {
	    con.setRequestProperty("User-Agent", USER_AGENT);
	    con.connect();
		if (!(con instanceof HttpURLConnection))
		    break;//Considering everything is OK, but lines below are pointless
		final HttpURLConnection httpCon = (HttpURLConnection)con;
httpCode = httpCon.getResponseCode();
		Log.debug("doctree", "response code is " + httpCode);
		if (httpCode >= 400 || httpCode < 200)
		    return false;
		if (httpCode >= 200 && httpCode <= 299)
		    break;
		final String location = httpCon.getHeaderField("location");
		if (location == null || location.isEmpty())
		{
		    Log.warning("doctree", "HTTP response code is " + httpCode + " but \'location\' field is empty");
		    return false;
		}
		Log.debug("doctree", "redirected to " + location);
		final URL locationUrl = new URL(location);
		con = locationUrl.openConnection();
	    }
responseStream = con.getInputStream();
responseUrl = con.getURL();
	    responseContentType = con.getContentType();
	    if (responseContentType == null)
		responseContentType = "";
	    responseContentEncoding = con.getContentEncoding();
						 if (responseContentEncoding == null)
						     responseContentEncoding = "";
						 //						 InputStream is = null;
	    if (responseContentEncoding.toLowerCase().trim().equals("gzip"))
	    {
		Log.debug("doctree", "enabling gzip decompressing");
		downloadToTmpFile(new GZIPInputStream(responseStream));
	    } else
		downloadToTmpFile(responseStream);
	    return true;
    }
    finally {
	if (responseStream != null)
						 responseStream.close();
    }
    }

private void downloadToTmpFile(InputStream s) throws IOException
    {
	NullCheck.notNull(s, "s");
tmpFile = Files.createTempFile("lwrdoctree-download", "");
	Log.debug("doctree", "creating temporary file " + tmpFile.toString());
	Files.copy(s, tmpFile, StandardCopyOption.REPLACE_EXISTING);
    }

private void selectCharset(Format format) throws IOException
{
    NullCheck.notNull(format, "format");
    NullCheck.notEmpty(selectedContentType, "selectedContentType");
    selectedCharset = extractCharset(selectedContentType);
    if (selectedCharset.isEmpty())
	return;
		switch(format)
		{
		case FB2:
selectedCharset = XmlEncoding.getEncoding(tmpFile);
		    break;
		case HTML:
		    selectedCharset = extractCharset(tmpFile);
		    break;
		}
		if (selectedCharset == null || selectedCharset.isEmpty())
		    selectedCharset = DEFAULT_CHARSET;
}

private Result parse(Format format)
{
    NullCheck.notNull(format, "format");
	    final Result res = new Result(Result.Type.OK);
	    switch(format)
	    {
	    case HTML:
		//		res.doc = new Html(effectiveStream, effectiveCharset, baseUrl).constructDocument();
		return res;
	    case FB2:
		//		res.doc = new FictionBook2(effectiveStream, effectiveCharset).createDoc();
		return res;
	    case ZIP:
		//		res.doc = new org.luwrain.doctree.filters.Zip(tmpFile.toString(), "", charset, baseUrl).createDoc();
		return res;
	    case FB2_ZIP:
		//		res.doc = new org.luwrain.doctree.filters.Zip(tmpFile.toString(), "application/fb2", charset, baseUrl).createDoc();
		return res;
	    default:
		return new Result(Result.Type.UNRECOGNIZED_FORMAT);
	    }
    }

    static private Format chooseFilterByContentType(String contentType)
    {
	NullCheck.notEmpty(contentType, "contentType");
	switch(contentType.toLowerCase().trim())
	{
	case "text/html":
	    return Format.HTML;
	case "application/xml":
	    return Format.XML;
	case "application/fb2":
	    return Format.FB2;
	case "application/fb2+zip":
	    return Format.FB2_ZIP;
	case "application/zip":
	    return Format.ZIP;
	default:
	    return null;
	}
    }

    static private String extractCharset(Path path) throws IOException
    {
	NullCheck.notNull(path, "path");
	final BufferedReader r = new BufferedReader(new InputStreamReader(Files.newInputStream(path), StandardCharsets.US_ASCII));
	final StringBuilder b = new StringBuilder();
	String line;
	while ( (line = r.readLine()) != null)
	    b.append(line + "\n");
	final String res = HtmlEncoding.getEncoding(new String(b));
	return res != null?res:"";
    }

    static private String extractBaseContentType(String value)
    {
	NullCheck.notEmpty(value, "value");
	try {
	    final MimeType mime = new MimeType(value);
	    final String res = mime.getBaseType();
	    return res != null?res:"";
	}
	catch(MimeTypeParseException e)
	{
	    e.printStackTrace();
	    return "";
	}
    }

    static private String extractCharset(String value)
    {
	NullCheck.notEmpty(value, "value");
	try {
	    final MimeType mime = new MimeType(value);
	    final String res = mime.getParameter("charset");
	    return res != null?res:"";
	}
	catch(MimeTypeParseException e)
	{
	    e.printStackTrace();
	    return "";
	}
    }

    static private String getDocTypeName(InputStream s) throws IOException
    {
	final org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse(s, "us-ascii", "", org.jsoup.parser.Parser.xmlParser());
	List<org.jsoup.nodes.Node>nods = doc.childNodes();
	for (org.jsoup.nodes.Node node : nods)
	    if (node instanceof org.jsoup.nodes.DocumentType)
	    {
		org.jsoup.nodes.DocumentType documentType = (org.jsoup.nodes.DocumentType)node;                  
		final String res = documentType.attr("name");
		if (res != null)
		    return res;
	    }                                                                    
	return "";
    }
    }
    

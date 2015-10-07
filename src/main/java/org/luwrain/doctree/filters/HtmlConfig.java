
package org.luwrain.doctree.filters;

import java.util.*;
import org.luwrain.util.*;

class HtmlConfig implements MlReaderConfig
{
    static private final String[] validTags = new String[]{
	"a",
	"abbr",
	"acronym",
	"address",
	"applet",
	"area",
	"article",
	"aside",
	"audio",
	"b",
	"base",
	"basefont",
	"bdi",
	"bdo",
	"big",
	"blockquote",
	"body",
	"br",
	"button",
	"canvas",
	"caption",
	"center",
	"cite",
	"code",
	"col",
	"colgroup",
	"datalist",
	"dd",
	"del",
	"details",
	"dfn",
	"dialog",
	"dir",
	"div",
	"dl",
	"dt",
	"em",
	"embed",
	"fb:like",
	"fieldset",
	"figcaption",
	"figure",
	"font",
	"footer",
	"form",
	"frame",
	"frameset",
	"h1",
	"h2",
	"h3",
	"h4",
	"h5",
	"h6",
	"h7",
	"h8",
	"h9",
	"head",
	"header",
	"hr",
	"html",
	"i",
	"iframe",
	"image",
	"img",
	"input",
	"ins",
	"kbd",
	"keygen",
	"label",
	"legend",
	"li",
	"link",
	"main",
	"map",
	"mark",
	"menu",
	"menuitem",
	"meta",
	"meter",
	"nav",
	"nobr",
	"noindex",
	"noframes",
	"noscript",
	"object",
	"ol",
	"optgroup",
	"option",
	"output",
	"p",
	"param",
	"path",
	"pre",
	"polyline",
	"progress",
	"q",
	"rp",
	"rt",
	"ruby",
	"s",
	"samp",
	"script",
	"section",
	"select",
	"small",
	"source",
	"span",
	"strike",
	"strong",
	"style",
	"sub",
	"summary",
	"sup",
	"svg",
	"table",
	"tbody",
	"td",
	"textarea",
	"tfoot",
	"th",
	"thead",
	"time",
	"title",
	"tr",
	"track",
	"tt",
	"u",
	"ul",
	"var",
	"video",
	"wbr"};

    static private final String[] nonClosingTags = new String[]{
	"!doctype",
	"input",
	"br",
	"hr",
	"link",
	"img",
	"meta"
    }; 

    @Override public boolean mlTagMustBeClosed(String tag)
    {
	final String adjusted = tag.toLowerCase().trim();
	for(String s: nonClosingTags)
	    if (s.equals(adjusted))
		return false;
	return true;
    }

    @Override public boolean mlAdmissibleTag(String tagName, LinkedList<String> tagsStack)
    {
	//May not open a tag inside of a script;
	if (!tagsStack.isEmpty())
	    if (tagsStack.getLast().toLowerCase().trim().equals("script"))
		return false;
	final String adjusted = tagName.toLowerCase().trim();
	for(String s: validTags)
	    if (s.equals(adjusted))
		return true;
	return false;
    }
}

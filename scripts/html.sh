#!/bin/sh -e

BASEDIR=~/doctree.git

jars()
{
    find "$1" -iname '*.jar' | 
    while read l; do
	echo -n "$l:"
    done
    echo
}

MAINCLASS=org.luwrain.doctree.filters.HtmlStructTool
CP="$(jars "$BASEDIR/lib/")$(jars "$BASEDIR/jar/")"
exec java -cp "$CP" "$MAINCLASS" "$@"

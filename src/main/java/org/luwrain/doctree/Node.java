

package org.luwrain.doctree;

public interface Node
{
    static public final int ROOT = 1;
    static public final int SECTION = 2;
    static public final int PARAGRAPH = 3;
    static public final int  TABLE = 4;
    static public final int  TABLE_ROW = 5;
    static public final int  TABLE_CELL = 6;
    static public final int  UNORDERED_LIST = 7;
    static public final int  ORDERED_LIST = 8;
    static public final int  LIST_ITEM = 9;
}

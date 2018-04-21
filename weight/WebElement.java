/*
   Copyright 2012-2017 Michael Pozhidaev <michael.pozhidaev@gmail.com>
   Copyright 2015-2016 Roman Volovodov <gr.rPman@gmail.com>

   This file is part of LUWRAIN.

   LUWRAIN is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   LUWRAIN is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

package org.luwrain.browser.weight;

import java.util.*;

import org.luwrain.browser.*;

public interface WebElement
{
    enum Type {Text, Edit, Button, Checkbox, Radio, Select, List, ListElement, Table, TableRow, TableCell};

    void init();
    Type getType();
    BrowserIterator getNode();
    WebElement getParent();
    boolean hasChildren();
    Vector<WebElement> getChildren();
    boolean isComplex();
    boolean alwaysFromNewLine();
    boolean needEndLine();
    boolean needToBeExpanded();
    boolean needToBeHidden();
    String getSplitter();
    String getText();
    String getTextShort();
    void toDelete();
    boolean isDeleted();
    boolean isVisible();
    long getWeight();
    void incWeight(long weight);
    void setAttribute(String name,String value);
    void mixAttributes(WebElement element);
    LinkedHashMap<String,String> getAttributes();
    Vector<Vector<WebElement>> getComplexMatrix();

    /** print this element debug info
     * @param lvl current level in recursive call, for root must be 0
     * @param printChildren if true, children will printed recursive */
    void print(int lvl,boolean printChildren);
    String getDescr();
}

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

package org.luwrain.controls.browser;

import java.util.concurrent.*;

/**
 * An interface to thread manager. A vast majority of work in the browser
 * engine is performed in background thread. So, the engine actually is
 * unable to return anything back to the browser area directly.  In
 * addition, general area functionality doesn't have anything allowing it
 * to manage threads. 
 *
 * This interface gives access to some implementation-dependent class
 * which is able to run some code in the main thread of
 * application. There are two types of the code which could be requested
 * to run: synchronous and asynchronous. The first type implies that
 * there is no need to wait until provided code is executed. The second
 * one means that the corresponding method may not return until the
 * requested code is fully executed. This allows to get the method return
 * value back to the caller.
 *
 * @see BrowserArea
 */
public interface ClientThread
{
    /**
     * Runs the requested code in asynchronous mode (no need to wait finishing).
     *
     * @param runnable The runnable object with the necessary code
     */
    void runAsync(Runnable runnable);

    /**
     * Runs the requested code in synchronous mode (it is necessary to wait finishing).
     *
     * @param callable The callable object with the necessary code
     * @return The return value of the provided code after the execution
     */
    Object runSync(Callable callable);
}

package org.nnsoft.nnlauncher;

/*
 *    Copyright 2012 The 99 Software Foundation
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

import static java.lang.String.format;

/**
 * An error message and the context in which it occurred.
 */
public class CliException
    extends Exception
{

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * Create a new {@link CliException} instance from the error message text.
     *
     * @param messagePattern The error message text pattern
     * @param arguments Arguments referenced by the format specifiers in the format string
     */
    public CliException( String messagePattern, Object... arguments )
    {
        super( format( messagePattern, arguments ) );
    }

    /**
     * Create a new {@link CliException} instance from the error message text and the related cause.
     *
     * @param message The error message text
     * @param cause The throwable that caused this message
     */
    public CliException( String message, Throwable cause )
    {
        super( message, cause );
    }

}

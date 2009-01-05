/*
 * Copyright (C) 2007-2009 Institute for Computational Biomedicine,
 *               Weill Medical College of Cornell University
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package edu.cornell.med.icb.io;

/**
 * Error with condition parsing.
 * @author Kevin Dorff
 */
public class ConditionsParsingException extends Exception {

    /**
     * No args exception.
     */
    public ConditionsParsingException() {
        super();
    }

    /**
     * Message only exception.
     * @param message the exception message
     */
    public ConditionsParsingException(final String message) {
        super(message);
    }

    /**
     * Message and throwable exception.
     * @param message the exception message
     * @param cause the root cause of the exception
     */
    public ConditionsParsingException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Throwable exception (no message).
     * @param cause the root cause of the exception
     */
    public ConditionsParsingException(final Throwable cause) {
        super(cause);
    }
}

/*
 * Copyright (C) 2008-2009 Institute for Computational Biomedicine,
 *                         Weill Medical College of Cornell University
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

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/**
 * A DataOutputStream but with a writeObject(o) method to
 * serialize an object to the stream.
 * @author Kevin Dorff
 */
public class DataObjectOutputStream extends DataOutputStream {
    /**
     * Creates a new data output stream to write data to the specified
     * underlying output stream. The counter <code>written</code> is
     * set to zero.
     * @param out the underlying output stream, to be saved for later
     *            use.
     * @see java.io.FilterOutputStream#out
     */
    public DataObjectOutputStream(final OutputStream out) {
        super(out);
    }

    /**
     * Write an object to the current stream position.
     * @param objToWrite the object to write
     * @throws java.io.IOException error reading the object
     */
    public void writeObject(final Object objToWrite) throws IOException {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        final ObjectOutput out = new ObjectOutputStream(bos);
        out.writeObject(objToWrite);
        out.close();
        // Get the bytes of the serialized object
        final byte[] buf = bos.toByteArray();
        // save the position and length of the serialized object
        writeInt(buf.length);
        write(buf);
    }
}

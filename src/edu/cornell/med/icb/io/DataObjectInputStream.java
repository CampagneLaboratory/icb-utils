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

import java.io.DataInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;

/**
 * A DataInputStream but with a readObject() method to
 * de-serialize an object from the stream.
 * @author Kevin Dorff
 */
public class DataObjectInputStream extends DataInputStream {

    /**
     * Creates a DataInputStream that uses the specified
     * underlying InputStream.
     * @param in the specified input stream
     */
    public DataObjectInputStream(InputStream in) {
        super(in);
    }

    /**
     * Read an object from the current stream position.
     * @return the object
     * @throws java.io.IOException error reading the object
     * @throws ClassNotFoundException error de-serializing the object
     */
    public Object readObject() throws IOException, ClassNotFoundException {
        final int size = readInt();
        final byte[] buf = new byte[size];
        readFully(buf);
        final ByteArrayInputStream bis = new ByteArrayInputStream(buf);
        final ObjectInputStream ois = new ObjectInputStream(bis);
        final Object deserializedObject = ois.readObject();
        ois.close();
        return deserializedObject;
    }
}

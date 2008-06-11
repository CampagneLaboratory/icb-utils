/*
 * Copyright (C) 2008 Institute for Computational Biomedicine,
 *                    Weill Medical College of Cornell University
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

package edu.cornell.med.icb.ip;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.net.InetAddress;

/**
 * An ip address and optional associated comment.
 * @author Kevin Dorff
 */
public class IpAddress {

    /** The inet address (ip address). */
    private final InetAddress ipAddress;

    /** Associated comment. */
    private final String comment;

    /** Associated comment. Immutable object so just calculate it at creation. */
    private final int hashCode;

    /**
     * Create an IpAddrss with an InetAddress and a comment.
     * @param ipAddressVal the InetAddress to use for this IpAddress or null
     * if there is no ip address (just a comment).
     * @param commentVal the comment portein of the IpAddress
     */
    public IpAddress(final InetAddress ipAddressVal, final String commentVal) {
        this.ipAddress = ipAddressVal;
        this.comment = commentVal;
        this.hashCode = new HashCodeBuilder(181, 431)
                .append(ipAddress).append(comment).toHashCode();

    }

    /**
     * Create an IpAddrss with an InetAddress but no comment.
     * @param ipAddressVal the InetAddress to use for this IpAddress or null
     * if there is no ip address... doesn't make a lot of sense for this
     * to ever be null since there won't be a comment.
     */
    public IpAddress(final InetAddress ipAddressVal) {
        this(ipAddressVal, "");
    }

    /**
     * Get the ip address (InetAddress).
     * @return ip address
     */
    public InetAddress getIpAddress() {
        return ipAddress;
    }

    /**
     * Get comment.
     * @return comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * Get the hash code for this object.
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return hashCode;
    }

    /**
     * Equals.
     * @param obj the object to compare to.
     * @return true if the two objects are equal.
     */
    @Override
    public boolean equals(final Object obj) {
        return ((obj != null) && (obj instanceof IpAddress) && this.hashCode == obj.hashCode());
    }
}

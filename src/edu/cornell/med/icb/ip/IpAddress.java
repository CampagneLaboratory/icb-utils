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

/**
 * An ip address and optional associated comment.
 * @author Kevin Dorff
 */
public class IpAddress {
    private String ipAddress;
    private String comment;

    public IpAddress(final String ipAddress, final String comment) {
        this.ipAddress = ipAddress;
        this.comment = comment;
    }

    public IpAddress(final String ipAddress) {
        this(ipAddress, null);
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getComment() {
        return comment;
    }
}
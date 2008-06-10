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

import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import java.util.HashSet;

/**
 * A list of ip addresses.
 * @author Kevin Dorff
 */
public class IpList {
    /** The list of ip addresses. */
    private List<IpAddress> ipList;

    /** The set of if ip address strings. */
    private Set<String> addressSet;

    /** Constructor. */
    public IpList() {
        ipList = new LinkedList<IpAddress>();
        addressSet = new HashSet<String>();
    }

    /** Clear the list. */
    public void clear() {
        ipList.clear();
        addressSet.clear();
    }

    /**
     * Obtain the underlying list.
     * @return the List[IpAddress] this object is based on
     */
    public List<IpAddress> getList() {
        return ipList;
    }

    /**
     * Add an IpAdress to the list.
     * @param ipAddress the IpAddress to add
     */
    public void add(final IpAddress ipAddress) {
        if (ipAddress == null) {
            return;
        }
        ipList.add(ipAddress);
        if (ipAddress.getIpAddress() != null) {
            addressSet.add(ipAddress.getIpAddress());
        }
    }

    /**
     * Returns true if the provided IpAddress is not null,
     * the ipAddress field is not null, and if the addressSet
     * contains the ipAddress field value.
     * @param address the address to check for
     * @return true if the ipAddress is in the list
     */
    public boolean contains(final IpAddress address) {
        if (address == null || address.getIpAddress() == null) {
            return false;
        }
        return addressSet.contains(address.getIpAddress());
    }

    /**
     * Returns true if the provided ip address string is not null,
     * is a valid ip address, and if the addressSet
     * contains the ipAddress field value.
     * @param address the address to check for
     * @return true if the ipAddress is in the list
     */
    public boolean contains(final String address) {
        try {
            return contains(IpUtils.validateIpAddress(address));
        } catch (IpValidationException e) {
            return false;
        }
    }

}

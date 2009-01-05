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

package edu.cornell.med.icb.ip;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Test IpAddress.
 * @author Kevin Dorff
 */
public class TestIpAddress {

    /**
     * Self explanatory.
     * @throws UnknownHostException error parsing ip address.
     */
    @Test
    public void testEqualsLocalhostWorks() throws UnknownHostException {
        IpAddress ipAddress127 = new IpAddress(InetAddress.getByName("127.0.0.1"), "localhost ip");
        IpAddress ipAddressLocalhost = new IpAddress(InetAddress.getByName("localhost"), "localhost ip");
        assertEquals(ipAddress127, ipAddressLocalhost);
        assertEquals(ipAddressLocalhost, ipAddress127);
    }

    /**
     * Self explanatory.
     * @throws UnknownHostException error parsing ip address.
     */
    @Test
    public void testSameValues() throws UnknownHostException {
        IpAddress ipAddress127 = new IpAddress(InetAddress.getByName("127.0.0.1"), "localhost ip");
        IpAddress ipAddress127b = new IpAddress(InetAddress.getByName("127.0.0.1"), "localhost ip");
        assertEquals(ipAddress127, ipAddress127b);
        assertEquals(ipAddress127b, ipAddress127);
    }

    /**
     * Self explanatory.
     * @throws UnknownHostException error parsing ip address.
     */
    @Test
    public void testNotEqualsDiffComment() throws UnknownHostException {
        IpAddress ipAddress127 = new IpAddress(InetAddress.getByName("127.0.0.1"), "localhost ip diff");
        IpAddress ipAddress127b = new IpAddress(InetAddress.getByName("127.0.0.1"), "localhost ip");
        assertNotSame(ipAddress127, ipAddress127b);
        assertNotSame(ipAddress127b, ipAddress127);
    }

    /**
     * Self explanatory.
     * @throws UnknownHostException error parsing ip address.
     */
    @Test
    public void testNotEqualsDiffIp() throws UnknownHostException {
        IpAddress ipAddress127 = new IpAddress(InetAddress.getByName("127.0.0.1"), "localhost ip");
        IpAddress ipAddress127b = new IpAddress(InetAddress.getByName("127.0.0.2"), "localhost ip");
        assertNotSame(ipAddress127, ipAddress127b);
        assertNotSame(ipAddress127b, ipAddress127);
    }

    /**
     * Self explanatory.
     * @throws UnknownHostException error parsing ip address.
     */
    @Test
    public void testNotEqualsDiffIpDiffComment() throws UnknownHostException {
        IpAddress ipAddress127 = new IpAddress(InetAddress.getByName("127.0.0.1"), "localhost ip diff");
        IpAddress ipAddress127b = new IpAddress(InetAddress.getByName("127.0.0.2"), "localhost ip");
        assertNotSame(ipAddress127, ipAddress127b);
        assertNotSame(ipAddress127b, ipAddress127);
    }

    /**
     * Self explanatory.
     * @throws UnknownHostException error parsing ip address.
     */
    @Test
    public void testEqualsNullComment() throws UnknownHostException {
        IpAddress ipAddress127 = new IpAddress(InetAddress.getByName("127.0.0.1"), null);
        IpAddress ipAddress127b = new IpAddress(InetAddress.getByName("127.0.0.1"), null);
        assertNotSame(ipAddress127, ipAddress127b);
        assertNotSame(ipAddress127b, ipAddress127);
    }

    /**
     * Self explanatory.
     * @throws UnknownHostException error parsing ip address.
     */
    @Test
    public void testNotEqualsOneNullComment() throws UnknownHostException {
        IpAddress ipAddress127 = new IpAddress(InetAddress.getByName("127.0.0.1"), null);
        IpAddress ipAddress127b = new IpAddress(InetAddress.getByName("127.0.0.1"), "comment");
        assertNotSame(ipAddress127, ipAddress127b);
        assertNotSame(ipAddress127b, ipAddress127);
    }

    /**
     * Self explanatory.
     * @throws UnknownHostException error parsing ip address.
     */
    @Test
    public void testNotEqualsOneNullIp() throws UnknownHostException {
        IpAddress ipAddress127 = new IpAddress(null, "comment");
        IpAddress ipAddress127b = new IpAddress(InetAddress.getByName("127.0.0.1"), "comment");
        assertNotSame(ipAddress127, ipAddress127b);
        assertNotSame(ipAddress127b, ipAddress127);
    }

    /**
     * Self explanatory.
     * @throws UnknownHostException error parsing ip address.
     */
    @Test
    public void testNotEqualsOneNullIpOneNullComment() throws UnknownHostException {
        IpAddress ipAddress127 = new IpAddress(null, "comment");
        IpAddress ipAddress127b = new IpAddress(InetAddress.getByName("127.0.0.1"), null);
        assertNotSame(ipAddress127, ipAddress127b);
        assertNotSame(ipAddress127b, ipAddress127);
    }
}

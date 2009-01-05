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
import static org.junit.Assert.fail;
import static junit.framework.Assert.assertEquals;

import java.net.UnknownHostException;
import java.net.InetAddress;

/**
 * Describe class here.
 *
 * @author Kevin Dorff
 */
public class TestIpUtils {

    /**
     * Test a blank line.
     * @throws UnknownHostException error parsing ip address
     */
    @Test
    public void testBlankLine() throws UnknownHostException {
        assertEquals(new IpAddress(null, ""), IpUtils.validateIpAddress(""));
    }

    /**
     * Test a blank-ish line.
     * @throws UnknownHostException error parsing ip address
     */
    @Test
    public void testBlankishLine() throws UnknownHostException {
        assertEquals(new IpAddress(null, ""), IpUtils.validateIpAddress("   \t   \t      "));
    }

    /**
     * Test a valid ip and comment.
     * @throws UnknownHostException error parsing ip address
     */
    @Test
    public void testValidIpAndComment() throws UnknownHostException {
        assertEquals(new IpAddress(InetAddress.getByName("192.168.5.1"), "some ip  # or another"),
                IpUtils.validateIpAddress("   192.168.5.1   #  some ip  # or another  "));
    }

    /**
     * Test a valid ip but no comment.
     * @throws UnknownHostException error parsing ip address
     */
    @Test
    public void testValidIpNoComment() throws UnknownHostException {
        assertEquals(new IpAddress(InetAddress.getByName("192.168.5.1"), ""),
                IpUtils.validateIpAddress("192.168.5.1"));
    }

    /* Ip addresses can change, not included. But it did pass.
    @Test
    public void testByName() throws UnknownHostException {
        assertEquals(new IpAddress(InetAddress.getByName("157.139.217.106"), ""),
                IpUtils.validateIpAddress("pc129964.med.cornell.edu"));
    }
    */

    /**
     * Test a valid ip but no comment 2.
     * @throws UnknownHostException error parsing ip address
     */
    @Test
    public void testValidIpNoComment2() throws UnknownHostException {
        assertEquals(new IpAddress(InetAddress.getByName("192.168.5.1"), ""),
                IpUtils.validateIpAddress("   192.168.5.1   "));
    }

    /**
     * Test a valid ip and a blank0ish comment.
     * @throws UnknownHostException error parsing ip address
     */
    @Test
    public void testValidIpMostlyNoComment() throws UnknownHostException {
        assertEquals(new IpAddress(InetAddress.getByName("192.168.5.1"), ""),
                IpUtils.validateIpAddress("   192.168.5.1   #     \t \t     "));
    }

    /**
     * Comment only.
     * @throws UnknownHostException error parsing ip address
     */
    @Test
    public void testValidCommentOnly() throws UnknownHostException {
        assertEquals(new IpAddress(null, "this is the \tcomment"),
                IpUtils.validateIpAddress("      #  \t  this is the \tcomment   \t \t     "));
    }

    /**
     * Invalid ip with comment.
     * @throws UnknownHostException error parsing ip address
     */
    @Test(expected = UnknownHostException.class)
    public void testInvalidIpWithComment() throws UnknownHostException {
        IpUtils.validateIpAddress("   192.168.256.1   #    some comment     ");
    }

    /**
     * Short ip, no comment. This is actually valid with InetAddress.
     * @throws UnknownHostException error parsing ip address
     */
    @Test
    public void tesIpShortNoComment() throws UnknownHostException {
        assertEquals(new IpAddress(InetAddress.getByName("168.254.0.1"), ""),
            IpUtils.validateIpAddress("168.254.1"));
    }

    /**
     * Long ip, no comment. Invalid.
     * @throws UnknownHostException error parsing ip address
     */
    @Test(expected = UnknownHostException.class)
    public void testInvalidIpLongNoComment() throws UnknownHostException {
        IpUtils.validateIpAddress("168.254.1.53.23");
    }

    /**
     * Bad ip.
     * @throws UnknownHostException error parsing ip address
     */
    @Test(expected = UnknownHostException.class)
    public void testInvalidIpLettersNoComment() throws UnknownHostException {
        IpUtils.validateIpAddress("254.abc.53.23");
    }

    /**
     * Bad ip with comment.
     * @throws UnknownHostException error parsing ip address
     */
    @Test(expected = UnknownHostException.class)
    public void testInvalidIpLettersComment() throws UnknownHostException {
        IpUtils.validateIpAddress("254.abc.53.23  #  another comment");
    }

    /**
     * Test reading from a stream, ignoring errors.
     * @throws UnknownHostException error parsing ip address
     */
    @Test(expected = UnknownHostException.class)
    public void testIgnoreErrors() throws UnknownHostException {
        StringBuilder source = new StringBuilder();
        source.append("192.168.1.1  # Some local pc");
        source.append("193.169.2.3  # Some other local pc");
        source.append("192.168.1.1  # Some local pc"); // Duplicate
        IpUtils.validateIpAddress("254.yabc.53.23  #  another comment");
    }

}

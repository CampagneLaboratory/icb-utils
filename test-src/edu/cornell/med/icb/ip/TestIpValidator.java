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

import org.junit.Test;
import static org.junit.Assert.fail;
import static junit.framework.Assert.assertEquals;

/**
 * Describe class here.
 *
 * @author Kevin Dorff
 */
public class TestIpValidator {

    @Test
    public void testBlankLine() throws IpValidationException {
        assertSameIp(new IpAddress(null, ""), IpValidator.validate(""));
    }

    @Test
    public void testBlankishLine() throws IpValidationException {
        assertSameIp(new IpAddress(null, ""), IpValidator.validate("   \t   \t      "));
    }

    @Test
    public void testValidIpAndComment() throws IpValidationException {
        assertSameIp(new IpAddress("192.168.5.1", "some ip  # or another"),
                IpValidator.validate("   192.168.5.1   #  some ip  # or another  "));
    }

    @Test
    public void testValidIpNoComment() throws IpValidationException {
        assertSameIp(new IpAddress("192.168.5.1", ""),
                IpValidator.validate("192.168.5.1"));
    }

    @Test
    public void testValidIpNoComment2() throws IpValidationException {
        assertSameIp(new IpAddress("192.168.5.1", ""),
                IpValidator.validate("   192.168.5.1   "));
    }

    @Test
    public void testValidIpMostlyNoComment() throws IpValidationException {
        assertSameIp(new IpAddress("192.168.5.1", ""),
                IpValidator.validate("   192.168.5.1   #     \t \t     "));
    }

    @Test
    public void testValidCommentOnly() throws IpValidationException {
        assertSameIp(new IpAddress(null, "this is the \tcomment"),
                IpValidator.validate("      #  \t  this is the \tcomment   \t \t     "));
    }

    @Test(expected = IpValidationException.class)
    public void testInvalidIpWithComment() throws IpValidationException {
        IpValidator.validate("   192.168.256.1   #    some comment     ");
    }

    @Test(expected = IpValidationException.class)
    public void testInvalidIpShortNoComment() throws IpValidationException {
        IpValidator.validate("168.254.1");
    }

    @Test(expected = IpValidationException.class)
    public void testInvalidIpLongNoComment() throws IpValidationException {
        IpValidator.validate("168.254.1.53.23");
    }

    @Test(expected = IpValidationException.class)
    public void testInvalidIpLettersNoComment() throws IpValidationException {
        IpValidator.validate("254.abc.53.23");
    }

    @Test(expected = IpValidationException.class)
    public void testInvalidIpLettersComment() throws IpValidationException {
        IpValidator.validate("254.abc.53.23  #  another comment");
    }

    private void assertSameIp(final IpAddress expected, final IpAddress actual) {
        if (expected == null && actual == null) {
            return;
        }
        if (expected == null || actual == null) {
            fail("One of expected, received was null but should not have been");
        }
        assertEquals("IP Address didn't match", expected.getIpAddress(), actual.getIpAddress());
        assertEquals("Comment didn't match", expected.getComment(), actual.getComment());
    }
}

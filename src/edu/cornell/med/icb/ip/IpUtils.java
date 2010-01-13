/*
 * Copyright (C) 2008-2010 Institute for Computational Biomedicine,
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

import org.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;

/**
 * Utility class for validating ip addresses, reading IpAddress files, etc.
 * Also to see if an ip address is within a given list of IpAddress.
 * @author Kevin Dorff
 */
public final class IpUtils {

    /** Private constructor for utility class. */
    private IpUtils() {
    }

    /**
     * Attempt to validate an IP address or hostname with optional comment.
     * This can be in the formation of "address_or_hostname # comment".
     * Whitespace (space or tab) can padd the valiues anywhere within
     * the value, except within the ip address / hostname iteself. If specifying
     * an ip address must be four values seperated by ".", each value must be 0 - 255...
     * this is not exactly true, see the documentation for java.net.InetAddress
     * for actual parsing rules of ip addresses.
     * @param toValidate the ip address and/or hostname (with optional comment) to try to validate
     * @return an IpAddress object containing the InetAddress and any comment.
     * If the line didn't parse UnknownHostname exception will be thrown. If the line
     * contains ONLY a comment IpAddress.ipAddress will be null but comment will be
     * filled in. If the line is blank (or empty) an IpAddress will be returned
     * with IpAddress.ipAddress as null and an empty String comment.
     * @throws UnknownHostException error parsing the ip address / hostname part of the string
     */
    public static IpAddress validateIpAddress(final String toValidate) throws UnknownHostException {
        final String trimmedIpAddress;
        if (StringUtils.isBlank(toValidate)) {
            // No IP, no comment. Blank-ish line
            return new IpAddress(null, "");
        } else {
            trimmedIpAddress = toValidate.trim();
        }

        String comment = "";
        // parse out any comments
        if (trimmedIpAddress.charAt(0) == '#') {
            // Comment only line
            return new IpAddress(null, trimmedIpAddress.substring(1).trim());
        }

        final String[] ipAndCommentParts = StringUtils.split(trimmedIpAddress, "#", 2);
        final String ipNoComment = ipAndCommentParts[0].trim();
        if (ipAndCommentParts.length == 2) {
            // Both IP AND comment
            comment = ipAndCommentParts[1].trim();
        }

        final InetAddress ipAddress = InetAddress.getByName(ipNoComment);
        return new IpAddress(ipAddress, comment);
    }

    /**
     * Read a data from a stream (generally a file) that contains ip addresses and/or
     * hostnames and comments.
     * @param input the input stream of ip addresses
     * @param keepCommentOnlyLines if true, lines that contain only comments will be discarded,
     * if false and a comment only line is found, an IpAddress object will be created with
     * a null ip adress.
     * @param ignoreParsingErrors if true, lines that contain unparsable ip adddresses / hostnames
     * will be silently ignored.
     * @return a List of IpAddress
     * @throws IOException error reading from the stream or parsing ip address / hostname
     */
    public static List<IpAddress> readIpList(
            final InputStream input,
            final boolean keepCommentOnlyLines,
            final boolean ignoreParsingErrors)
            throws IOException {
        final List<IpAddress> results = new LinkedList<IpAddress>();
        final BufferedReader br = new BufferedReader(new InputStreamReader(input));
        String line;
        while ((line = br.readLine()) != null)   {
            try {
                final IpAddress ipAddress = validateIpAddress(line);
                if ((ipAddress.getIpAddress() == null) && (!keepCommentOnlyLines)) {
                    // Comment only
                    continue;
                }
                results.add(validateIpAddress(line));
            } catch (UnknownHostException e) {
                if (!ignoreParsingErrors) {
                    throw e;
                }
            }
        }
        return results;
    }

    /**
     * See if the supplied IpAddress List contains the specified address string.
     * The address string can be a hostname or an ip address with an optional
     * comment (comments start with "#").
     * @param list the IpAddress List
     * @param address the ip address to find
     * @return true if the ip address is in the list
     * @throws UnknownHostException error parsing the ip address or hostname
     * from the address String
     */
    public static boolean ipListContains(final List<IpAddress> list, final String address)
            throws UnknownHostException {
        if (list == null || address == null) {
            return false;
        }
        return ipListContains(list, validateIpAddress(address).getIpAddress());
    }

    /**
     * See if the supplied IpAddress List contains the specified IpAddress,
     * specifically the value from address.getIpAddress() - any comment
     * with address is ignored.
     * @param list the IpAddress List
     * @param address the ip address to find
     * @return true if the ip address is in the list
     */
    public static boolean ipListContains(final List<IpAddress> list, final IpAddress address) {
        if (list == null || address == null) {
            return false;
        }
        return ipListContains(list, address.getIpAddress());
    }

    /**
     * See if the supplied IpAddress List contains the specified InetAddress address.
     * @param list the IpAddress List
     * @param address the ip address to find
     * @return true if the ip address is in the list
     */
    public static boolean ipListContains(final List<IpAddress> list, final InetAddress address) {
        if (list == null || address == null) {
            return false;
        }
        for (final IpAddress listAddr : list) {
            if (address.equals(listAddr.getIpAddress())) {
                return true;
            }
        }
        return false;
    }
}

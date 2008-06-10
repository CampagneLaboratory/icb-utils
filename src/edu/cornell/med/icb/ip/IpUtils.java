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

import org.apache.commons.lang.StringUtils;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;

/**
 * Utility class for validating, etc. IP addreses.
 * @author Kevin Dorff
 */
public class IpUtils {
    /**
     * Attempt to validate an IP address. This can be in the formation of
     * "address # comment". Whitespace (space or tab) can padd the valiues anywhere within
     * the string, except within the address iteself. The ip address must
     * be four values seperated by ".", each value must be 0 - 255.
     * @param toValidate the ip address (with optional comment) to try to validate
     * @return an IpAddress object containing the ip address and any comment.
     * If the line didn't parse IpValidationException will be thrown. If the line
     * contains ONLY a comment IpAddress.ipAddress will be null but comment will be
     * filled in. If the line is blank (or empty) an IpAddress will be returned
     * with IpAddress.ipAddress as null and an empty string comment.
     * @throws IpValidationException error parsing the ip part of the string
     */
    public static IpAddress validateIpAddress(final String toValidate) throws IpValidationException {
        String trimmedIpAddress;
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
        final String[] parts = StringUtils.split(ipNoComment, ".");
        if (parts.length != 4) {
            // Reject mal-formed IP address
            throw new IpValidationException("IP address " + ipNoComment
                    + " did not contain 4 parts.");
        }
        final StringBuilder newIp = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            final String part = parts[i];
            try {
                final int numericPart = Integer.parseInt(part);
                if ((numericPart < 0) || (numericPart > 255)) {
                    throw new IpValidationException("IP address " + ipNoComment
                            + " contained an invalud value " + part
                            + ". Values 0 - 255 expected.");
                } else {
                    if (newIp.length() != 0) {
                        newIp.append(".");
                    }
                    newIp.append(numericPart);
                }
            } catch (java.lang.NumberFormatException e) {
                throw new IpValidationException("IP address " + ipNoComment
                        + " contained an invalud value " + part
                        + ". Values 0 - 255 expected.");
            }
        }
        return new IpAddress(newIp.toString(), comment);
    }

    public static IpList readIpList(
            final InputStream input,
            final boolean keepCommentOnlyLines,
            final boolean ignoreParsingErrors)
            throws IpValidationException, IOException {
        IpList results = new IpList();
        BufferedReader br = new BufferedReader(new InputStreamReader(input));
        String line;
        while ((line = br.readLine()) != null)   {
            try {
                IpAddress ipAddress = validateIpAddress(line);
                if ((ipAddress.getIpAddress() == null) && (!keepCommentOnlyLines)) {
                    // Comment only
                    continue;
                }
                results.add(validateIpAddress(line));
            } catch (IpValidationException e) {
                if (!ignoreParsingErrors) {
                    throw e;
                }
            }
        }
        return results;
    }

}

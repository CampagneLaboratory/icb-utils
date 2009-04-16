/*
 * Copyright (C) 2007-2009 Institute for Computational Biomedicine,
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

package edu.cornell.med.icb.cli;

import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import junit.framework.TestCase;

/**
 * @author Fabien Campagne Date: Jul 8, 2007 Time: 11:29:41 AM
 */
public class TestUseModality extends TestCase {
    public void testModality() throws JSAPException, IllegalAccessException,
            InstantiationException {
        final String[] args = {"--mode", "mode1", "--help"};
        final UseModality<Object> mainModality = new UseModality<Object>() {
            @Override
            public void interpretArguments(final JSAP jsap,
                                           final JSAPResult result,
                                           final Object options) {
            }

            @Override
            public void process(final Object options) {
            }
        };
        mainModality.setTestingMode(true);
        mainModality.registerMode("mode1", ModalityTest.class);
        mainModality.process(args, null);
    }

    public static class ModalityTest extends UseModality<Object> {
        @Override
        public void process(final String[] args, final Object options) {
            System.out.println("Now processing mode1");
        }

        /**
         * Transfer arguments from  command line arguments to application options.
         *
         * @param jsap JSAP engine
         * @param result Parsed command line arguments.
         * @param options ignore.
         */
        @Override
        public void interpretArguments(final JSAP jsap, final JSAPResult result,
                                       final Object options) {
        }

        /**
         * Process with options. This method may exit the JVM at any time.
         *
         * @param options ignore.
         */
        @Override
        public void process(final Object options) {
        }
    }
}

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

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Parameter;
import com.martiansoftware.jsap.Switch;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.apache.commons.lang.StringUtils;

import java.util.Iterator;
import java.util.Map;

/**
 * Regroups parsing of command line options and processing for modular command line interface
 * definition. Each UseModality instance is responsible for parsing the options required
 * for the processing that it implements.
 *
 * @author Fabien Campagne Date: Jul 8, 2007 Time: 11:24:19 AM
 */
public abstract class UseModality<OptionType> {
    private static final String MODE_FLAG_NAME = "mode";
    private final Map<String, Class<? extends UseModality<OptionType>>> registeredModes;

    /**
     * Indicates whether or not this is being called from a junit test or not.
     */
    private boolean junitMode;
    private String[] originalArgs;

    /**
     * The use modality that was executed in response to --mode selection.
     */
    private UseModality<OptionType> executedModality;

    public UseModality<OptionType> getExecutedModality() {
        return executedModality;
    }

    protected UseModality() {
        super();
        registeredModes =
                new Object2ObjectOpenHashMap<String, Class<? extends UseModality<OptionType>>>();
    }

    /**
     * Define command line options.
     * @param jsap the JSAP command line parser
     * @throws JSAPException if there is an issue with the options
     */
    public void defineOptions(final JSAP jsap) throws JSAPException {
        if (jsap.getByID("help") == null) {
            final Parameter helpFlag = new Switch("help")
                    .setShortFlag('h')
                    .setLongFlag("help")
                    .setHelp("This message.");
            jsap.registerParameter(helpFlag);
        }
    }

    /**
     * Process the command line arguments.
     * @param args The arguments to process
     * @param options The command line options
     * @throws JSAPException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public void process(final String[] args, final OptionType options)
            throws JSAPException, IllegalAccessException, InstantiationException {
        final JSAP jsap = new JSAP();
        originalArgs = args;

        final FlaggedOption modeFlag = new FlaggedOption(MODE_FLAG_NAME)
                .setStringParser(JSAP.STRING_PARSER)
                .setDefault(JSAP.NO_DEFAULT)
                .setRequired(true)
                .setShortFlag('m')
                .setLongFlag("mode");

        modeFlag.setHelp(getHelpString(registeredModes));
        jsap.registerParameter(modeFlag);
        JSAPResult result = jsap.parse(args);
        final String mode = result.getString("mode");
        defineOptions(jsap);

        if (mode == null) {
            System.out.println(jsap.getHelp());
            System.exit(0);
        }
        final Class<? extends UseModality<OptionType>> modalityClass =
                registeredModes.get(mode.toLowerCase());

        if (modalityClass != null) {
            final UseModality<OptionType> modality =
                    modalityClass.newInstance();
            modality.setOriginalArgs(args);
            modality.defineOptions(jsap);
            // dumpArgs(args);
            // dumpJsapConfiguration(jsap);
            result = jsap.parse(args);
            executedModality = modality;

            if (!result.success()) {
                final Iterator errors = result.getErrorMessageIterator();
                while (errors.hasNext()) {
                    final String errorMessage = (String) errors.next();
                    System.out.println(errorMessage);
                }
            }

            if (checkPrintHelp(jsap, result)) {
                return;
            }

            modality.interpretArguments(jsap, result, options);
            modality.process(options);

        } else {
            if (StringUtils.isBlank(mode)) {
                printHelp(jsap);
            } else {
                System.err.println("Mode \"" + mode
                        + "\" is not defined for this utility.");
                if (!junitMode) {
                    System.exit(10);
                }
            }
        }
    }

    /**
     * Transfer arguments from command line arguments to application options.
     *
     * @param jsap JSAP engine.
     * @param result Parsed command line arguments.
     * @param options Where interpreted arguments will be stored.
     */
    public abstract void interpretArguments(final JSAP jsap,
                                            final JSAPResult result,
                                            final OptionType options);

    /**
     * Process with options. This method may exit the JVM at any time.
     *
     * @param options Interpreted command line options.
     */
    public abstract void process(final OptionType options);

    protected void printHelp(final JSAP jsap) {
        System.out.print(jsap.getHelp());
    }

    /**
     * Register a new mode.
     * @param modeName The name of the mode
     * @param modalityActor The class that implements the mode
     */
    public void registerMode(final String modeName,
                             final Class<? extends UseModality<OptionType>> modalityActor) {
        registeredModes.put(modeName.toLowerCase().intern(), modalityActor);
    }

    /**
     * Builds a "help" list based on the current defined modes.
     * @param modes The modes to build the help list for
     * @return A string of help text for the given modes.
     */
    private String getHelpString(
            final Map<String, Class<? extends UseModality<OptionType>>> modes) {
        final StringBuilder builder = new StringBuilder("Mode of execution, one of: ");
        int count = 0;
        for (final String mode : modes.keySet()) {
            if (count > 0) {
                builder.append(", ");
            }
            builder.append(mode);
            count++;
        }
        return builder.toString();
    }

    public void addHelpFlag(final JSAP jsap) throws JSAPException {
        final Switch helpFlag = new Switch("help")
                .setShortFlag('h')
                .setLongFlag("help");
        jsap.registerParameter(helpFlag);
    }

    /**
     * Determine whether or not a help message should be displayed.  There are two
     * cases for this, one where there is an error in the results from JSAP and the
     * other where the user explicitly asked for it.  Results are printed to
     * {@link java.lang.System#out}
     * @param jsap The parsed parameter lists
     * @param jsapResult The results of parsing the list.
     * @return true if a help message was displayed, false otherwise.
     */
    public static boolean checkPrintHelp(final JSAP jsap,
                                         final JSAPResult jsapResult) {
        if (!jsapResult.success()
                || (jsapResult.contains("help") && jsapResult.getBoolean("help"))) {
            System.out.println(jsap.getHelp());
            return true;
        }
        return false;
    }

    /**
     * Indiate that the code is in "test" mode.  The default processing mode will exit
     * on an error.  In test mode, this will not happen.
     * @param testing true if this class should be in "test" mode
     */
    public void setTestingMode(final boolean testing) {
        junitMode = testing;
    }

    /**
     * Get the command line args used to start the program.
     * @return the command line args used to start the program.
     */
    public String[] getOriginalArgs() {
        return originalArgs;
    }

    /**
     * Set the command line args used to start the program.
     * @param args the command line args used to start the program.
     */
    public void setOriginalArgs(final String[] args) {
        originalArgs = args;
    }
}

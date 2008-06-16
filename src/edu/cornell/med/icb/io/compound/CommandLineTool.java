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

package edu.cornell.med.icb.io.compound;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.Switch;
import com.martiansoftware.jsap.UnflaggedOption;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.JSAPException;

import java.io.IOException;
import java.util.Collection;

/**
 * Command line interface to Compound Files.
 * @author Kevin Dorff
 */
public class CommandLineTool {

    public enum PROGRAM_MODE {
        LIST,
        ADD,
        EXTRACT,
        HELP
    }

    private CompoundFileWriter compoundFileWriter;
    private CompoundFileReader compoundFileReader;
    private String[] filenames;

    public static void main(final String[] args) throws Exception {
        CommandLineTool tool = new CommandLineTool();
        tool.run(args);
    }

    private JSAP configureJsap() throws JSAPException {
        final JSAP jsap = new JSAP();

        final FlaggedOption compoundFileFlag = new FlaggedOption("compound-file")
                .setStringParser(JSAP.STRING_PARSER)
                .setRequired(true)
                .setShortFlag('c')
                .setLongFlag("compound-file");
        compoundFileFlag.setHelp("The compound file to read/write");
        jsap.registerParameter(compoundFileFlag);

        final Switch listSwitch = new Switch("list")
                .setShortFlag('l')
                .setLongFlag("list");
        listSwitch.setHelp("List the contents of the compound file");
        jsap.registerParameter(listSwitch);

        final Switch addSwitch = new Switch("add")
                .setShortFlag('a')
                .setLongFlag("add");
        addSwitch.setHelp("Add files to the compound file");
        jsap.registerParameter(addSwitch);

        final Switch extractSwitch = new Switch("extract")
                .setShortFlag('x')
                .setLongFlag("extract");
        extractSwitch.setHelp("Extract files from the compound file");
        jsap.registerParameter(extractSwitch);

        final Switch helpSwitch = new Switch("help")
                .setShortFlag('h')
                .setLongFlag("help");
        extractSwitch.setHelp("This help information");
        jsap.registerParameter(helpSwitch);

        final UnflaggedOption filenamesUnflag = new UnflaggedOption("filenames")
                .setStringParser(JSAP.STRING_PARSER)
                .setGreedy(true);
        filenamesUnflag.setHelp("Filenames (separated by spaces) or pattern(s) to add to the compound file");
        jsap.registerParameter(filenamesUnflag);

        return jsap;
    }

    private void run(final String args[]) throws JSAPException, IOException {
        JSAP jsap = configureJsap();
        JSAPResult config = jsap.parse(args);
        if (!config.success()) {
            help(jsap, config);
            System.exit(1);
        }

        final String compoundFilename = config.getString("compound-file");
        compoundFileWriter = new CompoundFileWriter(compoundFilename);
        compoundFileReader = compoundFileWriter.getCompoundFileReader();
        filenames = config.getStringArray("filenames");

        System.out.println("Compound file: " + compoundFilename);
        System.out.print("Filenames: (");
        int i = 0;
        for (String filename : filenames) {
            if (i++ > 0) {
                System.out.print(",");
            }
            System.out.print(filename);
        }
        System.out.println(")");
        PROGRAM_MODE mode = programMode(config);
        if (mode == PROGRAM_MODE.HELP) {
            help(jsap, config);
            System.exit(1);
        } else if (mode == PROGRAM_MODE.LIST) {
            System.out.println("List mode");
            list();
        } else if (mode == PROGRAM_MODE.ADD) {
            System.out.println("Add mode");
            if (filenames.length == 0) {
                System.err.println("Add mode specified but no filename(s)");
                System.exit(1);
            }
        } else if (mode == PROGRAM_MODE.EXTRACT) {
            System.out.println("Extract mode");
        }
    }

    private PROGRAM_MODE programMode(JSAPResult config) {
        if (config.getBoolean("help")) {
            return PROGRAM_MODE.HELP;
        } else if (config.getBoolean("list")) {
            return PROGRAM_MODE.LIST;
        } else if (config.getBoolean("add")) {
            return PROGRAM_MODE.ADD;
        } else if (config.getBoolean("extract")) {
            return PROGRAM_MODE.EXTRACT;
        } else {
            return PROGRAM_MODE.LIST;
        }
    }

    private void help(final JSAP jsap, JSAPResult config) {
        System.err.println();

        if (config != null) {
            for (java.util.Iterator errs = config.getErrorMessageIterator();
                    errs.hasNext();) {
                System.err.println("Error: " + errs.next());
            }
        }

        System.err.println();
        System.err.println("Usage:  java " + this.getClass().getName());
        System.err.println("                " + jsap.getUsage());
        System.err.println();
        System.err.println(jsap.getHelp());
        System.exit(1);
    }

    private void list() {
        Collection<CompoundDirectoryEntry> files = compoundFileReader.getDirectory();
        System.out.println("Directory of compound file");
        for (CompoundDirectoryEntry file : files) {
            System.out.println(file.getName() + "\t\t" + file.getFileSize());
        }
    }
}

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

package edu.cornell.med.icb.io;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.URL;
import java.net.MalformedURLException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.LinkedList;

/**
 * Help with obtaining resources. This will look
 * @author Marko, Kevin, code from log4j?
 */
public class ResourceFinder {

    /** Used to log debug and informational messages. */
    private static final Log LOG = LogFactory.getLog(ResourceFinder.class);

    /** The additional search paths to search. */
    private final List<String> searchPaths;

    /**
     * No extra paths configuration.
     */
    public ResourceFinder() {
        this(new String[0]);
    }

    /**
     * Create a resource finder. If the resource isn't found directly at the specified
     * path, each of searchPathsVal will be prepended to the resource requested to look
     * in those locations as well.
     * @param searchPathsVal the additional searchPaths to search for the resource.
     */
    public ResourceFinder(final String... searchPathsVal) {
        searchPaths = new LinkedList<String>();
        if (searchPathsVal != null) {
            for (String searchPath : searchPathsVal) {
                while (searchPath.endsWith("/") || searchPath.endsWith("\\")) {
                    searchPath = searchPath.substring(0, searchPath.length() - 1);
                }
                this.searchPaths.add(searchPath);
            }
        }
    }

    /**
     * Obtain a URL for a specified resource. Returns null if the resource could not be
     * obtained.
     * @param resource the resource name
     * @return A URL for the specified resource or null
     */
    public URL findResource(final String resource) {
        URL url = resourceToUrl(resource);

        // try a resource in the config directory
        if (url == null) {
            for (final String searchPath : searchPaths) {
                url = resourceToUrl(searchPath + "/" + resource);
                if (url != null) {
                    break;
                }
            }
        }

        if (url == null) {
            url = fileToURL(resource);
        }

        if (url == null) {
            for (final String searchPath : searchPaths) {
                url = fileToURL(searchPath + IOUtils.DIR_SEPARATOR + resource);
                if (url != null) {
                    break;
                }
            }
        }
        return url;
    }

    /**
     * Try to convert a file to a URL. Returns null if the file doesn't exist or isn't
     * a file or isn't readable.
     * @param filename the filename to try to map to URL
     * @return the URL
     */
    private URL fileToURL(final String filename) {
        URL url = null;
        try {
            final File file = new File(filename);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Trying to find [" + filename + "] as file in filesystem");
            }
            if (file.exists() && file.isFile() && file.canRead()) {
                url = file.toURI().toURL();
            }
        } catch (MalformedURLException e) { // NOPMD
            // resource is not a URL
        } catch (IOException e) { // NOPMD
            // Could not do file getCanonicalPath()
        }
        if (url != null && LOG.isDebugEnabled()) {
            LOG.debug("... found url is [" + url.toString() + "]");
        }
        return url;
    }

    /**
     * Search for a resource using the thread context class loader. If that fails, search
     * using the class loader that loaded this class.  If that still fails, try one last
     * time with {@link ClassLoader#getSystemResource(String)}.
     * @param resource The resource to search for
     * @return A url representing the resource or {@code null} if the resource was not found
     */
    private URL resourceToUrl(final String resource) {
        URL url;   // get the configuration from the classpath of the current thread
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (LOG.isDebugEnabled()) {
            LOG.debug("Trying to find [" + resource + "] using context class loader " + loader);
        }
        url = loader.getResource(resource);

        if (url == null) {
            // We couldn't find resource - now try with the class loader that loaded this class
            loader = ResourceFinder.class.getClassLoader();     // NOPMD
            if (LOG.isDebugEnabled()) {
                LOG.debug("Trying to find [" + resource + "] using class loader " + loader);
            }
            url = loader.getResource(resource);
        }

        if (url == null) {
            // make a last attempt to get the resource from the system class loader
            if (LOG.isDebugEnabled()) {
                LOG.debug("Trying to find [" + resource + "] using system class loader");
            }
            url = ClassLoader.getSystemResource(resource);
        }
        if (url != null && LOG.isDebugEnabled()) {
            LOG.debug("... found url is [" + url.toString() + "]");
        }
        return url;
    }

    /**
     * Return an InputStream for a given resource using the ResourceFinder.
     * @param resource the resource to get a BufferedReader for
     * @return the buffered reader or null if the resource could not be located
     * @throws IOException error obtaining reader after resource was located
     */
    public InputStream inputStreamForResource(final String resource) throws IOException {
        final URL url = findResource(resource);
        InputStream is = null;
        if (url != null) {
            is = url.openStream();
        }
        return is;
    }
}

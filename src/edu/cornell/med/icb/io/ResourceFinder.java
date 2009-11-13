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
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Help with obtaining resources. This will look
 * @author Marko, Kevin, code from log4j?
 */
public class ResourceFinder {

    /** When to search the classpath enum. */
    public enum SearchInClasspath {
        BEFORE_LOCAL,
        AFTER_LOCAL,
        NEVER
    }

    /** When to search the classpath. Default to search before searching locally. */
    public SearchInClasspath searchInClasspath = SearchInClasspath.BEFORE_LOCAL;

    /** Used to log debug and informational messages. */
    private static final Log LOG = LogFactory.getLog(ResourceFinder.class);

    /** The additional search paths to search. */
    private final List<String> searchPaths;

    /**
     * No extra paths configuration.
     */
    public ResourceFinder() {
        this(ArrayUtils.EMPTY_STRING_ARRAY);
    }

    /**
     * Create a resource finder. If the resource isn't found directly at the specified
     * path, each of searchPathsVal will be prepended to the resource requested to look
     * in those locations as well.
     * @param searchPathsVal the additional searchPaths to search for the resource.
     */
    public ResourceFinder(final String... searchPathsVal) {
        searchPaths = new LinkedList<String>();
        if (searchPathsVal != null && searchPathsVal.length > 0) {
            for (String searchPath : searchPathsVal) {
                searchPath = FilenameUtils.separatorsToUnix(searchPath);
                while (searchPath.endsWith("/")) {
                    searchPath = searchPath.substring(0, searchPath.length() - 1);
                }
                this.searchPaths.add(searchPath);
            }
        } else {
            // Default to searching "." (current directory)
            searchPaths.add(".");
        }
    }

    /**
     * Obtain a URL for a specified resource. Returns null if the resource could not be
     * obtained.
     * @param resource the resource name
     * @return A URL for the specified resource or null
     */
    public URL findResource(final String resource) {
        URL url;
        if (searchInClasspath == SearchInClasspath.BEFORE_LOCAL) {
            url = findResourceInClasspath(resource);
            if (url != null) {
                return url;
            }
        }

        url = findResourceInLocal(resource);
        if (url != null) {
            return url;
        }

        if (searchInClasspath == SearchInClasspath.AFTER_LOCAL) {
            url = findResourceInClasspath(resource);
            if (url != null) {
                return url;
            }
        }

        return null;
    }

    private URL findResourceInClasspath(final String resource) {
        // Find resource using the classpath in the root of the classpath
        URL url = resourceToUrl(resource);
        if (url != null) {
            return url;
        }

        // Find a resource using the classpath using searchPaths
        for (final String searchPath : searchPaths) {
            url = resourceToUrl(searchPath + "/" + resource);
            if (url != null) {
                return url;
            }
        }
        return null;
    }

    private URL findResourceInLocal(final String resource) {
        // Try to find the resource in the searchPaths
        for (final String searchPath : searchPaths) {
            final URL url = fileToURL(searchPath + IOUtils.DIR_SEPARATOR + resource);
            if (url != null) {
                return url;
            }
        }
        return null;
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

    /**
     * List directory contents for a resource folder that contains the specified
     * class, this list will include the .class file associated with clazz.
     *
     * @param clazz The java class which lives in the directory you want listed
     * @return Each member item (no path information)
     * @throws java.net.URISyntaxException bad URI syntax
     * @throws IOException error reading
     */
    public String[] getResourceListing(final Class clazz)
            throws URISyntaxException, IOException {
        final String classPackage = ClassUtils.getPackageName(clazz);
        final String classPacakgePath = classPackage.replace(".", "/");
        return getResourceListing(clazz, classPacakgePath);
    }

    /**
     * List directory contents for a resource folder. Not recursive, does not return
     * directory entries. Works for regular files and also JARs.
     *
     * @param clazz Any java class that lives in the same place as the resources you want.
     * @param pathVal the path to find files for
     * @return Each member item (no path information)
     * @throws URISyntaxException bad URI syntax
     * @throws IOException error reading
     */
    public String[] getResourceListing(final Class clazz, final String pathVal)
            throws URISyntaxException, IOException {
        // Enforce all paths are separated by "/", they do not start with "/" and
        // the DO end with "/".
        String path = pathVal.replace("\\", "/");
        if (!path.endsWith("/")) {
            path += "/";
        }
        while (path.startsWith("/")) {
            path = path.substring(1);
        }
        URL dirURL = findResource(path);
        if (dirURL != null && dirURL.getProtocol().equals("file")) {
            /* A file path: easy enough */
            return new File(dirURL.toURI()).list();
        }

        if (dirURL == null) {
            /*
             * In case of a jar file, we can't actually find a directory.
             * Have to assume the same jar as clazz.
             */
            final String classFilename = clazz.getName().replace(".", "/") + ".class";
            dirURL = findResource(classFilename);
        }

        if (dirURL.getProtocol().equals("jar")) {
            /* A JAR path */
            String jarPath = dirURL.getPath().substring(5, dirURL.getPath().indexOf('!'));
            if (jarPath.charAt(2) == ':') {
                jarPath = jarPath.substring(1);
            }
            jarPath = URLDecoder.decode(jarPath, "UTF-8");
            final JarFile jar = new JarFile(jarPath);
            final Enumeration<JarEntry> entries = jar.entries();
            final Set<String> result = new HashSet<String>();
            while (entries.hasMoreElements()) {
                final String name = entries.nextElement().getName();
                //filter according to the path
                if (name.startsWith(path)) {
                    final String entry = name.substring(path.length());
                    if (entry.length() == 0) {
                        // Skip the directory entry for path
                        continue;
                    }
                    final int checkSubdir = entry.indexOf('/');
                    if (checkSubdir >= 0) {
                        // Skip sub dirs
                        continue;
                    }
                    result.add(entry);
                }
            }
            return result.toArray(new String[result.size()]);
        }
        throw new UnsupportedOperationException("Cannot list files for URL " + dirURL);
    }

    /**
     * Get when to search in classpath for the resource (default is SearchInClasspath.BEFORE_LOCAL).
     * @return the searchInClasspath value
     */
    public SearchInClasspath getSearchInClasspath() {
        return searchInClasspath;
    }

    /**
     * Set when to search in classpath for the resource.
     * @param searchInClasspath the new searchInClasspath value
     */
    public void setSearchInClasspath(SearchInClasspath searchInClasspath) {
        this.searchInClasspath = searchInClasspath;
    }
}

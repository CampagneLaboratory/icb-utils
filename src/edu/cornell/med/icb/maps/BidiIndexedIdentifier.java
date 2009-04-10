/*
 * Copyright (C) 2009 Institute for Computational Biomedicine,
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

package edu.cornell.med.icb.maps;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Bidirectional "map" of index (int) <-> Identifier (String). Register identifiers
 * using getIndexForIdentifier(String) then use the various other map-like methods
 * to access the data. This isn't created as an acutal map as the method names would
 * be more confusing.
 * TODO: Add serialization value. serializable
 * @author Kevin Dorff
 */
public class BidiIndexedIdentifier {

    /** The string to index map. */
    private final Map<String, Integer> map;

    /** The index to string reverse map. */
    private final Map<Integer, String> reverseMap;

    /** The current position, used to obtain the "next" index value. */
    private int currentPosition;

    /**
     * Create a new empty BidiIndexedIdentifier.
     */
    public BidiIndexedIdentifier() {
        super();
        map = new HashMap<String, Integer>();
        reverseMap = new HashMap<Integer, String>();
        currentPosition = 0;
    }

    /**
     * Get the index for the given identifier. If the identifier isn't registered,
     * this will register it. Synchronzied because this will likely insert into
     * update currentPosition, map, and currentMap.
     * @param id the identifier to register
     * @return the index for that identifier
     */
    public synchronized int getIndexForIdentifier(final String id) {
        Integer index = map.get(id);
        if (index == null) {
            index = currentPosition++;
            map.put(id, index);
            reverseMap.put(index, id);
        }
        return index;
    }

    /**
     * Obtain the identifier from the given index. Will return null of the index
     * was not previously created. If the index isn't in the store, this will return null.
     * @param index the index to obtain the identifier for
     * @return the identifier
     */
    public String getIdentifierForIndex(final int index) {
        return reverseMap.get(index);
    }

    /**
     * True of the store contains the specified index.
     * @param index the index to look for
     * @return true if the store contains the specified index
     */
    public boolean containsIndex(final int index) {
        return reverseMap.containsKey(index);
    }

    /**
     * True of the store contains the specified identifier.
     * @param id the identifier to look for
     * @return true if the store contains the specified identifier
     */
    public boolean containsIdentifier(final String id) {
        return map.containsKey(id);
    }

    /**
     * The size of the identifier store.
     * @return size of the identifier store.
     */
    public int size() {
        return map.size();
    }

    /**
     * If the identifier store is empty.
     * @return true if the identifier store is empty
     */
    public boolean isEmpty() {
        return map.size() == 0;
    }


    /**
     * Register a list of identifiers, return their indexes. It is not a problem
     * to re-register and identifier, the old index value will be returned.
     * @param identifiers the identifers to register
     * @return the list of the indexes for the registered identifiers
     */
    public List<Integer> registerIdentifiers(final List<String> identifiers) {
        final List<Integer> indexes = new ArrayList<Integer>(identifiers.size());
        for (final String identifier : identifiers) {
            indexes.add(getIndexForIdentifier(identifier));
        }
        return indexes;
    }

    /**
     * Clear the identifier store.
     */
    public void clear() {
        map.clear();
        reverseMap.clear();
        currentPosition = 0;
    }

    /**
     * Obtain the set of indexes in the store.
     * @return the set of indexes in the store
     */
    public Set<Integer> indexSet() {
        return reverseMap.keySet();
    }

    /**
     * Obtain the set of identifiers in the store.
     * @return the set of identifiers in the store
     */
    public Set<String> identifiersSet() {
        return map.keySet();
    }

    /**
     * Return the entry set for this map. Entries will contain [index, string].
     * @return true if the store contains the specified identifier
     */
    public Set<Map.Entry<Integer, String>> entrySet() {
        return reverseMap.entrySet();
    }
}

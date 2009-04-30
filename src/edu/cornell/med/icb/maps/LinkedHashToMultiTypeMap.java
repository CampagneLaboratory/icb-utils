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

package edu.cornell.med.icb.maps;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import java.util.LinkedHashMap;

/**
 * An extension of LinkedHashMap[T, String]. Internally, all values are stored as strings
 * but they can be retrieved directly as String, Integer, Double, in[], or double[].
 * @author Kevin Dorff
 */
public class LinkedHashToMultiTypeMap<T> extends LinkedHashMap<T, String> {
    /**
     * Synonym for just using get(field). Returns the mapped value as a String
     * or null if the field doesn't exist in the map.
     * @param field the field to get
     * @return the String value for the field
     */
    public String getString(final T field) {
        return get(field);
    }

    /**
     * Returns the mapped value as a Double or null if the field doesn't exist in the map.
     * @param field the field to get
     * @return the Double value for the field
     */
    public Double getDouble(final T field) {
        final String value = get(field);
        if (value == null) {
            return null;
        }
        return NumberUtils.toDouble(value);
    }

    /**
     * Returns the mapped value as a Integer or null if the field doesn't exist in the map.
     * @param field the field to get
     * @return the Integer value for the field
     */
    public Integer getInt(final T field) {
        final String value = get(field);
        if (value == null) {
            return null;
        }
        return NumberUtils.toInt(value);
    }

    /**
     * Returns the mapped value as a double[] or null if the field doesn't exist in the map.
     * This will try to split the doubles with the ',' character.
     * @param field the field to get
     * @return the double[] value for the field
     */
    public double[] getDoubleArray(final T field) {
        return getDoubleArray(field, ',');
    }

    /**
     * Returns the mapped value as a double[] or null if the field doesn't exist in the map.
     * @param field the field to get
     * @param splitChar the char to split the doubles, often ',' or '\t' is a good choice.
     * @return the double[] value for the field
     */
    public double[] getDoubleArray(final T field, final char splitChar) {
        final String value = get(field);
        if (value == null) {
            return null;
        }
        final String[] splits;
        if (value.indexOf(splitChar) != -1) {
            splits = StringUtils.split(value, splitChar);
        } else {
            return new double[] {getDouble(field)};
        }
        final double[] result = new double[splits.length];
        int i = 0;
        for (String split : splits) {
            if (StringUtils.isBlank(split)) {
                split = "0";
            }
            result[i++] = NumberUtils.toDouble(split.trim());
        }
        return result;
    }

    /**
     * Returns the mapped value as a int[] or null if the field doesn't exist in the map.
     * This will try to split the ints with the ',' character.
     * @param field the field to get
     * @return the int[] value for the field
     */
    public int[] getIntArray(final T field) {
        return getIntArray(field, ',');
    }

    /**
     * Returns the mapped value as a int[] or null if the field doesn't exist in the map.
     * @param field the field to get
     * @param splitChar the char to split the ints, often ',' or '\t' is a good choice.
     * @return the int[] value for the field
     */
    public int[] getIntArray(final T field, final char splitChar) {
        final String value = get(field);
        if (value == null) {
            return null;
        }
        final String[] splits;
        if (value.indexOf(splitChar) != -1) {
            splits = StringUtils.split(value, splitChar);
        } else {
            return new int[] {getInt(field)};
        }
        final int[] result = new int[splits.length];
        int i = 0;
        for (String split : splits) {
            if (StringUtils.isBlank(split)) {
                split = "0";
            }
            result[i++] = NumberUtils.toInt(split.trim());
        }
        return result;
    }

    /**
     * Put a Double into the map.
     * @param field the field to assocate with the value
     * @param val the value to store as a double
     */
    public void put(final T field, final Double val) {
        if (val == null) {
            put(field, (String) null);
        } else {
            put(field, Double.toString(val));
        }
    }

    /**
     * Put a double[] into the map.
     * @param field the field to assocate with the value
     * @param val the value to store as a double[]
     */
    public void put(final T field, final double[] val) {
        if (val == null) {
            put(field, (String) null);
        } else {
            final StringBuilder result = new StringBuilder();
            for (int i = 0; i < val.length; i++) {
                if (i > 0) {
                    result.append(",");
                }
                result.append(Double.toString(val[i]));
            }
            put(field, result.toString());
        }
    }

    /**
     * Put an Integer into the map.
     * @param field the field to assocate with the value
     * @param val the value to store as a Integer
     */
    public void put(final T field, final Integer val) {
        if (val == null) {
            put(field, (String) null);
        } else {
            put(field, Integer.toString(val));
        }
    }

    /**
     * Put an int[] into the map.
     * @param field the field to assocate with the value
     * @param val the value to store as a int[]
     */
    public void put(final T field, final int[] val) {
        if (val == null) {
            put(field, (String) null);
        } else {
            final StringBuilder result = new StringBuilder();
            for (int i = 0; i < val.length; i++) {
                if (i > 0) {
                    result.append(",");
                }
                result.append(Integer.toString(val[i]));
            }
            put(field, result.toString());
        }
    }

}

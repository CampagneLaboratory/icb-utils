/*
 * Copyright (C) 2007-2009 Institute for Computational Biomedicine,
 *               Weill Medical College of Cornell University
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

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.ArrayUtils;

import java.io.Reader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

import edu.cornell.med.icb.util.ICBStringUtils;

/**
 * This class is a very glorified {@link TSVReader}. This class provides
 * a lot of extra functionality, such as
 * <ul>
 * <li> Know the number of field (columns) you have and want to name those
 *   fields and access the fields by name.
 * <li> Fields are sepeareted by ConditionsParser.fieldSeparator
 *   (default is the tab character).
 * <li> Commented and blank lines are skipped automatically
 * <li> A field of type ConditionField.FieldType.VALUE can
 *   contain a single value, which can be retrieved as a
 *   String, int, double, or boolean. Alternatively, if
 *   ConditionField.list is set to true,
 *   the field can contain a list of values (individual values
 *   separated by ConditionField.listSeparator, default is ',')
 *   and returned as an array of String, int, double, or boolean.
 *   Access via
 *   <ul>
 *   <li> {@link #parseFieldValueString}
 *   <li> {@link #parseFieldValueStringArray}
 *   <li> {@link #parseFieldValueInt}
 *   <li> {@link #parseFieldValueIntArray}
 *   <li> {@link #parseFieldValueDouble}
 *   <li> {@link #parseFieldValueDoubleArray}
 *   <li> {@link #parseFieldValueBoolean}
 *   <li> {@link #parseFieldValueBooleanArray}
 *   </ul>
 * <li> A field of type ConditionField.FieldType.MAP,
 *   ConditionField.keyValueSeparator (default is '=')
 *   will be used to specify key/value pairs seperated by
 *   ConditionField.listSeparator (default is ',').
 *   Access via {@link #parseFieldMap}
 * <li> Alternatively, when a field is of type
 *   ConditionField.FieldType.MAP, you can use
 *   {@link #parseFieldBean} to provide extra functionality,
 *   which is to use the bean setters on the
 *   targetObject to set the properties of the
 *   keys to the specified values.
 *   If one of the key/value pairs in the map is
 *   ConditionField.classnameKey (default is
 *   "_CLASSNAME_") and targetObject isn't specified (null),
 *   the object will be created and the bean properties
 *   set.
 * </ul>
 * @author Kevin Dorff (Nov 21, 2007)
 */
public class ConditionsParser {

    /**
     * The condition fields.
     */
    private final List<ConditionField> fields;

    /**
     * Default field separator (tab).
     */
    private static final char FIELD_SEPARATOR_DEFAULT = '\t';

    /**
     * Default comment prefix.
     */
    private static final String COMMENT_PREFIX_DEFAULT = "#";

    /**
     * Default escape character.
     */
    private static final char ESCAPE_CHAR_DEFAULT = '\\';

    /**
     * The field separator that is being used.
     */
    private char fieldSeparator = FIELD_SEPARATOR_DEFAULT;

    /**
     * The comment prefix that is being used.
     */
    private String commentPrefix = COMMENT_PREFIX_DEFAULT;

    /**
     * The escape character that is being used.
     */
    private Character escapeChar = ESCAPE_CHAR_DEFAULT;

    /**
     * The TSV reader that is being used.
     */
    private TSVReader tsvReader;

    /**
     * True if hasNext.
     */
    private boolean hasNext;

    /**
     * The current line number.
     */
    private int lineNumber;

    /**
     * A map of the field name to the field number.
     */
    private Map<String, Integer> nameToFieldNumberMap;

    /**
     * Create a new ConditionsParser with no fields
     * specified. This is provided assuming addField will
     * be called later to add fields.
     */
    public ConditionsParser() {
        this(new ArrayList<ConditionField>());
    }

    /**
     * Create a new ConditionsParser with a List[ConditionField]
     * of fields.
     * @param fieldsVal the ConditionFields to use in the parsing.
     */
    public ConditionsParser(final List<ConditionField> fieldsVal) {
        super();
        this.fields = new ArrayList<ConditionField>(fieldsVal.size());
        this.fields.addAll(fieldsVal);
        this.tsvReader = null;
        this.hasNext = false;
    }

    /**
     * Add a new ConditionField to the parser.
     * @param field the ConditionField to add to the parser
     * @return this ConditionsParser object, so configuration can be
     * chained.
     */
    public ConditionsParser addField(final ConditionField field) {
        fields.add(field);
        return this;
    }

    /**
     * Clear the fields for the parser.
     * @return this ConditionsParser object, so configuration can be
     * chained.
     */
    public ConditionsParser clearFields() {
        fields.clear();
        return this;
    }

    /**
     * Set the fieldSeparator, the char value that will
     * separate multiple fields in the input. The default
     * is '\t' (tab). Such as with a MAP "123\t456"
     * will define two fields, the first field has a value of
     * "123" and the second field has a value of "456".
     * This is NOT a regex.
     * @param fieldSeparatorVal the char which is used to separate
     * multiple fields in the input
     * @return this ConditionsParser object, so configuration can be
     * chained.
     */
    public ConditionsParser setFieldSeparator(final char fieldSeparatorVal) {
        this.fieldSeparator = fieldSeparatorVal;
        return this;
    }

    /**
     * Get the fieldSeparator char.
     * @return the fieldSeparator char
     */
    public char getFieldSeparator() {
        return this.fieldSeparator;
    }

    /**
     * Get the number of fields the parser
     * will try to retrieve from the file.
     * @return the the number of fields
     */
    public int getFieldsCount() {
        if (fields == null) {
            return 0;
        } else {
            return fields.size();
        }
    }

    /**
     * Get the list of fields the parser
     * will use when parsing the file.
     * @return the list of fields
     */
    public List<ConditionField> getFields() {
        return fields;
    }

    /**
     * Set the commentPrefix, the String value (normally
     * one or two characters) that specify the line
     * is a comment and should be ignored, treated like
     * a blank line. The default is "#". The line
     * will not be treated as a comment unless this
     * String is the VERY FIRST THING on the line,
     * no extra whitespace, etc. will be tolerated.
     * This is NOT a regex.
     * @param commentPrefixVal the String which specify a line
     * is a comment if the line starts with this String
     * @return this ConditionsParser object, so configuration can be
     * chained.
     */
    public ConditionsParser setCommentPrefix(final String commentPrefixVal) {
        this.commentPrefix = commentPrefixVal;
        return this;
    }

    /**
     * Get the commentPrefix String.
     * @return the commentPrefix String
     */
    public String getCommentPrefix() {
        return this.commentPrefix;
    }

    /**
     * Set the escape character that will be used when
     * parsing.
     * @param escapeCharVal which escape char to use, or null
     * to not use any escape char
     * @return this ConditionsParser object, so configuration can be
     * chained.
     */
    public ConditionsParser setEscapeChar(final Character escapeCharVal) {
        this.escapeChar = escapeCharVal;
        return this;
    }

    /**
     * Get the escape char to use when parsing.
     * @return the escape char to use when parsing
     */
    public Character getEscapeChar() {
        return this.escapeChar;
    }

    /**
     * This method is called first to begin parsing
     * condition data.
     * @param sourceReader the reader for the condition data
     * to parse
     */
    public void beginParse(final Reader sourceReader) {
        tsvReader = new TSVReader(sourceReader, fieldSeparator);
        hasNext = false;
        lineNumber = 0;
        tsvReader.setCommentPrefix(commentPrefix);
        tsvReader.setEscapeChar(escapeChar);
        tsvReader.setUnescapeResults(false);
        nameToFieldNumberMap = new HashMap<String, Integer>(fields.size());
        int pos = 0;
        for (final ConditionField field : fields) {
            final Integer current = nameToFieldNumberMap.get(field.getFieldName());
            if (current == null) {
                // Only use the name for ONE field, the first one
                nameToFieldNumberMap.put(field.getFieldName(), pos);
            }
            pos++;
        }
    }

    /**
     * This does complete parsing of the entire file into objects.
     * All fields should have ConditionField.valueBeanProperty
     * set or else they will get ignored.
     * @param sourceReader the reader for the condition data
     * to parse
     * @param templateClass the template object class. Each item in
     * the return list will start as a new object of this
     * class type. The class must have an empty constructor.
     * @param valuesMap this OPTIONAL map can be provided
     * and all values that are set on the bean will be
     * placed in this map. It is fine to pass null for this
     * paramter.
     * @param <T> the generic type in question
     * @throws IOException Error reading the source file
     * @throws ConditionsParsingException no data to parse,
     * beginParse or hasNext probably wasn't called (or called
     * passed the end of the data). Also thrown if there is
     * a catastrphic BeanUtils problem or a problem creating
     * an object.
     * @return List of objects, each item represents
     * one (non comment) line in the input file.
     */
    public <T> List<T> beginParseAllToBeans(final Reader sourceReader,
                                            final Class<T> templateClass,
                                            final Map<String, String> valuesMap)
            throws IOException, ConditionsParsingException {
        final List<T> resultObjs = new ArrayList<T>();
        beginParse(sourceReader);
        while (hasNext()) {
            final T object;
            try {
                object = templateClass.newInstance();
            } catch (IllegalAccessException e) {
                throw new ConditionsParsingException("Could not create new object "
                    + "for classname " + templateClass.getName()
                    + " - IllegalAccessException for line " + lineNumber, e);
            } catch (InstantiationException e) {
                throw new ConditionsParsingException("Could not create new object "
                    + "for classname " + templateClass.getName()
                    + " - InstantiationException for line " + lineNumber, e);
            }
            resultObjs.add(object);
            parseAllFieldsBean(object, valuesMap);
        }
        return resultObjs;
    }

    /**
     * This does complete parsing of the entire file into objects.
     * All fields should have ConditionField.valueBeanProperty
     * set or else they will get ignored.
     * @param <T> the generic type in question
     * @param sourceReader the reader for the condition data
     * to parse
     * @param templateObject the template Object. Each item in
     * the return list will start as a property-for-property
     * copy of this object (each property must be in the
     * bean pattern). The template object must have an
     * empty constructor. If a getter throws an exception
     * during the property-for-property copy, this will
     * cause this method to fail. The close the
     * templateObject is to simple bean better the
     * @param valuesMap this OPTIONAL map can be provided
     * and all values that are set on the bean will be
     * placed in this map. It is fine to pass null for this
     * paramter.
     * @throws IOException Error reading the source file
     * @throws ConditionsParsingException no data to parse,
     * beginParse or hasNext probably wasn't called (or called
     * passed the end of the data). Also thrown if there is
     * a catastrphic BeanUtils problem or a problem creating
     * an object.
     * @return List of objects, each item represents
     * one (non comment) line in the input file.
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> beginParseAllToBeans(final Reader sourceReader,
                                            final T templateObject,
                                            final Map<String, String> valuesMap)
            throws IOException, ConditionsParsingException {
        if (templateObject == null) {
            throw new ConditionsParsingException(
                    "templateObject cannot be is null when calling parseAllFieldsBean");
        }
        final List<T> resultObjs = new ArrayList<T>();
        beginParse(sourceReader);
        while (hasNext()) {
            final T object;
            try {
                object = (T) BeanUtils.cloneBean(templateObject);
            } catch (IllegalAccessException e) {
                throw new ConditionsParsingException("Could not create new object "
                        + "for classname " + templateObject.getClass().getName()
                        + " - IllegalAccessException for line " + lineNumber, e);
            } catch (InvocationTargetException e) {
                throw new ConditionsParsingException("Could not create new object "
                    + "for classname " + templateObject.getClass().getName()
                    + " - InvocationTargetException for line " + lineNumber, e);
            } catch (NoSuchMethodException e) {
                throw new ConditionsParsingException("Could not create new object "
                    + "for classname " + templateObject.getClass().getName()
                    + " - NoSuchMethodException for line " + lineNumber, e);
            } catch (InstantiationException e) {
                throw new ConditionsParsingException("Could not create new object "
                    + "for classname " + templateObject.getClass().getName()
                    + " - InstantiationException for line " + lineNumber, e);
            }
            resultObjs.add(object);
            parseAllFieldsBean(object, valuesMap);
        }
        return resultObjs;
    }

    /**
     * Determine if a next condition line is available. This should
     * ONLY be called once per line, if you call it multiple times you
     * will be skipping lines. This will automatically skip over
     * blank or comment lines.
     * @return true if a next line is available.
     * @throws IOException error reading condition file.
     * @throws ConditionsParsingException when tsvReader doesn't exist,
     * such as beginParse() not called before hasNext()
     */
    public boolean hasNext() throws IOException, ConditionsParsingException {
        if (tsvReader == null) {
            throw new ConditionsParsingException(
                    "No tsvReader, did you call beginParse(...)?");
        }

        for (final ConditionField field : fields) {
            // Reset back to "defaults"
            field.resetValueToDefault();
        }

        while (true) {
            hasNext = tsvReader.hasNext();
            if (hasNext) {
                lineNumber++;
                if (tsvReader.isCommentLine() || tsvReader.isEmptyLine()) {
                    // Do nothing, this is a comment or empty line
                    tsvReader.skip();
                } else {
                    tsvReader.next();
                    final int numFields = tsvReader.numTokens();
                    if (numFields > fields.size()) {
                        // Too many fields. This is a problem.
                        tsvReader.close();
                        tsvReader = null;
                        hasNext = false;
                        throw new ConditionsParsingException(
                                "Source file has too many fields on line "
                                + lineNumber);
                    }
                    for (int i = 0; i < numFields; i++) {
                        fields.get(i).setCurrentValue(tsvReader.getString());
                    }
                    break;
                }
            } else {
                tsvReader.close();
                tsvReader = null;
                break;
            }
        }
        return hasNext;
    }

    /**
     * Get the current line number.
     * @return the current line number
     */
    public int getLineNumber() {
        return lineNumber;
    }

    /**
     * Return the field number for the given field name.
     * @param fieldName the field name
     * @return the field number for that field name
     * @throws ConditionsParsingException the field name
     * wasn't specified in the configuration of
     * this object.
     */
    private int getFieldNumber(final String fieldName)
            throws ConditionsParsingException {
        final Integer fieldNumber = nameToFieldNumberMap.get(fieldName);
        if (fieldNumber == null) {
            throw new ConditionsParsingException("Field name "
                    + fieldName + " does not exist in specified fields");
        }
        return fieldNumber;
    }

    /**
     * Return the String value of the field named with fieldName
     * or the default if that field wasn't in the conditions file.
     * If isList is set for this field, the first value is
     * returned, otherwise the entire value is returned
     * @param fieldName the field name
     * @return the string value for that field
     * @throws ConditionsParsingException no data to parse,
     * beginParse or hasNext probably wasn't called (or called
     * passed the end of the data)
     */
    public String parseFieldValueString(final String fieldName)
            throws ConditionsParsingException {
        if (!hasNext) {
            throw new ConditionsParsingException(
                    "No line to parse. Did you call beginParse(...) and hasNext()?");
        }
        final int fieldNumber = getFieldNumber(fieldName);
        final ConditionField field = fields.get(fieldNumber);
        String wholeValue = field.getCurrentValue();
        if (wholeValue == null) {
            wholeValue = "";
        }
        if (field.isList()) {
            // Only return the first one
            final String[] listVals = ICBStringUtils.split(wholeValue,
                    field.getListSeparator(), escapeChar);
            if (listVals.length == 0) {
                return "";
            } else {
                return ICBStringUtils.unescape(listVals[0],
                        escapeChar);
            }
        } else {
            return ICBStringUtils.unescape(field.getCurrentValue(),
                    escapeChar);
        }
    }

    /**
     * Return the String[] values of the field named with fieldName
     * or the default if that field wasn't in the conditions file.
     * If isList this will return the data as split by
     * ConditionField.listSeparator, otherwise a single element
     * string array will be returned containing the single value.
     * @param fieldName the field name
     * @return the string array value for that field
     * @throws ConditionsParsingException no data to parse,
     * beginParse or hasNext probably wasn't called (or called
     * passed the end of the data)
     */
    public String[] parseFieldValueStringArray(final String fieldName)
            throws ConditionsParsingException {
        if (!hasNext) {
            throw new ConditionsParsingException(
                    "No line to parse. Did you call beginParse(...) and hasNext()?");
        }
        final int fieldNumber = getFieldNumber(fieldName);
        final ConditionField field = fields.get(fieldNumber);
        String wholeValue = field.getCurrentValue();
        if (wholeValue == null) {
            wholeValue = "";
        }
        if (field.isList()) {
            // Return all
            String[] listVals = ICBStringUtils.split(wholeValue,
                    field.getListSeparator(), escapeChar);
            listVals = ICBStringUtils.unescape(listVals, escapeChar);
            return listVals;
        } else {
            // Make an array with the single element
            final String[] listVals = new String[1];
            listVals[0] = ICBStringUtils.unescape(field.getCurrentValue(),
                    escapeChar);
            return listVals;
        }
    }

    /**
     * Return the int value of the field named with fieldName
     * or the default (converted to an int) if that field wasn't
     * in the conditions file. If isList is set for this
     * field, the first value is returned, otherwise the entire
     * value is returned. Note that if the data isn't parsable
     * to an int this will throw a NumberFormatException.
     * @param fieldName the field name
     * @return the int value for that field
     * @throws ConditionsParsingException no data to parse,
     * beginParse or hasNext probably wasn't called (or called
     * passed the end of the data).
     */
    public int parseFieldValueInt(final String fieldName)
            throws ConditionsParsingException {
        final String fieldValue = parseFieldValueString(fieldName);
        return Integer.parseInt(fieldValue);
    }

    /**
     * Return the int[] values of the field named with fieldName
     * or the default if that field wasn't in the conditions file.
     * If isList this will return the data as split by
     * ConditionField.listSeparator, otherwise a single element
     * int array will be returned containing the single value.
     * Note that if the data isn't parsable to a int this
     * will throw a NumberFormatException.
     * @param fieldName the field name
     * @return the int array value for that field
     * @throws ConditionsParsingException no data to parse,
     * beginParse or hasNext probably wasn't called (or called
     * passed the end of the data)
     */
    public int[] parseFieldValueIntArray(final String fieldName)
            throws ConditionsParsingException {
        final String[] fieldValues = parseFieldValueStringArray(fieldName);
        final int[] outValues = new int[fieldValues.length];
        for (int i = 0; i < fieldValues.length; i++) {
            outValues[i] = Integer.parseInt(fieldValues[i]);
        }
        return outValues;
    }

    /**
     * Return the double value of the field named with fieldName
     * or the default (converted to an double) if that field wasn't
     * in the conditions file. If isList is set for this
     * field, the first value is returned, otherwise the entire
     * value is returned. Note that if the data isn't parsable
     * to an double this will throw a NumberFormatException.
     * @param fieldName the field name
     * @return the int value for that field
     * @throws ConditionsParsingException no data to parse,
     * beginParse or hasNext probably wasn't called (or called
     * passed the end of the data).
     */
    public double parseFieldValueDouble(final String fieldName)
            throws ConditionsParsingException {
        final String fieldValue = parseFieldValueString(fieldName);
        return Double.parseDouble(fieldValue);
    }

    /**
     * Return the double[] values of the field named with fieldName
     * or the default if that field wasn't in the conditions file.
     * If isList this will return the data as split by
     * ConditionField.listSeparator, otherwise a single element
     * double array will be returned containing the single value.
     * Note that if the data isn't parsable to a double this
     * will throw a NumberFormatException.
     * @param fieldName the field name
     * @return the double array value for that field
     * @throws ConditionsParsingException no data to parse,
     * beginParse or hasNext probably wasn't called (or called
     * passed the end of the data)
     */
    public double[] parseFieldValueDoubleArray(final String fieldName)
            throws ConditionsParsingException {
        final String[] fieldValues = parseFieldValueStringArray(fieldName);
        final double[] outValues = new double[fieldValues.length];
        for (int i = 0; i < fieldValues.length; i++) {
            outValues[i] = Double.parseDouble(fieldValues[i]);
        }
        return outValues;
    }

    /**
     * Return the boolean value of the field named with fieldName
     * or the default (converted to an boolean) if that field wasn't
     * in the conditions file. If isList is set for this
     * field, the first value is returned, otherwise the entire
     * value is returned. Note that if the data isn't equal
     * to (ignoring case) "true", this will return false.
     * @param fieldName the field name
     * @return the int value for that field
     * @throws ConditionsParsingException no data to parse,
     * beginParse or hasNext probably wasn't called (or called
     * passed the end of the data).
     */
    public boolean parseFieldValueBoolean(final String fieldName)
            throws ConditionsParsingException {
        final String fieldValue = parseFieldValueString(fieldName);
        return Boolean.parseBoolean(fieldValue);
    }

    /**
     * Return the boolean[] values of the field named with fieldName
     * or the default if that field wasn't in the conditions file.
     * If isList this will return the data as split by
     * ConditionField.listSeparator, otherwise a single element
     * boolean array will be returned containing the single value.
     * Note that if the values don't parse to (ignoring case)
     * true, the value of false will be set.
     * @param fieldName the field name
     * @return the boolean array value for that field
     * @throws ConditionsParsingException no data to parse,
     * beginParse or hasNext probably wasn't called (or called
     * passed the end of the data)
     */
    public boolean[] parseFieldValueBooleanArray(final String fieldName)
            throws ConditionsParsingException {
        final String[] fieldValues = parseFieldValueStringArray(fieldName);
        final boolean[] outValues = new boolean[fieldValues.length];
        for (int i = 0; i < fieldValues.length; i++) {
            outValues[i] = Boolean.parseBoolean(fieldValues[i]);
        }
        return outValues;
    }

    /**
     * Parse the field to a MAP. MAP entries are specified by
     * ConditionField.listSeparator (default is ",") and key=value
     * (the "=") are separated by ConditionField.valueSeparator.
     * The Map is returned in a Map[String,String].
     * Default will be used if that field wasn't in the
     * conditions file.
     * @param fieldName the field name
     * @return the Map for the field name
     * @throws ConditionsParsingException no data to parse,
     * beginParse or hasNext probably wasn't called (or called
     * passed the end of the data)
     */
    public Map<String, String> parseFieldMap(final String fieldName)
            throws ConditionsParsingException {
        final String[] fieldValues = parseFieldValueStringArray(fieldName);
        final ConditionField field = fields.get(getFieldNumber(fieldName));
        final char valueSeparator = field.getKeyValueSeparator();
        final Map<String, String> outValues = new HashMap<String, String>();
        for (final String fieldValue : fieldValues) {
            final int pos = fieldValue.indexOf(valueSeparator);
            if (pos == -1) {
                // No "=". Just place an empty string
                outValues.put(fieldValue,  "");
                continue;
            }
            final String left = fieldValue.substring(0, pos);
            final String right = fieldValue.substring(pos + 1, fieldValue.length());
            outValues.put(left, right);
        }
        return outValues;
    }

    /**
     * This is a special case that allows ALL fields to be
     * parsed at once as bean. This will attempt to parse
     * any field which has ConditionField.valueBeanProperty
     * set. This is equivalent to calling parseFieldBean
     * for each field individually. This requires that
     * targetObject be not null..
     * @param targetObject Required. The object to set
     * the bean values
     * @param valuesMap this OPTIONAL map can be provided
     * and all values that are set on the bean will be
     * placed in this map. It is fine to pass null for this
     * paramter.
     * @throws ConditionsParsingException no data to parse,
     * beginParse or hasNext probably wasn't called (or called
     * passed the end of the data). Also thrown if there is
     * a catastrphic BeanUtils problem or a problem creating
     * an object.
     */
    public void parseAllFieldsBean(final Object targetObject,
                                 final Map<String, String> valuesMap)
            throws ConditionsParsingException {
        if (targetObject == null) {
            throw new ConditionsParsingException(
                    "targetObject cannot be is null when calling parseAllFieldsBean");
        }
        for (final ConditionField field : fields) {
            if (StringUtils.isNotBlank(field.getValueBeanProperty())) {
                parseFieldBean(field.getFieldName(), targetObject, valuesMap);
            }
        }
    }

    /**
     * Parse the field (should be FieldType of MAP) to bean
     * names and values to be set on targetObject.
     * Multiple bean key=values are specified
     * by ConditionField.listSeparator (default of ",")
     * and key=value (the "=") are separated by
     * ConditionField.valueSeparator. Note that if a property for
     * a key is not available on the targetObject this will
     * just do nothing (not set any value) - this is done
     * silently. If targetObject is null and a key of
     * ConditionField.classnameKey (default of "_CLASSNAME_")
     * is provided in the field, a new object will be created
     * and the properties set on that object.
     * @param fieldName the field name
     * @param targetObject the object to set the bean values on
     * or null to create a new object using class defined
     * in the key that matches ConditionField.classnameKey.
     * @param valuesMap this OPTIONAL map can be provided
     * and all values that are set on the bean will be
     * placed in this map. It is fine to pass null for this
     * paramter.
     * @return The object which the beans properties were set
     * on. If targetObject is not null, this will be object,
     * if targetObject IS null and a key was provided
     * equal to ConditionField.classnameKey, the value associated
     * with that key will be the object that is created,
     * beans properties set, and the object returned.
     * @throws ConditionsParsingException no data to parse,
     * beginParse or hasNext probably wasn't called (or called
     * passed the end of the data). This can also be returned
     * if there is no object to set the bean properties on, ie,
     * targetObject is null and ConditionField.classnameKey is no
     * specified in the field. Also thrown if there is
     * a catastrphic BeanUtils problem or a problem creating
     * an object.
     */
    public Object parseFieldBean(final String fieldName,
                                 final Object targetObject,
                                 final Map<String, String> valuesMap)
            throws ConditionsParsingException {

        final int fieldNumber = getFieldNumber(fieldName);
        final ConditionField field = fields.get(fieldNumber);

        if (field.getFieldType() == ConditionField.FieldType.VALUE) {
            if (targetObject != null && StringUtils.isNotBlank(field.getValueBeanProperty())) {
                parseFieldBeanValue(field, targetObject, valuesMap);
            }
            return targetObject;
        }

        final Map<String, String> fieldValues = parseFieldMap(fieldName);
        final String classname = fieldValues.get(field.getClassnameKey());

        String prependObjectBean = "";
        if (StringUtils.isNotBlank(field.getValueBeanProperty())
                && StringUtils.isNotBlank(classname)) {
            prependObjectBean = field.getValueBeanProperty();
        }

        Object object = targetObject;
        if (object == null || prependObjectBean.length() > 0) {
            if (classname == null) {
                throw new ConditionsParsingException("targetObject is null and "
                    + field.getClassnameKey()
                    + " is not specified for line " + lineNumber);
            }
            try {
                final Object newObject = Class.forName(classname).newInstance();
                if (prependObjectBean.length() > 0) {
                    BeanUtils.setProperty(object, prependObjectBean, newObject);
                    prependObjectBean = prependObjectBean + ".";
                } else {
                    object = newObject;
                }
            } catch (InvocationTargetException e) {
                throw new ConditionsParsingException("Could not create new object "
                    + "for classname " + classname
                    + " - InvocationTargetException for line " + lineNumber, e);
            } catch (InstantiationException e) {
                throw new ConditionsParsingException("Could not create new object "
                    + "for classname " + classname
                    + " - InstantiationException for line " + lineNumber, e);
            } catch (IllegalAccessException e) {
                throw new ConditionsParsingException("Could not create new object "
                    + "for classname " + classname
                    + " - IllegalAccessException for line " + lineNumber, e);
            } catch (ClassNotFoundException e) {
                throw new ConditionsParsingException("Could not create new object "
                    + "for classname " + classname
                    + " - ClassNotFoundException for line " + lineNumber, e);
            }
        }

        for (final String key : fieldValues.keySet()) {
            if (key.equals(field.getClassnameKey())) {
                continue;
            }
            final String value = fieldValues.get(key);
            try {
                BeanUtils.setProperty(object, prependObjectBean + key, value);
                if (valuesMap != null) {
                    valuesMap.put(key, value);
                }
            } catch (IllegalAccessException e) {
                throw new ConditionsParsingException(
                    "Could not create new object "
                    + "for classname " + classname
                    + " - IllegalAccessException for line " + lineNumber
                    + " key = " + prependObjectBean + key
                    + " / value = " + value, e);
            } catch (InvocationTargetException e) {
                throw new ConditionsParsingException(
                    "Could not create new object "
                    + "for classname " + classname
                    + " - InvocationTargetException for line " + lineNumber
                    + " key = " + prependObjectBean + key
                    + " / value = " + value, e);
            }
        }
        return object;
    }

    /**
     * When parseFieldBean is called and the field in question has
     * ConditionField.FieldType of VALUE and
     * ConditionField.valueBeanProperty is defined, this method
     * will be used to assign the value. Not only does this work
     * with values (ConditionField.list is false),
     * it also works arrays (ConditionField.list is true). The
     * The caveat is that the target object (bean) should define
     * the property to be an array, not a collection/list/set.
     * @param field the field (not fieldName as seen elsewhere)
     * we are setting the bean value for
     * @param targetObject the object to set the bean values on
     * or null to create a new object using class defined
     * in the key that matches ConditionField.classnameKey.
     * @param valuesMap this OPTIONAL map can be provided
     * and all values that are set on the bean will be
     * placed in this map. It is fine to pass null for this
     * paramter.
     * @throws ConditionsParsingException no data to parse,
     * beginParse or hasNext probably wasn't called (or called
     * passed the end of the data). Also thrown if there is
     * a catastrphic BeanUtils problem or a problem creating
     * an object.
     */
    private void parseFieldBeanValue(final ConditionField field,
                                     final Object targetObject,
                                 final Map<String, String> valuesMap)
            throws ConditionsParsingException {
        final Object fieldValue;
        boolean isClassname = false;
        if (field.isList()) {
            fieldValue = parseFieldValueStringArray(field.getFieldName());
        } else {
            if (field.isClassname()) {
                isClassname = true;
            }
            fieldValue = parseFieldValueString(field.getFieldName());
        }
        try {
            if (isClassname) {
                final Object newObject =
                        Class.forName((String) fieldValue).newInstance();
                BeanUtils.setProperty(targetObject,
                        field.getValueBeanProperty(), newObject);
            } else {
                BeanUtils.setProperty(targetObject,
                        field.getValueBeanProperty(), fieldValue);
                if (valuesMap != null) {
                        valuesMap.put(field.getValueBeanProperty(),
                                ArrayUtils.toString(fieldValue));
                }
            }
        } catch (InvocationTargetException e) {
            throw new ConditionsParsingException("BeanUtils problem "
                + " - InvocationTargetException for line " + lineNumber
                + " key = " + field.getValueBeanProperty()
                + " / value = " + fieldValue, e);
        } catch (InstantiationException e) {
            throw new ConditionsParsingException("BeanUtils problem "
                + " - InstantiationException for line " + lineNumber
                + " key = " + field.getValueBeanProperty()
                + " / value = " + fieldValue, e);
        } catch (IllegalAccessException e) {
            throw new ConditionsParsingException("BeanUtils problem "
                + " - IllegalAccessException for line " + lineNumber
                + " key = " + field.getValueBeanProperty()
                + " / value = " + fieldValue, e);
        } catch (ClassNotFoundException e) {
            throw new ConditionsParsingException("Could not create new object "
                + " - ClassNotFoundException for line " + lineNumber
                + " key = " + field.getValueBeanProperty()
                + " / value = " + fieldValue, e);
        }
    }

}

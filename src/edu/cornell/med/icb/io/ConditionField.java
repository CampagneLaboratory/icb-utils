/*
 * Copyright (C) 2007-2008 Institute for Computational Biomedicine,
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

import org.apache.commons.lang.StringUtils;

/**
 * A condition field for parsing condition files with the
 * {@link ConditionsParser}.
 * @author Kevin Dorff (Nov 21, 2007)
 */
public class ConditionField {
    public enum FieldType {
        MAP,
        VALUE
    }

    private final static char LIST_SEPARATOR_DEFAULT = ',';
    private final static char KEY_VALUE_SEPERATOR_DEFAULT = '=';
    private final static String CLASSNAME_KEY_DEFAULT = "_CLASSNAME_";
    private final static String DEFAULT_VALUE_DEFAULT = "";

    private char listSeparator = LIST_SEPARATOR_DEFAULT;
    private char keyValueSeparator = KEY_VALUE_SEPERATOR_DEFAULT;
    private String defaultValue = DEFAULT_VALUE_DEFAULT;
    private String currentValue = DEFAULT_VALUE_DEFAULT;
    private String classnameKey = CLASSNAME_KEY_DEFAULT;

    private FieldType fieldType;
    private String fieldName;
    private boolean list;
    private String valueBeanProperty;
    private boolean isClassname;

    /**
     * Create a new field, specifying the type and the name.
     * List defaults true if fieldType is
     * MAP, otherwise it defaults to false.
     * @param fieldType the type of field to create
     * @param name the name of the field.
     */
    public ConditionField(final FieldType fieldType, String name) {
        this.fieldType = fieldType;
        this.fieldName = name;
        list = (fieldType == FieldType.MAP);
    }

    /**
     * Get the field type for this field.
     * @return the field type for this field
     */
    public FieldType getFieldType() {
        return this.fieldType;
    }

    /**
     * Set if this field contains list values, separated
     * by listSeparator.
     * @param list true of this field contains list values
     * @return this ConditionField object, so configuration can be
     * chained.
     */
    public ConditionField setList(final boolean list) {
        this.list = list;
        return this;
    }

    /**
     * Get if this field may contain list values, separated
     * by listSeparator. This should be true, by default,
     * for MAP and should be set to true if
     * this should contain an array of items.
     * @return true if field may contain list values
     */
    public boolean isList() {
        return this.list;
    }

    /**
     * Set the listSeparator, the char value
     * that will separate mutliple values for the field. The default
     * is ','. Such as with a MAP "a=123,b=456"
     * will have two entries a being equal to 123, b being equal to 456.
     * or if "123,456" was retrieved as an int array, itwould have two
     * values 123 and 456. This is NOT a regex.
     * @param listSeparator the char which is used to separate
     * list values in a field
     * @return this ConditionField object, so configuration can be
     * chained.
     */
    public ConditionField setListSeparator(final char listSeparator) {
        this.listSeparator = listSeparator;
        return this;
    }

    /**
     * Get the listSeparator char.
     * @return the listSeparator char
     */
    public char getListSeparator() {
        return this.listSeparator;
    }

    /**
     * When specifying a FieldType BEAN, it is possible to
     * provide a classname and have a new object be created,
     * the value associated with the key specified by classnameKey
     * specifies the full class path of the object that should
     * be created, and when you call parseFieldBean pass
     * a null targetObject. The default for classnameKey
     * is "_CLASSNAME_". For instance you could have a
     * field of
     * "_CLASSNAME_=edu.cornell.med.icb.SampleBean,oneInt=543"
     * and the object edu.cornell.med.icb.SampleBean will be
     * created and setOneInt(543) will be called on that object.
     * @param classnameKey the key to use to specify the class
     * to b created.
     * @return this ConditionField object, so configuration can be
     * chained.
     */
    public ConditionField setClassnameKey(final String classnameKey) {
        this.classnameKey = classnameKey;
        return this;
    }

    /**
     * Get the classnameKey String.
     * @return the classnameKey String
     */
    public String getClassnameKey() {
        return this.classnameKey;
    }

    /**
     * Set the keyValueSeparator, the char value
     * that will separate key from value, used when FieldType is
     * MAP. The default is '='. Such as with a
     * MAP "a=123,b=456" will have two entries a being
     * equal to 123, b being equal to 456. This is NOT a regex.
     * @param keyValueSeparator the char value that will separate
     * the key from the value.
     * @return this ConditionField object, so configuration can be
     * chained.
     */
    public ConditionField setKeyValueSeparator(final char keyValueSeparator) {
        this.keyValueSeparator = keyValueSeparator;
        return this;
    }

    /**
     * Get the keyValueSeparator char.
     * @return the valueSeparator char
     */
    public char getKeyValueSeparator() {
        return this.keyValueSeparator;
    }

    /**
     * Get the field name of this field. This is the value that is used to
     * retrieve the data from the field.
     * @return the field name of this field.
     */
    public String getFieldName() {
        return this.fieldName;
    }

    /**
     * Set the default value for this field, if no value is provided
     * (or if the field is omitted). An empty string in the source data
     * will be returned as default value.
     * @param defaultValue the default value to use for this field
     * @return this ConditionField object, so configuration can be
     * chained.
     */
    public ConditionField setDefaultValue(final String defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    /**
     * Get the defaultValue String.
     * @return the defaultValue String
     */
    public String getDefaultValue() {
        return this.defaultValue;
    }

    /**
     * Set the bean property that will automatically be set
     * if (a) FieldType is VALUE and (b) parsing for the field
     * is done via parseFieldBean.
     * @param valueBeanProperty the property name that will be
     * set for this bean property
     * @return this ConditionField object, so configuration can be
     * chained.
     */
    public ConditionField setValueBeanProperty(final String valueBeanProperty) {
        this.valueBeanProperty = valueBeanProperty;
        return this;
    }

    /**
     * Get the defaultValue String.
     * @return the defaultValue String
     */
    public String getValueBeanProperty() {
        return this.valueBeanProperty;
    }

    /**
     * Reset currentValue of this field to default value.
     */
    protected void resetValueToDefault() {
        this.currentValue = defaultValue;
    }

    /**
     * Set the currentValue of this field. Used by
     * ConditionsParser during parsing for storage.
     * @param currentValue the new currentValue of this field
     */
    protected void setCurrentValue(final String currentValue) {
        if (StringUtils.isNotBlank(currentValue)) {
            this.currentValue = currentValue;
        }
    }

    /**
     * Get the currentValue of this field. Used by
     * ConditionsParser during parsing for storage.
     * @return the currentValue of this field
     */
    protected String getCurrentValue() {
        return currentValue;
    }

    /**
     * If FieldType is VALUE, this specifies that
     * the value is a fully qualified pathname for
     * a class with an no args constructor. If you
     * also have valueBeanProperty and parse using
     * is done via parseFieldBean the object will
     * be created and set to that property.
     * @param classname the fully qualified classname.
     * @return this ConditionField object, so configuration can be
     * chained.
     */
    public ConditionField setClassname(boolean classname) {
        isClassname = classname;
        return this;
    }

    /**
     * The classname to use. {@see setClassname}
     * @return true of classname is true
     */
    public boolean isClassname() {
        return isClassname;
    }
}

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

import junit.framework.TestCase;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

/**
 * Test the ConditionsParser.
 * @author Kevin Dorff (Nov 21, 2007)
 */
public class TestConditionsParser extends TestCase {
    public void testBasic() throws IOException, ConditionsParsingException {
        final String input =
                "field1\tfield2\tfield3\n" +
                "#comment\n" +
                "field1a\tfield2a";
        final Reader source = new StringReader(input);

        final ConditionsParser parser = new ConditionsParser();
        parser.addField(new ConditionField(ConditionField.FieldType.VALUE, "one").setDefaultValue("1default"));
        parser.addField(new ConditionField(ConditionField.FieldType.VALUE, "two").setDefaultValue("2default"));
        parser.addField(new ConditionField(ConditionField.FieldType.VALUE, "three").setDefaultValue("3default"));

        parser.beginParse(source);

        assertTrue(parser.hasNext());
        assertEquals("field1", parser.parseFieldValueString("one"));
        assertEquals("field2", parser.parseFieldValueString("two"));
        assertEquals("field3", parser.parseFieldValueString("three"));
        assertEquals(1, parser.getLineNumber());
        assertTrue(parser.hasNext());
        assertEquals("field1a", parser.parseFieldValueString("one"));
        assertEquals("field2a", parser.parseFieldValueString("two"));
        assertEquals("3default", parser.parseFieldValueString("three"));
        assertEquals(3, parser.getLineNumber());
        assertFalse(parser.hasNext());

        IOUtils.closeQuietly(source);
    }

    public void testTooManyFields() throws IOException, ConditionsParsingException {
        final String input =
                "field1\tfield2\tfield3\tfield4\n";
        final Reader source = new StringReader(input);

        final ConditionsParser parser = new ConditionsParser();
        parser.addField(new ConditionField(ConditionField.FieldType.VALUE, "one").setDefaultValue("1default"));
        parser.addField(new ConditionField(ConditionField.FieldType.VALUE, "two").setDefaultValue("2default"));
        parser.addField(new ConditionField(ConditionField.FieldType.VALUE, "three").setDefaultValue("3default"));

        parser.beginParse(source);

        try {
            parser.hasNext();
            fail("Should have thown an exception, too many fields in the file");
        } catch (ConditionsParsingException e) {
            assertEquals(1, parser.getLineNumber());
            // This is correct
        } finally {
            IOUtils.closeQuietly(source);
        }
    }

    public void testInvalidFieldException() throws IOException {
        final String input =
                "field1\tfield2\tfield3\n" +
                "#comment\n" +
                "field1a\tfield2a";
        final Reader source = new StringReader(input);
        ConditionsParser parser = null;
        try {
            parser = new ConditionsParser();
            parser.addField(new ConditionField(ConditionField.FieldType.VALUE, "one").setDefaultValue("1default"));
            parser.addField(new ConditionField(ConditionField.FieldType.VALUE, "two").setDefaultValue("2default"));
            parser.addField(new ConditionField(ConditionField.FieldType.VALUE, "three").setDefaultValue("3default"));

            parser.beginParse(source);

            assertTrue(parser.hasNext());
            parser.parseFieldValueString("INVALIDFIELDNAME");
            fail("There should have been an exception when requesting an invalid field name");
        } catch (ConditionsParsingException e) {
            // This is correct.
            assertEquals(1, parser.getLineNumber());
        } finally {
            IOUtils.closeQuietly(source);
        }
    }

    public void testParseBeforeStartException() throws IOException {
        final String input =
                "field1\tfield2\tfield3\n" +
                "#comment\n" +
                "field1a\tfield2a";
        final Reader source = new StringReader(input);
        ConditionsParser parser = null;
        try {
            parser = new ConditionsParser();
            parser.addField(new ConditionField(ConditionField.FieldType.VALUE, "one").setDefaultValue("1default"));
            parser.addField(new ConditionField(ConditionField.FieldType.VALUE, "two").setDefaultValue("2default"));
            parser.addField(new ConditionField(ConditionField.FieldType.VALUE, "three").setDefaultValue("3default"));

            assertTrue(parser.hasNext());
            parser.parseFieldValueString("one");
            fail("There should have been an exception hasNext before beginParse");
        } catch (ConditionsParsingException e) {
            // This is correct.
            assertEquals(0, parser.getLineNumber());
        } finally {
            IOUtils.closeQuietly(source);
        }
    }

    public void testNoHasNextException() throws IOException {
        final String input =
                "field1\tfield2\tfield3\n" +
                "#comment\n" +
                "field1a\tfield2a";
        final Reader source = new StringReader(input);
        ConditionsParser parser = null;
        try {
            parser = new ConditionsParser();
            parser.addField(new ConditionField(ConditionField.FieldType.VALUE, "one").setDefaultValue("1default"));
            parser.addField(new ConditionField(ConditionField.FieldType.VALUE, "two").setDefaultValue("2default"));
            parser.addField(new ConditionField(ConditionField.FieldType.VALUE, "three").setDefaultValue("3default"));

            parser.beginParse(source);

            parser.parseFieldValueString("one");
            fail("There should have been an exception when no call to hasNext");
        } catch (ConditionsParsingException e) {
            // This is correct.
            assertEquals(0, parser.getLineNumber());
        } finally {
            IOUtils.closeQuietly(source);
        }
    }

    public void testReadAfterNotHasNextException() throws IOException {
        final String input =
                "field1\tfield2\tfield3\n" +
                "#comment\n" +
                "field1a\tfield2a";
        final Reader source = new StringReader(input);
        ConditionsParser parser = null;
        try {
            parser = new ConditionsParser();
            parser.addField(new ConditionField(ConditionField.FieldType.VALUE, "one").setDefaultValue("1default"));
            parser.addField(new ConditionField(ConditionField.FieldType.VALUE, "two").setDefaultValue("2default"));
            parser.addField(new ConditionField(ConditionField.FieldType.VALUE, "three").setDefaultValue("3default"));

            parser.beginParse(source);
            assertTrue(parser.hasNext());
            assertTrue(parser.hasNext());
            assertFalse(parser.hasNext());

            parser.parseFieldValueString("one");
            fail("There should have been an exception when call parseField* after hasNext returned false");
        } catch (ConditionsParsingException e) {
            // This is correct.
            assertEquals(3, parser.getLineNumber());
        } finally {
            IOUtils.closeQuietly(source);
        }
    }


    public void testMultiValsBlankLines() throws IOException, ConditionsParsingException {
        final String input =
                "\n\n\n" +
                "1,2,3:1.0,2.1,3.2:4|5|6:someval1\n" +
                "\n\n\n" +
                "5,6,7:1.2,2.3,3.4:8|7|6\n" +
                "\n\n\n" +
                ":1.3,2.4,3.5\n" +
                "\n\n\n";
        final Reader source = new StringReader(input);

        final ConditionsParser parser = new ConditionsParser();
        parser.setFieldSeparator(':');
        parser.addField(new ConditionField(ConditionField.FieldType.VALUE, "one").setList(true).setDefaultValue("-1"));
        parser.addField(new ConditionField(ConditionField.FieldType.VALUE, "two").setList(true).setDefaultValue("-2"));
        parser.addField(new ConditionField(ConditionField.FieldType.VALUE, "three").setList(true).setListSeparator('|').setDefaultValue("-3"));
        parser.addField(new ConditionField(ConditionField.FieldType.VALUE, "four").setList(false).setDefaultValue("nothing"));

        parser.beginParse(source);

        assertTrue(parser.hasNext());
        checkIntArray(new int[] {1, 2, 3}, parser.parseFieldValueIntArray("one"));
        checkDoubleArray(new double[] {1.0, 2.1, 3.2}, parser.parseFieldValueDoubleArray("two"));
        checkIntArray(new int[] {4, 5, 6}, parser.parseFieldValueIntArray("three"));
        checkStringArray(new String[] {"someval1"}, parser.parseFieldValueStringArray("four"));
        assertEquals(4, parser.getLineNumber());
        assertTrue(parser.hasNext());
        checkIntArray(new int[] {5, 6, 7}, parser.parseFieldValueIntArray("one"));
        checkDoubleArray(new double[] {1.2, 2.3, 3.4}, parser.parseFieldValueDoubleArray("two"));
        checkIntArray(new int[] {8, 7, 6}, parser.parseFieldValueIntArray("three"));
        checkStringArray(new String[] {"nothing"}, parser.parseFieldValueStringArray("four"));
        assertEquals(8, parser.getLineNumber());
        assertTrue(parser.hasNext());
        checkIntArray(new int[] {-1}, parser.parseFieldValueIntArray("one"));
        checkDoubleArray(new double[] {1.3, 2.4, 3.5}, parser.parseFieldValueDoubleArray("two"));
        checkIntArray(new int[] {-3}, parser.parseFieldValueIntArray("three"));
        checkStringArray(new String[] {"nothing"}, parser.parseFieldValueStringArray("four"));
        assertEquals(12, parser.getLineNumber());
        assertFalse(parser.hasNext());

        IOUtils.closeQuietly(source);
    }

    public void testMultiValsAndBoolean() throws IOException, ConditionsParsingException {
        final String input =
                "1:2:3\ttrue,xxx,TRUE,false,True,yes";
        final Reader source = new StringReader(input);

        final ConditionsParser parser = new ConditionsParser();
        parser.addField(new ConditionField(ConditionField.FieldType.VALUE, "one").setListSeparator(':').setList(true));
        parser.addField(new ConditionField(ConditionField.FieldType.VALUE, "two").setList(true));

        parser.beginParse(source);

        assertTrue(parser.hasNext());
        checkIntArray(new int[] {1, 2, 3}, parser.parseFieldValueIntArray("one"));
        checkBooleanArray(new boolean[] {true,false,true,false,true,false}, parser.parseFieldValueBooleanArray("two"));
        assertEquals(1, parser.getLineNumber());
        assertFalse(parser.hasNext());

        IOUtils.closeQuietly(source);
    }

    public void testDictionary() throws IOException, ConditionsParsingException {
        final String input =
                "a=123,b=456,c=def\t123\t5,6,7\n" +
                "#comment\n" +
                "c=567,d=843,e=3bf";
        final Reader source = new StringReader(input);

        final ConditionsParser parser = new ConditionsParser();
        parser.addField(new ConditionField(ConditionField.FieldType.MAP, "one"));
        parser.addField(new ConditionField(ConditionField.FieldType.VALUE, "two"));
        parser.addField(new ConditionField(ConditionField.FieldType.VALUE, "three").setList(true));

        parser.beginParse(source);

        final Map<String, String> expectedDict = new HashMap<String, String>();

        assertTrue(parser.hasNext());
        expectedDict.clear();
        expectedDict.put("a", "123");
        expectedDict.put("b", "456");
        expectedDict.put("c", "def");
        checkMap(expectedDict, parser.parseFieldMap("one"));
        assertEquals(1, parser.getLineNumber());
        assertEquals("123", parser.parseFieldValueString("two"));
        checkIntArray(new int[] {5, 6, 7}, parser.parseFieldValueIntArray("three"));
        assertTrue(parser.hasNext());
        expectedDict.clear();
        expectedDict.put("c", "567");
        expectedDict.put("d", "843");
        expectedDict.put("e", "3bf");
        checkMap(expectedDict, parser.parseFieldMap("one"));
        assertEquals("", parser.parseFieldValueString("two"));
        checkIntArray(new int[0], parser.parseFieldValueIntArray("three"));
        assertEquals(3, parser.getLineNumber());
        assertFalse(parser.hasNext());

        IOUtils.closeQuietly(source);
    }


    public void testBean() throws IOException, ConditionsParsingException {
        final String input =
                "oneInt=123,twoDouble=456,threeString=def\t123\t5,6,7\n" +
                "#comment\n" +
                "oneInt=567,twoDouble=8.367,threeString=3bf,fourNada=abc";
        final Reader source = new StringReader(input);

        final ConditionsParser parser = new ConditionsParser();
        parser.addField(new ConditionField(ConditionField.FieldType.MAP, "one"));
        parser.addField(new ConditionField(ConditionField.FieldType.VALUE, "two"));
        parser.addField(new ConditionField(ConditionField.FieldType.VALUE, "three").setList(true));

        parser.beginParse(source);
        SampleBean testBean;

        final Map<String, String> expValues = new HashMap<String, String>();
        final Map<String, String> foundValues = new HashMap<String, String>();

        assertTrue(parser.hasNext());
        testBean = new SampleBean();

        expValues.clear();
        expValues.put("oneInt", "123");
        expValues.put("twoDouble", "456");
        expValues.put("threeString", "def");
        foundValues.clear();
        Object retval = parser.parseFieldBean("one", testBean, foundValues);
        assertTrue(testBean == retval);
        checkMap(expValues, foundValues);
        assertEquals("123", parser.parseFieldValueString("two"));
        checkIntArray(new int[] {5, 6, 7}, parser.parseFieldValueIntArray("three"));
        assertEquals(123, testBean.getOneInt());
        assertEquals(456d, testBean.getTwoDouble());
        assertEquals("def", testBean.getThreeString());
        assertEquals(1, parser.getLineNumber());
        assertTrue(parser.hasNext());

        testBean = new SampleBean();
        expValues.clear();
        expValues.put("oneInt", "567");
        expValues.put("twoDouble", "8.367");
        expValues.put("threeString", "3bf");
        expValues.put("fourNada", "abc");
        foundValues.clear();
        retval = parser.parseFieldBean("one", testBean, foundValues);
        assertTrue(testBean == retval);
        checkMap(expValues, foundValues);
        assertEquals("", parser.parseFieldValueString("two"));
        checkIntArray(new int[0], parser.parseFieldValueIntArray("three"));
        assertEquals(567, testBean.getOneInt());
        assertEquals(8.367d, testBean.getTwoDouble());
        assertEquals("3bf", testBean.getThreeString());
        assertEquals(3, parser.getLineNumber());
        assertFalse(parser.hasNext());

        IOUtils.closeQuietly(source);
    }

    public void testBeanWithCreation() throws IOException, ConditionsParsingException {
        final String input =
                "_CLASSNAME_=edu.cornell.med.icb.io.SampleBean," +
                        "oneInt=123,twoDouble=456,threeString=def\t123\t5,6,7\n";
        final Reader source = new StringReader(input);

        final ConditionsParser parser = new ConditionsParser();
        parser.addField(new ConditionField(ConditionField.FieldType.MAP, "one"));
        parser.addField(new ConditionField(ConditionField.FieldType.VALUE, "two"));
        parser.addField(new ConditionField(ConditionField.FieldType.VALUE, "three").setList(true));

        parser.beginParse(source);

        final Map<String, String> expValues = new HashMap<String, String>();
        final Map<String, String> foundValues = new HashMap<String, String>();

        assertTrue(parser.hasNext());

        expValues.clear();
        expValues.put("oneInt", "123");
        expValues.put("twoDouble", "456");
        expValues.put("threeString", "def");
        foundValues.clear();
        final Object testBeanObj = parser.parseFieldBean("one", null, foundValues);
        assertTrue(testBeanObj instanceof SampleBean);
        final SampleBean testBean = (SampleBean)testBeanObj;
        checkMap(expValues, foundValues);
        assertEquals("123", parser.parseFieldValueString("two"));
        checkIntArray(new int[] {5, 6, 7}, parser.parseFieldValueIntArray("three"));
        assertEquals(123, testBean.getOneInt());
        assertEquals(456d, testBean.getTwoDouble());
        assertEquals("def", testBean.getThreeString());
        assertEquals(1, parser.getLineNumber());
        assertFalse(parser.hasNext());
    }

    /**
     * Test setting VALUE (via bean) automatically when the
     * ConditionField.valueBeanProperty property is set
     * and ConditionsParser.parseFieldBean is called. This
     * test checks that single values can be set (ConditionField.list
     * set to false).
     * @throws IOException error reading input
     * @throws ConditionsParsingException error parsing
     */
    public void testValueBeanSingleValues() throws IOException, ConditionsParsingException {
        final String input =
                "50\t3.5\tfield3\n" +
                "#comment\n" +
                "40";
        final Reader source = new StringReader(input);
        SampleBean testBean;

        final ConditionsParser parser = new ConditionsParser();
        parser.addField(new ConditionField(ConditionField.FieldType.VALUE, "one").setDefaultValue("1").setValueBeanProperty("oneInt"));
        parser.addField(new ConditionField(ConditionField.FieldType.VALUE, "two").setDefaultValue("2.2").setValueBeanProperty("twoDouble"));
        parser.addField(new ConditionField(ConditionField.FieldType.VALUE, "three").setDefaultValue("threedefault").setValueBeanProperty("threeString"));

        parser.beginParse(source);

        assertTrue(parser.hasNext());
        testBean = new SampleBean();
        parser.parseFieldBean("one", testBean, null);
        parser.parseFieldBean("two", testBean, null);
        parser.parseFieldBean("three", testBean, null);
        assertEquals(50, testBean.getOneInt());
        assertEquals(3.5, testBean.getTwoDouble());
        assertEquals("field3", testBean.getThreeString());

        assertTrue(parser.hasNext());
        testBean = new SampleBean();
        parser.parseFieldBean("one", testBean, null);
        parser.parseFieldBean("two", testBean, null);
        parser.parseFieldBean("three", testBean, null);
        assertEquals(40, testBean.getOneInt());
        assertEquals(2.2, testBean.getTwoDouble());
        assertEquals("threedefault", testBean.getThreeString());

        assertFalse(parser.hasNext());
    }

    /**
     * Test setting VALUE (via bean) automatically when the
     * ConditionField.valueBeanProperty property is set
     * and ConditionsParser.parseFieldBean is called. This
     * test checks that arrays can be set (ConditionField.list
     * set to true).
     * @throws IOException error reading input
     * @throws ConditionsParsingException error parsing
     */
    public void testValueBeanLists() throws IOException, ConditionsParsingException {
        final String input =
                "50,40,30\t3.5, 2.5, 1.5\tfield3, field3b, field 3c\n" +
                "#comment\n" +
                "40";
        final Reader source = new StringReader(input);
        SampleBean testBean;

        final ConditionsParser parser = new ConditionsParser();
        parser.addField(new ConditionField(ConditionField.FieldType.VALUE, "one").setList(true).setDefaultValue("1").setValueBeanProperty("oneBeeIntArray"));
        parser.addField(new ConditionField(ConditionField.FieldType.VALUE, "two").setList(true).setDefaultValue("2.2").setValueBeanProperty("twoBeeDoubleArray"));
        parser.addField(new ConditionField(ConditionField.FieldType.VALUE, "three").setList(true).setDefaultValue("threedefault").setValueBeanProperty("threeBeeStringArray"));

        parser.beginParse(source);

        assertTrue(parser.hasNext());
        testBean = new SampleBean();
        parser.parseFieldBean("one", testBean, null);
        parser.parseFieldBean("two", testBean, null);
        parser.parseFieldBean("three", testBean, null);
        checkIntArray(new int[] {50, 40, 30}, testBean.getOneBeeIntArray());
        checkDoubleArray(new double[] {3.5, 2.5, 1.5}, testBean.getTwoBeeDoubleArray());
        // It is worth noting that the extra spaces in the INPUT are not
        // Stripped out INTENTIONALLY. Since there was a space before the second
        // and third values, it will be propogated to the output.
        checkStringArray(new String[] {"field3", " field3b", " field 3c"}, testBean.getThreeBeeStringArray());

        assertTrue(parser.hasNext());
        testBean = new SampleBean();
        parser.parseFieldBean("one", testBean, null);
        parser.parseFieldBean("two", testBean, null);
        parser.parseFieldBean("three", testBean, null);
        checkIntArray(new int[] {40}, testBean.getOneBeeIntArray());
        checkDoubleArray(new double[] {2.2}, testBean.getTwoBeeDoubleArray());
        checkStringArray(new String[] {"threedefault"}, testBean.getThreeBeeStringArray());

        assertFalse(parser.hasNext());
    }

    public void testSubBean() throws IOException, ConditionsParsingException {
        final String input =
                "50\t_CLASSNAME_=edu.cornell.med.icb.io.SampleBean\t60.4\n" +
                "#comment\n" +
                "40\t_CLASSNAME_=edu.cornell.med.icb.io.SampleBean,oneInt=80\t70.3\n";

        final Reader source = new StringReader(input);
        SampleBean testBean;

        final ConditionsParser parser = new ConditionsParser();
        parser.addField(new ConditionField(ConditionField.FieldType.VALUE, "one").setValueBeanProperty("oneInt"));
        parser.addField(new ConditionField(ConditionField.FieldType.MAP, "two").setValueBeanProperty("subBean"));
        parser.addField(new ConditionField(ConditionField.FieldType.VALUE, "three").setValueBeanProperty("subBean.twoDouble"));

        parser.beginParse(source);

        assertTrue(parser.hasNext());
        testBean = new SampleBean();
        parser.parseFieldBean("one", testBean, null);
        parser.parseFieldBean("two", testBean, null);
        parser.parseFieldBean("three", testBean, null);
        assertEquals(50, testBean.getOneInt());
        assertNotNull(testBean.getSubBean());
        assertEquals(60.4d, testBean.getSubBean().getTwoDouble());

        assertTrue(parser.hasNext());
        testBean = new SampleBean();
        parser.parseFieldBean("one", testBean, null);
        parser.parseFieldBean("two", testBean, null);
        parser.parseFieldBean("three", testBean, null);
        assertEquals(40, testBean.getOneInt());
        assertNotNull(testBean.getSubBean());
        assertEquals(80, testBean.getSubBean().getOneInt());
        assertEquals(70.3d, testBean.getSubBean().getTwoDouble());

        assertFalse(parser.hasNext());
    }

    public void testSubBeanNotMap() throws IOException, ConditionsParsingException {
        final String input =
                "50\tedu.cornell.med.icb.io.SampleBean\t60.4\t10\n" +
                "#comment\n" +
                "40\tedu.cornell.med.icb.io.SampleBean\t70.3\t20\n";

        final Reader source = new StringReader(input);
        SampleBean testBean;

        final ConditionsParser parser = new ConditionsParser();
        parser.addField(new ConditionField(ConditionField.FieldType.VALUE, "one").setValueBeanProperty("oneInt"));
        parser.addField(new ConditionField(ConditionField.FieldType.VALUE, "two").setClassname(true).setValueBeanProperty("subBean"));
        parser.addField(new ConditionField(ConditionField.FieldType.VALUE, "three").setValueBeanProperty("subBean.twoDouble"));
        parser.addField(new ConditionField(ConditionField.FieldType.VALUE, "four").setValueBeanProperty("subBean.oneInt"));

        parser.beginParse(source);

        assertTrue(parser.hasNext());
        testBean = new SampleBean();
        parser.parseAllFieldsBean(testBean, null);
        assertEquals(50, testBean.getOneInt());
        assertNotNull(testBean.getSubBean());
        assertEquals(10, testBean.getSubBean().getOneInt());
        assertEquals(60.4d, testBean.getSubBean().getTwoDouble());

        assertTrue(parser.hasNext());
        testBean = new SampleBean();
        parser.parseAllFieldsBean(testBean, null);
        assertEquals(40, testBean.getOneInt());
        assertNotNull(testBean.getSubBean());
        assertEquals(20, testBean.getSubBean().getOneInt());
        assertEquals(70.3d, testBean.getSubBean().getTwoDouble());

        assertFalse(parser.hasNext());
    }

    public void testSubBeanAllAtOnceObject() throws IOException, ConditionsParsingException {
        final String input =
                "50\t_CLASSNAME_=edu.cornell.med.icb.io.SampleBean\t60.4\n" +
                "#comment\n" +
                "40\t_CLASSNAME_=edu.cornell.med.icb.io.SampleBean,oneInt=80\t70.3\n";

        final Reader source = new StringReader(input);
        final SampleBean templateBean = new SampleBean();
        SampleBean testBean;
        templateBean.setThreeString("came from template");

        final ConditionsParser parser = new ConditionsParser();
        parser.addField(new ConditionField(ConditionField.FieldType.VALUE, "one").setValueBeanProperty("oneInt"));
        parser.addField(new ConditionField(ConditionField.FieldType.MAP, "two").setValueBeanProperty("subBean"));
        parser.addField(new ConditionField(ConditionField.FieldType.VALUE, "three").setValueBeanProperty("subBean.twoDouble"));

        final List<SampleBean> results = parser.beginParseAllToBeans(source, templateBean, null);
        assertEquals(2, results.size());

        testBean = results.get(0);
        assertEquals(50, testBean.getOneInt());
        assertNotNull(testBean.getSubBean());
        assertEquals(60.4d, testBean.getSubBean().getTwoDouble());
        assertEquals("came from template", testBean.getThreeString());

        testBean = results.get(1);
        assertEquals(40, testBean.getOneInt());
        assertNotNull(testBean.getSubBean());
        assertEquals(80, testBean.getSubBean().getOneInt());
        assertEquals(70.3d, testBean.getSubBean().getTwoDouble());
        assertEquals("came from template", testBean.getThreeString());
    }

    public void testSubBeanAllAtOnceClass() throws IOException, ConditionsParsingException {
        final String input =
                "50\t_CLASSNAME_=edu.cornell.med.icb.io.SampleBean\t60.4\n" +
                "#comment\n" +
                "40\t_CLASSNAME_=edu.cornell.med.icb.io.SampleBean,oneInt=80\t70.3\n";

        final Reader source = new StringReader(input);
        SampleBean testBean;

        final ConditionsParser parser = new ConditionsParser();
        parser.addField(new ConditionField(ConditionField.FieldType.VALUE, "one").setValueBeanProperty("oneInt"));
        parser.addField(new ConditionField(ConditionField.FieldType.MAP, "two").setValueBeanProperty("subBean"));
        parser.addField(new ConditionField(ConditionField.FieldType.VALUE, "three").setValueBeanProperty("subBean.twoDouble"));

        final List<SampleBean> results = parser.beginParseAllToBeans(source, SampleBean.class, null);
        assertEquals(2, results.size());

        testBean = results.get(0);
        assertEquals(50, testBean.getOneInt());
        assertNotNull(testBean.getSubBean());
        assertEquals(60.4d, testBean.getSubBean().getTwoDouble());
        assertEquals(null, testBean.getThreeString());

        testBean = results.get(1);
        assertEquals(40, testBean.getOneInt());
        assertNotNull(testBean.getSubBean());
        assertEquals(80, testBean.getSubBean().getOneInt());
        assertEquals(70.3d, testBean.getSubBean().getTwoDouble());
        assertEquals(null, testBean.getThreeString());
    }

    /*------------------ Validation Methods ------------------*/

    private void checkIntArray(final int[] expected, final int[] found) {
        if (expected == null && found == null) {
            return;
        }
        if (expected == null) {
            // Expected is null but source is not
            fail("Source should have been null, but it was " + ArrayUtils.toString(found));
            return;
        }
        if (found == null) {
            // Found is null but expected is not
            fail("Expected to find " + ArrayUtils.toString(expected) + " but found null");
            return;
        }

        if (expected.length != found.length) {
            fail("Incorrect length. Expected was " + ArrayUtils.toString(expected)
                    + " and found was " + ArrayUtils.toString(found));
            return;
        }
        for (int i = 0; i < expected.length; i++) {
            if (expected[i] != found[i]) {
                fail("Arrays differ. Expected was " + ArrayUtils.toString(expected)
                        + " and found was " + ArrayUtils.toString(found));
                return;
            }
        }
    }

    private void checkBooleanArray(final boolean[] expected, final boolean[] found) {
        if (expected == null && found == null) {
            return;
        }
        if (expected == null) {
            // Expected is null but source is not
            fail("Source should have been null, but it was " + ArrayUtils.toString(found));
            return;
        }
        if (found == null) {
            // Found is null but expected is not
            fail("Expected to find " + ArrayUtils.toString(expected) + " but found null");
            return;
        }

        if (expected.length != found.length) {
            fail("Incorrect length. Expected was " + ArrayUtils.toString(expected)
                    + " and found was " + ArrayUtils.toString(found));
            return;
        }
        for (int i = 0; i < expected.length; i++) {
            if (expected[i] != found[i]) {
                fail("Arrays differ. Expected was " + ArrayUtils.toString(expected)
                        + " and found was " + ArrayUtils.toString(found));
                return;
            }
        }
    }

    private void checkDoubleArray(final double[] expected, final double[] found) {
        if (expected == null && found == null) {
            return;
        }
        if (expected == null) {
            // Expected is null but source is not
            fail("Source should have been null, but it was " + ArrayUtils.toString(found));
            return;
        }
        if (found == null) {
            // Found is null but expected is not
            fail("Expected to find " + ArrayUtils.toString(expected) + " but found null");
            return;
        }

        if (expected.length != found.length) {
            fail("Incorrect length. Expected was " + ArrayUtils.toString(expected)
                    + " and found was " + ArrayUtils.toString(found));
            return;
        }
        for (int i = 0; i < expected.length; i++) {
            if (expected[i] != found[i]) {
                fail("Arrays differ. Expected was " + ArrayUtils.toString(expected)
                        + " and found was " + ArrayUtils.toString(found));
                return;
            }
        }
    }

    private void checkStringArray(final String[] expected, final String[] found) {
        if (expected == null && found == null) {
            return;
        }
        if (expected == null) {
            // Expected is null but source is not
            fail("Source should have been null, but it was " + ArrayUtils.toString(found));
            return;
        }
        if (found == null) {
            // Found is null but expected is not
            fail("Expected to find " + ArrayUtils.toString(expected) + " but found null");
            return;
        }

        if (expected.length != found.length) {
            fail("Incorrect length. Expected was " + ArrayUtils.toString(expected)
                    + " and found was " + ArrayUtils.toString(found));
            return;
        }
        for (int i = 0; i < expected.length; i++) {
            if (!expected[i].equals(found[i])) {
                fail("Arrays differ. Expected was " + ArrayUtils.toString(expected)
                        + " and found was " + ArrayUtils.toString(found));
                return;
            }
        }
    }

    private void checkMap(final Map<String, String> expected, final Map<String, String> found) {
        if (expected == null && found == null) {
            return;
        }
        if (expected == null) {
            // Expected is null but source is not
            fail("Source should have been null, but it was " + ArrayUtils.toString(found));
            return;
        }
        if (found == null) {
            // Found is null but expected is not
            fail("Expected to find " + ArrayUtils.toString(expected) + " but found null");
            return;
        }

        if (expected.size() != found.size()) {
            fail("Incorrect length. Expected was " + ArrayUtils.toString(expected)
                    + " and found was " + ArrayUtils.toString(found));
            return;
        }
        for (final String key : found.keySet() ) {
            if (!expected.containsKey(key)) {
                fail("Missing Key. Expected keys were " + ArrayUtils.toString(expected.keySet())
                        + " and found keys were " + ArrayUtils.toString(found.keySet()));
            }
            final String foundValue = found.get(key);
            final String expectedValue = expected.get(key);
            if (!foundValue.equals(expectedValue)) {
                fail("Missing Key. Value at key " + key + " was incorrect. "
                        + " Expected " + expectedValue + " but found " +
                        foundValue);
            }
        }
    }


}

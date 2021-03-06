/*
 * The MIT License
 *
 *   Copyright (c) 2014, benas (md.benhassine@gmail.com)
 *
 *   Permission is hereby granted, free of charge, to any person obtaining a copy
 *   of this software and associated documentation files (the "Software"), to deal
 *   in the Software without restriction, including without limitation the rights
 *   to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *   copies of the Software, and to permit persons to whom the Software is
 *   furnished to do so, subject to the following conditions:
 *
 *   The above copyright notice and this permission notice shall be included in
 *   all copies or substantial portions of the Software.
 *
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *   FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *   AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *   LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *   THE SOFTWARE.
 */

package io.github.benas.easybatch.flatfile.test;

import io.github.benas.easybatch.flatfile.FlatFileField;
import io.github.benas.easybatch.flatfile.FlatFileRecordObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * Unit test class for {@link io.github.benas.easybatch.flatfile.FlatFileRecordObjectMapper}
 * @author benas (md.benhassine@gmail.com)
 */
public class FlatFileRecordObjectMapperTest {

    private FlatFileRecordObjectMapper mapper;


    @Before
    public void setUp() throws Exception {
        mapper = new FlatFileRecordObjectMapper<Person>(Person.class,
                new String[]{"firstName","lastName", "age", "birthDate", "isMarried"});
    }

    @Test
    public void testPersonTypeMapping() throws Exception {

        // prepare mock record fields
        FlatFileField firstNameField = new FlatFileField(0, "foo");
        FlatFileField lastNameField = new FlatFileField(1, "bar");
        FlatFileField ageField = new FlatFileField(2, "30");
        FlatFileField birthDateField = new FlatFileField(3, "1990-12-12");
        FlatFileField isMarriedField = new FlatFileField(4, "true");
        List<FlatFileField> fields = new ArrayList<FlatFileField>();
        fields.add(firstNameField);
        fields.add(lastNameField);
        fields.add(ageField);
        fields.add(birthDateField);
        fields.add(isMarriedField);

        // map record to Person bean
        Person person = (Person) mapper.mapObject(fields);

        // person bean must be not null and correctly populated
        assertNotNull(person);
        assertEquals("foo", person.getFirstName());
        assertEquals("bar", person.getLastName());
        assertEquals(30, person.getAge());
        assertEquals("1990-12-12", new SimpleDateFormat("yyyy-MM-dd").format(person.getBirthDate()));
        assertEquals(true, person.isMarried());
    }

    @After
    public void tearDown() throws Exception {
        mapper = null;
        System.gc();
    }

}




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

package io.github.benas.easybatch.flatfile.filter;

import io.github.benas.easybatch.core.api.Record;
import io.github.benas.easybatch.core.api.RecordFilter;

/**
 * A {@link io.github.benas.easybatch.core.api.RecordFilter} that filters flat file records ending with one of the given suffixes.
 * The parameter negate can be set to true to inverse this behavior :
 * this filter will filter records that do not start with one of the given suffixes.
 *
 * @author benas (md.benhassine@gmail.com)
 */
public class EndsWithFlatFileRecordFilter implements RecordFilter {

    /**
     * Suffixes that causes the record to be filtered.
     */
    private String[] suffixes;

    /**
     * Parameter to filter a record if it does not end with one of the given suffixes.
     */
    private boolean negate;

    /**
     * @param suffixes suffixes that cause the record to be filtered.
     */
    public EndsWithFlatFileRecordFilter(final String... suffixes) {
        this(false, suffixes);
    }

    /**
     * @param negate true if the filter should filter records that do not end with any of the given suffixes.
     * @param suffixes suffixes that cause the record to be filtered.
     */
    public EndsWithFlatFileRecordFilter(final boolean negate, final String... suffixes) {
        this.negate = negate;
        this.suffixes = suffixes;
    }

    /**
     * {@inheritDoc}
     */
    public boolean filterRecord(final Record record) {
        String recordRawContent = (String) record.getRawContent();
        for (String prefix : suffixes) {
            if (recordRawContent.endsWith(prefix)) {
                return !negate;
            }
        }
        return false;
    }

}

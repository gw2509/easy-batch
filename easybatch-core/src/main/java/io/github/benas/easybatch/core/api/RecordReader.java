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

package io.github.benas.easybatch.core.api;

/**
 * An interface for record reader.
 * This will be used by easy batch to <strong>sequentially</strong> read records from a data source.
 *
 * @author benas (md.benhassine@gmail.com)
 */
public interface RecordReader {

    /**
     * Open the reader.
     * @throws Exception thrown if an exception occurs during reader opening
     */
    void open() throws Exception;

    /**
     * Check if the reader has a next record.
     * @return true if the reader has a next record, false else.
     */
    boolean hasNextRecord();

    /**
     * Read next record from the data source.
     * @return the next record from the data source.
     */
    Record readNextRecord();

    /**
     * Get the total record number in the data source. This is useful to calculate execution progress.
     * @return the total record number in the data source.
     */
    long getTotalRecords();

    /**
     * Close the reader.
     */
    void close();

}

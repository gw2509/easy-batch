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

package io.github.benas.easybatch.core.impl;

import io.github.benas.easybatch.core.api.*;
import io.github.benas.easybatch.core.jmx.EasyBatchMonitor;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Core Easy Batch engine implementation.
 *
 * @author benas (md.benhassine@gmail.com)
 */
public final class EasyBatchEngine implements Callable<EasyBatchReport> {

    private Logger logger = Logger.getLogger(EasyBatchEngine.class.getName());

    private RecordReader recordReader;

    private RecordFilter recordFilter;

    private RecordMapper recordMapper;

    private RecordValidator recordValidator;

    private RecordProcessor recordProcessor;

    private EasyBatchMonitor easyBatchMonitor;

    private EasyBatchReport easyBatchReport;


    EasyBatchEngine(final RecordReader recordReader, final RecordFilter recordFilter, final RecordMapper recordMapper,
                    final RecordValidator recordValidator, final RecordProcessor recordProcessor) {
        this.recordReader = recordReader;
        this.recordFilter = recordFilter;
        this.recordMapper = recordMapper;
        this.recordValidator = recordValidator;
        this.recordProcessor = recordProcessor;
        easyBatchReport = new EasyBatchReport();
        easyBatchMonitor = new EasyBatchMonitor(easyBatchReport);
        configureJmxMBean();
    }

    @Override
    public EasyBatchReport call() throws Exception {

        logger.info("Initializing easy batch engine");
        recordReader.open();

        long totalRecords = recordReader.getTotalRecords();
        logger.info("Total records = " + totalRecords);
        logger.info("easy batch engine is running...");

        easyBatchReport.setTotalRecords(totalRecords);
        easyBatchReport.setStartTime(System.currentTimeMillis());

        long currentRecordNumber;

        while (recordReader.hasNextRecord()) {

            long currentRecordProcessingStartTime = System.currentTimeMillis();

            //read next record
            Record currentRecord = recordReader.readNextRecord();
            currentRecordNumber = currentRecord.getNumber();
            easyBatchReport.setCurrentRecordNumber(currentRecordNumber);

            //filter record if any
            if (recordFilter.filterRecord(currentRecord)) {
                logger.log(Level.INFO, "Record #" + currentRecordNumber + " [" + currentRecord + "] has been filtered");
                easyBatchReport.addFilteredRecord(currentRecordNumber);
                easyBatchReport.addProcessingTime(currentRecordNumber, System.currentTimeMillis() - currentRecordProcessingStartTime);
                continue;
            }

            //map record
            Object typedRecord;
            try {
                typedRecord = recordMapper.mapRecord(currentRecord);
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error while mapping record #" + currentRecordNumber + " [" + currentRecord + "] : " + e.getMessage());
                easyBatchReport.addIgnoredRecord(currentRecordNumber);
                easyBatchReport.addProcessingTime(currentRecordNumber, System.currentTimeMillis() - currentRecordProcessingStartTime);
                continue;
            }

            //validate record
            Set<ValidationError> validationsErrors = recordValidator.validateRecord(typedRecord);

            if (!validationsErrors.isEmpty()) {
                StringBuilder stringBuilder = new StringBuilder();
                for (ValidationError validationError : validationsErrors) {
                    stringBuilder.append(validationError.getMessage()).append(" | ");
                }
                logger.log(Level.SEVERE, "Error while validating record #" + currentRecordNumber + " [" + currentRecord + "] : " + stringBuilder.toString());
                easyBatchReport.addRejectedRecord(currentRecordNumber);
                easyBatchReport.addProcessingTime(currentRecordNumber, System.currentTimeMillis() - currentRecordProcessingStartTime);
                continue;
            }

            //process record
            try {
                recordProcessor.processRecord(typedRecord);
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error while processing record #" + currentRecordNumber + "[" + currentRecord + "]", e);
                easyBatchReport.addErrorRecord(currentRecordNumber);
                easyBatchReport.addProcessingTime(currentRecordNumber, System.currentTimeMillis() - currentRecordProcessingStartTime);
            }

            //log processing time for the current record
            easyBatchReport.addProcessingTime(currentRecordNumber, System.currentTimeMillis() - currentRecordProcessingStartTime);

        }

        easyBatchReport.setEndTime(System.currentTimeMillis());
        easyBatchReport.setEasyBatchResult(recordProcessor.getEasyBatchResult());

        //close the record reader
        recordReader.close();

        logger.info("Shutting down easy batch engine");
        return easyBatchReport;

    }

    /*
    * Configure JMX MBean
    */
    private void configureJmxMBean() {

        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        ObjectName name;
        try {
            name = new ObjectName("io.github.benas.easybatch.core.jmx:type=EasyBatchMonitorMBean");
            if (!mbs.isRegistered(name)) {
                easyBatchMonitor = new EasyBatchMonitor(easyBatchReport);
                mbs.registerMBean(easyBatchMonitor, name);
                logger.info("Easy batch JMX MBean registered successfully as: " + name.getCanonicalName());
            }
        } catch (Exception e) {
            String error = "Unable to register Easy batch JMX MBean. Root exception is :" + e.getMessage();
            logger.warning(error);
        }
    }

    void setRecordFilter(final RecordFilter recordFilter) {
        this.recordFilter = recordFilter;
    }

    void setRecordReader(final RecordReader recordReader) {
        this.recordReader = recordReader;
    }

    void setRecordMapper(final RecordMapper recordMapper) {
        this.recordMapper = recordMapper;
    }

    void setRecordValidator(final RecordValidator recordValidator) {
        this.recordValidator = recordValidator;
    }

    void setRecordProcessor(final RecordProcessor recordProcessor) {
        this.recordProcessor = recordProcessor;
    }

}

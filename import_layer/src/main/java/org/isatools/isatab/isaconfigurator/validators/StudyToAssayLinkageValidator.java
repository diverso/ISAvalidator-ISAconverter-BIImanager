package org.isatools.isatab.isaconfigurator.validators;

import org.apache.log4j.Logger;
import org.isatools.isatab.gui_invokers.GUIInvokerResult;
import org.isatools.isatab.mapping.AssayGroup;
import org.isatools.tablib.schema.Field;
import org.isatools.tablib.schema.Record;
import org.isatools.tablib.schema.SectionInstance;

import java.util.HashSet;
import java.util.Set;

/**
 * This code checks if all Assays have their sample name defined in the Study Sample file.
 * @author: Eamonn Maguire (eamonnmag@gmail.com)
 */
public class StudyToAssayLinkageValidator {

    protected static final Logger log = Logger.getLogger(StudyToAssayLinkageValidator.class);

    public GUIInvokerResult validate(SectionInstance studySampleTable, AssayGroup assayGroup) {

        SectionInstance assayTable = assayGroup.getAssaySectionInstance();

        Field studySampleNameField = studySampleTable.getFieldByHeader("Sample Name");

        Field assaySampleNameField = assayTable.getFieldByHeader("Sample Name");

        Set<String> assaySamples = new HashSet<String>();
        Set<String> studySamples = new HashSet<String>();

        for(Record assayRecord : assayTable.getRecords()) {
            assaySamples.add(assayRecord.get(assaySampleNameField.getIndex()).toString());
        }

        for(Record studySampleRecord : studySampleTable.getRecords()) {
            studySamples.add(studySampleRecord.get(studySampleNameField.getIndex()).toString());
        }

        boolean errors = false;
        for(String assaySample : assaySamples) {
            if(!studySamples.contains(assaySample)) {
                errors = true;
                log.error(String.format("%s is a Sample Name in %s, but it is not defined in the Study Sample File.", assaySample, assayGroup.getFilePath()));
            }
        }

        return errors ? GUIInvokerResult.ERROR : GUIInvokerResult.SUCCESS;
    }
}

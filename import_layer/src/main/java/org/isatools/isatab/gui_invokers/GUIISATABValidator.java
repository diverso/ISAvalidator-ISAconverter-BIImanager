/**

 The ISAconverter, ISAvalidator & BII Management Tool are components of the ISA software suite (http://www.isa-tools.org)

 Exhibit A
 The ISAconverter, ISAvalidator & BII Management Tool are licensed under the Mozilla Public License (MPL) version
 1.1/GPL version 2.0/LGPL version 2.1

 "The contents of this file are subject to the Mozilla Public License
 Version 1.1 (the "License"). You may not use this file except in compliance with the License.
 You may obtain copies of the Licenses at http://www.mozilla.org/MPL/MPL-1.1.html.

 Software distributed under the License is distributed on an "AS IS"
 basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 License for the specific language governing rights and limitations
 under the License.

 The Original Code is the ISAconverter, ISAvalidator & BII Management Tool.

 The Initial Developer of the Original Code is the ISA Team (Eamonn Maguire, eamonnmag@gmail.com;
 Philippe Rocca-Serra, proccaserra@gmail.com; Susanna-Assunta Sansone, sa.sanson@gmail.com;
 http://www.isa-tools.org). All portions of the code written by the ISA Team are Copyright (c)
 2007-2011 ISA Team. All Rights Reserved.

 Contributor(s):
 Rocca-Serra P, Brandizi M, Maguire E, Sklyar N, Taylor C, Begley K, Field D,
 Harris S, Hide W, Hofmann O, Neumann S, Sterk P, Tong W, Sansone SA. ISA software suite:
 supporting standards-compliant experimental annotation and enabling curation at the community level.
 Bioinformatics 2010;26(18):2354-6.

 Alternatively, the contents of this file may be used under the terms of either the GNU General
 Public License Version 2 or later (the "GPL") - http://www.gnu.org/licenses/gpl-2.0.html, or
 the GNU Lesser General Public License Version 2.1 or later (the "LGPL") -
 http://www.gnu.org/licenses/lgpl-2.1.html, in which case the provisions of the GPL
 or the LGPL are applicable instead of those above. If you wish to allow use of your version
 of this file only under the terms of either the GPL or the LGPL, and not to allow others to
 use your version of this file under the terms of the MPL, indicate your decision by deleting
 the provisions above and replace them with the notice and other provisions required by the
 GPL or the LGPL. If you do not delete the provisions above, a recipient may use your version
 of this file under the terms of any one of the MPL, the GPL or the LGPL.

 Sponsors:
 The ISA Team and the ISA software suite have been funded by the EU Carcinogenomics project
 (http://www.carcinogenomics.eu), the UK BBSRC (http://www.bbsrc.ac.uk), the UK NERC-NEBC
 (http://nebc.nerc.ac.uk) and in part by the EU NuGO consortium (http://www.nugo.org/everyone).

 */

package org.isatools.isatab.gui_invokers;

import org.isatools.isatab.ISATABValidator;
import org.isatools.isatab.commandline.AbstractImportLayerShellCommand;
import org.isatools.isatab_v1.ISATABLoader;
import org.isatools.isatab_v1.mapping.ISATABMapper;
import org.isatools.tablib.schema.FormatSetInstance;
import org.isatools.tablib.utils.BIIObjectStore;
import uk.ac.ebi.bioinvindex.model.Study;

import java.util.ArrayList;
import java.util.List;


/**
 * A Draft of the ISATAB validator.
 * <p/>
 * For the moment the validator simply tries to load the ISATAB and map it to the BII model. This interface is intended
 * to be used by another Java program that need to validate a submission (we wrote it for the ISATABCreator).
 *
 * @author brandizi
 *         <b>date</b>: Apr 5, 2009
 */
public class GUIISATABValidator extends AbstractGUIInvoker {
    private BIIObjectStore store;
    private String isatabSubmissionPath;

    /**
     * Do the job, the log returned by {@link #getLog()} is reset by this call.
     */
    public GUIInvokerResult validate(String isatabSubmissionPath) {
        return validate(isatabSubmissionPath, false);
    }

    /**
     * Do the job, the log returned by {@link #getLog()} is reset by this call.
     * @param reportWarnings - return WARNING whenever they occur
     */
    public GUIInvokerResult validate(String isatabSubmissionPath, boolean reportWarnings) {
        try {
            // Save there the log file
            AbstractImportLayerShellCommand.setupLog4JPath(isatabSubmissionPath + "/isatools.log");

            ISATABLoader isatabLoader = new ISATABLoader(isatabSubmissionPath);
            FormatSetInstance isatabInstance = isatabLoader.load();

            System.out.println("Loaded...");

            ISATABValidator validator = new ISATABValidator(isatabInstance);
            System.out.println("Now validating...");
            GUIInvokerResult result = validator.validate();
            if (GUIInvokerResult.WARNING == result) {
                vlog.warn("ISA-Configurator Validation reported problems, see the messages above or the log file");
            }

            this.store = validator.getStore();
            this.isatabSubmissionPath = isatabSubmissionPath;

            return reportWarnings ? result : GUIInvokerResult.SUCCESS;
        } catch (Exception e) {
            vlog.error(e.getMessage(), e);
            return GUIInvokerResult.ERROR;
        }
    }


    /**
     * Reports a human-readable summary of all objects mapped.
     * This is a wrapper of {@link ISATABMapper#report(BIIObjectStore)}.
     */
    public String report(boolean asHTML) {
        return asHTML ? ISATABMapper.htmlReport(store) : ISATABMapper.report(store);
    }

    public List<Study> getStudiesInSubmission() {
        List<Study> studyInFile = new ArrayList<Study>();
        for (Study study : store.valuesOfType(Study.class)) {
            studyInFile.add(study);
        }

        return studyInFile;
    }

    /**
     * Defaults to false, i.e.: plain text version.
     */
    public String report() {
        return report(false);
    }

    /**
     * The object store created by the last {@link #validate(String)} invocation.
     */
    public BIIObjectStore getStore() {
        return store;
    }


    /**
     * The submission path passed to the last {@link #validate(String)} invocation.
     */
    public String getIsatabSubmissionPath() {
        return isatabSubmissionPath;
    }

}

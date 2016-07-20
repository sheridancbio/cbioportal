/*
 * Copyright (c) 2015 Memorial Sloan-Kettering Cancer Center.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF MERCHANTABILITY OR FITNESS
 * FOR A PARTICULAR PURPOSE. The software and documentation provided hereunder
 * is on an "as is" basis, and Memorial Sloan-Kettering Cancer Center has no
 * obligations to provide maintenance, support, updates, enhancements or
 * modifications. In no event shall Memorial Sloan-Kettering Cancer Center be
 * liable to any party for direct, indirect, special, incidental or
 * consequential damages, including lost profits, arising out of the use of this
 * software and its documentation, even if Memorial Sloan-Kettering Cancer
 * Center has been advised of the possibility of such damage.
 */

/*
 * This file is part of cBioPortal.
 *
 * cBioPortal is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.cbioportal.util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import org.cbioportal.GlobalProperties;
import org.cbioportal.QueryBuilderParameter;
import org.cbioportal.PatientViewParameter;
import org.cbioportal.CancerStudyViewParameter;
import org.cbioportal.IGVLinkingJSONParameter;

/**
 * Utility class for building internal URIs
 */

public class WebServerUriBuilder {

    public static String getLinkToPatientView(String caseId, String cancerStudyId) {
        return "case.do?" + QueryBuilderParameter.CANCER_STUDY_ID + "=" + cancerStudyId +
                "&"+ PatientViewParameter.SAMPLE_ID + "=" + caseId;
    }

    public static String getLinkToSampleView(String caseId, String cancerStudyId) {
        return "case.do?" + QueryBuilderParameter.CANCER_STUDY_ID + "=" + cancerStudyId +
                "&"+ PatientViewParameter.SAMPLE_ID + "=" + caseId;
    }

    public static String getLinkToCancerStudyView(String cancerStudyId) {
        return "study?" + CancerStudyViewParameter.ID +
                "=" + cancerStudyId;
    }

    public static String getLinkToIGVForBAM(String cancerStudyId, String caseId, String locus) {
        return ("igvlinking.json?" + IGVLinkingJSONParameter.CANCER_STUDY_ID + "=" + cancerStudyId +
                "&" + IGVLinkingJSONParameter.CASE_ID + "=" + caseId +
                "&" + IGVLinkingJSONParameter.LOCUS + "=" + locus);
    }

    public static String getDigitalSlideArchiveIframeUrl(String caseId) {
        String url = GlobalProperties.getProperty(GlobalProperties.PATIENT_VIEW_DIGITAL_SLIDE_IFRAME_URL);
        return url+caseId;
    }

    public static String getDigitalSlideArchiveMetaUrl(String caseId) {
        String url = GlobalProperties.getProperty(GlobalProperties.PATIENT_VIEW_DIGITAL_SLIDE_META_URL);
        return url+caseId;
    }

    public static String[] getTCGAPathReportUrl(String typeOfCancer) {
        String url = GlobalProperties.getProperty(GlobalProperties.PATIENT_VIEW_TCGA_PATH_REPORT_URL);
        if (url == null) {
            return null;
        }
        if (typeOfCancer.equalsIgnoreCase("coadread")) {
            return new String[] {
                url.replace("{cancer.type}", "coad"),
                url.replace("{cancer.type}", "read")
            };
        }
        return new String[] {url.replace("{cancer.type}", typeOfCancer)};
    }

    public static String getOncoKBUrl() {
        String oncokbUrl = GlobalProperties.getProperty(GlobalProperties.ONCOKB_URL);
        //Test connection of OncoKB website.
        if (oncokbUrl == null || oncokbUrl.isEmpty()) {
            return "";
        }
        HttpURLConnection conn = null;
        try {
            URL url = new URL(oncokbUrl + "access");
            conn = (HttpURLConnection)url.openConnection();
            if (conn.getResponseCode() != 200) {
                return "";
            }
        } catch (Exception e) {
            return "";
        } finally {
            conn.disconnect();
        }
        return oncokbUrl;
    }
}

package org.cbioportal.web;

import org.cbioportal.model.CancerStudy;
import org.springframework.stereotype.Component;
import org.cbioportal.web.parameter.PatientFilter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@Component
public class PatientFilterExtractor {

    private static final Log log = LogFactory.getLog(PatientFilter.class);

    public String extractStudyFromPatientFilter(PatientFilter patientFilter) {
        CancerStudy mskimpact = new CancerStudy();
        mskimpact.setCancerStudyIdentifier("mskimpact");
        log.error("returning mskimpact, hardcoded");
        return mskimpact.getCancerStudyIdentifier();
    }

}

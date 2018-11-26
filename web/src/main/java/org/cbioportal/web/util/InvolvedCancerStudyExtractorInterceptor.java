package org.cbioportal.web.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.cbioportal.web.parameter.PatientFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

@Component
public class InvolvedCancerStudyExtractorInterceptor extends HandlerInterceptorAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(InvolvedCancerStudyExtractorInterceptor.class);
    public static final String PATIENT_FETCH_PATH = "/patients/fetch";

    @Override public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!request.getMethod().equals("POST")) {
            return true; // no attribute extraction needed because all user supplied filter objects are in POST requests
        }
        ObjectMapper mapper = new ObjectMapper();
        String requestPathInfo = request.getPathInfo();
        if (requestPathInfo.equals(PATIENT_FETCH_PATH)) {
            try {
                PatientFilter patientFilter = mapper.readValue(request.getReader(), PatientFilter.class);
                LOG.error("extracted patientFilter: " + patientFilter.toString());
                LOG.error("patientFilter patientIdentifiers: " + patientFilter.getPatientIdentifiers());
                LOG.error("patientFilter uniquePatientKeys: " + patientFilter.getUniquePatientKeys());
/*
            //TODO: add the logic from CancerStudyPermissionEvalator here to get the cancer study list
*/           
                LOG.error("setting patientFilterInterceptor to " + patientFilter.toString());
                request.setAttribute("patientFilterInterceptor", patientFilter);
                LOG.error("setting involvedCancerStudies to " + "mskimpact");
                request.setAttribute("involvedCancerStudies", "mskimpact");
            } catch (Exception e) {
                LOG.error("exception thrown during extraction of patientFilter: " + e);
                //TODO: do the right thing when an invalidly formatted JSON argument is passed in. Previously the @Valid tag in the controller did automatic validation of this argument
                return false;
            }
            return true; // return "OK" if exception not thrown
        }
        return true; // default is "OK" for all non-POST requests
    }
}

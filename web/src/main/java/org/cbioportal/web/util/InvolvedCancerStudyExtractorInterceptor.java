package org.cbioportal.web.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.Enumeration;
import java.util.List;
import java.io.BufferedReader;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.cbioportal.web.PatientController;
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

        HttpServletRequest properRequest = request;
        RequestWrapper wrappedRequest = new RequestWrapper(properRequest);
        request = wrappedRequest;

//        int contentLength = request.getContentLength();
        ObjectMapper mapper = new ObjectMapper();
        String requestPathInfo = request.getPathInfo();
        if (requestPathInfo.equals(PATIENT_FETCH_PATH)) {
            PatientFilter patientFilter = mapper.readValue(request.getReader(), PatientFilter.class);
            LOG.error("extracted patientFilter: " + patientFilter.toString());
            LOG.error("patientFilter patientIdentifiers: " + patientFilter.getPatientIdentifiers());
            LOG.error("patientFilter uniquePatientKeys: " + patientFilter.getUniquePatientKeys());
            //TODO: add the logic from CancerStudyPermissionEvalator here to get the cancer study list
            
            return true;
        }
/*
        if (bodyTree.has("patientIdentifiers") || bodyTree.has("uniquePatientKeys")) {
            LOG.error("we have found a PatientFilter in the body of the request");
        }
*/
        request.setAttribute("involvedCancerStudies", "mskimpact");
        return true;
    }
}

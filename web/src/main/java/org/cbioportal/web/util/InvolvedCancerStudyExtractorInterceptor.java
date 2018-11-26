package org.cbioportal.web.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.cbioportal.web.parameter.PatientFilter;
import org.cbioportal.web.parameter.PatientIdentifier;
import org.cbioportal.web.util.UniqueKeyExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

@Component
public class InvolvedCancerStudyExtractorInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private UniqueKeyExtractor uniqueKeyExtractor;

    @Autowired
    private ObjectMapper objectMapper;

    private static final Logger LOG = LoggerFactory.getLogger(InvolvedCancerStudyExtractorInterceptor.class);
    public static final String PATIENT_FETCH_PATH = ".patients.fetch";

    @Override public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!request.getMethod().equals("POST")) {
            return true; // no attribute extraction needed because all user supplied filter objects are in POST requests
        }
        ResettableHttpServletRequestWrapper wrappedRequest = new ResettableHttpServletRequestWrapper((HttpServletRequest) request);
        String requestPathInfo = request.getPathInfo();
        if (requestPathInfo.equals(PATIENT_FETCH_PATH)) {
            return extractAttributesFromPatientFilter(wrappedRequest);
        }
        return true;
    }

    private boolean extractAttributesFromPatientFilter(HttpServletRequest request) {
        try {
            PatientFilter patientFilter = objectMapper.readValue(request.getReader(), PatientFilter.class);
            LOG.debug("extracted patientFilter: " + patientFilter.toString());
            Collection<String> cancerStudyIdCollection = extractCancerStudyIdsFromPatientFilter(patientFilter);
            LOG.debug("setting interceptedPatientFilter to " + patientFilter);
            request.setAttribute("interceptedPatientFilter", patientFilter);
            LOG.debug("setting involvedCancerStudies to " + cancerStudyIdCollection);
            request.setAttribute("involvedCancerStudies", cancerStudyIdCollection);
        } catch (Exception e) {
            LOG.error("exception thrown during extraction of patientFilter: " + e);
            //TODO: do the right thing when an invalidly formatted JSON argument is passed in. Previously the @Valid tag in the controller did automatic validation of this argument
            return false;
        }
        return true;
    }

    private Collection<String> extractCancerStudyIdsFromPatientFilter(PatientFilter patientFilter) {
        // use hashset as the study list in the patientFilter will usually be populated with many duplicate values
        Set<String> studyIdSet = new HashSet<String>();
        if (patientFilter.getPatientIdentifiers() != null) {
            for (PatientIdentifier patientIdentifier : patientFilter.getPatientIdentifiers()) {
                studyIdSet.add(patientIdentifier.getStudyId());
            }
        } else {
            uniqueKeyExtractor.extractUniqueKeys(patientFilter.getUniquePatientKeys(), studyIdSet);
        }
        return studyIdSet;
    }

}

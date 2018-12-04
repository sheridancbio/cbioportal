package org.cbioportal.web.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.cbioportal.model.SampleList;
import org.cbioportal.model.MolecularProfile;
import org.cbioportal.persistence.mybatis.util.CacheMapUtil;
import org.cbioportal.web.parameter.ClinicalAttributeCountFilter;
import org.cbioportal.web.parameter.PatientFilter;
import org.cbioportal.web.parameter.PatientIdentifier;
import org.cbioportal.web.parameter.SampleFilter;
import org.cbioportal.web.parameter.SampleIdentifier;
import org.cbioportal.web.parameter.MolecularProfileFilter;
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

    @Autowired
    private CacheMapUtil cacheMapUtil;

    private static final Logger LOG = LoggerFactory.getLogger(InvolvedCancerStudyExtractorInterceptor.class);
    public static final String PATIENT_FETCH_PATH = "/patients/fetch";
    public static final String SAMPLE_FETCH_PATH = "/samples/fetch";
    public static final String MOLECULAR_PROFILE_FETCH_PATH = "/molecular-profiles/fetch";
    public static final String CLINICAL_ATTRIBUTE_COUNT_FETCH_PATH = "/clinical-attributes/counts/fetch";

    @Override public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!request.getMethod().equals("POST")) {
            return true; // no attribute extraction needed because all user supplied filter objects are in POST requests
        }
        ResettableHttpServletRequestWrapper wrappedRequest = new ResettableHttpServletRequestWrapper((HttpServletRequest) request);
        String requestPathInfo = request.getPathInfo();
        if (requestPathInfo.equals(PATIENT_FETCH_PATH)) {
            return extractAttributesFromPatientFilter(wrappedRequest);
        } else if (requestPathInfo.equals(SAMPLE_FETCH_PATH)) {
            return extractAttributesFromSampleFilter(wrappedRequest);
        } else if (requestPathInfo.equals(MOLECULAR_PROFILE_FETCH_PATH)) {
            return extractAttributesFromMolecularProfileFilter(wrappedRequest);
        } else if (requestPathInfo.equals(CLINICAL_ATTRIBUTE_COUNT_FETCH_PATH)) {
            return extractAttributesFromClinicalAttributeCountFilter(wrappedRequest);
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
            return false;
        }
        return true;
    }

    private boolean extractAttributesFromSampleFilter(HttpServletRequest request) {
        try {
            SampleFilter sampleFilter = objectMapper.readValue(request.getReader(), SampleFilter.class);
            LOG.debug("extracted sampleFilter: " + sampleFilter.toString());
            Collection<String> cancerStudyIdCollection = extractCancerStudyIdsFromSampleFilter(sampleFilter);
            LOG.debug("setting interceptedSampleFilter to " + sampleFilter);
            request.setAttribute("interceptedSampleFilter", sampleFilter);
            LOG.debug("setting involvedCancerStudies to " + cancerStudyIdCollection);
            request.setAttribute("involvedCancerStudies", cancerStudyIdCollection);
        } catch (Exception e) {
            LOG.error("exception thrown during extraction of sampleFilter: " + e);
            return false;
        }
        return true;
    }

    private boolean extractAttributesFromMolecularProfileFilter(HttpServletRequest request) {
        try {
            MolecularProfileFilter molecularProfileFilter = objectMapper.readValue(request.getReader(), MolecularProfileFilter.class);
            LOG.debug("extracted molecularProfileFilter: " + molecularProfileFilter.toString());
            Collection<String> cancerStudyIdCollection = extractCancerStudyIdsFromMolecularProfileFilter(molecularProfileFilter);
            LOG.debug("setting interceptedMolecularProfileFilter to " + molecularProfileFilter);
            request.setAttribute("interceptedMolecularProfileFilter", molecularProfileFilter);
            LOG.debug("setting involvedCancerStudies to " + cancerStudyIdCollection);
            request.setAttribute("involvedCancerStudies", cancerStudyIdCollection);
        } catch (Exception e) {
            LOG.error("exception thrown during extraction of molecularProfileFilter: " + e);
            return false;
        }
        return true;
    }

    private boolean extractAttributesFromClinicalAttributeCountFilter(HttpServletRequest request) {
        try {
            ClinicalAttributeCountFilter clinicalAttributeCountFilter = objectMapper.readValue(request.getReader(), ClinicalAttributeCountFilter.class);
            LOG.debug("extracted clinicalAttributeCountFilter: " + clinicalAttributeCountFilter.toString());
            Collection<String> cancerStudyIdCollection = extractCancerStudyIdsFromClinicalAttributeCountFilter(clinicalAttributeCountFilter);
            LOG.debug("setting interceptedClinicalAttributeCountFilter to " + clinicalAttributeCountFilter);
            request.setAttribute("interceptedClinicalAttributeCountFilter", clinicalAttributeCountFilter);
            LOG.debug("setting involvedCancerStudies to " + cancerStudyIdCollection);
            request.setAttribute("involvedCancerStudies", cancerStudyIdCollection);
        } catch (Exception e) {
            LOG.error("exception thrown during extraction of clinicalAttributeCountFilter: " + e);
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

    private Collection<String> extractCancerStudyIdsFromSampleFilter(SampleFilter sampleFilter) {
        // use hashset as the study list in the sampleFilter will usually be populated with many duplicate values
        Set<String> studyIdSet = new HashSet<String>();
        if (sampleFilter.getSampleListIds() != null) {
            extractCancerStudyIdsFromSampleListIds(sampleFilter.getSampleListIds(), studyIdSet);
        } else if (sampleFilter.getSampleIdentifiers() != null) {
            extractCancerStudyIdsFromSampleIdentifiers(sampleFilter.getSampleIdentifiers(), studyIdSet);
        } else {
            uniqueKeyExtractor.extractUniqueKeys(sampleFilter.getUniqueSampleKeys(), studyIdSet);
        }
        return studyIdSet;
    }

    private void extractCancerStudyIdsFromSampleIdentifiers(Collection<SampleIdentifier> sampleIdentifiers, Set<String> studyIdSet) {
        for (SampleIdentifier sampleIdentifier : sampleIdentifiers) {
            studyIdSet.add(sampleIdentifier.getStudyId());
        }
    }

    private void extractCancerStudyIdsFromSampleListIds(List<String> sampleListIds, Set<String> studyIdSet) {
        for (String sampleListId : sampleListIds) {
            SampleList sampleList = cacheMapUtil.getSampleListMap().get(sampleListId);
            studyIdSet.add(sampleList.getCancerStudyIdentifier());
        }
    }

    private Collection<String> extractCancerStudyIdsFromMolecularProfileFilter(MolecularProfileFilter molecularProfileFilter) {
        // use hashset as the study list in the molecularProfileFilter may be populated with many duplicate values
        Set<String> studyIdSet = new HashSet<String>();
        if (molecularProfileFilter.getStudyIds() != null) {
            studyIdSet.addAll(molecularProfileFilter.getStudyIds());
        } else {
            extractCancerStudyIdsFromMolecularProfileIds(molecularProfileFilter.getMolecularProfileIds(), studyIdSet);
        }
        return studyIdSet;
    }

    private void extractCancerStudyIdsFromMolecularProfileIds(List<String> molecularProfileIds, Set<String> studyIdSet) {
        for (String molecularProfileId : molecularProfileIds) {
            MolecularProfile molecularProfile = cacheMapUtil.getMolecularProfileMap().get(molecularProfileId);
            studyIdSet.add(molecularProfile.getCancerStudyIdentifier());
        }
    }

    private Collection<String> extractCancerStudyIdsFromClinicalAttributeCountFilter(ClinicalAttributeCountFilter clinicalAttributeCountFilter) {
        // use hashset as the study list in the clinicalAttributeCountFilter may be populated with many duplicate values
        Set<String> studyIdSet = new HashSet<String>();
        if (clinicalAttributeCountFilter.getSampleListId() != null) {
            extractCancerStudyIdsFromSampleListIds(Arrays.asList(clinicalAttributeCountFilter.getSampleListId()), studyIdSet);
        } else {
            extractCancerStudyIdsFromSampleIdentifiers(clinicalAttributeCountFilter.getSampleIdentifiers(), studyIdSet);
        }
        return studyIdSet;
    }

}

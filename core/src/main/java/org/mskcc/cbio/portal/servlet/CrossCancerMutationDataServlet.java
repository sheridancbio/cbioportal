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

package org.mskcc.cbio.portal.servlet;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import org.cbioportal.util.StringParser;
import org.cbioportal.QueryBuilderParameter;
//import org.mskcc.cbio.portal.dao.*;
//import org.mskcc.cbio.portal.model.*;
//import org.mskcc.cbio.portal.util.*;
//import org.mskcc.cbio.portal.web_api.*;
import org.cbioportal.service.AnnotatedSampleSets;
import org.cbioportal.model.SampleList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * A servlet designed to return a JSON array of mutation objects.
 *
 * @author Arman
 * @author Selcuk Onur Sumer
 */
public class CrossCancerMutationDataServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(CrossCancerMutationDataServlet.class);
    private AccessControl accessControl; // access control to cancer studies
    @Autowired
    private MutationDataUtils mutationDataUtils;
    @Autowired
    private StringParser stringParser;

    public void init() throws ServletException {
        super.init();
        accessControl = SpringUtil.getAccessControl();
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, config.getServletContext());
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ArrayList<String> targetGeneList = stringParser.splitBySpacesOrCommas(request.getParameter("geneList"));
        Integer dataTypePriority;
        try {
            dataTypePriority = Integer.parseInt(request.getParameter(QueryBuilderParameter.DATA_PRIORITY).trim());
        } catch (NumberFormatException e) {
            dataTypePriority = 0;
        }
        ArrayList<String> cancerStudyIdList = stringParser.splitBySpacesOrCommas(request.getParameter(QueryBuilderParameter.CANCER_STUDY_LIST));
        HashSet<String> studySet = new HashSet<String>(cancerStudyIdList);
        JSONArray data = new JSONArray();
        try {
            List<CancerStudy> cancerStudiesList = accessControl.getCancerStudies();
            for (CancerStudy cancerStudy : cancerStudiesList) {
                if (!studySet.contains(cancerStudy.getCancerStudyStableId())) {
                    continue;
                }
                String cancerStudyId = cancerStudy.getCancerStudyStableId();
                if(cancerStudyId.equalsIgnoreCase("all")) {
                    continue;
                }
                //  Get all Genetic Profiles Associated with this Cancer Study ID.
                ArrayList<GeneticProfile> geneticProfileList = GetGeneticProfiles.getGeneticProfiles(cancerStudyId);
                //  Get all Patient Lists Associated with this Cancer Study ID.
                ArrayList<SampleList> sampleSetList = GetSampleLists.getSampleLists(cancerStudyId);
                //  Get the default patient set
                AnnotatedSampleSets annotatedSampleSets = new AnnotatedSampleSets(sampleSetList, dataTypePriority);
                SampleList defaultSampleSet = annotatedSampleSets.getDefaultSampleList();
                if (defaultSampleSet == null) {
                    continue;
                }
                List<String> sampleList = defaultSampleSet.getSampleList();
                //  Get the default genomic profiles
                CategorizedGeneticProfileSet categorizedGeneticProfileSet = new CategorizedGeneticProfileSet(geneticProfileList);
                HashMap<String, GeneticProfile> defaultGeneticProfileSet = null;
                switch (dataTypePriority) {
                    case 2:
                        defaultGeneticProfileSet = categorizedGeneticProfileSet.getDefaultCopyNumberMap();
                        break;
                    case 1:
                        defaultGeneticProfileSet = categorizedGeneticProfileSet.getDefaultMutationMap();
                        break;
                    case 0:
                    default:
                        defaultGeneticProfileSet = categorizedGeneticProfileSet.getDefaultMutationAndCopyNumberMap();
                }
                for (GeneticProfile profile : defaultGeneticProfileSet.values()) {
                    if(!profile.getGeneticAlterationType().equals(GeneticAlterationType.MUTATION_EXTENDED)) {
                        continue;
                    }
                    // add mutation data for each genetic profile
                    JSONArray mutationData = mutationDataUtils.getMutationData(profile.getStableId(), targetGeneList, sampleList);
                    data.addAll(mutationData);
                }
            }
        } catch (DaoException e) {
            throw new ServletException(e);
        } catch (ProtocolException e) {
            throw new ServletException(e);
        }
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        try {
            JSONValue.writeJSONString(data, out);
        } finally {
            out.close();
        }
    }
}

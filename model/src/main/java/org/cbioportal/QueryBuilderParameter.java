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

package org.cbioportal;

public final class QueryBuilderParameter {
    public static final String ACTION_NAME = "Action";
    public static final String CANCER_STUDY_ID = "cancer_study_id";
    public static final String CANCER_STUDY_LIST = "cancer_study_list";
    public static final String CASE_IDS = "case_ids";
    public static final String CASE_IDS_KEY = "case_ids_key";
    public static final String CASE_SET_ID = "case_set_id";
    public static final String DATA_PRIORITY = "data_priority";
    public static final String GENETIC_PROFILE_IDS = "genetic_profile_ids";
    public static final String GENE_LIST = "gene_list";
    public static final String MRNA_PROFILES_SELECTED = "MRNA_PROFILES_SELECTED";
    public static final String OUTPUT = "output";
    public static final String PATIENT_CASE_SELECT = "patient_case_select";
    public static final String RPPA_SCORE_THRESHOLD = "RPPA_SCORE_THRESHOLD";
    public static final String TAB_INDEX = "tab_index";
    public static final String Z_SCORE_THRESHOLD = "Z_SCORE_THRESHOLD";

    private QueryBuilderParameter() {}
}

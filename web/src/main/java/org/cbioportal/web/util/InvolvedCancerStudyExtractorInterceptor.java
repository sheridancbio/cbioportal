package org.cbioportal.web.util;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.cbioportal.web.PatientController;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

@Component
public class InvolvedCancerStudyExtractorInterceptor extends HandlerInterceptorAdapter {

    @Override public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        request.setAttribute("involvedCancerStudies", "mskimpact");
        return true;
    }

}

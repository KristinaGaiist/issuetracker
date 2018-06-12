package com.axmor.queries;

import com.axmor.ApplicationConstants;
import com.axmor.RedirectService;
import com.axmor.exceptions.DataConnectionException;
import com.axmor.helpers.ArgumentHelper;
import com.axmor.helpers.StringHelper;
import com.axmor.models.Issue;
import com.axmor.service.interfaces.IIssueService;
import com.axmor.service.interfaces.IUserService;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.template.freemarker.FreeMarkerEngine;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class IssuesQueryHandler implements Route {
    private final FreeMarkerEngine freeMarkerEngine;
    private final IIssueService issueService;
    private final IUserService userService;
    private final RedirectService redirectService;

    public IssuesQueryHandler(
            FreeMarkerEngine freeMarkerEngine,
            IIssueService issueService,
            IUserService userService,
            RedirectService redirectService) {
        ArgumentHelper.ensureNotNull("freeMarkerEngine", freeMarkerEngine);
        ArgumentHelper.ensureNotNull("userService", userService);
        ArgumentHelper.ensureNotNull("issueService", issueService);
        ArgumentHelper.ensureNotNull("redirectService", redirectService);

        this.freeMarkerEngine = freeMarkerEngine;
        this.issueService = issueService;
        this.userService = userService;
        this.redirectService = redirectService;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        if (redirectService.redirectIfNotAuthenticate(request, response)) {
            return null;
        }

        String pageIndexString = request.queryParams("pageIndex");
        int pageNumber = 1;
        if (!StringHelper.isNullOrEmpty(pageIndexString)) {
            pageNumber = Integer.parseInt(pageIndexString);
        }

        int pageCount;
        String searchName = request.queryParams("searchName");
        try {
            pageCount = issueService.getPageCount(searchName);
        } catch (DataConnectionException e) {
            response.redirect("/error");
            return null;
        }

        List <Integer> pageList = new ArrayList <>();
        for (int i = 1; i < pageCount + 1; i++) {
            pageList.add(i);
        }

        String sortValue = request.queryParams("sortValue");
        List <Issue> issueList;
        if(StringHelper.isNullOrEmpty(sortValue)) {
            try {
                issueList = issueService.getIssues(searchName, pageNumber, "id");
            } catch (DataConnectionException e) {
                return null;
            }
        } else {
            try {
                issueList = issueService.getIssues(searchName, pageNumber, sortValue);
            } catch (DataConnectionException e) {
                response.redirect("/error");
                return null;
            }
        }

        Map <String, Object> model = userService.setHidden(request);
        model.put("issues", issueList);
        model.put("pageList", pageList);
        model.put("sortValue", sortValue == null ? ApplicationConstants.EMPTY : sortValue);
        model.put("hasSearchName", !StringHelper.isNullOrEmpty(searchName));
        model.put("searchName", searchName);
        ModelAndView modelAndView = new ModelAndView(model, "view/issues.html");

        return freeMarkerEngine.render(modelAndView);
    }
}

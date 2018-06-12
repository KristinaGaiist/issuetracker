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

public class SearchIssuesQueryHandler implements Route {
    private final FreeMarkerEngine freeMarkerEngine;
    private final IIssueService issueService;
    private final IUserService userService;
    private final RedirectService redirectService;

    public SearchIssuesQueryHandler(
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

        String name = request.queryParams("search");
        if (StringHelper.isNullOrEmpty(name)) {
            response.redirect("/issues");
            return null;
        }

        int pageCount;
        try {
            pageCount = issueService.getPageCount();
        } catch (DataConnectionException e) {
            response.redirect("/error");
            return null;
        }
        List <Integer> pageList = new ArrayList <>();
        for (int i = 1; i < pageCount + 1; i++) {
            pageList.add(i);
        }

        List <Issue> issueList;
        try {
            issueList = issueService.getFoundIssues(name);
        } catch (DataConnectionException e) {
            response.redirect("/error");
            return null;
        }

        String sortValue = request.queryParams("sortValue");
        Map <String, Object> model = userService.setHidden(request);
        model.put("issues", issueList);
        model.put("pageList", pageList);
        model.put("sortValue", sortValue == null ? ApplicationConstants.EMPTY : sortValue);
        ModelAndView modelAndView = new ModelAndView(model, "view/issues.html");

        return freeMarkerEngine.render(modelAndView);
    }
}

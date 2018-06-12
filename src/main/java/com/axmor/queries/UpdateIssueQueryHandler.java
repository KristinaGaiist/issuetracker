package com.axmor.queries;

import com.axmor.RedirectService;
import com.axmor.exceptions.DataConnectionException;
import com.axmor.helpers.ArgumentHelper;
import com.axmor.models.Issue;
import com.axmor.models.Status;
import com.axmor.service.interfaces.IIssueService;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.template.freemarker.FreeMarkerEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateIssueQueryHandler implements Route {
    private final FreeMarkerEngine freeMarkerEngine;
    private final IIssueService issueService;
    private final RedirectService redirectService;

    public UpdateIssueQueryHandler(
            FreeMarkerEngine freeMarkerEngine,
            IIssueService issueService,
            RedirectService redirectService) {
        ArgumentHelper.ensureNotNull("freeMarkerEngine", freeMarkerEngine);
        ArgumentHelper.ensureNotNull("issueService", issueService);
        ArgumentHelper.ensureNotNull("redirectService", redirectService);

        this.freeMarkerEngine = freeMarkerEngine;
        this.issueService = issueService;
        this.redirectService = redirectService;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        if (redirectService.redirectIfNotAuthenticate(request, response)) {
            return null;
        }
        int issueId = Integer.parseInt(request.queryParams("issueId"));
        ArgumentHelper.ensureNotNull("issueId", issueId);

        List <Status> statusList;
        Issue issue;
        try {
            statusList = issueService.getStatuses();
            issue = issueService.getIssueById(issueId);
        } catch (DataConnectionException e) {
            response.redirect("/error");
            return null;
        }
        Map<String, Object> model = new HashMap<>();
        model.put("issue", issue);
        model.put("statuses", statusList);
        model.put("validationErrors", new ArrayList<>());

        return freeMarkerEngine.render(new ModelAndView(model, "view/update.html"));
    }
}

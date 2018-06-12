package com.axmor.commands;

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

public class UpdateIssueCommandHandler implements Route {
    private final FreeMarkerEngine freeMarkerEngine;
    private final IIssueService issueService;
    private final RedirectService redirectService;

    public UpdateIssueCommandHandler(
            FreeMarkerEngine freeMarkerEngine,
            IIssueService issueService,
            RedirectService redirectService) {
        this.redirectService = redirectService;
        ArgumentHelper.ensureNotNull("freeMarkerEngine", freeMarkerEngine);
        ArgumentHelper.ensureNotNull("issueService", issueService);

        this.freeMarkerEngine = freeMarkerEngine;
        this.issueService = issueService;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        if (redirectService.redirectIfNotAuthenticate(request, response)) {
            return null;
        }

        int issueId = Integer.parseInt(request.queryParams("issueId"));
        int status = Integer.parseInt(request.queryParams("status"));
        String issueName = request.queryParams("issueName");
        String description = request.queryParams("description");
        ArgumentHelper.ensureNotNull("issueId", issueId);
        ArgumentHelper.ensureNotNull("status", status);

        List<String> validationErrors = new ArrayList<>();
        if (!issueService.validateCreationOrUpdateIssue(issueName, description, validationErrors)) {
            HashMap<String, Object> model = new HashMap <>();
            Issue issue = issueService.getIssueById(issueId);
            List<Status> statusList = issueService.getStatuses();
            model.put("issue", issue);
            model.put("statuses", statusList);
            model.put("validationErrors", validationErrors);
            ModelAndView modelAndView = new ModelAndView(model, "view/update.html");

            return freeMarkerEngine.render(modelAndView);
        }

        try {
            issueService.update(issueId, issueName, description, status);
        } catch (DataConnectionException e) {
            response.redirect("/error");
            return null;
        }
        response.redirect("/issues");

        return null;
    }
}

package com.axmor.commands;

import com.axmor.exceptions.DataConnectionException;
import com.axmor.helpers.ArgumentHelper;
import com.axmor.service.interfaces.IIssueService;
import spark.Request;
import spark.Response;
import spark.Route;

public class DeleteIssueCommandHandler implements Route {
    private final IIssueService issueService;

    public DeleteIssueCommandHandler(IIssueService issueService) {
        ArgumentHelper.ensureNotNull("issueService", issueService);
        this.issueService = issueService;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        int issueId = Integer.parseInt(request.queryParams("issueId"));
        ArgumentHelper.ensureNotNull("issueId", issueId);
        try {
            issueService.delete(issueId);
        } catch (DataConnectionException e) {
            response.redirect("/error");
            return null;
        }

        response.redirect("/issues");
        return null;
    }
}

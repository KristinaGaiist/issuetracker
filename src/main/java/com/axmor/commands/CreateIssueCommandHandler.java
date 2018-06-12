package com.axmor.commands;


import com.axmor.ApplicationConstants;
import com.axmor.RedirectService;
import com.axmor.exceptions.DataConnectionException;
import com.axmor.helpers.ArgumentHelper;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Route;
import com.axmor.service.interfaces.ICommentService;
import com.axmor.service.interfaces.IIssueService;
import com.axmor.service.interfaces.IUserService;
import spark.template.freemarker.FreeMarkerEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CreateIssueCommandHandler implements Route {
    private final IIssueService issueService;
    private final FreeMarkerEngine freeMarkerEngine;
    private final RedirectService redirectService;

    public CreateIssueCommandHandler(
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

        String author = request.session().attribute(ApplicationConstants.SESSION_USER_NAME);
        String issueName = request.queryParams("issueName");
        String description = request.queryParams("description");
        ArgumentHelper.ensureNotNull("issueName", issueName);
        ArgumentHelper.ensureNotNull("description", description);

        List <String> validationErrors = new ArrayList <>();
        if (!issueService.validateCreationOrUpdateIssue(issueName, description, validationErrors)) {
            HashMap <String, Object> model = new HashMap <>();
            model.put("validationErrors", validationErrors);
            model.put("issueName", issueName);
            model.put("description", description);
            ModelAndView modelAndView = new ModelAndView(model, "view/create.html");

            return freeMarkerEngine.render(modelAndView);
        }

        try {
            issueService.create(issueName, author, description);
        } catch (DataConnectionException e) {
            response.redirect("/error");
            return null;
        }

        response.redirect("/issues");
        return null;
    }
}

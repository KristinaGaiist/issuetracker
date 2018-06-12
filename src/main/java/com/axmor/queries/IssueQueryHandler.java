package com.axmor.queries;

import com.axmor.ApplicationConstants;
import com.axmor.RedirectService;
import com.axmor.exceptions.DataConnectionException;
import com.axmor.helpers.ArgumentHelper;
import com.axmor.models.Issue;
import com.axmor.models.Status;
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

public class IssueQueryHandler implements Route {
    private final FreeMarkerEngine freeMarkerEngine;
    private final IIssueService issueService;
    private final IUserService userService;
    private final RedirectService redirectService;

    public IssueQueryHandler(
            FreeMarkerEngine freeMarkerEngine,
            IIssueService issueService,
            IUserService userService,
            RedirectService redirectService) {
        ArgumentHelper.ensureNotNull("freeMarkerEngine", freeMarkerEngine);
        ArgumentHelper.ensureNotNull("issueService", issueService);
        ArgumentHelper.ensureNotNull("userService", userService);
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

        ArgumentHelper.ensureNotNull("statusList", statusList);
        ArgumentHelper.ensureNotNull("issue", issue);

        Map<String, Object> model = userService.setHidden(request);
        ArgumentHelper.ensureNotNull("model", model);
        String userName = request.session().attribute(ApplicationConstants.SESSION_USER_NAME);

        model.put("issue", issue);
        model.put("comments", issue.getCommentList());
        model.put("userName", userName);
        model.put("statuses", statusList);
        model.put("validationErrors", new ArrayList<String>());

        return freeMarkerEngine.render(new ModelAndView(model, "view/show.html"));
    }
}

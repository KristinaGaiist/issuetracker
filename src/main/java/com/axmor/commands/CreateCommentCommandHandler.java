package com.axmor.commands;

import com.axmor.ApplicationConstants;
import com.axmor.RedirectService;
import com.axmor.exceptions.DataConnectionException;
import com.axmor.helpers.ArgumentHelper;
import com.axmor.models.Issue;
import com.axmor.models.Status;
import com.axmor.service.interfaces.ICommentService;
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

public class CreateCommentCommandHandler implements Route {
    private final FreeMarkerEngine freeMarkerEngine;
    private final IIssueService issueService;
    private final IUserService userService;
    private final ICommentService commentService;
    private final RedirectService redirectService;

    public CreateCommentCommandHandler(
            FreeMarkerEngine freeMarkerEngine,
            IIssueService issueService,
            IUserService userService,
            ICommentService commentService,
            RedirectService redirectService) {
        ArgumentHelper.ensureNotNull("freeMarkerEngine", freeMarkerEngine);
        ArgumentHelper.ensureNotNull("issueService", issueService);
        ArgumentHelper.ensureNotNull("userService", userService);
        ArgumentHelper.ensureNotNull("redirectService", redirectService);
        ArgumentHelper.ensureNotNull("commentService", commentService);

        this.redirectService = redirectService;
        this.freeMarkerEngine = freeMarkerEngine;
        this.issueService = issueService;
        this.userService = userService;
        this.commentService = commentService;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        if (redirectService.redirectIfNotAuthenticate(request, response)) {
            return null;
        }

        int statusId = Integer.parseInt(request.queryParams("status"));
        int issueId = Integer.parseInt(request.queryParams("issueId"));
        String comment = request.queryParams("text");
        String userName = request.session().attribute(ApplicationConstants.SESSION_USER_NAME);
        ArgumentHelper.ensureNotNull("statusId", statusId);
        ArgumentHelper.ensureNotNull("issueId", issueId);
        ArgumentHelper.ensureNotNull("comment", comment);

        List <String> validationErrors = new ArrayList <>();
        if (!commentService.validateCreationOrUpdateComment(comment, validationErrors)) {
            Issue issue;
            List <Status> statusList;
            try {
                issue = issueService.getIssueById(issueId);
                statusList = issueService.getStatuses();
            } catch (DataConnectionException e) {
                response.redirect("/error");
                return null;
            }

            Map <String, Object> model = userService.setHidden(request);
            model.put("issue", issue);
            model.put("comments", issue.getCommentList());
            model.put("userName", userName);
            model.put("statuses", statusList);
            model.put("validationErrors", validationErrors);
            ModelAndView modelAndView = new ModelAndView(model, "view/show.html");

            return freeMarkerEngine.render(modelAndView);
        }
        try {
            commentService.createComment(issueId, statusId, userName, comment);
        } catch (DataConnectionException e) {
            response.redirect("/error");
            return null;
        }

        response.redirect(String.format("/show?issueId=%d", issueId));
        return null;
    }
}

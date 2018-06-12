package com.axmor.commands;

import com.axmor.RedirectService;
import com.axmor.exceptions.DataConnectionException;
import com.axmor.helpers.ArgumentHelper;
import com.axmor.models.Comment;
import com.axmor.models.Issue;
import com.axmor.models.Status;
import com.axmor.service.interfaces.ICommentService;
import com.axmor.service.interfaces.IIssueService;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.template.freemarker.FreeMarkerEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UpdateCommentCommandHandler implements Route {
    private final FreeMarkerEngine freeMarkerEngine;
    private final IIssueService issueService;
    private final ICommentService commentService;
    private final RedirectService redirectService;

    public UpdateCommentCommandHandler(
            FreeMarkerEngine freeMarkerEngine,
            IIssueService issueService,
            ICommentService commentService, RedirectService redirectService) {
        ArgumentHelper.ensureNotNull("freeMarkerEngine", freeMarkerEngine);
        ArgumentHelper.ensureNotNull("issueService", issueService);
        ArgumentHelper.ensureNotNull("commentService", commentService);

        this.freeMarkerEngine = freeMarkerEngine;
        this.issueService = issueService;
        this.commentService = commentService;
        this.redirectService = redirectService;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        if (redirectService.redirectIfNotAuthenticate(request, response)) {
            return null;
        }

        int commentId = Integer.parseInt(request.queryParams("commentId"));
        int status = Integer.parseInt(request.queryParams("status"));
        String commentText = request.queryParams("description");
        ArgumentHelper.ensureNotNull("commentId", commentId);
        ArgumentHelper.ensureNotNull("status", status);

        List <String> validationErrors = new ArrayList <>();
        if (!commentService.validateCreationOrUpdateComment(commentText, validationErrors)){
            HashMap <String, Object> model = new HashMap <>();
            Comment comment = commentService.getCommentById(commentId);
            List <Status> statusList = issueService.getStatuses();
            Issue issue = issueService.getIssueById(comment.getIssueId());
            model.put("comment", comment);
            model.put("issue", issue);
            model.put("statuses", statusList);
            model.put("validationErrors", validationErrors);
            ModelAndView modelAndView = new ModelAndView(model, "view/update.html");

            return freeMarkerEngine.render(modelAndView);
        }

        try {
            commentService.updateComment(commentId, status, commentText);
        } catch (DataConnectionException e) {
            response.redirect("/error");
            return null;
        }
        response.redirect("/issues");

        return null;
    }
}

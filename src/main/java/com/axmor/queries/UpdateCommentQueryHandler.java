package com.axmor.queries;

import com.axmor.RedirectService;
import com.axmor.exceptions.DataConnectionException;
import com.axmor.helpers.ArgumentHelper;
import com.axmor.models.Comment;
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
import java.util.Map;

public class UpdateCommentQueryHandler implements Route {
    private final FreeMarkerEngine freeMarkerEngine;
    private final ICommentService commentService;
    private final IIssueService issueService;
    private final RedirectService redirectService;

    public UpdateCommentQueryHandler(
            FreeMarkerEngine freeMarkerEngine,
            ICommentService commentService,
            IIssueService issueService,
            RedirectService redirectService) {
        ArgumentHelper.ensureNotNull("freeMarkerEngine", freeMarkerEngine);
        ArgumentHelper.ensureNotNull("commentService", commentService);
        ArgumentHelper.ensureNotNull("redirectService", redirectService);
        ArgumentHelper.ensureNotNull("issueService", issueService);

        this.freeMarkerEngine = freeMarkerEngine;
        this.commentService = commentService;
        this.redirectService = redirectService;
        this.issueService = issueService;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        if (redirectService.redirectIfNotAuthenticate(request, response)) {
            return null;
        }
        int commentId = Integer.parseInt(request.queryParams("commentId"));
        ArgumentHelper.ensureNotNull("commentId", commentId);

        List<Status> statusList;
        Comment comment;
        try {
            statusList = issueService.getStatuses();
            comment = commentService.getCommentById(commentId);
        } catch (DataConnectionException e) {
            response.redirect("/error");
            return null;
        }
        Map<String, Object> model = new HashMap<>();
        model.put("comment", comment);
        model.put("statuses", statusList);
        model.put("validationErrors", new ArrayList<>());

        return freeMarkerEngine.render(new ModelAndView(model, "view/updateComment.html"));
    }
}

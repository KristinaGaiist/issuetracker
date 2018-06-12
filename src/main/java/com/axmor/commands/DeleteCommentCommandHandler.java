package com.axmor.commands;

import com.axmor.exceptions.DataConnectionException;
import com.axmor.helpers.ArgumentHelper;
import com.axmor.models.Comment;
import com.axmor.service.interfaces.ICommentService;
import spark.Request;
import spark.Response;
import spark.Route;

public class DeleteCommentCommandHandler implements Route {
    private final ICommentService commentService;

    public DeleteCommentCommandHandler(ICommentService commentService) {
        ArgumentHelper.ensureNotNull("commentService", commentService);
        this.commentService = commentService;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        int commentId = Integer.parseInt(request.queryParams("commentId"));
        ArgumentHelper.ensureNotNull("commentId", commentId);

        Comment comment = commentService.getCommentById(commentId);

        try {
            commentService.deleteComment(commentId);
        } catch (DataConnectionException e) {
            response.redirect("/error");
            return null;
        }

        response.redirect(String.format("/show?issueId=%d", comment.getIssueId()));
        return null;
    }
}

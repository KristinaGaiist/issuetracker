package com.axmor.mapping;

import com.axmor.Entities;
import com.axmor.errorhelper.ErrorHelper;
import com.axmor.exceptions.DataConnectionException;
import com.axmor.models.Comment;
import com.axmor.models.Status;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CommentMapping {
    private CommentMapping() {
    }

    public static Comment map(ResultSet commentResultSet) throws DataConnectionException {
        try {
            Comment comment = new Comment();
            comment.setId(commentResultSet.getInt(Entities.ID));
            comment.setName(commentResultSet.getString(Entities.Comment.NAME));
            comment.setAuthor(commentResultSet.getString(Entities.Comment.AUTHOR));
            comment.setIssueId(commentResultSet.getInt(Entities.Comment.ISSUE_ID));
            comment.setStatusId(commentResultSet.getInt(Entities.Comment.STATUS_ID));
            comment.setDate(commentResultSet.getDate(Entities.Comment.DATE));

            Status commentStatus = new Status();
            commentStatus.setId(comment.getStatusId());
            commentStatus.setName(commentResultSet.getString(Entities.Status.NAME));
            comment.setStatus(commentStatus);

            return comment;
        } catch (SQLException e) {
            ErrorHelper.trowDateBaseConnectionOrRequestException(e);

            return null;
        }
    }
}

package com.axmor.service.interfaces;

import com.axmor.exceptions.DataConnectionException;
import com.axmor.models.Comment;

import java.util.List;

public interface ICommentService {
    Comment getCommentById(int id) throws DataConnectionException;

    void createComment(int issueId, int statusId, String author, String name) throws DataConnectionException;

    void updateComment(int id, int statusId, String name) throws DataConnectionException;

    void deleteComment(int id) throws DataConnectionException;

    boolean validateCreationOrUpdateComment(String text, List <String> validationErrors);
}

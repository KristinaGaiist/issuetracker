package com.axmor.service;

import com.axmor.DataSource;
import com.axmor.errorhelper.ErrorHelper;
import com.axmor.exceptions.DataConnectionException;
import com.axmor.helpers.ArgumentHelper;
import com.axmor.helpers.StringHelper;
import com.axmor.mapping.CommentMapping;
import com.axmor.models.Comment;
import com.axmor.service.interfaces.ICommentService;
import com.axmor.service.interfaces.ISQLRequestGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Date;
import java.util.List;

public class CommentService implements ICommentService {
    private final Logger logger;
    private final ISQLRequestGenerator requestGenerator;
    private final DataSource dataSource;

    public CommentService(ISQLRequestGenerator requestGenerator, DataSource dataSource) {
        ArgumentHelper.ensureNotNull("dataSource", dataSource);
        ArgumentHelper.ensureNotNull("requestGenerator", requestGenerator);
        this.dataSource = dataSource;
        this.requestGenerator = requestGenerator;
        logger = LoggerFactory.getLogger("IssueService");
    }

    @Override
    public Comment getCommentById(int id) throws DataConnectionException {
        String getCommentRequest = requestGenerator.generateCommentByIdRequest(id);
        Comment comment = new Comment();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(getCommentRequest);
             ResultSet commentResultSet = preparedStatement.executeQuery()) {
            while (commentResultSet.next()) {
                comment = CommentMapping.map(commentResultSet);
            }

            return comment;
        } catch (SQLException e) {
            logger.error("Comment can't be find. Check your generateCommentByIdRequest.");
            ErrorHelper.trowDateBaseConnectionOrRequestException(e);

            return null;
        }
    }

    @Override
    public void createComment(
            int issueId,
            int statusId,
            String author,
            String name)
            throws DataConnectionException {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            String getCreateCommentRequest = requestGenerator.generateCreateCommentRequest(name, author, issueId, statusId, new Date());
            statement.execute(getCreateCommentRequest);
        } catch (SQLException e) {
            logger.error("Comment can't be create. Check your generateCreateCommentRequest.");
            ErrorHelper.trowDateBaseConnectionOrRequestException(e);
        }
    }

    @Override
    public void updateComment(
            int id,
            int statusId,
            String name)
            throws DataConnectionException {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            String getUpdateCommentRequest = requestGenerator.generateUpdateCommentRequest(name, statusId, id);
            statement.execute(getUpdateCommentRequest);
        } catch (SQLException e) {
            logger.error("Comment can't be update. Check your generateUpdateCommentRequest.");
            ErrorHelper.trowDateBaseConnectionOrRequestException(e);
        }
    }

    @Override
    public void deleteComment(int id) throws DataConnectionException {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            String getDeleteCommentRequest = requestGenerator.generateDeleteCommentRequest(id);
            statement.execute(getDeleteCommentRequest);
        } catch (SQLException e) {
            logger.error("Comment can't be delete. Check your generateDeleteCommentRequest.");
            ErrorHelper.trowDateBaseConnectionOrRequestException(e);
        }
    }

    @Override
    public boolean validateCreationOrUpdateComment(
            String text,
            List<String> validationErrors) {
        if (StringHelper.isNullOrEmpty(text)) {
            validationErrors.add("Comment can't be empty.");
        }

        return validationErrors.isEmpty();
    }
}

package com.axmor.service;

import com.axmor.errorhelper.ErrorHelper;
import com.axmor.exceptions.DataConnectionException;
import com.axmor.helpers.ArgumentHelper;
import com.axmor.helpers.StringHelper;
import com.axmor.mapping.CommentMapping;
import com.axmor.models.Comment;
import com.axmor.models.ISettings;
import com.axmor.service.interfaces.ICommentService;
import com.axmor.service.interfaces.ISQLRequestGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.List;

public class CommentService implements ICommentService {
    private final Logger logger;
    private final ISQLRequestGenerator requestGenerator;
    private final ISettings settings;

    public CommentService(ISQLRequestGenerator requestGenerator, ISettings settings) {
        ArgumentHelper.ensureNotNull("settings", settings);
        ArgumentHelper.ensureNotNull("requestGenerator", requestGenerator);
        this.settings = settings;
        this.requestGenerator = requestGenerator;
        logger = LoggerFactory.getLogger("IssueService");
    }

    @Override
    public Comment getCommentById(int id) throws DataConnectionException {
        String getCommentRequest = requestGenerator.generateCommentByIdRequest(id);
        Comment comment = new Comment();
        try (ResultSet commentResultSet = DriverManager
                .getConnection(
                        settings.getDbHost(),
                        settings.getDbLogin(),
                        settings.getDbPassword())
                .createStatement()
                .executeQuery(getCommentRequest)) {
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
        try (Statement statement = DriverManager
                .getConnection(
                        settings.getDbHost(),
                        settings.getDbLogin(),
                        settings.getDbPassword())
                .createStatement()) {
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
        try (Statement statement = DriverManager
                .getConnection(
                        settings.getDbHost(),
                        settings.getDbLogin(),
                        settings.getDbPassword())
                .createStatement()) {
            String getUpdateCommentRequest = requestGenerator.generateUpdateCommentRequest(name, statusId, id);
            statement.execute(getUpdateCommentRequest);
        } catch (SQLException e) {
            logger.error("Comment can't be update. Check your generateUpdateCommentRequest.");
            ErrorHelper.trowDateBaseConnectionOrRequestException(e);
        }
    }

    @Override
    public void deleteComment(int id) throws DataConnectionException {
        try (Statement statement = DriverManager
                .getConnection(
                        settings.getDbHost(),
                        settings.getDbLogin(),
                        settings.getDbPassword())
                .createStatement()) {
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

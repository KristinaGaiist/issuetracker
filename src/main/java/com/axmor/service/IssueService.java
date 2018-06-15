package com.axmor.service;

import com.axmor.DataSource;
import com.axmor.Entities;
import com.axmor.errorhelper.ErrorHelper;
import com.axmor.exceptions.DataConnectionException;
import com.axmor.helpers.ArgumentHelper;
import com.axmor.helpers.StringHelper;
import com.axmor.mapping.CommentMapping;
import com.axmor.mapping.IssueMapping;
import com.axmor.mapping.StatusMapping;
import com.axmor.models.Comment;
import com.axmor.models.Issue;
import com.axmor.models.Status;
import com.axmor.service.interfaces.IIssueService;
import com.axmor.service.interfaces.ISQLRequestGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class IssueService implements IIssueService {
    private final Logger logger;
    private final ISQLRequestGenerator requestGenerator;
    private final DataSource dataSource;

    public IssueService(ISQLRequestGenerator requestGenerator, DataSource dataSource) {
        ArgumentHelper.ensureNotNull("dataSource", dataSource);
        this.dataSource = dataSource;
        ArgumentHelper.ensureNotNull("requestGenerator", requestGenerator);
        this.requestGenerator = requestGenerator;
        logger = LoggerFactory.getLogger("IssueService");
    }

    @Override
    public int getPageCount(String searchName) throws DataConnectionException {
        String getIssuesRequest = requestGenerator.generateSelectIssuesCountRequest(searchName);
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(getIssuesRequest);
             ResultSet issueCountResultSet = preparedStatement.executeQuery()) {
            if (issueCountResultSet.next()) {
                return (int) Math.ceil((double) issueCountResultSet.getInt(
                        Entities.Issue.COUNT) / (double) Entities.Issue.PAGE_ITEM_COUNT);
            } else {
                ErrorHelper.trowDateBaseConnectionOrRequestException();
                return 0;
            }
        } catch (SQLException e) {
            logger.error("Page count can't be find. Check your generateSelectIssuesCountRequest");
            ErrorHelper.trowDateBaseConnectionOrRequestException(e);

            return 0;
        }
    }

    @Override
    public List <Issue> getIssues(
            String searchName,
            int pageNumber,
            String sortValue)
            throws DataConnectionException {
        List <Issue> issueList = new ArrayList <>();
        int startIndex = (pageNumber - 1) * Entities.Issue.PAGE_ITEM_COUNT;
        String getIssuesRequest = requestGenerator.generateSelectAllIssuesRequest(
                sortValue,
                searchName,
                startIndex,
                Entities.Issue.PAGE_ITEM_COUNT);
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(getIssuesRequest);
             ResultSet issueResultSet = preparedStatement.executeQuery()) {
            while (issueResultSet.next()) {
                Issue issue = IssueMapping.map(issueResultSet);
                issueList.add(issue);
            }
        } catch (SQLException e) {
            logger.error("Issues can't be find. Check your generateSelectAllIssuesRequest.");
            ErrorHelper.trowDateBaseConnectionOrRequestException(e);

            return new ArrayList <>();
        }

        return issueList;
    }

    @Override
    public Issue getIssueById(int id) throws DataConnectionException {
        Issue issue = new Issue();
        String getIssuesRequest = requestGenerator.generateIssueByIdRequest(id);
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(getIssuesRequest);
             ResultSet issueResultSet = preparedStatement.executeQuery()) {
            while (issueResultSet.next()) {
                issue = IssueMapping.map(issueResultSet);
                setComment(id, issue);
            }

            return issue;
        } catch (SQLException e) {
            logger.error("Issue can't be find. Check your generateIssueByIdRequest.");
            ErrorHelper.trowDateBaseConnectionOrRequestException(e);

            return null;
        }
    }

    private void setComment(
            int id,
            Issue issue)
            throws DataConnectionException {
        String getCommentsRequest = requestGenerator.generateSelectWithCommentsRequest(id);
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(getCommentsRequest);
             ResultSet commentResultSet = preparedStatement.executeQuery()) {
            while (commentResultSet.next()) {
                Comment comment = CommentMapping.map(commentResultSet);
                issue.getCommentList().add(comment);
            }
        } catch (SQLException e) {
            logger.error("Comments can't be find. Check your generateSelectWithCommentsRequest.");
            ErrorHelper.trowDateBaseConnectionOrRequestException(e);
        }
    }

    @Override
    public List <Status> getStatuses() throws DataConnectionException {
        List <Status> statusList = new ArrayList <>();
        String getStatusesRequest = requestGenerator.generateSelectStatusesRequest();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(getStatusesRequest);
             ResultSet statusResultSet = preparedStatement.executeQuery()) {
            while (statusResultSet.next()) {
                Status status = StatusMapping.map(statusResultSet, Entities.ID);
                statusList.add(status);
            }
        } catch (SQLException e) {
            logger.error("Statuses can't be found. Check your generateSelectStatusesRequest.");
            ErrorHelper.trowDateBaseConnectionOrRequestException(e);
        }

        return statusList;
    }

    @Override
    public void create(
            String name,
            String author,
            String description)
            throws DataConnectionException {
        String request = requestGenerator.generateCreateIssueRequest(
                name,
                author,
                description,
                new Date(),
                1);
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(request);
        } catch (SQLException e) {
            logger.error("Issue can't be create. Check your generateCreateIssueRequest.");
            ErrorHelper.trowDateBaseConnectionOrRequestException(e);
        }
    }

    @Override
    public void update(
            int id,
            String name,
            String description,
            int status)
            throws DataConnectionException {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();) {
            String getUpdateIssuesRequest = requestGenerator.generateUpdateIssueRequest(name, description, status, id);
            statement.execute(getUpdateIssuesRequest);
        } catch (SQLException e) {
            logger.error("Issue can't be update. Check your generateUpdateIssueRequest.");
            ErrorHelper.trowDateBaseConnectionOrRequestException(e);
        }
    }

    @Override
    public void delete(int id) throws DataConnectionException {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            String getDeleteIssueRequest = requestGenerator.generateDeleteIssueRequest(id);
            statement.execute(getDeleteIssueRequest);
        } catch (SQLException e) {
            logger.error("Issue can't be delete. Check your generateDeleteIssueRequest.");
            ErrorHelper.trowDateBaseConnectionOrRequestException(e);
        }
    }

    @Override
    public boolean validateCreationOrUpdateIssue(
            String name,
            String description,
            List <String> validationErrors) {
        if (StringHelper.isNullOrEmpty(name)) {
            validationErrors.add("Name can't be empty.");
        }
        if (StringHelper.isNullOrEmpty(description)) {
            validationErrors.add("Description can't be empty.");
        }

        return validationErrors.isEmpty();
    }
}

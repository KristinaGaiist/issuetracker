package com.axmor.service;


import com.axmor.service.interfaces.ISQLRequestGenerator;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SQLRequestGenerator implements ISQLRequestGenerator {
    private static final String DATE_FORMAT_STRING = "yyyy-MM-dd";
    private static final String CREATE_ISSUE_REQUEST_TEMPLATE =
            "INSERT INTO " +
                    "issues (name, author, description, date, status_id) " +
                    "VALUES('%s', '%s', '%s', '%s', %d)";
    private static final String SELECT_FROM_ISSUES_AND_STATUSES_PAGE = "SELECT " +
            "issues.id, " +
            "issues.name, " +
            "issues.description, " +
            "issues.date, " +
            "issues.author, " +
            "issues.status_id, " +
            "statuses.status " +
            "FROM issues " +
            "JOIN statuses ON statuses.id = issues.status_id " +
            "ORDER BY issues.%s " +
            "LIMIT %d,%d";
    private static final String SELECT_FOUND_ISSUES_PAGE = "SELECT " +
            "issues.id, " +
            "issues.name, " +
            "issues.description, " +
            "issues.date, " +
            "issues.author, " +
            "issues.status_id, " +
            "statuses.status " +
            "FROM issues " +
            "JOIN statuses ON statuses.id = issues.status_id " +
            "where name = '%s' " +
            "ORDER BY issues.id " +
            "LIMIT %d";
    private static final String SELECT_ISSUE_BY_ID = "SELECT " +
            "issues.id, " +
            "issues.name, " +
            "issues.description, " +
            "issues.date, " +
            "issues.author, " +
            "issues.status_id, " +
            "statuses.status " +
            "FROM issues " +
            "JOIN statuses ON statuses.id = issues.status_id " +
            "WHERE issues.id = %d";
    private static final String SELECT_ISSUE_WITH_COMMENTS = "SELECT " +
            "comments.id, " +
            "comments.name, " +
            "comments.author, " +
            "comments.issue_id, " +
            "comments.status_id, " +
            "statuses.status, " +
            "comments.date " +
            "FROM comments " +
            "JOIN statuses ON statuses.id = comments.status_id " +
            "WHERE comments.issue_id = %d";
    private static final String GET_COMMENT_BY_ID = "SELECT " +
            "comments.id, " +
            "comments.name, " +
            "comments.author, " +
            "comments.status_id, " +
            "comments.issue_id, " +
            "comments.date, " +
            "statuses.status " +
            "FROM comments " +
            "JOIN statuses on statuses.id = comments.status_id " +
            "WHERE comments.id = %d";
    private static final String SELECT_FROM_STATUSES = "SELECT *FROM statuses";
    private static final String SELECT_ISSUES_COUNT = "SELECT COUNT(issues.id) as count from issues";
    private static final String UPDATE_ISSUE = "UPDATE issues SET name='%s', description = '%s', status_id = %d WHERE id = %d";
    private static final String UPDATE_COMMENT= "UPDATE comments SET name='%s', status_id = %d WHERE id = %d";
    private static final String DELETE_ISSUE = "DELETE FROM issues WHERE id = %d";
    private static final String DELETE_COMMENT = "DELETE FROM comments WHERE id = %d";
    private static final String SELECT_USER_LOGIN_EXIST = "SELECT * FROM users where name = '%s'";
    private static final String SELECT_USER_EXIST = "SELECT * FROM users where name = '%s' and password = '%s'";
    private static final String INSERT_USER = "INSERT INTO users(name, password, access_right_id) VALUES('%s', '%s', %d,)";
    private static final String SELECT_USER_BY_LOGIN = "SELECT * FROM users where name = '%s'";

    private final java.text.DateFormat dateFormat;

    public SQLRequestGenerator() {
        dateFormat = new SimpleDateFormat(DATE_FORMAT_STRING);
    }

    @Override
    public String generateCreateIssueRequest(
            String name,
            String author,
            String description,
            Date creationDate,
            int statusId) {
        return String.format(
                CREATE_ISSUE_REQUEST_TEMPLATE,
                name,
                author,
                description,
                dateFormat.format(creationDate),
                statusId);
    }

    @Override
    public String generateSelectAllIssuesRequest(
            String orderValue,
            int startIndex,
            int endIndex) {
        return String.format(SELECT_FROM_ISSUES_AND_STATUSES_PAGE, orderValue, startIndex, endIndex);
    }

    @Override
    public String generateSelectFoundIssuesRequest(
            String name,
            int maxCount) {
        return String.format(SELECT_FOUND_ISSUES_PAGE, name, maxCount);
    }

    @Override
    public String generateIssueByIdRequest(int id) {
        return String.format(SELECT_ISSUE_BY_ID, id);
    }

    @Override
    public String generateCommentByIdRequest(int id) {
        return String.format(GET_COMMENT_BY_ID, id);
    }

    @Override
    public String generateSelectWithCommentsRequest(int id) {
        return String.format(SELECT_ISSUE_WITH_COMMENTS, id);
    }

    @Override
    public String generateSelectStatusesRequest() {
        return SELECT_FROM_STATUSES;
    }

    @Override
    public String generateSelectIssuesCountRequest() {
        return SELECT_ISSUES_COUNT;
    }

    @Override
    public String generateUpdateIssueRequest(
            String name,
            String description,
            int status,
            int id) {
        return String.format(UPDATE_ISSUE, name, description, status, id);
    }

    @Override
    public String generateUpdateCommentRequest (
            String name,
            int statusId,
            int id) {
        return String.format(UPDATE_COMMENT, name, statusId, id);
    }

    @Override
    public String generateDeleteIssueRequest(int id) {
        return String.format(DELETE_ISSUE, id);
    }

    @Override
    public String generateDeleteCommentRequest(int id) {
        return String.format(DELETE_COMMENT, id);
    }

    @Override
    public String generateCreateCommentRequest(
            String name,
            String author,
            int issueId,
            int statusId,
            Date creationDate) {
        return String.format("INSERT INTO comments(name, author, issue_id, status_id, date) VALUES('%s', '%s', %d, %d, '%s')", name, author, issueId, statusId, dateFormat.format(creationDate));
    }

    @Override
    public String generateUserLoginExistRequest(String login) {
        return String.format(SELECT_USER_LOGIN_EXIST, login);
    }

    @Override
    public String generateUserExistRequest(
            String login,
            String password) {
        return String.format(SELECT_USER_EXIST, login, password);
    }

    @Override
    public String generateCreateUserRequest(
            String login, String password,
            int accessRight) {
        return String.format(INSERT_USER, login, password, accessRight);
    }

    @Override
    public String generateSelectUserByLoginRequest(String login) {
        return String.format(SELECT_USER_BY_LOGIN, login);
    }
}

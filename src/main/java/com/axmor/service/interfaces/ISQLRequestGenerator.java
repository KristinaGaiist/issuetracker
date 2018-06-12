package com.axmor.service.interfaces;

import java.util.Date;

public interface ISQLRequestGenerator {
    String generateSelectAllIssuesRequest(String orderValue, String searchName, int startIndex, int endIndex);

//    String generateSelectFoundIssuesRequest(String name, int maxCount);

    String generateIssueByIdRequest(int id);

    String generateCommentByIdRequest(int id);

    String generateSelectWithCommentsRequest(int id);

    String generateSelectStatusesRequest();

    String generateSelectIssuesCountRequest(String searchName);

    String generateCreateIssueRequest(String name, String author, String description, Date creationDate, int statusId);

    String generateCreateCommentRequest(String name, String author, int issueId, int statusId, Date creationDate);

    String generateUpdateIssueRequest(String name, String description, int status, int id);

    String generateUpdateCommentRequest(String name, int statusId, int id);

    String generateDeleteIssueRequest(int id);

    String generateDeleteCommentRequest(int id);

    String generateUserLoginExistRequest(String login);

    String generateUserExistRequest(String login, String password);

    String generateCreateUserRequest(String login, String password, int accessRight);

    String generateSelectUserByLoginRequest(String login);
}

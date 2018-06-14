package com.axmor.service.interfaces;

import com.axmor.exceptions.DataConnectionException;
import com.axmor.models.Issue;
import com.axmor.models.Status;

import java.sql.SQLException;
import java.util.List;

public interface IIssueService {
    int getPageCount(String searchName) throws DataConnectionException, SQLException;

    List<Issue> getIssues(String searchName, int pageNumber, String sortValue) throws DataConnectionException, SQLException;

    Issue getIssueById(int id) throws DataConnectionException;

    List <Status> getStatuses() throws DataConnectionException;

    void create(String name, String author, String description) throws DataConnectionException;

    void update(int id, String name, String description, int status) throws DataConnectionException;

    void delete(int id) throws DataConnectionException;

    boolean validateCreationOrUpdateIssue(String name, String description, List <String> validationErrors);
}

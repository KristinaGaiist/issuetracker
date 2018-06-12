package com.axmor.mapping;


import com.axmor.Entities;
import com.axmor.errorhelper.ErrorHelper;
import com.axmor.exceptions.DataConnectionException;
import com.axmor.models.Issue;
import com.axmor.models.Status;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class IssueMapping {
    private IssueMapping() {
    }

    public static Issue map(ResultSet issueResultSet) throws DataConnectionException {
        try {
            Issue issue = new Issue();
            issue.setId(issueResultSet.getInt(Entities.ID));
            issue.setName(issueResultSet.getString(Entities.Issue.NAME));
            issue.setDescription(issueResultSet.getString(Entities.Issue.DESCRIPTION));
            issue.setDate(issueResultSet.getDate(Entities.Issue.DATE));
            issue.setAuthor(issueResultSet.getString(Entities.Issue.AUTHOR));
            issue.setCommentList(new ArrayList <>());

            Status status = StatusMapping.map(issueResultSet, Entities.Issue.STATUS_ID);
            issue.setStatus(status);

            return issue;
        }catch (SQLException e) {
            ErrorHelper.trowDateBaseConnectionOrRequestException(e);

            return null;
        }
    }
}

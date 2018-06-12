package com.axmor.mapping;

import com.axmor.Entities;
import com.axmor.errorhelper.ErrorHelper;
import com.axmor.exceptions.DataConnectionException;
import com.axmor.models.Status;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StatusMapping {
    private StatusMapping() {
    }

    public static Status map(ResultSet statusResultSet, String idType) throws DataConnectionException {
        Status status = new Status();
        try {
            status.setId(statusResultSet.getInt(idType));
            status.setName(statusResultSet.getString(Entities.Status.NAME));

            return status;
        } catch (SQLException e) {
            ErrorHelper.trowDateBaseConnectionOrRequestException(e);

            return null;
        }
    }
}

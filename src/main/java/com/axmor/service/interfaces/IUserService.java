package com.axmor.service.interfaces;

import com.axmor.exceptions.DataConnectionException;
import spark.Request;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface IUserService {
    boolean validateRegisterUser(String name, String password, List <String> validationErrors) throws DataConnectionException, SQLException;

    boolean validateLogInUser(String name, String password, List <String> validationErrors) throws DataConnectionException, SQLException;

    void createUser(String login, String password) throws DataConnectionException;

    boolean isAuthenticated(Request request);

    Map<String, Object> setHidden(Request request) throws DataConnectionException;


}

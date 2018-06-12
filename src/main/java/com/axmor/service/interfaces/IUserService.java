package com.axmor.service.interfaces;

import com.axmor.exceptions.DataConnectionException;
import spark.Request;

import java.util.List;
import java.util.Map;

public interface IUserService {
    boolean validateRegisterUser(String name, String password, List <String> validationErrors) throws DataConnectionException;

    boolean validateLogInUser(String name, String password, List <String> validationErrors) throws DataConnectionException;

    void createUser(String login, String password) throws DataConnectionException;

    boolean isAuthenticated(Request request);

    Map<String, Object> setHidden(Request request) throws DataConnectionException;


}

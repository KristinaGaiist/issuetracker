package com.axmor.service;

import com.axmor.ApplicationConstants;
import com.axmor.errorhelper.ErrorHelper;
import com.axmor.exceptions.DataConnectionException;
import com.axmor.helpers.ArgumentHelper;
import com.axmor.helpers.StringHelper;
import com.axmor.models.ISettings;
import com.axmor.server.AccessRigts;
import com.axmor.service.interfaces.ISQLRequestGenerator;
import com.axmor.service.interfaces.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserService implements IUserService {
    private final ISQLRequestGenerator requestGenerator = new SQLRequestGenerator();
    private final Logger logger;
    private final ISettings settings;

    public UserService(ISettings settings) {
        ArgumentHelper.ensureNotNull("settings", settings);
        this.settings = settings;
        logger = LoggerFactory.getLogger("UserService");
    }

    private boolean isLoginExist(String login) throws DataConnectionException {
        try (ResultSet hasLoginResult = DriverManager
                .getConnection(
                        settings.getDbHost(),
                        settings.getDbLogin(),
                        settings.getDbPassword())
                .createStatement()
                .executeQuery(requestGenerator.generateUserLoginExistRequest(login))) {

            return hasLoginResult.next();
        } catch (SQLException e) {
            logger.error("User can't be found. Check your generateUserLoginExistRequest.");
            ErrorHelper.trowDateBaseConnectionOrRequestException(e);

            return false;
        }
    }

    @Override
    public boolean validateRegisterUser(
            String name,
            String password,
            List <String> validationErrors)
            throws DataConnectionException {
        boolean userExist = isLoginExist(name);
        if (StringHelper.isNullOrEmpty(name)) {
            validationErrors.add("Name can't be empty.");
        }
        if (userExist) {
            validationErrors.add("Name exist.");
        }
        if (StringHelper.isNullOrEmpty(password)) {
            validationErrors.add("Password can't be empty.");
        }

        return validationErrors.isEmpty();
    }

    @Override
    public boolean validateLogInUser(
            String name,
            String password,
            List <String> validationErrors)
            throws DataConnectionException {
        boolean userExist = isLoginExist(name);
        if (StringHelper.isNullOrEmpty(name)) {
            validationErrors.add("Name can't be empty.");
        } else if (userExist) {
            if (!isUserExist(name, password)) {
                validationErrors.add("Wrong password.");
            }
        } else {
            validationErrors.add("Wrong login.");
        }
        if (StringHelper.isNullOrEmpty(password)) {
            validationErrors.add("Password can't be empty.");
        }

        return validationErrors.isEmpty();
    }

    private boolean isUserExist(
            String login,
            String password)
            throws DataConnectionException {
        try (ResultSet hasUserResult = DriverManager
                .getConnection(
                        settings.getDbHost(),
                        settings.getDbLogin(),
                        settings.getDbPassword())
                .createStatement()
                .executeQuery(requestGenerator.generateUserExistRequest(login, password))) {
            return hasUserResult.next();
        } catch (SQLException e) {
            logger.error("User can't be found. Check your generateUserExistRequest.");
            ErrorHelper.trowDateBaseConnectionOrRequestException(e);

            return false;
        }
    }

    @Override
    public void createUser(
            String login,
            String password)
            throws DataConnectionException {
        try (Statement statement = DriverManager
                .getConnection(
                        settings.getDbHost(),
                        settings.getDbLogin(),
                        settings.getDbPassword())
                .createStatement()) {
            statement.execute(requestGenerator.generateCreateUserRequest(login, password, 3));
        } catch (SQLException e) {
            logger.error("User can't be create. Check your generateCreateUserRequest.");
            ErrorHelper.trowDateBaseConnectionOrRequestException(e);
        }
    }

    @Override
    public boolean isAuthenticated(Request request) {
        String name = request.session().attribute(ApplicationConstants.SESSION_USER_NAME);
        return !StringHelper.isNullOrEmpty(name);
    }

    @Override
    public Map <String, Object> setHidden(Request request) throws DataConnectionException {
        String login = request.session().attribute(ApplicationConstants.SESSION_USER_NAME);
        int accessRight = getUserAccessRight(login);
        String hiddenDelete = "hiddenDelete";
        String hiddenUpdate = "hiddenUpdate";
        Map <String, Object> map = new HashMap <>();
        if (accessRight == AccessRigts.VIEW_ONLY.ordinal() + 1) {
            map.put(ApplicationConstants.HIDDEN, ApplicationConstants.HIDDEN);
            map.put(hiddenDelete, ApplicationConstants.HIDDEN);
            map.put(hiddenUpdate, ApplicationConstants.HIDDEN);
        } else if (accessRight == AccessRigts.ADD_ISSUESS.ordinal() + 1) {
            map.put(ApplicationConstants.HIDDEN, ApplicationConstants.EMPTY);
            map.put(hiddenUpdate, ApplicationConstants.EMPTY);
            map.put(hiddenDelete, ApplicationConstants.HIDDEN);
        } else if (accessRight == AccessRigts.FULL.ordinal() + 1) {
            map.put(ApplicationConstants.HIDDEN, ApplicationConstants.EMPTY);
            map.put(hiddenDelete, ApplicationConstants.EMPTY);
            map.put(hiddenUpdate, ApplicationConstants.EMPTY);
        }

        return map;
    }

    private int getUserAccessRight(String login) throws DataConnectionException {
        int accessRight = 0;
        try (ResultSet userAccessRightResult = DriverManager
                .getConnection(
                        settings.getDbHost(),
                        settings.getDbLogin(),
                        settings.getDbPassword())
                .createStatement()
                .executeQuery(requestGenerator.generateSelectUserByLoginRequest(login))) {
            while (userAccessRightResult.next()) {
                accessRight = userAccessRightResult.getInt("access_right_id");
            }
        } catch (SQLException e) {
            logger.error("User's access right can't be found. Check your generateSelectUserByLoginRequest.");
            ErrorHelper.trowDateBaseConnectionOrRequestException(e);
        }

        return accessRight;
    }
}

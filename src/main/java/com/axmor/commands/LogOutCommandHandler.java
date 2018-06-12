package com.axmor.commands;

import com.axmor.ApplicationConstants;
import spark.Request;
import spark.Response;
import spark.Route;

public class LogOutCommandHandler implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
        request.session().attribute(ApplicationConstants.SESSION_USER_NAME, null);
        response.redirect("/");

        return null;
    }
}

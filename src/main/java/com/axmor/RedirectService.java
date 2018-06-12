package com.axmor;

import com.axmor.helpers.ArgumentHelper;
import com.axmor.service.interfaces.IUserService;
import spark.Request;
import spark.Response;

public class RedirectService {
    private final IUserService userService;

    public RedirectService(IUserService userService) {
        ArgumentHelper.ensureNotNull("userService", userService);

        this.userService = userService;
    }

    public boolean redirectIfNotAuthenticate(Request request, Response response) {
        if (!userService.isAuthenticated(request)) {
            response.redirect("/");
            return true;
        }

        return false;
    }
}
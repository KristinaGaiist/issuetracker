package com.axmor.commands;

import com.axmor.ApplicationConstants;
import com.axmor.helpers.ArgumentHelper;
import com.axmor.service.interfaces.IUserService;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.template.freemarker.FreeMarkerEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogInCommandHandler implements Route {
    private final FreeMarkerEngine freeMarkerEngine;
    private final IUserService userService;

    public LogInCommandHandler(
            FreeMarkerEngine freeMarkerEngine,
            IUserService userService) {
        ArgumentHelper.ensureNotNull("freeMarkerEngine", freeMarkerEngine);
        ArgumentHelper.ensureNotNull("userService", userService);

        this.freeMarkerEngine = freeMarkerEngine;
        this.userService = userService;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        String userName = request.queryParams("login");
        String password = request.queryParams("password");
        ArgumentHelper.ensureNotNull("userName", userName);
        ArgumentHelper.ensureNotNull("password", password);

        List <String> validationErrors = new ArrayList <>();
        if (!userService.validateLogInUser(userName, password, validationErrors)) {
            Map<String, Object> model = new HashMap<>();
            model.put("validationErrors", validationErrors);
            model.put("register", true);
            ModelAndView modelAndView = new ModelAndView(model, "view/index.html");

            return freeMarkerEngine.render(modelAndView);
        }
        request.session().attribute(ApplicationConstants.SESSION_USER_NAME, userName);
        response.redirect("/issues");

        return null;
    }
}

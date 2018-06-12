package com.axmor.queries;

import com.axmor.helpers.ArgumentHelper;
import com.axmor.service.interfaces.IUserService;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.template.freemarker.FreeMarkerEngine;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LogInQueryHandler implements Route {
    private final FreeMarkerEngine freeMarkerEngine;
    private final IUserService userService;

    public LogInQueryHandler(
            FreeMarkerEngine freeMarkerEngine,
            IUserService userService) {
        ArgumentHelper.ensureNotNull("freeMarkerEngine", freeMarkerEngine);
        ArgumentHelper.ensureNotNull("userService", userService);

        this.freeMarkerEngine = freeMarkerEngine;
        this.userService = userService;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        if (!userService.isAuthenticated(request)) {
            Map <String, Object> model = new HashMap <>();
            model.put("validationErrors", new ArrayList <>());
            model.put("register", true);
            ModelAndView modelAndView = new ModelAndView(model, "view/index.html");

            return freeMarkerEngine.render(modelAndView);
        }
        response.redirect("/issues");
        return null;
    }
}

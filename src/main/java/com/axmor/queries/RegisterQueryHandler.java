package com.axmor.queries;

import com.axmor.helpers.ArgumentHelper;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.template.freemarker.FreeMarkerEngine;

import java.util.ArrayList;
import java.util.HashMap;

public class RegisterQueryHandler implements Route {
    private final FreeMarkerEngine freeMarkerEngine;

    public RegisterQueryHandler(FreeMarkerEngine freeMarkerEngine) {
        ArgumentHelper.ensureNotNull("freeMarkerEngine", freeMarkerEngine);

        this.freeMarkerEngine = freeMarkerEngine;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        HashMap <String, Object> model = new HashMap <>();
        model.put("validationErrors", new ArrayList <String>());
        model.put("register", false);
        ModelAndView modelAndView = new ModelAndView(model, "view/index.html");

        return freeMarkerEngine.render(modelAndView);
    }
}

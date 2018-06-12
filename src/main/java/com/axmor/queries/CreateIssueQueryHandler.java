package com.axmor.queries;

import com.axmor.ApplicationConstants;
import com.axmor.RedirectService;
import com.axmor.helpers.ArgumentHelper;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.template.freemarker.FreeMarkerEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CreateIssueQueryHandler implements Route {
    private final FreeMarkerEngine freeMarkerEngine;
    private final RedirectService redirectService;

    public CreateIssueQueryHandler(
            FreeMarkerEngine freeMarkerEngine, RedirectService redirectService) {
        this.redirectService = redirectService;
        ArgumentHelper.ensureNotNull("freeMarkerEngine", freeMarkerEngine);
        ArgumentHelper.ensureNotNull("redirectService", redirectService);

        this.freeMarkerEngine = freeMarkerEngine;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        if (redirectService.redirectIfNotAuthenticate(request, response)) {
            return null;
        }

        Map <String, Object> model = new HashMap <>();
        model.put("validationErrors", new ArrayList <String>());
        model.put("issueName", ApplicationConstants.EMPTY);
        model.put("description", ApplicationConstants.EMPTY);

        return freeMarkerEngine.render(new ModelAndView(model, "view/create.html"));
    }
}

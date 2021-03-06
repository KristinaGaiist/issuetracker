package com.axmor.server;

import com.axmor.ApplicationConstants;
import com.axmor.DataSource;
import com.axmor.Main;
import com.axmor.RedirectService;
import com.axmor.commands.*;
import com.axmor.queries.*;
import com.axmor.service.CommentService;
import com.axmor.service.IssueService;
import com.axmor.service.SQLRequestGenerator;
import com.axmor.service.UserService;
import com.axmor.service.interfaces.ICommentService;
import com.axmor.service.interfaces.IIssueService;
import com.axmor.service.interfaces.IUserService;
import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import spark.template.freemarker.FreeMarkerEngine;

import static spark.Spark.*;

public class Controller {
    private final FreeMarkerEngine freeMarkerEngine;
    private final IIssueService issueService;
    private final IUserService userService;
    private final RedirectService redirectService;
    private final ICommentService commentService;

    public Controller(DataSource dataSource) {
        port(80);
        Configuration freeMarkerConfiguration = new Configuration(Configuration.VERSION_2_3_23);
        ClassTemplateLoader templateLoader = new ClassTemplateLoader(Main.class, "/");
        freeMarkerConfiguration.setTemplateLoader(templateLoader);

        freeMarkerEngine = new FreeMarkerEngine(freeMarkerConfiguration);
        SQLRequestGenerator requestGenerator = new SQLRequestGenerator();
        issueService = new IssueService(requestGenerator, dataSource);
        userService = new UserService(dataSource);
        redirectService = new RedirectService(userService);
        commentService = new CommentService(requestGenerator, dataSource);
    }

    public void register() {
        staticFiles.location("/staticFiles");
        get("/", new LogInQueryHandler(freeMarkerEngine, userService));
        get("/register", new RegisterQueryHandler(freeMarkerEngine));
        get("/issues", new IssuesQueryHandler(freeMarkerEngine, issueService, userService, redirectService));
        get("/show", new IssueQueryHandler(freeMarkerEngine, issueService, userService, redirectService));
        get("/create", new CreateIssueQueryHandler(freeMarkerEngine, redirectService));
        get("/update", new UpdateIssueQueryHandler(freeMarkerEngine, issueService, redirectService));
        get("/updateComment", new UpdateCommentQueryHandler(freeMarkerEngine, commentService, issueService, redirectService));
        get("/error", (req, res) -> ApplicationConstants.SERVER_FAIL);
        get("/deleteComment", new DeleteCommentCommandHandler(commentService));

        post("/registerIssues", new RegisterCommandHandler(freeMarkerEngine, userService));
        post("/issues", new LogInCommandHandler(freeMarkerEngine, userService));
        post("/create", new CreateIssueCommandHandler(freeMarkerEngine, issueService, redirectService));
        post("/update", new UpdateIssueCommandHandler(freeMarkerEngine, issueService, redirectService));
        post("/updateComment", new UpdateCommentCommandHandler(freeMarkerEngine, issueService, commentService,redirectService));
        post("/delete", new DeleteIssueCommandHandler(issueService));
        post("/createComment", new CreateCommentCommandHandler(freeMarkerEngine, issueService, userService, commentService, redirectService));
        post("/out", new LogOutCommandHandler());
    }
}

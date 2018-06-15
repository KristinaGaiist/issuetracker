package com.axmor;

public class Entities {
    public static final String ID = "id";

    public static class Issue {
        public static final String NAME = "name";
        public static final String DESCRIPTION = "description";
        public static final String DATE ="date";
        public static final String AUTHOR = "author";
        public static final String STATUS_ID = "status_id";
        public static final String COUNT = "count";
        public static final int PAGE_ITEM_COUNT = 7;
    }

    public static class Comment {
        public static final String NAME = "name";
        public static final String DATE ="date";
        public static final String AUTHOR = "author";
        public static final String STATUS_ID = "status_id";
        public static final String ISSUE_ID = "issue_id";
    }

    public static class Status {
        public static final String NAME = "status";
    }
}

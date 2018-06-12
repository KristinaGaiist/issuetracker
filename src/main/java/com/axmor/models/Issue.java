package com.axmor.models;

import java.util.Date;
import java.util.List;

public class Issue {
    private int id;
    private String name;
    private String description;
    private Status status;
    private Date date;
    private List<Comment> commentList;
    private String author;

    public int getStatusId() {
        return statusId;
    }

    private int statusId;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List <Comment> getCommentList() {
        return commentList;
    }

    public void setCommentList(List <Comment> commentList) {
        this.commentList = commentList;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }
}

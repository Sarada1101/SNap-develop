package com.example.snap_develop.bean;

import java.io.Serializable;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PostBean implements Serializable {
    private String message;
    private String picture;
    private double lat;
    private double lon;
    private Date datetime;
    private boolean danger;
    private boolean anonymous;
    private String path;
    private String uid;
    private String type;
    private String parentPost;
}

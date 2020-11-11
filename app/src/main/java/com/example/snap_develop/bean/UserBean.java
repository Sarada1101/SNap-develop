package com.example.snap_develop.bean;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserBean implements Serializable {
    private String uid;
    private String email;
    private String password;
    private String name;
    private String message;
    private String icon;
    private boolean followNotice;
    private boolean goodNotice;
    private boolean commentNotice;
    private String publicationArea;
}

package com.acando.todohelper.internal;

import java.io.Serializable;

public class ToDo implements Serializable {
    public int id;
    public String title, text;
    public long lastModifyDate;
}

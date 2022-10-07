package com.techflux.oyebhangarwala.dataModel;

/**
 * Created by Lenovo on 04/05/2017.
 */
public class CommentModel {
    String user_Id;
    String user_Name;
    String user_Comment;
    String user_Date;
    String user_Image;

    public CommentModel(String user_Id, String user_Name, String user_Comment, String user_Date, String user_Image) {
        this.user_Id = user_Id;
        this.user_Name = user_Name;
        this.user_Comment = user_Comment;
        this.user_Date = user_Date;
        this.user_Image = user_Image;
    }

    public String getUser_Id() {
        return user_Id;
    }

    public void setUser_Id(String user_Id) {
        this.user_Id = user_Id;
    }

    public String getUser_Image() {
        return user_Image;
    }

    public void setUser_Image(String user_Image) {
        this.user_Image = user_Image;
    }

    public String getUser_Comment() {
        return user_Comment;
    }

    public void setUser_Comment(String user_Comment) {
        this.user_Comment = user_Comment;
    }

    public String getUser_Name() {
        return user_Name;
    }

    public void setUser_Name(String user_Name) {
        this.user_Name = user_Name;
    }

    public String getUser_Date() {
        return user_Date;
    }

    public void setUser_Date(String user_Date) {
        this.user_Date = user_Date;
    }
}

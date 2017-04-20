package es.esy.shashanksingh.kmvonline;

import java.io.Serializable;

/**
 * Created by shashank on 08-Nov-16.
 */

public class collegeNotification implements Serializable {
    private int id;
    private int day;
    private int month;
    private String title;
    private int department;
    private String description;
    private String teacherName;
    public collegeNotification(int i, String h, String db, int dp, int d, int m, String t){
        id=i;
        day=d;
        month=m;
        title =h;
        department=dp;
        description=db;
        teacherName=t;
    }
    public int getId(){
        return id;
    }
    public int getDay(){
        return day;
    }
    public int getMonth(){
        return month;
    }
    public String getTitle(){
        return title;
    }
    public int getDepartment(){
        return department;
    }
    public String getDescription(){
        return description;
    }
    public String getTeacherName(){return teacherName;}
}
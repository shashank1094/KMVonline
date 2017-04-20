package es.esy.shashanksingh.kmvonline;

public class Config {
    //URL to our login.php file
    public static final String LOGIN_URL = "http://shashanksingh.esy.es/php/login.php";

    //Keys for email and password as defined in our $_POST['key'] in login.php
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";

    //If server response is equal to this that means login is successful
    public static final String LOGIN_SUCCESS = "1";

    //Keys for Sharedpreferences
    //This would be the name of our shared preferences
    public static final String SHARED_PREF_NAME_LOGIN_DETAILS = "logindetails";
    public static final String SHARED_PREF_LAST_SEARCHED_QUERY = "lastsearchedquery";
    public static final String SEARCHED_QUERY = "searchedquery";

    public static final String SHARED_PREF_NAME_CACHE = "cachedcollegenotifications";
    public static final String COLLEGE_NOTIFICATIONS_SHARED_PREF = "collegenotifications";



    //This would be used to store the username of current logged in user
    public static final String USERNAME_SHARED_PREF = "username";
    public static final String NAME_SHARED_PREF = "name";
    public static final String WORK_DEPARTMENT_SHARED_PREF = "wdepartment";


    //We will use this to store the boolean in sharedpreference to track user is loggedin or not
    public static final String LOGGEDIN_SHARED_PREF = "loggedin";
}

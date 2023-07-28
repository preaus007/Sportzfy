package com.example.sportzfy.sessions;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SessionManager {

    SharedPreferences usersSession;
    SharedPreferences.Editor editor;
    Context context;

    public static final String USER_LOGIN_SESSION = "userLoginSession";
    public static final String REMEMBER_ME_SESSION = "rememberMe";

    //user session variables
    private static final String IS_LOGIN = "IsLoggedIn";
    public static final String KEY_FULL_NAME = "fullName";
    public static final String KEY_USER_NAME = "userName";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_PHONE = "phone";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_BIRTHDAY = "birthday";

    //remember me variables
    private static final String IS_REMEMBER_ME = "IsRememberMe";
    public static final String KEY_REMEMBER_ME_EMAIL = "email";
    public static final String KEY_REMEMBER_ME_PASSWORD = "password";

    public SessionManager(Context _context, String sessionName) {
        context = _context;
        usersSession = context.getSharedPreferences( sessionName, Context.MODE_PRIVATE);
        editor = usersSession.edit();
    }

    public void createLoginSession(String fullName, String userName, String email, String address, String phone, String birthday) {
        editor.putBoolean(IS_LOGIN, true);

        editor.putString(KEY_FULL_NAME, fullName);
        editor.putString(KEY_USER_NAME, userName);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_ADDRESS, address);
        editor.putString(KEY_PHONE, phone);
        editor.putString(KEY_BIRTHDAY, birthday);

        editor.commit();
    }

    public HashMap<String, String> getUsersDetailsFromSession() {
        HashMap<String, String> userData = new HashMap<>();

        userData.put(KEY_FULL_NAME, usersSession.getString(KEY_FULL_NAME, null));
        userData.put(KEY_USER_NAME, usersSession.getString(KEY_USER_NAME, null));
        userData.put(KEY_EMAIL, usersSession.getString(KEY_EMAIL, null));
        userData.put(KEY_PASSWORD, usersSession.getString(KEY_PASSWORD, null));
        userData.put(KEY_ADDRESS, usersSession.getString(KEY_ADDRESS, null));
        userData.put(KEY_PHONE, usersSession.getString(KEY_PHONE, null));
        userData.put(KEY_BIRTHDAY, usersSession.getString(KEY_BIRTHDAY, null));

        return userData;
    }

    public boolean checkLogin() {
        if (usersSession.getBoolean(IS_LOGIN, false)) {
            return true;
        } else {
            return false;
        }
    }

    public void logoutUserFromSession() {
        editor.clear();
        editor.commit();
    }


    /* Remember me */
    public void createRememberMeSession(String email, String password) {
        editor.putBoolean(IS_REMEMBER_ME, true);

        editor.putString(KEY_REMEMBER_ME_EMAIL, email);
        editor.putString(KEY_REMEMBER_ME_PASSWORD, password);

        editor.commit();
    }

    public HashMap<String, String> getRememberMeDetailsFromSession() {
        HashMap<String, String> userData = new HashMap<String, String>();

        userData.put(KEY_REMEMBER_ME_EMAIL, usersSession.getString(KEY_REMEMBER_ME_EMAIL, null));
        userData.put(KEY_REMEMBER_ME_PASSWORD, usersSession.getString(KEY_REMEMBER_ME_PASSWORD, null));

        return userData;
    }

    public boolean checkRememberMe() {
        if (usersSession.getBoolean(IS_REMEMBER_ME, false)) {
            return true;
        } else {
            return false;
        }
    }
}

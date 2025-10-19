package com.example.chatmessenger.session;

import com.example.chatmessenger.user.entity.Admin;

public class AdminSession {
    private static Admin currentAdmin;

    public AdminSession() {}

    public static Admin getCurrentAdmin() {
        return currentAdmin;
    }

    public static void setCurrentAdmin(Admin currentAdmin) {
        AdminSession.currentAdmin = currentAdmin;
    }
}

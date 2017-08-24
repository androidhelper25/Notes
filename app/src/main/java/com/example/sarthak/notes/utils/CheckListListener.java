package com.example.sarthak.notes.utils;

public interface CheckListListener {

    void checklistEnterKeyPressed(String data, String status);

    void checklistReminderEnterKeyPressed(String data, String status);

    void checklistCheckBoxStatus(boolean b, int pos);

    void checklistReminderCheckBoxStatus(boolean b, int pos);
}

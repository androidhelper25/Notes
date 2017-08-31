package com.example.sarthak.notes.utils;

public interface CheckListListener {

    void checklistEnterKeyPressed(String data, String status, int position);

    void checklistReminderEnterKeyPressed(String data, String status, int position);

    void checklistCheckboxChecked(boolean b, int pos);

    void checklistReminderCheckboxChecked(boolean b, int pos);

    void checklistDeleteButtonPressed(int position);

    void checklistReminderDeleteButtonPressed(int position);
}

package com.example.sarthak.notes.utils;

public interface CheckListListener {

    void checklistEnterKeyPressed(String data, String status);

    void checklistReminderEnterKeyPressed(String data, String status);

    void checklistCheckboxChecked(boolean b, int pos);

    void checklistReminderCheckboxChecked(boolean b, int pos);

    void checklistDeleteButtonPressed(int position);

    void checklistReminderDeleteButtonPressed(int position);
}

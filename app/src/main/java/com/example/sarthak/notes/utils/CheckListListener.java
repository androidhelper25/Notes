package com.example.sarthak.notes.utils;

/**
 * Interface to handle various events in checklists.
 *
 * Events include enterKeyPressed for editText, checkBoxChecked for checkBox and
 * deleteButtonPressed for 'Remove' imageButton for both checklists and checklistReminders.
 */

public interface CheckListListener {

    void checklistEnterKeyPressed(String data, String status, int position);

    void checklistReminderEnterKeyPressed(String data, String status, int position);

    void checklistCheckboxChecked(boolean b, int pos);

    void checklistReminderCheckboxChecked(boolean b, int pos);

    void checklistDeleteButtonPressed(int position);

    void checklistReminderDeleteButtonPressed(int position);
}

package com.example.sarthak.notes.models;

import com.example.sarthak.notes.utils.Constants;

import java.io.Serializable;
import java.util.HashMap;

public class ChecklistReminders implements Serializable {

    private String notesType;
    private String notesTitle;
    private HashMap<String, HashMap<String, String>> content = new HashMap<>();
    private String imageUri;

    private String noteReminderYear;
    private String noteReminderMonth;
    private String noteReminderDate;
    private String noteReminderHour;
    private String noteReminderMinute;

    public ChecklistReminders() {
        // Default constructor required for calls to DataSnapshot.getValue(ChecklistReminders.class)
    }

    public ChecklistReminders(String notesTitle, HashMap<String, HashMap<String, String>> dataMap, String year, String month, String date, String hour, String minute) {

        this.notesType = Constants.TYPE_CHECKLISTS;
        this.notesTitle = notesTitle;
        this.content = dataMap;

        this.noteReminderYear = year;
        this.noteReminderMonth = month;
        this.noteReminderDate = date;
        this.noteReminderHour = hour;
        this.noteReminderMinute = minute;
    }

    public ChecklistReminders(String notesTitle, HashMap<String, HashMap<String, String>> dataMap, String year, String month, String date, String hour, String minute, String uri) {

        this.notesType = Constants.TYPE_CHECKLISTS;
        this.notesTitle = notesTitle;
        this.content = dataMap;
        this.imageUri = uri;

        this.noteReminderYear = year;
        this.noteReminderMonth = month;
        this.noteReminderDate = date;
        this.noteReminderHour = hour;
        this.noteReminderMinute = minute;
    }

    public String getNotesType() {
        return Constants.TYPE_CHECKLISTS;
    }

    public String getNotesTitle() {
        return notesTitle;
    }

    public HashMap<String, HashMap<String, String>> getContent() {
        return content;
    }

    public String getImageUri() {
        return imageUri;
    }

    public String getNoteReminderYear() {
        return noteReminderYear;
    }

    public String getNoteReminderMonth() {
        return noteReminderMonth;
    }

    public String getNoteReminderDate() {
        return noteReminderDate;
    }

    public String getNoteReminderHour() {
        return noteReminderHour;
    }

    public String getNoteReminderMinute() {
        return noteReminderMinute;
    }
}

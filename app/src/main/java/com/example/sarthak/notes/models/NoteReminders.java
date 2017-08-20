package com.example.sarthak.notes.models;

public class NoteReminders {

    private String noteType;
    private String notesTitle;
    private String notesBody;

    private String noteReminderYear;
    private String noteReminderMonth;
    private String noteReminderDate;
    private String noteReminderHour;
    private String noteReminderMinute;

    public NoteReminders() {
        // Default constructor required for calls to DataSnapshot.getValue(NoteReminders.class)
    }

    public NoteReminders(String notesTitle, String notesBody, String year, String month, String date, String hour, String minute) {

        this.noteType = "Notes";
        this.notesTitle = notesTitle;
        this.notesBody = notesBody;
        this.noteReminderYear = year;
        this.noteReminderMonth = month;
        this.noteReminderDate = date;
        this.noteReminderHour = hour;
        this.noteReminderMinute = minute;
    }

    public String getNoteType() {
        return "Notes";
    }

    public String getNotesTitle() {
        return notesTitle;
    }

    public String getNotesBody() {
        return notesBody;
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

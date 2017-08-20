package com.example.sarthak.notes.models;

public class Notes {

    private String noteType;
    private String notesTitle;
    private String notesBody;

    public Notes() {
        // Default constructor required for calls to DataSnapshot.getValue(Notes.class)
    }

    public Notes(String notesTitle, String notesBody) {

        this.noteType = "Notes";
        this.notesTitle = notesTitle;
        this.notesBody = notesBody;
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
}

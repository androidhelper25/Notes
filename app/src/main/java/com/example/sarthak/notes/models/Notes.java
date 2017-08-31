package com.example.sarthak.notes.models;

import com.example.sarthak.notes.utils.Constants;

import java.io.Serializable;

public class Notes implements Serializable {

    private String noteType;
    private String notesTitle;
    private String notesBody;
    private String imageUri;

    public Notes() {
        // Default constructor required for calls to DataSnapshot.getValue(Notes.class)
    }

    public Notes(String notesTitle, String notesBody, String uri) {

        this.noteType = Constants.TYPE_NOTES;
        this.notesTitle = notesTitle;
        this.notesBody = notesBody;
        this.imageUri = uri;
    }

    public Notes(String notesTitle, String notesBody) {

        this.noteType = Constants.TYPE_NOTES;
        this.notesTitle = notesTitle;
        this.notesBody = notesBody;
    }

    public String getNoteType() {
        return Constants.TYPE_NOTES;
    }

    public String getNotesTitle() {
        return notesTitle;
    }

    public String getNotesBody() {
        return notesBody;
    }

    public String getImageUri() {
        return imageUri;
    }
}

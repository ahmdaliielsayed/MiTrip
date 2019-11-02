package com.example.mitrip.AddNotePackage.ModelPackage;

public class Notes {
    private String noteID, note, tripID;


    public Notes() {
    }

    public Notes(String noteID, String note, String tripID) {
        this.note = note;
        this.noteID = noteID;
        this.tripID = tripID;
    }

    public String getNote() {
        return note;
    }

    public String getNoteID() {
        return noteID;
    }

    public String getTripID() {
        return tripID;
    }
}

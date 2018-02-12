package com.example.rafaelsavaris.noteapplicationrx.data.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.common.base.Strings;

import java.util.UUID;

/**
 * Created by rafael.savaris on 09/05/2017.
 */

@Entity(tableName = "notes")
public final class Note {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private final String mId;

    @ColumnInfo(name = "title")
    private final String mTitle;

    @ColumnInfo(name = "descrption")
    private final String mText;

    @ColumnInfo(name = "marked")
    private final boolean mMarked;

    public Note(String title, String text, String id, boolean marked) {
        mId = id;
        mTitle = title;
        mText = text;
        mMarked = marked;
    }

    @Ignore
    public Note(String title, String text, boolean market) {
        this(title, text, UUID.randomUUID().toString(), market);
    }

    @Ignore
    public Note(String title, String text, String id) {
        this(title, text, id, false);
    }

    @Ignore
    public Note(String title, String text) {
        this(title, text, UUID.randomUUID().toString(), false);
    }

    public String getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getText() {
        return mText;
    }

    public boolean isMarked() {
        return mMarked;
    }

    public boolean isEmpty() {
        return Strings.isNullOrEmpty(mTitle) &&
                Strings.isNullOrEmpty(mText);
    }


}

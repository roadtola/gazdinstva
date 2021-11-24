package com.ebookfrenzy.gazdinstvaa.model;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Date;

@Entity(tableName = "comment")
public class Comment {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @NotNull
    @ColumnInfo(name = "company_id")
    private int companyId = -1;

    @NotNull
    @ColumnInfo(name = "company_name")
    private String companyName = "";

    @NotNull
    @ColumnInfo(name = "checked")
    private boolean checked = false;

    @NotNull
    @ColumnInfo(name = "date")
    private Date date = new Date();

    @NotNull
    @ColumnInfo(name = "time")
    private String time = "";

    @NotNull
    @ColumnInfo(name = "comment")
    private String comment = "";

    @NotNull
    @ColumnInfo(name = "expanded")
    private boolean expanded = false;

    public Comment() {
    }

    public Comment(int id, @NotNull int companyId, @NotNull String companyName, @NotNull boolean checked, @NotNull Date date, @NotNull String time, @NotNull String comment, @NotNull boolean expanded) {
        this.id = id;
        this.companyId = companyId;
        this.companyName = companyName;
        this.checked = checked;
        this.date = date;
        this.time = time;
        this.comment = comment;
        this.expanded = expanded;
    }

    public Comment(@NotNull int companyId, @NotNull String companyName, @NotNull boolean checked, @NotNull Date date, @NotNull String time, @NotNull String comment, @NotNull boolean expanded) {
        this.companyId = companyId;
        this.companyName = companyName;
        this.checked = checked;
        this.date = date;
        this.time = time;
        this.comment = comment;
        this.expanded = expanded;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public static class SortByDate implements Comparator<Comment> {

        @Override
        public int compare(Comment o1, Comment o2) {
            return o1.getDate().compareTo(o2.getDate());
        }
    }
}

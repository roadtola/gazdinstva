package com.ebookfrenzy.gazdinstvaa.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.Date;
import java.util.List;

@Dao
public interface CommentDao {

    @Insert
    void insertComment(Comment comment);

    @Query("SELECT * FROM comment")
    LiveData<List<Comment>> getAllComments();

    @Delete
    void deleteComment(Comment comment);

    @Update
    void updateComment(Comment comment);

    @Query("SELECT * FROM comment WHERE date BETWEEN :startDate AND :endDate")
    List<Comment> getCommentsByDate(Date startDate,Date endDate);

}

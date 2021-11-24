package com.ebookfrenzy.gazdinstvaa.model;


import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.Date;
import java.util.List;

public class CommentRepository {
    private final CommentDao commentDao;
    private final LiveData<List<Comment>> allComments;

    public CommentRepository(Application application) {
        AppDatabase database = AppDatabase.getDatabase(application);
        commentDao = database.commentDao();
        allComments = commentDao.getAllComments();
    }

    public LiveData<List<Comment>> getAllComments() {
        return allComments;
    }

    public void insert(Comment comment){
        new InsertAsyncTask(commentDao).execute(comment);
    }

    public static class InsertAsyncTask extends AsyncTask<Comment,Void,Void> {
        private final CommentDao commentDao;

        public InsertAsyncTask(CommentDao dao){
            commentDao = dao;
        }

        @Override
        protected Void doInBackground(Comment... comments) {
            commentDao.insertComment(comments[0]);
            return null;
        }
    }

    public void delete(Comment comment){
        new DeleteAsyncTask(commentDao).execute(comment);
    }

    public static class DeleteAsyncTask extends AsyncTask<Comment,Void,Void> {
        private final CommentDao commentDao;

        public DeleteAsyncTask(CommentDao dao){
            commentDao = dao;
        }

        @Override
        protected Void doInBackground(Comment... comments) {
            commentDao.deleteComment(comments[0]);
            return null;
        }
    }

    public void editComment(Comment comment){
        commentDao.updateComment(comment);
    }

    public List<Comment> getCommentsByDates(Date startDate, Date endDate){
        return commentDao.getCommentsByDate(startDate,endDate);
    }


}

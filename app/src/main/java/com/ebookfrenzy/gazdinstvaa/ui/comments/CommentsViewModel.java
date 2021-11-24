package com.ebookfrenzy.gazdinstvaa.ui.comments;

import android.app.Application;
import android.widget.CalendarView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ebookfrenzy.gazdinstvaa.model.Comment;
import com.ebookfrenzy.gazdinstvaa.model.CommentRepository;
import com.ebookfrenzy.gazdinstvaa.model.Company;
import com.ebookfrenzy.gazdinstvaa.model.CompanyRepository;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class CommentsViewModel extends ViewModel {
    // TODO: Implement the ViewModel

    LiveData<List<Comment>> commentsMutable;
    MutableLiveData<List<Comment>> commentsMutableData = new MutableLiveData<>();

    public void populateComments(Application application){
        CommentRepository commentRepository = new CommentRepository(application);
        commentsMutable = commentRepository.getAllComments();
    }

    public LiveData<List<Comment>> getCommentsMutable() {
        return commentsMutable;
    }

    public void dateFilter(Application application, List<Date> dates){
        CommentRepository commentRepository = new CommentRepository(application);
        List<Comment> comments;
        if(dates.size() == 1){
            Calendar calStart = new GregorianCalendar();
            calStart.setTime(new Date(dates.get(0).getTime()));
            calStart.set(Calendar.HOUR_OF_DAY, 0);
            calStart.set(Calendar.MINUTE, 0);
            calStart.set(Calendar.SECOND, 0);
            calStart.set(Calendar.MILLISECOND, 0);
            Date dayStart = calStart.getTime();

            calStart.add(Calendar.HOUR_OF_DAY,23);
            calStart.add(Calendar.MINUTE,59);
            Date dayEnd = calStart.getTime();

            dates.clear();
            dates.add(dayStart);
            dates.add(dayEnd);

        }else{
            Calendar calStart = new GregorianCalendar();
            calStart.setTime(new Date(dates.get(0).getTime()));
            calStart.set(Calendar.HOUR_OF_DAY, 0);
            calStart.set(Calendar.MINUTE, 0);
            calStart.set(Calendar.SECOND, 0);
            calStart.set(Calendar.MILLISECOND, 0);
            Date firstDayStart = calStart.getTime();

            Calendar calEnd = new GregorianCalendar();
            calEnd.setTime(new Date(dates.get(dates.size() - 1).getTime()));
            calEnd.set(Calendar.HOUR_OF_DAY, 23);
            calEnd.set(Calendar.MINUTE, 59);
            calEnd.set(Calendar.SECOND, 58);
            calEnd.set(Calendar.MILLISECOND, 0);
            Date lastDayEnd = calEnd.getTime();

            dates.clear();
            dates.add(firstDayStart);
            dates.add(lastDayEnd);

        }
        comments = commentRepository.getCommentsByDates(dates.get(0),dates.get(dates.size() - 1));

        if(comments == null){
            comments = new ArrayList<>();
            commentsMutableData.setValue(comments);
        }else{
            comments.sort(new Comment.SortByDate());
            commentsMutableData.setValue(commentRepository.getCommentsByDates(dates.get(0),dates.get(dates.size() - 1)));
        }
    }

    public MutableLiveData<List<Comment>> getCommentsMutableData() {
        return commentsMutableData;
    }

    public void setCommentsMutableData(MutableLiveData<List<Comment>> commentsMutableData) {
        this.commentsMutableData = commentsMutableData;
    }
}
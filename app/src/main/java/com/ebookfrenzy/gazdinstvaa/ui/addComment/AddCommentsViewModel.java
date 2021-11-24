package com.ebookfrenzy.gazdinstvaa.ui.addComment;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.ebookfrenzy.gazdinstvaa.model.Comment;
import com.ebookfrenzy.gazdinstvaa.model.CommentRepository;
import com.ebookfrenzy.gazdinstvaa.model.Company;
import com.ebookfrenzy.gazdinstvaa.model.CompanyRepository;
import com.ebookfrenzy.gazdinstvaa.ui.comments.CommentRecyclerAdapter;

import java.util.List;

public class AddCommentsViewModel extends ViewModel {
    // TODO: Implement the ViewModel

    LiveData<List<Company>> companiesMutable;

    public void populateCompanies(Application application){
        CompanyRepository companyRepository = new CompanyRepository(application);
        companiesMutable = companyRepository.getAllCompanies();
    }
    public void saveComment(Comment comment,Application application){
        CommentRepository commentRepository = new CommentRepository(application);
        commentRepository.insert(comment);
    }
    public void editComment(Comment comment,Application application){
        CommentRepository commentRepository = new CommentRepository(application);
        commentRepository.editComment(comment);
    }

    public LiveData<List<Company>> getCompaniesMutable() {
        return companiesMutable;
    }

    public void setCompaniesMutable(LiveData<List<Company>> companiesMutable) {
        this.companiesMutable = companiesMutable;
    }
}
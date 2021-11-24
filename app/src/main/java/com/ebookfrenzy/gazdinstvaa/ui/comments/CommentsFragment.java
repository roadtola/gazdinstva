package com.ebookfrenzy.gazdinstvaa.ui.comments;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.ArraySet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Toast;

import com.applikeysolutions.cosmocalendar.utils.SelectionType;
import com.applikeysolutions.cosmocalendar.view.CalendarView;
import com.ebookfrenzy.gazdinstvaa.R;
import com.ebookfrenzy.gazdinstvaa.databinding.CommentsFragmentBinding;
import com.ebookfrenzy.gazdinstvaa.model.Comment;
import com.ebookfrenzy.gazdinstvaa.model.CommentRepository;
import com.ebookfrenzy.gazdinstvaa.ui.addComment.AddCommentFragment;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class CommentsFragment extends Fragment {

    private CommentsViewModel mViewModel;
    private CommentsFragmentBinding binding;
    private CommentRecyclerAdapter adapter;

    public static CommentsFragment newInstance() {
        return new CommentsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = CommentsFragmentBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(CommentsViewModel.class);
        binding.commentsRecycler.setHasFixedSize(true);
        binding.commentsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.commentsAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.nav_host_fragment,new AddCommentFragment())
                        .addToBackStack("AddComment")
                        .commit();
            }
        });
        // TODO: Use the ViewModel
        mViewModel.populateComments(getActivity().getApplication());
        mViewModel.getCommentsMutable().observe(getViewLifecycleOwner(), new Observer<List<Comment>>() {
            @Override
            public void onChanged(List<Comment> comments) {
                adapter = new CommentRecyclerAdapter(comments,getContext(),getActivity().getApplication());
                binding.commentsRecycler.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        });
        binding.commentsSearch.setOnClickListener(v -> binding.commentsSearch.setIconified(false));
        binding.commentsSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (adapter != null) {
                    adapter.getFilter().filter(query);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (adapter != null) {
                    adapter.getFilter().filter(newText);
                }
                return true;
            }
        });

        binding.commentsDateFromParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendarPopup();
            }
        });
        binding.commentsDateToParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendarPopup();
            }
        });

        mViewModel.getCommentsMutableData().observe(getViewLifecycleOwner(), new Observer<List<Comment>>() {
            @Override
            public void onChanged(List<Comment> comments) {
                adapter = new CommentRecyclerAdapter(comments,getContext(),getActivity().getApplication());
                binding.commentsRecycler.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        });

        binding.commentsDateFromParent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                adapter = new CommentRecyclerAdapter(mViewModel.getCommentsMutable().getValue(),getContext(),getActivity().getApplication());
                binding.commentsRecycler.setAdapter(adapter);
                binding.commentsDateFrom.setVisibility(View.GONE);
                binding.commentsDateTo.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
                return true;
            }
        });

        binding.commentsDateToParent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                adapter = new CommentRecyclerAdapter(mViewModel.getCommentsMutable().getValue(),getContext(),getActivity().getApplication());
                binding.commentsRecycler.setAdapter(adapter);
                binding.commentsDateFrom.setVisibility(View.GONE);
                binding.commentsDateTo.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
                return true;
            }
        });
        binding.commentsSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(adapter != null){
                    adapter.getFilter().filter(query);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(adapter != null){
                    adapter.getFilter().filter(newText);
                }
                return true;
            }
        });


    }

    void calendarPopup(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.calendar_popup,null);

        builder.setView(view);

        AlertDialog calendarDialog = builder.create();
        calendarDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        calendarDialog.getWindow().getAttributes().windowAnimations = R.style.alert_dialog;
        calendarDialog.setCanceledOnTouchOutside(true);
        CalendarView calendarView = view.findViewById(R.id.week_stats_calendar);

        calendarView.setSelectionType(SelectionType.RANGE);

        calendarDialog.show();

        ImageButton selectButton = view.findViewById(R.id.calendar_popup_select);
        ImageButton cancelButton = view.findViewById(R.id.calendar_popup_cancel);
        selectButton.setOnClickListener(click -> {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy");
            List<Calendar> selectedDay = calendarView.getSelectedDates();
            if(selectedDay.size() ==  0){
                Toast.makeText(getContext(), "Nije izabran datum!", Toast.LENGTH_SHORT).show();
            }else{
                List<Date> dates = new ArrayList<>();
                for(Calendar day:selectedDay){
                    dates.add(day.getTime());
                }
                mViewModel.dateFilter(getActivity().getApplication(),dates);
                String from = simpleDateFormat.format(dates.get(0));
                String to;
                if(dates.size() == 1){
                    to = simpleDateFormat.format(dates.get(0));
                }else{
                    to = simpleDateFormat.format(dates.get(dates.size() - 1));
                }

                binding.commentsDateFrom.setText(from);
                binding.commentsDateFrom.setVisibility(View.VISIBLE);
                binding.commentsDateTo.setText(to);
                binding.commentsDateTo.setVisibility(View.VISIBLE);
                calendarDialog.dismiss();
            }
        });

        cancelButton.setOnClickListener(click -> {
            calendarDialog.dismiss();

        });
        calendarDialog.setCanceledOnTouchOutside(true);
    }


}
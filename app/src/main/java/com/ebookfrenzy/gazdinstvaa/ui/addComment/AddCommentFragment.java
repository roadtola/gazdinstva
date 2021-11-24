package com.ebookfrenzy.gazdinstvaa.ui.addComment;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.applikeysolutions.cosmocalendar.utils.SelectionType;
import com.applikeysolutions.cosmocalendar.view.CalendarView;
import com.ebookfrenzy.gazdinstvaa.R;
import com.ebookfrenzy.gazdinstvaa.databinding.FragmentAddCommentBinding;
import com.ebookfrenzy.gazdinstvaa.model.Comment;
import com.ebookfrenzy.gazdinstvaa.model.Company;
import com.ebookfrenzy.gazdinstvaa.ui.comments.AutoCompleteAdapter;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;


public class AddCommentFragment extends Fragment {

    private static final String ID = "id";
    private static final String DATE = "date";
    private static final String TIME = "time";
    private static final String COMPANY = "company";
    private static final String COMMENT = "comment";
    public final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy");
    private TimePicker timePicker;

    private int idParam = -1;

    private FragmentAddCommentBinding binding;
    private AddCommentsViewModel mViewModel;
    private List<Company> companiesGlobal;

    public AddCommentFragment() {
        // Required empty public constructor
    }


    public static AddCommentFragment newInstance(Comment comment) {
        AddCommentFragment fragment = new AddCommentFragment();
        Bundle args = new Bundle();
        args.putInt(ID, comment.getId());

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy");

        args.putString(DATE, simpleDateFormat.format(comment.getDate()));
        args.putString(TIME, comment.getTime());
        args.putString(COMPANY, comment.getCompanyName());
        args.putString(COMMENT, comment.getComment());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(AddCommentsViewModel.class);

        mViewModel.populateCompanies(getActivity().getApplication());
        //set current date
        LocalDate localDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy");
        binding.addCommentDate.setText(formatter.format(localDate));

        //set current time
        LocalDateTime dateTime = LocalDateTime.now();
        String currentTimeGlobal = dateTime.getHour() + ":" + dateTime.getMinute();
        binding.addCommentTime.setText(currentTimeGlobal);

        binding.addCommentCompany.setEnabled(false);
        mViewModel.getCompaniesMutable().observe(getViewLifecycleOwner(), companies -> {
            companiesGlobal = companies;
            List<String> companyNames = new ArrayList<>();
            for(Company company:companies){
                companyNames.add(company.getCompanyName());
            }
            AutoCompleteAdapter adapter = new AutoCompleteAdapter(getContext(), android.R.layout.simple_dropdown_item_1line, android.R.id.text1, companyNames);
            binding.addCommentCompany.setAdapter(adapter);
            binding.addCommentCompany.setEnabled(true);
        });

        binding.addCommentSave.setOnClickListener(v -> {
            String time = binding.addCommentTime.getText().toString();
            String date = binding.addCommentDate.getText().toString();
            String company = binding.addCommentCompany.getText().toString();
            String comment = binding.addCommentComment.getText().toString();
            int companyId = -1;
            for(Company companyTemp: companiesGlobal){
                if(companyTemp.getCompanyName().contains(company)){
                    companyId = companyTemp.getId();
                }
            }

            boolean checked = false;
            try {
                DateFormat dateFormat = new SimpleDateFormat("hh:mm");
                Date commentTime = dateFormat.parse(time);
                String hour = String.valueOf(commentTime.getHours());
                String minutes = String.valueOf(commentTime.getMinutes());
                hour = hour.length() == 1 ? "0" + hour : hour;
                minutes = minutes.length() == 1 ? "0" + minutes : minutes;

                String commentTimeString = hour + ":" + minutes;
                Date currentTime = Calendar.getInstance().getTime();
                if(commentTime.after(currentTime) || commentTimeString.contains(currentTimeGlobal) ){
                    checked = true;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if(!company.isEmpty() && !comment.isEmpty()){
                saveComment(time,date,company,comment,companyId,checked);
            }else{
                Toast.makeText(getContext(), "Kompanija i Komentar ne mogu biti prazni!", Toast.LENGTH_SHORT).show();
            }

        });
        if(getArguments() != null){
            String date = getArguments().getString(DATE);
            String time = getArguments().getString(TIME);
            String company = getArguments().getString(COMPANY);
            String comment = getArguments().getString(COMMENT);
            idParam = getArguments().getInt(ID);
            binding.addCommentTime.setText(time);
            binding.addCommentDate.setText(date);
            binding.addCommentCompany.setText(company);
            binding.addCommentComment.setText(comment);
            binding.addCommentTitle.setText("Izmeni Komentar");
        }

        binding.addCommentDateParent.setOnClickListener(v -> {
            createCalendarPopup();
        });
        
        binding.addCommentTimeParent.setOnClickListener(v -> {
            createTimePopup(dateTime,false,"");
        });

        binding.addCommentCheckboxParent.setOnClickListener(v -> binding.addCommentCheckbox.setChecked(!binding.addCommentCheckbox.isChecked()));

    }

    private void createTimePopup(LocalDateTime localDateTime,boolean fromAlarm,String company) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.time_picer,null);
        timePicker = view.findViewById(R.id.time_time_picker);
        timePicker.setIs24HourView(true);
        final Button setButton = view.findViewById(R.id.time_popup_set);
        final TextView tvTitle = view.findViewById(R.id.time_popup_title);
        if(fromAlarm){
            tvTitle.setText("Navij Alarm");
        }

        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.alert_dialog;
        dialog.show();

        dialog.setCanceledOnTouchOutside(!fromAlarm);

        setButton.setOnClickListener(v -> {
            String hour = String.valueOf(timePicker.getHour());
            hour = hour.length() == 1 ? "0" + hour : hour;
            String minute = String.valueOf(timePicker.getMinute());
            minute = minute.length() == 1 ? "0" + minute : minute;
            String time = hour + ":" + minute;
            if(!fromAlarm){
                binding.addCommentTime.setText(time);
                dialog.dismiss();
                if((timePicker.getHour() > localDateTime.getHour()) ||
                        (timePicker.getHour() == localDateTime.getHour() && timePicker.getMinute() > localDateTime.getMinute())){
                    binding.addCommentCheckboxParent.setVisibility(View.VISIBLE);
                }else{
                    binding.addCommentCheckboxParent.setVisibility(View.GONE);
                }
            }else{
                Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
                intent.putExtra(AlarmClock.EXTRA_HOUR, Integer.parseInt(hour));
                intent.putExtra(AlarmClock.EXTRA_MINUTES, Integer.parseInt(minute));
                intent.putExtra(AlarmClock.EXTRA_MESSAGE, "Podsetnik za " + company);
                getParentFragmentManager()
                        .popBackStack();
                dialog.dismiss();
                startActivity(intent);


            }

        });
    }

    private void createCalendarPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.calendar_popup,null);

        builder.setView(view);

        AlertDialog calendarDialog = builder.create();
        calendarDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        calendarDialog.getWindow().getAttributes().windowAnimations = R.style.alert_dialog;
        calendarDialog.setCanceledOnTouchOutside(true);
        CalendarView calendarView = view.findViewById(R.id.week_stats_calendar);

        calendarView.setSelectionType(SelectionType.SINGLE);

        calendarDialog.show();

        ImageButton selectButton = view.findViewById(R.id.calendar_popup_select);
        ImageButton cancelButton = view.findViewById(R.id.calendar_popup_cancel);
        selectButton.setOnClickListener(click -> {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy");
            List<Calendar> selectedDay = calendarView.getSelectedDates();
            binding.addCommentDate.setText(simpleDateFormat.format(selectedDay.get(0)));
            calendarDialog.dismiss();

        });

        cancelButton.setOnClickListener(click -> {
            calendarDialog.dismiss();

        });
        calendarDialog.setCanceledOnTouchOutside(true);
    }

    private void saveComment(String time, String date, String company, String comment, int companyId,boolean checked) {
        DateFormat dateFormat = new SimpleDateFormat("hh:mm");

        Date dateDate = new Date();
        try {
            dateDate = simpleDateFormat.parse(date);
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(dateDate);
            Date commentTime = dateFormat.parse(time);
            calendar.add(Calendar.HOUR_OF_DAY,commentTime.getHours());
            calendar.add(Calendar.MINUTE,commentTime.getMinutes());
            dateDate = calendar.getTime();

        }catch (ParseException e){
            Log.d("TAG", "saveComment: " + e.getLocalizedMessage());
        }



        Comment comment1 = new Comment(companyId,company,checked,dateDate,time,comment,false);
        if(idParam == -1){
            mViewModel.saveComment(comment1,getActivity().getApplication());
            Toast.makeText(getContext(), "Uspesno snimljeno!", Toast.LENGTH_SHORT).show();
        }else{
            comment1.setId(idParam);
            mViewModel.editComment(comment1,getActivity().getApplication());
            Toast.makeText(getContext(), "Uspesno izmenjeno!", Toast.LENGTH_SHORT).show();
        }
        if(binding.addCommentCheckbox.isChecked()){
            createTimePopup(LocalDateTime.now(),true,company);
        }else{
            getParentFragmentManager()
                    .popBackStack();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAddCommentBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }
}
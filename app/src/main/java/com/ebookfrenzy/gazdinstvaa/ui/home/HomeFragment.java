package com.ebookfrenzy.gazdinstvaa.ui.home;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.ebookfrenzy.gazdinstvaa.MapsActivity;
import com.ebookfrenzy.gazdinstvaa.R;
import com.ebookfrenzy.gazdinstvaa.ui.addCompany.AddCompanyFragment;
import com.ebookfrenzy.gazdinstvaa.ui.comments.CommentsFragment;
import com.ebookfrenzy.gazdinstvaa.ui.findCompany.FindCompanyFragment;

public class HomeFragment extends Fragment {

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final Button addCompanyButton = root.findViewById(R.id.add_company);
        final Button mapsButton = root.findViewById(R.id.get_maps);
        final Button findCompanyButton = root.findViewById(R.id.company_find);
        final Button commentsButton = root.findViewById(R.id.comments_list);
        //logo.setImageResource(R.drawable.ic_logo);

        mapsButton.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), MapsActivity.class));
        });

        addCompanyButton.setOnClickListener(v -> {
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment,new AddCompanyFragment())
                    .addToBackStack("addCompany")
                    .commit();
        });

        findCompanyButton.setOnClickListener(v -> {
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment,new FindCompanyFragment())
                    .addToBackStack("findCompany")
                    .commit();
        });

        commentsButton.setOnClickListener(v -> {
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment,new CommentsFragment())
                    .addToBackStack("commentsList")
                    .commit();
        });

        return root;
    }
}
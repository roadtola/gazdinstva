package com.ebookfrenzy.gazdinstvaa.ui.findCompany;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.ebookfrenzy.gazdinstvaa.databinding.FindCompanyFragmentBinding;
import com.ebookfrenzy.gazdinstvaa.model.CompanyRepository;

public class FindCompanyFragment extends Fragment {

    private FindCompanyViewModel mViewModel;
    private FindCompanyRecyclerAdapter adapter;
    private FindCompanyFragmentBinding binding;

    public static FindCompanyFragment newInstance() {
        return new FindCompanyFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FindCompanyFragmentBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(FindCompanyViewModel.class);
        binding.findCompanySearch.setOnClickListener(v -> {
            binding.findCompanySearch.setIconified(false);
        });
        CompanyRepository companyRepository = new CompanyRepository(getActivity().getApplication());
        companyRepository.getAllCompanies().observe(getViewLifecycleOwner(), companies -> {
            companies.sort((o1, o2) -> o1.getCompanyName().compareTo(o2.getCompanyName()));
            adapter = new FindCompanyRecyclerAdapter(getActivity(),getContext(),companies);
            binding.findCompanyRecycler.setHasFixedSize(true);
            binding.findCompanyRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
            binding.findCompanyRecycler.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        });
        binding.findCompanySearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
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

}
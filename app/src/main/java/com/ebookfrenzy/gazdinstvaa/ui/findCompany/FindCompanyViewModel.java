package com.ebookfrenzy.gazdinstvaa.ui.findCompany;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ebookfrenzy.gazdinstvaa.model.Company;
import com.ebookfrenzy.gazdinstvaa.model.CompanyRepository;

import java.util.List;

public class FindCompanyViewModel extends ViewModel {
    private final MutableLiveData<List<Company>> companies = new MutableLiveData<>();
    private CompanyRepository companyRepository;

    public FindCompanyViewModel(){

    }

    public void populateList(Application application){

    }

    public MutableLiveData<List<Company>> getCompanies() {
        return companies;
    }

    public void setCompanies(List<Company> companies){
        this.companies.setValue(companies);
    }
}
package com.ebookfrenzy.gazdinstvaa.model;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

public class CompanyRepository {
    private final CompanyDao companyDao;
    private final LiveData<List<Company>> allCompanies;

    public CompanyRepository(Application application){
        AppDatabase database = AppDatabase.getDatabase(application);
        companyDao = database.companyDao();
        allCompanies = companyDao.getAllCompanies();
    }

    public void insert(Company company){
        new InsertAsyncTask(companyDao).execute(company);
    }

    public static class InsertAsyncTask extends AsyncTask<Company,Void,Void>{
        private final CompanyDao companyDao;

        public InsertAsyncTask(CompanyDao dao){
            companyDao = dao;
        }

        @Override
        protected Void doInBackground(Company... companies) {
            companyDao.insert(companies[0]);
            return null;
        }
    }

    public LiveData<List<Company>> getAllCompanies() {
        return allCompanies;
    }

    public void deleteAllFromCompany(){
        new DeleteAsyncTask(companyDao).execute();
    }

    public static class DeleteAsyncTask extends AsyncTask<Void,Void,Void>{
        private final CompanyDao companyDao;

        public DeleteAsyncTask(CompanyDao dao){
            companyDao = dao;
        }


        @Override
        protected Void doInBackground(Void... voids) {
            companyDao.deleteAllFromCompany();
            return null;
        }
    }

    public void updateCompany(Company company){
        companyDao.updateCompany(company);
    }

    public void deleteCompany(Company company){
        companyDao.deleteCompany(company);
    }

    public Company findCompanyByLatAndLong(double lat,double longatude){
        return companyDao.getCompanyBtLatAndLong(lat,longatude);
    }
}

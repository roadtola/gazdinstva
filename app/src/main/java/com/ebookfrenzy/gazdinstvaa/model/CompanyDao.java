package com.ebookfrenzy.gazdinstvaa.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CompanyDao {

    @Insert
    void insert(Company company);

    @Query("SELECT * FROM company")
    LiveData<List<Company>> getAllCompanies();

    @Query("SELECT * FROM company WHERE lat=:lat AND long=:longatude ")
    Company getCompanyBtLatAndLong(double lat,double longatude);

    @Query("DELETE FROM company")
    void deleteAllFromCompany();

    @Update()
    void updateCompany(Company company);

    @Delete
    void deleteCompany(Company company);
}

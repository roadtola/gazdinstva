package com.ebookfrenzy.gazdinstvaa;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.AssetManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;

import com.ebookfrenzy.gazdinstvaa.model.Company;
import com.ebookfrenzy.gazdinstvaa.model.CompanyRepository;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //deleteDatabase("app_data_base");
        //upload to database from excel
        /*CompanyRepository companyRepository = new CompanyRepository(getApplication());
        AssetManager assetManager = getAssets();
        try {
            InputStream inputStream = assetManager.open("plan.xls");
            Workbook workbook = Workbook.getWorkbook(inputStream);
            Sheet sheet = workbook.getSheet(0);
            int rowCount = sheet.getRows();
            int colCount = sheet.getColumns();

            for(int row = 0; row < rowCount; row ++){
                //skip headers
                if(row == 0){
                    continue;
                }
                Company company = new Company();

                for(int column = 0; column < colCount; column++){
                    Cell cell = sheet.getCell(column,row);

                    switch (column){
                        case 0:
                            break;
                        case 1:
                            company.setCompanyName(cell.getContents());
                            break;
                        case 2:
                            company.setPlace(cell.getContents());
                            break;
                        case 3:
                            company.setAddress(cell.getContents());
                            break;
                        case 4:
                            company.setPhoneNumber(cell.getContents());
                            break;
                        case 5:
                            company.setContactPerson(cell.getContents());
                            break;
                    }
                }
                Geocoder geocoder = new Geocoder(this);
                List<Address> addressList = new ArrayList<>();
                try{
                    addressList  = geocoder.getFromLocationName(company.getAddress() + " " + company.getPlace(),1);
                }catch (IOException e){
                    Log.d("TAG", "saveCompany: " + e.getLocalizedMessage());
                }
                if(addressList.size() > 0){
                    if(addressList.get(0).hasLatitude()){
                        company.setLat(addressList.get(0).getLatitude());
                    }
                    if(addressList.get(0).hasLongitude()){
                        company.setLongatude(addressList.get(0).getLongitude());
                    }
                }
                //save to db
                companyRepository.insert(company);

            }

        } catch (IOException | BiffException e) {
            e.printStackTrace();
        }*/
    }

    @Override
    public void onBackPressed() {
        int stackCount = getSupportFragmentManager().getBackStackEntryCount();
        if(stackCount > 0){
            getSupportFragmentManager().popBackStack();
        }else{
            super.onBackPressed();
        }

    }
}
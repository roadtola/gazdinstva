package com.ebookfrenzy.gazdinstvaa.ui.addCompany;

import androidx.lifecycle.ViewModelProvider;

import android.app.Application;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ebookfrenzy.gazdinstvaa.MapsActivity;
import com.ebookfrenzy.gazdinstvaa.R;
import com.ebookfrenzy.gazdinstvaa.databinding.AddCompanyFragmentBinding;
import com.ebookfrenzy.gazdinstvaa.model.Company;
import com.ebookfrenzy.gazdinstvaa.model.CompanyRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AddCompanyFragment extends Fragment {

    private AddCompanyViewModel mViewModel;
    private AddCompanyFragmentBinding binding;

    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String ADDRESS = "address";
    public static final String PLACE = "place";
    public static final String PHONE = "phone";
    public static final String PERSON = "person";
    public static final String LAT = "lat";
    public static final String LONG = "long";

    private int idParam = -1;
    private double latParam = 0D,longParam = 0D;

    public static AddCompanyFragment newInstance(Company company) {
        AddCompanyFragment addCompanyFragment = new AddCompanyFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ID,company.getId());
        bundle.putString(NAME,company.getCompanyName());
        bundle.putString(ADDRESS,company.getAddress());
        bundle.putString(PLACE,company.getPlace());
        bundle.putString(PHONE,company.getPhoneNumber());
        bundle.putString(PERSON,company.getContactPerson());
        addCompanyFragment.setArguments(bundle);
        return addCompanyFragment;
    }

    public static AddCompanyFragment newInstance(double lat,double longatude) {
        AddCompanyFragment addCompanyFragment = new AddCompanyFragment();
        Bundle bundle = new Bundle();
        bundle.putDouble(LAT,lat);
        bundle.putDouble(LONG,longatude);
        addCompanyFragment.setArguments(bundle);
        return addCompanyFragment;
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = AddCompanyFragmentBinding.inflate(inflater,container,false);

        if(getArguments() != null){
            idParam = getArguments().getInt(ID);
            String name = getArguments().getString(NAME);
            String address = getArguments().getString(ADDRESS);
            String place = getArguments().getString(PLACE);
            String phone = getArguments().getString(PHONE);
            String person = getArguments().getString(PERSON);

            binding.addCompanyName.setText(name);
            binding.addCompanyAddress.setText(address);
            binding.addCompanyPlace.setText(place);
            binding.addCompanyPhone.setText(phone);
            binding.addCompanyPerson.setText(person);

            latParam = getArguments().getDouble(LAT);
            longParam = getArguments().getDouble(LONG);
            if(idParam == 0){
                idParam = -1;
            }
        }

        binding.addCompanySaveButton.setOnClickListener(v -> {
            String name = binding.addCompanyName.getText().toString();
            String address = binding.addCompanyAddress.getText().toString();
            String place = binding.addCompanyPlace.getText().toString();
            String person = binding.addCompanyPerson.getText().toString();
            String phone = binding.addCompanyPhone.getText().toString();
            if(!name.isEmpty() && !person.isEmpty() && !phone.isEmpty()){
                if(latParam != 0 && longParam != 0){
                    saveCompany(name,address,place,person,phone);
                }else{
                    if(!address.isEmpty() && place.isEmpty()){
                        Toast.makeText(getContext(), "Ako je uneta adresa potrebno je uneti i mesto!", Toast.LENGTH_SHORT).show();
                    }else{
                        saveCompany(name,address,place,person,phone);
                    }
                }

            }else{
                Toast.makeText(getContext(), "Osnovna polja moraju biti popunjena!", Toast.LENGTH_SHORT).show();
            }
        });

        return binding.getRoot();
    }

    private void saveCompany(String name, String address, String place, String person, String phone) {
        CompanyRepository companyRepository = new CompanyRepository(getActivity().getApplication());
        Company company = new Company(name,phone,person,address,place);
        if(latParam != 0 && longParam != 0){
            company.setLat(latParam);
            company.setLongatude(longParam);
        }else{
            Geocoder geocoder = new Geocoder(getContext());
            List<Address> addressList = new ArrayList<>();
            try{
                if(address.isEmpty()){
                    place = place.replaceAll("(Ž)","Z");
                    place = place.replaceAll("(ž)","z");
                    place = place.replaceAll("(Đ)","D");
                    place = place.replaceAll("(đ)","d");

                    addressList = geocoder.getFromLocationName(place + "Serbia",1);
                }else{
                    addressList  = geocoder.getFromLocationName(address + " " + place + "Serbia",1);
                }
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
        }

        if(idParam == -1){
            companyRepository.insert(company);
            Toast.makeText(getContext(), "Uspesno Dodato!", Toast.LENGTH_SHORT).show();
        }else{
            company.setId(idParam);
            companyRepository.updateCompany(company);
            Toast.makeText(getContext(), "Uspesno Izmenjeno!", Toast.LENGTH_SHORT).show();
        }


            getParentFragmentManager()
                    .popBackStack();


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(AddCompanyViewModel.class);
        // TODO: Use the ViewModel
    }

}
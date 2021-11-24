package com.ebookfrenzy.gazdinstvaa;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ebookfrenzy.gazdinstvaa.databinding.ActivityMapsBinding;
import com.ebookfrenzy.gazdinstvaa.model.Company;
import com.ebookfrenzy.gazdinstvaa.model.CompanyRepository;
import com.ebookfrenzy.gazdinstvaa.model.MapAnimate;
import com.ebookfrenzy.gazdinstvaa.ui.addCompany.AddCompanyFragment;
import com.ebookfrenzy.gazdinstvaa.ui.home.MapsRecycler;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.ClusterManager;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("PotentialBehaviorOverride")
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, MapAnimate {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private boolean centered = false;
    private CompanyRepository companyRepository;
    private ClusterManager<Company> mClusterManager;
    private double currentLat = 45.515130;
    private double currentLong = 19.248140;
    private List<Company> companiesGlobal = new ArrayList<>();
    private Company selectedCompanyForDragging = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.mapsRecycler.setHasFixedSize(true);
        binding.mapsRecycler.setLayoutManager(new LinearLayoutManager(this));

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        companyRepository = new CompanyRepository(getApplication());
        //CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(currentLat, currentLong));

        //mMap.moveCamera(center);
        mClusterManager = new ClusterManager<>(this, googleMap);
        mClusterManager.setRenderer(new MarkerClusterRenderer(this,mMap,mClusterManager));
        mClusterManager.setOnClusterItemInfoWindowClickListener(new ClusterManager.OnClusterItemInfoWindowClickListener<Company>() {
            @Override
            public void onClusterItemInfoWindowClick(Company item) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                View view = getLayoutInflater().inflate(R.layout.map_popup, null);
                builder.setView(view);

                CardView parent = view.findViewById(R.id.popup_parent);
                TextView title = view.findViewById(R.id.title);
                TextView snippet = view.findViewById(R.id.snippet);
                ImageButton callLocal = view.findViewById(R.id.popup_call);
                ImageButton directions = view.findViewById(R.id.popup_directions);

                AlertDialog dialog = builder.create();
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().getAttributes().windowAnimations = R.style.alert_dialog;
                dialog.show();

                title.setText(item.getTitle());
                snippet.setText(item.getSnippet());
                String[] snippets = item.getSnippet().split("\n");
                StringBuilder phoneNumber = new StringBuilder("tel");
                StringBuilder address = new StringBuilder("geo:"+item.getPosition().latitude + item.getPosition().longitude + "?q=");

                for(String snippetLocal : snippets){
                    if(snippetLocal.startsWith("Adresa")){
                        address.append(snippetLocal);
                    }else{
                        phoneNumber.append(snippetLocal.substring(snippetLocal.indexOf(":")));
                    }
                }

                directions.setOnClickListener(v -> {
                    //Toast.makeText(MapsActivity.this, "Calling..", Toast.LENGTH_SHORT).show();
                    if(item.getAddress().isEmpty() || item.getPlace().isEmpty()){
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                Uri.parse("http://maps.google.com/maps?daddr=" + item.getLat() + "," + item.getLongatude()));
                        startActivity(intent);
                    }else{
                        Uri gmmIntentUri = Uri.parse(address.toString().replaceAll("Adresa:","").replaceAll(","," "));
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        startActivity(mapIntent);
                    }

                    dialog.dismiss();
                });

                callLocal.setOnClickListener(v -> {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse(phoneNumber.toString()));
                    startActivity(callIntent);
                });

                parent.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        PopupMenu popupMenu = new PopupMenu(MapsActivity.this,parent);
                        popupMenu.getMenuInflater().inflate(R.menu.company_menu,popupMenu.getMenu());
                        popupMenu.show();
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem itemMenu) {
                                if(itemMenu.getItemId() == R.id.company_call){
                                    //make call
                                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                                    callIntent.setData(Uri.parse("tel:" + item.getPhoneNumber()));
                                    dialog.dismiss();
                                    startActivity(callIntent);
                                    return true;
                                }
                                if(itemMenu.getItemId() == R.id.company_address){
                                    //directions in google maps
                                    Uri gmmIntentUri = Uri.parse("geo:" +
                                            item.getLat() +
                                            item.getLongatude() +
                                            "?q=" + item.getAddress() +
                                            " " +
                                            item.getPlace());
                                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                    mapIntent.setPackage("com.google.android.apps.maps");
                                    dialog.dismiss();
                                    startActivity(mapIntent);
                                    return true;
                                }
                                if(itemMenu.getItemId() == R.id.company_edit){
                                    //edit company
                                    getSupportFragmentManager()
                                            .beginTransaction()
                                            .replace(R.id.map, AddCompanyFragment.newInstance(item))
                                            .addToBackStack("EditCompany")
                                            .commit();
                                    dialog.dismiss();
                                    companiesGlobal.remove(item);
                                    return true;
                                };
                                if(itemMenu.getItemId() == R.id.company_delete){
                                    //delete company
                                    CompanyRepository companyRepository = new CompanyRepository(MapsActivity.this.getApplication());
                                    companyRepository.deleteCompany(item);
                                    dialog.dismiss();
                                    companiesGlobal.remove(item);
                                    return true;
                                }
                                return false;
                            }
                        });
                        return true;
                    }
                });

            }
        });

        googleMap.setOnCameraIdleListener(mClusterManager);

        companyRepository.getAllCompanies().observe(this, companies -> {
            mMap.clear();
            for(int i = 0; i < companies.size(); i++){
                for(int j = 0; j < companies.size(); j++){
                    if(!(i == j)){
                        if(companies.get(i).getLongatude() == companies.get(j).getLongatude() &&
                        companies.get(i).getLat() == companies.get(j).getLat()){
                            companies.get(i).setLat(companies.get(i).getLat() + 0.001D);
                        }
                    }
                }
            }
            companiesGlobal.clear();
                for(Company company:companies){
                    if(!company.getAddress().isEmpty() || !company.getPlace().isEmpty() || company.getLat() != 0){
                        mClusterManager.addItem(company);
                        companiesGlobal.add(company);
                    }
                }
                mClusterManager.cluster();


        });
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            mMap.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }

        binding.mapsProgressBar.setVisibility(View.VISIBLE);
        centered = !centered;
        if(centered){
            mMap.setOnMyLocationChangeListener(location -> {
                CameraUpdate centerLocal = CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
                CameraUpdate zoomLocal = CameraUpdateFactory.zoomTo(17);
                binding.mapsProgressBar.setVisibility(View.GONE);
                mMap.moveCamera(centerLocal);
                mMap.animateCamera(zoomLocal);
                currentLat = location.getLatitude();
                currentLong = location.getLongitude();
            });
        }else{
            mMap.setOnMyLocationChangeListener(location -> binding.mapsProgressBar.setVisibility(View.GONE));
        }

        ColorStateList colorStateList = binding.call.getBackgroundTintList();
        binding.call.setOnClickListener(v -> {
            binding.mapsProgressBar.setVisibility(View.VISIBLE);
            centered = !centered;
            if(centered){
                mMap.setOnMyLocationChangeListener(location -> {
                    CameraUpdate centerLocal = CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
                    float zoom=googleMap.getCameraPosition().zoom;
                    CameraUpdate zoomLocal = CameraUpdateFactory.zoomTo(zoom);
                    binding.mapsProgressBar.setVisibility(View.GONE);
                    mMap.moveCamera(centerLocal);
                    mMap.animateCamera(zoomLocal);
                    currentLat = location.getLatitude();
                    currentLong = location.getLongitude();
                    binding.call.setBackgroundTintList(colorStateList);
                });
            }else{
                mMap.setOnMyLocationChangeListener(location -> binding.mapsProgressBar.setVisibility(View.GONE));
                binding.call.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            }
        });

        binding.mapsSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.length() > 0){
                    List<Company> results = filter(newText);
                    MapsRecycler adapter = new MapsRecycler(results,MapsActivity.this);
                    binding.mapsRecycler.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    if(results.size() > 0){
                        binding.mapsRecycler.setVisibility(View.VISIBLE);
                    }else{
                        binding.mapsRecycler.setVisibility(View.GONE);
                    }
                }else{
                    binding.mapsRecycler.setVisibility(View.GONE);
                }
                return true;
            }
        });
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(@NonNull @NotNull LatLng latLng) {
                new AlertDialog.Builder(MapsActivity.this)
                        .setTitle("Da li zelis da dodas pin")
                        .setPositiveButton("Da", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getSupportFragmentManager()
                                        .beginTransaction()
                                        .replace(R.id.map,AddCompanyFragment.newInstance(latLng.latitude,latLng.longitude))
                                        .addToBackStack("AddCompany")
                                        .commit();
                            }
                        })
                        .setNegativeButton("Ne", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();

            }
        });

        CompanyRepository companyRepository = new CompanyRepository(getApplication());
        //on dragging pin change address

        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(@NonNull @NotNull CameraPosition cameraPosition) {
                if(cameraPosition.zoom > 10){
                    mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {

                        @Override
                        public void onMarkerDragStart(@NonNull @NotNull Marker marker) {
                            selectedCompanyForDragging = null;
                            for(Company company:companiesGlobal){
                                if (marker.getSnippet() != null && marker.getTitle() != null) {
                                    if(company.getSnippet() != null && company.getTitle() != null){
                                        if(company.getSnippet().contains(marker.getSnippet()) &&
                                                company.getTitle().contains(marker.getTitle())){
                                            selectedCompanyForDragging = company;
                                        }
                                    }
                                }
                            }
                        }

                        @Override
                        public void onMarkerDrag(@NonNull @NotNull Marker marker) {

                        }

                        @Override
                        public void onMarkerDragEnd(@NonNull @NotNull Marker marker) {
                            if(selectedCompanyForDragging != null){
                                companiesGlobal.remove(selectedCompanyForDragging);
                                selectedCompanyForDragging.setLat(marker.getPosition().latitude);
                                selectedCompanyForDragging.setLongatude(marker.getPosition().longitude);
                                companyRepository.updateCompany(selectedCompanyForDragging);
                                //mClusterManager.clearItems();
                                mClusterManager.clearItems();
                                mMap.clear();
                                companiesGlobal.add(selectedCompanyForDragging);
                                for(Company company: companiesGlobal){
                                    mClusterManager.addItem(company);
                                }
                            }
                        }
                    });
                }
            }
        });




    }


    private List<Company> filter(String newText) {
        List<Company> companies = new ArrayList<>();
        for(Company company:companiesGlobal){
            if(company.getCompanyName().toLowerCase().startsWith(newText.toLowerCase())){
                companies.add(company);
            }
        }
        return companies;
    }

    @Override
    public void findFocus(Company company){
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(
                new CameraPosition(new LatLng(company.getLat(),
                company.getLongatude()),
                        18f,0f,0f));
        binding.mapsRecycler.setVisibility(View.GONE);
        binding.mapsSearch.setQuery("",false);
        binding.mapsSearch.setIconified(true);
        InputMethodManager imm = (InputMethodManager)this.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(binding.mapsSearch.getWindowToken(), 0);
        if(centered){
            binding.call.callOnClick();
        }
        mMap.animateCamera(cameraUpdate);
    }


}
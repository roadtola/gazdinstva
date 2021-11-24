package com.ebookfrenzy.gazdinstvaa.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import org.jetbrains.annotations.NotNull;

@Entity(tableName = "company")
public class Company implements ClusterItem {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @NotNull
    @ColumnInfo(name = "company_name")
    private String companyName = "";

    @NotNull
    @ColumnInfo(name = "phone_number")
    private String phoneNumber = "";

    @NotNull
    @ColumnInfo(name = "contact_person")
    private String contactPerson = "";

    @NotNull
    @ColumnInfo(name = "address")
    private String address = "";

    @NotNull
    @ColumnInfo(name = "place")
    private String place = "";

    @NotNull
    @ColumnInfo(name = "lat")
    private double lat = 0D;

    @NotNull
    @ColumnInfo(name = "long")
    private double longatude = 0D;

    @NotNull
    @ColumnInfo(name = "expanded")
    private boolean expanded = false;

    public Company() {
    }

    public Company(int id, @NotNull String companyName, @NotNull String phoneNumber, @NotNull String contactPerson, @NotNull String address, @NotNull String place, @NotNull double lat, @NotNull double longatude, @NotNull boolean expanded) {
        this.id = id;
        this.companyName = companyName;
        this.phoneNumber = phoneNumber;
        this.contactPerson = contactPerson;
        this.address = address;
        this.place = place;
        this.lat = lat;
        this.longatude = longatude;
        this.expanded = expanded;
    }

    public Company(@NotNull String companyName, @NotNull String phoneNumber, @NotNull String contactPerson, @NotNull String address, @NotNull String place) {
        this.companyName = companyName;
        this.phoneNumber = phoneNumber;
        this.contactPerson = contactPerson;
        this.address = address;
        this.place = place;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLongatude() {
        return longatude;
    }

    public void setLongatude(double longatude) {
        this.longatude = longatude;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    @Override
    public LatLng getPosition() {
        return new LatLng(this.lat,this.longatude);
    }

    @Override
    public String getTitle() {
        return this.companyName;
    }

    @Override
    public String getSnippet() {
        return this.contactPerson + ": " + this.phoneNumber + "\n"
                + "Adresa: " + this.address + "," + this.place;
    }


}

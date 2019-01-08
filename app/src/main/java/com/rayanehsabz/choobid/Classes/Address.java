package com.rayanehsabz.choobid.Classes;

import org.json.JSONObject;

public class Address {

    public long id;
    public long accountId;
    public String status;
    public String cityId;
    public String postalCode;
    public String street;
    public String alley;
    public String number;
    public String floor;
    public String apartmentUnit;
    public String recepterFullName;
    public String mobile;
    public String phone;

    public boolean selected = false;

    public Address() {}

    public Address(JSONObject jso) throws Exception {

        this.id = jso.getLong("id");
        this.accountId = jso.getLong("accountId");
        this.status = jso.getString("status");
        this.cityId = jso.getString("cityId");
        this.postalCode = jso.getString("postalCode");
        this.street = jso.getString("street");
        this.alley = jso.getString("alley");
        this.number = jso.getString("number");
        this.floor = jso.getString("floor");
        this.apartmentUnit = jso.getString("apartmentUnit");
        this.recepterFullName = jso.getString("recepterFullName");
        this.mobile = jso.getString("mobile");
        this.phone = jso.getString("phone");

    }

}

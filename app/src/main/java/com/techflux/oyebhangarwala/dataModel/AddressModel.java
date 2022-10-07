package com.techflux.oyebhangarwala.dataModel;

/**
 * Created by Lenovo on 05/06/2017.
 */
public class AddressModel {
    String Id,Name,Mobile,Address1,Address2,Address3,City,State,Country;


    public AddressModel(String id, String name, String mobile, String address1, String address2, String address3, String city, String state, String country) {
        Id = id;
        Name = name;
        Mobile = mobile;
        Address1 = address1;
        Address2 = address2;
        Address3 = address3;
        City = city;
        State = state;
        Country = country;
    }

    public AddressModel(String address2, String address3, String address1, String city, String state, String country, String id) {
        Address2 = address2;
        Address3 = address3;
        Address1 = address1;
        City = city;
        State = state;
        Country = country;
        Id = id;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getMobile() {
        return Mobile;
    }

    public void setMobile(String mobile) {
        Mobile = mobile;
    }

    public String getAddress1() {
        return Address1;
    }

    public void setAddress1(String address1) {
        Address1 = address1;
    }

    public String getAddress2() {
        return Address2;
    }

    public void setAddress2(String address2) {
        Address2 = address2;
    }

    public String getAddress3() {
        return Address3;
    }

    public void setAddress3(String address3) {
        Address3 = address3;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }

    public String getCountry() {
        return Country;
    }

    public void setCountry(String country) {
        Country = country;
    }
}

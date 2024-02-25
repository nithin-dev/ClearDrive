package com.example.cleardrive;

public class Mechanic {
    private String name;
    private String address;
    private int imageResource;
    private double price;
    private String pinCode;
    private String email;
    private String mobile;




    public Mechanic(String name, String address, int imageResource, double price, String pinCode,String email, String mobile) {
        this.name = name;
        this.address = address;
        this.imageResource = imageResource;
        this.price = price;
        this.pinCode = pinCode;
        this.email = email;
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPinCode() {
        return pinCode;
    }

    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public int getImageResource() {
        return imageResource;
    }

    public double getPrice() {
        return price;
    }
}

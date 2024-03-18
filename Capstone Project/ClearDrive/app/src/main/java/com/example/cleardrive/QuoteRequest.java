package com.example.cleardrive;

public class QuoteRequest {
    private String mechanicName;
    private String mechanicEmail;
    private String ownerName;
    private String ownerAddress;
    private String mobileNumber;
    private String carMake;
    private String carModel;
    private String carYear;
    private double amountInCAD;

    // Constructor
    public QuoteRequest(String mechanicName, String mechanicEmail, String ownerName, String ownerAddress,
                          String mobileNumber, String carMake, String carModel, String carYear, double amountInCAD) {
        this.mechanicName = mechanicName;
        this.mechanicEmail = mechanicEmail;
        this.ownerName = ownerName;
        this.ownerAddress = ownerAddress;
        this.mobileNumber = mobileNumber;
        this.carMake = carMake;
        this.carModel = carModel;
        this.carYear = carYear;
        this.amountInCAD = amountInCAD;
    }

    // Getters and setters
    public String getMechanicName() {
        return mechanicName;
    }

    public void setMechanicName(String mechanicName) {
        this.mechanicName = mechanicName;
    }

    public String getMechanicEmail() {
        return mechanicEmail;
    }

    public void setMechanicEmail(String mechanicEmail) {
        this.mechanicEmail = mechanicEmail;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerAddress() {
        return ownerAddress;
    }

    public void setOwnerAddress(String ownerAddress) {
        this.ownerAddress = ownerAddress;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getCarMake() {
        return carMake;
    }

    public void setCarMake(String carMake) {
        this.carMake = carMake;
    }

    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public String getCarYear() {
        return carYear;
    }

    public void setCarYear(String carYear) {
        this.carYear = carYear;
    }

    public double getAmountInCAD() {
        return amountInCAD;
    }

    public void setAmountInCAD(double amountInCAD) {
        this.amountInCAD = amountInCAD;
    }
}

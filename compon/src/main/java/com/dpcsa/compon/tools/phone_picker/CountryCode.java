package com.dpcsa.compon.tools.phone_picker;

import androidx.annotation.NonNull;

public class CountryCode implements Comparable<CountryCode>{
    private int code;
    private String countryName;
    private String codeS;
    private String region;
    private boolean isPopular = false;

    public CountryCode() {
        countryName = "";
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    @Override
    public String toString() {
        return "+" + code + " " + countryName;
    }

    @Override
    public int compareTo(@NonNull CountryCode o) {
        String compareValue = o.getCountryName();
        return countryName.compareTo(compareValue);
    }

    public boolean isPopular() {
        return isPopular;
    }

    public void setPopular(boolean popular) {
        isPopular = popular;
    }

    public String getCodeS() {
        return codeS;
    }

    public void setCodeS(String codeS) {
        this.codeS = codeS;
    }

}

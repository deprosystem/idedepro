package com.dpcsa.compon.tools.phone_picker;

import androidx.annotation.Nullable;

import java.util.List;

public class CountryCodesResponse {
    private List<CountryCode> countryCodes;
    private int specialItemsAmount;

    public List<CountryCode> getCountryCodes() {
        return countryCodes;
    }

    public void setCountryCodes(List<CountryCode> countryCodes) {
        this.countryCodes = countryCodes;
    }

    public int getSpecialItemsAmount() {
        return specialItemsAmount;
    }

    public void setSpecialItemsAmount(int specialItemsAmount) {
        this.specialItemsAmount = specialItemsAmount;
    }

    @Nullable
    public CountryCode getItem(int pos) {
        if (pos == -1) {
            return null;
        }

        if (countryCodes == null || countryCodes.size() < pos) {
            return null;
        }

        return countryCodes.get(pos);
    }

}

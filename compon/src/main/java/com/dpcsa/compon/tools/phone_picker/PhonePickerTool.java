package com.dpcsa.compon.tools.phone_picker;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import rx.Observable;
import com.dpcsa.compon.single.Injector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class PhonePickerTool {
//    private static final int SPECIAL_CODES[] = {380, 48, 995, 374, 994};
    private int[] SPECIAL_CODES;
    private static final int DEFAULT_COUNTRY_CODE = 968;
    private PhoneNumberUtil phoneNumberUtil;
    private Context context;
    private CountryCodesResponse lastResponse;
    private Locale locale;

    public PhonePickerTool(PhoneNumberUtil phoneNumberUtil, Context context, String popularCode) {
        this.phoneNumberUtil = phoneNumberUtil;
        this.context = context;
        locale = new Locale(Injector.getPreferences().getLocale());
        if (popularCode != null && popularCode.length() > 0) {
            String[] stPopular = popularCode.split(",");
            int ik = stPopular.length;
            if (ik > 0) {
                SPECIAL_CODES = new int[stPopular.length];
                for (int i = 0; i < ik; i++) {
                    String st = stPopular[i].trim();
                    SPECIAL_CODES[i] = Integer.valueOf(st);
                }
            }
        }
    }

    private String getRegionCode() {
        final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (tm != null) {
            return tm.getSimCountryIso().toUpperCase(Locale.US);
        }
        return "";
    }

    public Observable<CountryCodesResponse> createCodesList() {
        return Observable.fromCallable(() -> {
            if (lastResponse == null) {
                HashSet<Integer> specialCodesSet = createSpecialCodesList();
                Set<String> regionsList = getSupportedRegions();
                List<CountryCode> specialItems = new ArrayList<>();
                List<CountryCode> notSpecialItems = new ArrayList<>();
                for (String region : regionsList) {
                    CountryCode countryCode = createCountryCode(region);
                    countryCode.setCodeS("+" + countryCode.getCode());

                    if (specialCodesSet.contains(countryCode.getCode())) {
                        countryCode.setPopular(true);
                        specialItems.add(countryCode);
                    } else {
                        countryCode.setPopular(false);
                        notSpecialItems.add(countryCode);
                    }
                }

                lastResponse = new CountryCodesResponse();
                Collections.sort(notSpecialItems);
                int specialItemsFound = specialItems.size();
                List<CountryCode> result = sortSpecialItems(specialItems);
                // result.add(codeHeader);
                result.addAll(notSpecialItems);

                lastResponse.setSpecialItemsAmount(specialItemsFound);
                lastResponse.setCountryCodes(result);
            }

            return lastResponse;
        });
    }

    private List<CountryCode> sortSpecialItems(List<CountryCode> specialItems) {
        List<CountryCode> result = new ArrayList<>();

        /*CountryCode currentCode = findItem(specialItems, findCurrentCountryCode());
        if (currentCode != null) {
            result.add(currentCode);
        }*/

        if (SPECIAL_CODES != null && SPECIAL_CODES.length > 0) {
            for (Integer specialCode : SPECIAL_CODES) {
                CountryCode countryCode = findItem(specialItems, specialCode);
                if (countryCode != null) {
                    result.add(countryCode);
                }
            }
        }
        return result;
    }

    private CountryCode findItem(List<CountryCode> items, int code) {
        for (CountryCode item : items) {
            if (item.getCode() == code) {
                return item;
            }
        }

        return null;
    }

    private CountryCode createCountryCode(String region) {
        CountryCode countryCode = new CountryCode();
        int code = phoneNumberUtil.getCountryCodeForRegion(region);
        String countryName = getRegionDisplayName(region, locale);
        countryCode.setRegion(region);
        countryCode.setCode(code);
        countryCode.setCountryName(countryName);

        return countryCode;
    }

    private HashSet<Integer> createSpecialCodesList() {
        HashSet<Integer> result = new HashSet<>();
        if (SPECIAL_CODES != null && SPECIAL_CODES.length > 0) {
            for (int code : SPECIAL_CODES) {
                result.add(code);
            }
        }
        int currentCountryCode = findCurrentCountryCode();
        if (!result.contains(currentCountryCode)) {
            result.add(currentCountryCode);
        }

        return result;
    }

    public int findCurrentCountryCode() {
        String regionCode = getRegionCode();
        if (regionCode.isEmpty()) {
            return DEFAULT_COUNTRY_CODE;
        } else {
            return phoneNumberUtil.getCountryCodeForRegion(getRegionCode());
        }
    }

    private String getRegionDisplayName(String regionCode, Locale language) {
        return (regionCode == null || regionCode.equals("ZZ") ||
                regionCode.equals(PhoneNumberUtil.REGION_CODE_FOR_NON_GEO_ENTITY))
                ? "" : new Locale("", regionCode).getDisplayCountry(language);
    }

    public Phonenumber.PhoneNumber tryParsePhone(String phoneNumber) throws NumberParseException {
        return phoneNumberUtil.parse(phoneNumber, "");
    }

    @SuppressWarnings("unchecked")
    private Set<String> getSupportedRegions() {
        try {
            return (Set<String>) phoneNumberUtil.getClass()
                    .getMethod("getSupportedRegions")
                    .invoke(phoneNumberUtil);
        } catch (NoSuchMethodException e) {
            try {
                return (Set<String>) phoneNumberUtil.getClass()
                        .getMethod("getSupportedCountries")
                        .invoke(phoneNumberUtil);
            } catch (Exception helpme) {
                // ignored
            }
        } catch (Exception e) {
            // ignored
        }
        return new HashSet<>();
    }

    public int findCountryCodePosition(int codeToFind) {
        List<CountryCode> codeList = lastResponse.getCountryCodes();
        for (int i = 0; i < codeList.size(); i++) {
            CountryCode countryCode = codeList.get(i);

            if (countryCode.getCode() == codeToFind) {
                return i;
            }
        }

        return -1;
    }

}

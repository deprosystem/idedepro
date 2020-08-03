package com.dpcsa.compon.tools.phone_picker;

import android.content.Context;
import android.util.Log;

import com.google.i18n.phonenumbers.PhoneNumberUtil;

import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import com.dpcsa.compon.base.BaseInternetProvider;
import com.dpcsa.compon.interfaces_classes.IBase;
import com.dpcsa.compon.interfaces_classes.IPresenterListener;
import com.dpcsa.compon.json_simple.Field;
import com.dpcsa.compon.json_simple.ListRecords;
import com.dpcsa.compon.json_simple.Record;

public class GetCountryCode {

    Context context;
    CompositeSubscription subscriptionsToUnbind;
    IPresenterListener listener;
    IBase iBase;

    public void getCountryCode(IBase iBase, IPresenterListener listener, String popularCode) {
        this.iBase = iBase;
        this.listener = listener;
        context = iBase.getBaseActivity();
        subscriptionsToUnbind = new CompositeSubscription();
        ListRecords listRecords = new ListRecords();
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        PhonePickerTool phonePicker = new PhonePickerTool(phoneNumberUtil, context, popularCode);
        Subscription subscription = phonePicker.createCodesList()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    response(response);
                }, throwable -> {
                    error(BaseInternetProvider.COUNTRY_CODE, throwable.getMessage());
                });
        subscriptionsToUnbind.add(subscription);
    }

    private void response(CountryCodesResponse response) {
        subscriptionsToUnbind.clear();
        if (response != null) {
            List<CountryCode> countryCodes = response.getCountryCodes();
            if (countryCodes != null && countryCodes.size() > 0) {
                ListRecords listRecords = new ListRecords();
                for (CountryCode cc : countryCodes) {
                    Record rec = new Record();
                    rec.add(new Field("code", Field.TYPE_INTEGER, cc.getCode()));
                    rec.add(new Field("countryName", Field.TYPE_STRING, cc.getCountryName()));
                    rec.add(new Field("codePlus", Field.TYPE_STRING, cc.getCodeS()));
                    rec.add(new Field("region", Field.TYPE_STRING, cc.getRegion()));
                    if (cc.isPopular()) {
                        rec.add(new Field("isPopular", Field.TYPE_INTEGER, 1));
                    } else {
                        rec.add(new Field("isPopular", Field.TYPE_INTEGER, 0));
                    }
                    listRecords.add(rec);
                }
                listener.onResponse(new Field("", Field.TYPE_LIST_RECORD, listRecords));
            }
        }
    }

    private void error(int statusCode, String message) {
        subscriptionsToUnbind.clear();
        iBase.showDialog(statusCode, message, null);
    }
}

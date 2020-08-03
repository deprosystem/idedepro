package com.dpcsa.compon.components;

import android.app.SearchManager;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import com.dpcsa.compon.base.BaseComponent;
import com.dpcsa.compon.base.Screen;
import com.dpcsa.compon.interfaces_classes.ActionsAfterResponse;
import com.dpcsa.compon.interfaces_classes.ActivityResult;
import com.dpcsa.compon.interfaces_classes.IBase;
import com.dpcsa.compon.json_simple.Field;
import com.dpcsa.compon.json_simple.Record;
import com.dpcsa.compon.param.ParamComponent;
import com.dpcsa.compon.tools.Constants;

import static android.app.Activity.RESULT_OK;

public class RecognizeVoiceComponent extends BaseComponent {

    public View view;
    public TextView textView;

    public RecognizeVoiceComponent(IBase iBase, ParamComponent paramMV, Screen multiComponent) {
        super(iBase, paramMV, multiComponent);
    }

    @Override
    public void initView() {
        if (paramMV.paramView == null || paramMV.paramView.viewId != 0) {
            view = parentLayout.findViewById(paramMV.paramView.viewId);
            textView = (TextView) parentLayout.findViewById(paramMV.paramView.layoutTypeId[0]);
        }
        if (view == null) {
            iBase.log("0009 Не найден ClickView для RecognizeVoiceComponent в " + multiComponent.nameComponent);
            return;
        }
        if (textView == null) {
            iBase.log("0009 Не найден TextView для вывода распознаного текста в " + multiComponent.nameComponent);
            return;
        }

        view.setOnClickListener(clickMicrophone);
    }

    View.OnClickListener clickMicrophone = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass().getPackage().getName());
//            if (textView != null) {
//                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, textView.getText().toString());
//            }
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
            intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
            activity.addForResult(Constants.REQUEST_CODE_VOICE_RECOGNITION, activityResult);
            activity.startActivityForResult(intent, Constants.REQUEST_CODE_VOICE_RECOGNITION);
        }
    };

    private ActivityResult activityResult = new ActivityResult() {
        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data, ActionsAfterResponse afterResponse) {
            if (resultCode == RESULT_OK){
                ArrayList<String> textMatchlist = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                if (!textMatchlist.isEmpty()){
                    if (textMatchlist.get(0).contains("search")){
                        String searchQuery = textMatchlist.get(0).replace("search"," ");
                        Intent search = new Intent(Intent.ACTION_WEB_SEARCH);
                        search.putExtra(SearchManager.QUERY,searchQuery);
                        activity.startActivity(search);
                    }
                    else {
                        String st = "";
                        String sep = "";
                        for (String stV : textMatchlist) {
                            st += sep + stV;
                            sep = " ";
                        }
                        if (textView != null) {
                            textView.setText(st);
                        } else {
                            Record rec = new Record();
                            rec.add(new Field("", Field.TYPE_STRING, st));
                            setParam(paramMV.startScreen, rec);
                            if (paramMV.after != null) {
                                afterHandler(new Field("", Field.TYPE_RECORD, rec), paramMV.after.viewHandlers);
                            }
                        }
                    }
                }
            }
            else {
                String stError = "";
                if (resultCode == RecognizerIntent.RESULT_AUDIO_ERROR) {
                    stError = "Audio Error";
                } else if ((resultCode == RecognizerIntent.RESULT_CLIENT_ERROR)) {
                    stError = "Client Error";
                } else if (resultCode == RecognizerIntent.RESULT_NETWORK_ERROR) {
                    stError = "Network Error";
                } else if (resultCode == RecognizerIntent.RESULT_NO_MATCH) {
                    stError = "No Match";
                } else if (resultCode == RecognizerIntent.RESULT_SERVER_ERROR) {
                    stError = "Server Error";
                }
                iBase.log(stError);
            }
        }
    };

    @Override
    public void changeData(Field field) {

    }
}

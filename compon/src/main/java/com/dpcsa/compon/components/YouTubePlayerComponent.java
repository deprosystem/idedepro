package com.dpcsa.compon.components;

import android.view.View;
import android.widget.Toast;

import com.dpcsa.compon.base.BaseComponent;
import com.dpcsa.compon.base.BaseFragment;
import com.dpcsa.compon.base.Screen;
import com.dpcsa.compon.custom_components.ComponYouTubePlayer;
import com.dpcsa.compon.interfaces_classes.IBase;
import com.dpcsa.compon.json_simple.Field;
import com.dpcsa.compon.param.ParamComponent;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

public class YouTubePlayerComponent extends BaseComponent {

    public View view;
    public String apiKey;
    public YouTubePlayerSupportFragment youTubePlayerFragment;
    public String VIDEO_ID;

    public YouTubePlayerComponent(IBase iBase, ParamComponent paramMV, Screen multiComponent) {
        super(iBase, paramMV, multiComponent);
    }

    @Override
    public void initView() {
        view = parentLayout.findViewById(paramMV.paramView.viewId);
        if (view == null) {
            iBase.log("Не найден YouTubePlayer в " + multiComponent.nameComponent);
            return;
        }
        if (view instanceof ComponYouTubePlayer) {
            ((ComponYouTubePlayer) view).setYoutubeComponent(this);
        }
        if (componGlob.appParams.youtubeApiKey == 0) {
            iBase.log("Нет ApiKey для YouTubePlayer в " + multiComponent.nameComponent);
            return;
        } else {
            apiKey = activity.getString(componGlob.appParams.youtubeApiKey);
        }
        youTubePlayerFragment = YouTubePlayerSupportFragment.newInstance();
        BaseFragment bf = iBase.getBaseFragment();
        if (bf == null) {
            activity.getSupportFragmentManager().beginTransaction().replace(paramMV.paramView.viewId,
                    youTubePlayerFragment, "youFF").commit();
        } else {
            bf.getChildFragmentManager().beginTransaction().replace(paramMV.paramView.viewId,
                    youTubePlayerFragment, "youFF").commit();
        }
        paramMV.startActual = false;
        if (paramMV.paramView.fieldType != null && paramMV.paramView.fieldType.length() > 0) {
            startVideo(paramMV.paramView.fieldType);
        }
    }

    public void startVideo(String adr) {
        int i = adr.lastIndexOf("=");
        if (i > -1) {
            VIDEO_ID = adr.substring(i + 1);
        } else {
            VIDEO_ID = adr.substring(adr.lastIndexOf("/") + 1);
        }
        if (VIDEO_ID != null && VIDEO_ID.length() > 0) {
            youTubePlayerFragment.initialize(apiKey, listener);
        }
    }

    YouTubePlayer.OnInitializedListener listener = new YouTubePlayer.OnInitializedListener() {
        @Override
        public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
//            youTubePlayer.setFullscreenControlFlags(YouTubePlayer.FULLSCREEN_FLAG_CONTROL_ORIENTATION |
//                    YouTubePlayer.FULLSCREEN_FLAG_ALWAYS_FULLSCREEN_IN_LANDSCAPE);
            youTubePlayer.setFullscreen(false);
            youTubePlayer.setShowFullscreenButton(false);
            if(!b) {
                youTubePlayer.cueVideo(VIDEO_ID);
            }
        }

        @Override
        public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult error) {
            final int REQUEST_CODE = 1;

            if(error.isUserRecoverableError()) {
                error.getErrorDialog(activity, REQUEST_CODE).show();
            } else {
                String errorMessage = String.format("There was an error initializing the YoutubePlayer (%1$s)", error.toString());
                Toast.makeText(activity, errorMessage, Toast.LENGTH_LONG).show();
            }
        }
    };

    @Override
    public void changeData(Field field) {

    }
}

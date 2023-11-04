package com.dpcsa.compon.components;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.view.View;
import com.dpcsa.compon.base.BaseComponent;
import com.dpcsa.compon.base.BasePresenter;
import com.dpcsa.compon.base.Screen;
import com.dpcsa.compon.custom_components.ComponImageView;
import com.dpcsa.compon.custom_components.Gallery;
import com.dpcsa.compon.interfaces_classes.ActionsAfterResponse;
import com.dpcsa.compon.interfaces_classes.ActivityResult;
import com.dpcsa.compon.interfaces_classes.IBase;
import com.dpcsa.compon.interfaces_classes.IPresenterListener;
import com.dpcsa.compon.interfaces_classes.PermissionsResult;
import com.dpcsa.compon.json_simple.Field;
import com.dpcsa.compon.json_simple.Record;
import com.dpcsa.compon.param.ParamComponent;
import com.dpcsa.compon.param.ParamModel;
import com.dpcsa.compon.tools.Constants;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import jp.wasabeef.glide.transformations.BlurTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;
import static com.bumptech.glide.request.RequestOptions.circleCropTransform;
import static com.dpcsa.compon.param.ParamModel.POST;

public class EditGalleryComponent extends BaseComponent {

    Gallery gallery;
    private Uri photoURI;
    private String photoPath;
    private String imgPath;
    private View addView, delView, moveView;

    public EditGalleryComponent(IBase iBase, ParamComponent paramMV, Screen multiComponent) {
        super(iBase, paramMV, multiComponent);
    }

    @Override
    public void initView() {
        gallery = (Gallery) parentLayout.findViewById(paramMV.paramView.viewId);
        if (gallery == null) {
            iBase.log( "Не найден Gallery в " + multiComponent.nameComponent);
            return;
        }
        paramMV.startActual = false;
        int addId = paramMV.paramView.layoutTypeId[0];
        int delId = paramMV.paramView.layoutTypeId[1];
        int moveId = paramMV.paramView.layoutTypeId[2];

        addView = parentLayout.findViewById(addId);
        addView.setOnClickListener(clicAddkListener);
        delView = parentLayout.findViewById(delId);
        delView.setOnClickListener(clickDelListener);
        moveView = parentLayout.findViewById(moveId);
        moveView.setOnClickListener(clickMoveListener);
    }

    public String getFilePath() {
        return imgPath + "";
    }

    View.OnClickListener clicAddkListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            obtainingPermits();
        }
    };

    View.OnClickListener clickDelListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String qu = paramMV.paramModel.url + "del_img";
            ParamModel pm = new ParamModel(POST,qu);
            int pos = gallery.getCurrentItem();
            if (pos < 0) {
                return;
            }
            String nameF = (String) gallery.listData.get(pos).value;
            Record rec = new Record();
            rec.addField(new Field("name", Field.TYPE_STRING, nameF));
            new BasePresenter(iBase, pm, null, rec, listener_del_img);
        }
    };

    public IPresenterListener listener_del_img = new IPresenterListener() {

        @Override
        public void onResponse(Field response) {
            int pos = gallery.getCurrentItem();
            gallery.listData.remove(pos);
            gallery.setAdapter(gallery.adapter);
            if (pos >= gallery.listData.size()) {
                pos = gallery.listData.size();
            }
            gallery.setCurrentItem(pos);
        }

        @Override
        public void onError(int statusCode, String message, View.OnClickListener click) {

        }
    };

    View.OnClickListener clickMoveListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int pos = gallery.getCurrentItem();
            if (pos > 0) {
                Collections.swap(gallery.listData, pos - 1, pos);
//            gallery.listData.add(pos, new Field("", Field.TYPE_STRING, rec.getString("photo")));
                gallery.setAdapter(gallery.adapter);
                gallery.setCurrentItem(pos - 1);
            }
        }
    };

    public void obtainingPermits() {
        if(ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            obtainingPermitsStorage();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions();
            }
        }
    }

    public void requestPermissions() {
        activity.addPermissionsResult(Constants.REQUEST_CODE_CAMERA, permissionsResult);
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA},
                Constants.REQUEST_CODE_CAMERA);
    }

    public PermissionsResult permissionsResult = new PermissionsResult() {
        @Override
        public void onPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
            if (requestCode == Constants.REQUEST_CODE_CAMERA && grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    obtainingPermitsStorage();
                }
            }
        }
    };

    private void obtainingPermitsStorage() {
        if(ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            startPhoto();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissionsStorage();
            }
        }
    }


    public void requestPermissionsStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.addPermissionsResult(Constants.REQUEST_CODE_WRITE_EXTERNAL_STORAGE, permissionsResultStorage);
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    Constants.REQUEST_CODE_WRITE_EXTERNAL_STORAGE);
        }
    }

    public PermissionsResult permissionsResultStorage = new PermissionsResult() {
        @Override
        public void onPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
            if (requestCode == Constants.REQUEST_CODE_WRITE_EXTERNAL_STORAGE && grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startPhoto();
                }
            }
        }
    };

    private void startPhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            iBase.log("Error occurred while creating the File");
        }
        if (photoFile != null) {
            photoURI = FileProvider.getUriForFile(activity,
                    activity.getPackageName() +".provider",
                    photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
        }
        List<Intent> intentList = new ArrayList<>();
        Intent pickIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intentList = addIntentsToList(activity, intentList, pickIntent);
        intentList = addIntentsToList(activity, intentList, takePictureIntent);
        Intent chooserIntent = null;
        String st;
        if (paramMV.paramView.idStringExtra == 0) {
            st = "";
        } else {
            st = activity.getString(paramMV.paramView.idStringExtra);
        }
        if(intentList.size() > 0) {
            chooserIntent = Intent.createChooser(intentList.remove(intentList.size() - 1), st);
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentList.toArray(new Parcelable[]{}));
        }
        activity.addForResult(Constants.REQUEST_CODE_PHOTO, activityResult);
        activity.startActivityForResult(chooserIntent, Constants.REQUEST_CODE_PHOTO);
    }

    private List<Intent> addIntentsToList(Context context, List<Intent> list, Intent intent) {
        List<ResolveInfo> resInfo = context.getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resInfo) {
            String packageName = resolveInfo.activityInfo.packageName;
            Intent targetedIntent = new Intent(intent);
            targetedIntent.setPackage(packageName);
            targetedIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            list.add(targetedIntent);
        }
        return list;
    }

    private ActivityResult activityResult = new ActivityResult() {
        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data, ActionsAfterResponse afterResponse) {
            if(resultCode == activity.RESULT_OK) {
                Uri selectedImage;
                if (data == null) {
                    selectedImage = null;
                } else {
                    selectedImage = data.getData();
                }
                imgPath = null;
                if (selectedImage != null) {
                    imgPath = getRealPathFromURI(selectedImage);
                } else {
                    imgPath = photoPath;
                }
                showImg(imgPath);
                if (imgPath != null && imgPath.length() > 0) {
                    if (paramMV.paramForPathFoto != null) {
                        componGlob.setParamValue(paramMV.paramForPathFoto, imgPath);
                    }
                }
            }
        }
    };

    private void showImg(String url) {
        Record rec = new Record();
        rec.addField(new Field("photo", Field.TYPE_FILE_PATH, url));
        String qu = paramMV.paramModel.url + "save_img";
        ParamModel pm = new ParamModel(POST, qu);
        new BasePresenter(iBase, pm, null, rec, listener_save_img);
    }

    public IPresenterListener listener_save_img = new IPresenterListener() {
        @Override
        public void onResponse(Field response) {
            Record rec = (Record) response.value;
            int pos = gallery.getCurrentItem();
            gallery.listData.add(pos, new Field("", Field.TYPE_STRING, rec.getString("photo")));
            gallery.setAdapter(gallery.adapter);
            gallery.setCurrentItem(pos);
        }

        @Override
        public void onError(int statusCode, String message, View.OnClickListener click) {
            onErrorModel(statusCode, message, click);
        }
    };

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName,".jpg", storageDir);
        photoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void changeData(Field field) {

    }

    public String getRealPathFromURI(Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = activity.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}

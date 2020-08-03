package com.dpcsa.compon.components;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import androidx.core.app.ActivityCompat;

import android.view.View;
import android.widget.TextView;
import android.hardware.Camera;

import com.dpcsa.compon.base.BaseComponent;
import com.dpcsa.compon.base.Screen;
import com.dpcsa.compon.json_simple.Field;
import com.google.zxing.Result;

import com.dpcsa.compon.custom_components.BarcodeScanner;
import com.dpcsa.compon.interfaces_classes.IBase;
import com.dpcsa.compon.interfaces_classes.OnResumePause;
import com.dpcsa.compon.interfaces_classes.PermissionsResult;
import com.dpcsa.compon.param.ParamComponent;
import com.dpcsa.compon.tools.Constants;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class BarcodeComponent extends BaseComponent {

    public BarcodeScanner scanner;
    private String rawResult;
    private TextView viewResult;
    private View repeat, torch;
    private Camera camera;

    public BarcodeComponent(IBase iBase, ParamComponent paramMV, Screen multiComponent) {
        super(iBase, paramMV, multiComponent);
    }

    @Override
    public void initView() {
        if (paramMV.paramView == null || paramMV.paramView.viewId != 0) {
            scanner = (BarcodeScanner) parentLayout.findViewById(paramMV.paramView.viewId);
        }
//        Log.d("QWERT","BarcodeComponent scanner="+scanner);
        if (scanner == null) {
            iBase.log("0009 Не найден BarcodeScanner в " + multiComponent.nameComponent);
            return;
        }
        if (paramMV.paramView.layoutTypeId != null) {
            int[] lt = paramMV.paramView.layoutTypeId;
            if (lt.length > 1) {
                int id = lt[1];
                if (id != 0) {
                    viewResult = (TextView) parentLayout.findViewById(id);
                } else {
                    iBase.log("0009 Не найден элемент для отображения результата в " + multiComponent.nameComponent);
                }
                if (lt.length > 2) {
                    id = lt[2];
                    if (id != 0) {
                        repeat = parentLayout.findViewById(id);
                        if (repeat != null) {
                            repeat.setOnClickListener(repeatListener);
                        } else {
                            iBase.log("0009 Не найден элемент для кнопки 'обновить' в " + multiComponent.nameComponent);
                        }
                    }
                    if (lt.length > 3) {
                        id = lt[3];
                        if (id != 0) {
                            torch = parentLayout.findViewById(id);
                            if (torch != null) {
                                torch.setOnClickListener(torchListener);
                            } else {
                                iBase.log("0009 Не найден элемент для кнопки 'фонарик' в " + multiComponent.nameComponent);
                            }
                        }
                    }
                }
            }

        }

        iBase.setResumePause(resumePause);

        if(ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            initiateScannerView();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions();
            }
        }
    }

    @Override
    public void changeData(Field field) {

    }

    View.OnClickListener repeatListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(scanner != null) {
                scanner.setResultHandler(null);
                scanner.stopCamera();
                scanner.setAutoFocus(true);
                scanner.setResultHandler(resultHandler);
                scanner.startCamera();
            }
        }
    };

    View.OnClickListener torchListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (camera == null) {
                try {
                    camera = Camera.open();
                } catch (Exception e) {
                }
            }
            Camera.Parameters parameters = camera.getParameters();
            if (v.isSelected()) {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                v.setSelected(false);
            } else {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                v.setSelected(true);
            }
            camera.setParameters(parameters);
            camera.startPreview();
        }
    };

    OnResumePause resumePause = new OnResumePause() {
        @Override
        public void onResume() {
            if(scanner != null) {
                scanner.setAutoFocus(true);
                scanner.setResultHandler(resultHandler);
                scanner.startCamera();
            }
        }

        @Override
        public void onPause() {
            if(scanner != null) {
                scanner.setResultHandler(null);
                scanner.stopCamera();
            }
            if (camera != null) {
                camera.release();
                camera = null;
            }
//            scanner = null;
        }
    };

    ZXingScannerView.ResultHandler resultHandler = new ZXingScannerView.ResultHandler() {
        @Override
        public void handleResult(Result result) {
            Ringtone r = null;
            try {
                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                r = RingtoneManager.getRingtone(activity.getApplicationContext(), notification);
                r.play();
            } catch (Exception e) {}
            rawResult = result.getText();
//            if (viewResult != buttonView) {
//                viewResult.setText(rawResult);
//            }
            viewResult.setText(rawResult);
            scanner.result = rawResult;
        }
    };

    public void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.addPermissionsResult(Constants.REQUEST_CODE_CAMERA, permissionsResult);
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA},
                    Constants.REQUEST_CODE_CAMERA);
        }
    }

    public PermissionsResult permissionsResult = new PermissionsResult() {
        @Override
        public void onPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
            if (requestCode == Constants.REQUEST_CODE_CAMERA && grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initiateScannerView();
                }
            }
        }
    };

    private void initiateScannerView() {
        scanner.setAutoFocus(true);
    }



}

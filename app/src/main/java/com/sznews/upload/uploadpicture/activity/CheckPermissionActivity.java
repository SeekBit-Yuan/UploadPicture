package com.sznews.upload.uploadpicture.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.sznews.upload.uploadpicture.R;

public class CheckPermissionActivity extends AppCompatActivity {

    private AlertDialog dialog;

    //动态获取SD卡权限
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE};

    private int permissionCount = 0;//统计以获取的

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_permission);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            System.out.println("========================系统大于23");
            // 检查该权限是否已经获取
            for (int i = 0; i < PERMISSIONS_STORAGE.length; i++) {
                System.out.println("==========================检查权限");
                int j = ContextCompat.checkSelfPermission(this, PERMISSIONS_STORAGE[i]);
                // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
                if (j != PackageManager.PERMISSION_GRANTED) {
                    System.out.println("================申请授权");
                    // 如果没有授予该权限，就去提示用户请求
                    //showDialogTipUserRequestPermission();
                    startRequestPermission();
                } else {
                    ++permissionCount;
                }
            }
            //获取到所有所需权限时跳转至登录页面
            if(permissionCount == PERMISSIONS_STORAGE.length) {
                //跳转到登录页面
                Intent intent;
                intent = new Intent(CheckPermissionActivity.this, MainActivity.class);
                startActivity(intent);
            }

        } else {
            Intent intent;
            intent = new Intent(CheckPermissionActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

    // 开始提交请求权限
    private void startRequestPermission() {
        ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, 321);
    }

    // 用户权限 申请 的回调方法
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        System.out.println("onRequestPermissionsResult");
        if (requestCode == 321) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        // 判断用户是否 点击了不再提醒。(检测该权限是否还可以申请)
                        boolean b = shouldShowRequestPermissionRationale(permissions[0]);
                        if (!b) {
                            // 用户还是想用我的 APP 的
                            // 提示用户去应用设置界面手动开启权限
                            showDialogTipUserGoToAppSettting();
                            break;
                        } else
                            finish();
                    } else {
                        Toast.makeText(this, "权限获取成功0", Toast.LENGTH_SHORT).show();
                        Intent intent;
                        intent = new Intent(CheckPermissionActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                }
            }
        }
    }

    // 提示用户去应用设置界面手动开启权限

    private void showDialogTipUserGoToAppSettting() {

        dialog = new AlertDialog.Builder(this)
                .setTitle("存储权限不可用")
                .setMessage("请在-应用设置-权限-中，允许使用存储及电话权限来保存用户数据")
                .setPositiveButton("立即开启", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 跳转到应用设置界面
                        goToAppSetting();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setCancelable(false).show();
    }

    // 跳转到当前应用的设置界面
    private void goToAppSetting() {
        Intent intent = new Intent();

        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);

        startActivityForResult(intent, 123);
    }

    //
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // 检查该权限是否已经获取
                for (int i = 0; i < PERMISSIONS_STORAGE.length; i++) {
                    int j = ContextCompat.checkSelfPermission(this, PERMISSIONS_STORAGE[i]);
                    // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
                    if (j != PackageManager.PERMISSION_GRANTED) {
                        // 提示用户应该去应用设置界面手动开启权限
                        showDialogTipUserGoToAppSettting();
                    } else {
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        Toast.makeText(this, "权限获取成功1", Toast.LENGTH_SHORT).show();
                        Intent intent;
                        intent = new Intent(CheckPermissionActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                }
            }
        }

    }

}

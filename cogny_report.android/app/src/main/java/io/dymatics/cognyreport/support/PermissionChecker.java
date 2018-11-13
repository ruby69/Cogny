package io.dymatics.cognyreport.support;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import io.dymatics.cognyreport.Constants;
import io.dymatics.cognyreport.R;

public final class PermissionChecker {

    public interface Executor {
        void execute();
    }

    public static void check(Activity context, String[] permissions, Executor executor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> notGrantedPermissions = getNotGrantedPermissions(context, permissions);
            if (notGrantedPermissions.isEmpty()) {
                if (executor != null) {
                    executor.execute();
                }
            } else {
                String[] requestedPermissions = notGrantedPermissions.toArray(new String[notGrantedPermissions.size()]);
                ActivityCompat.requestPermissions(context, requestedPermissions, Constants.ID_REQUEST_PERMISSIONS);
            }
        } else {
            if (executor != null) {
                executor.execute();
            }
        }
    }

    private static List<String> getNotGrantedPermissions(Activity context, String[] permissions) {
        List<String> list = new ArrayList<>();
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                list.add(permission);
            }
        }
        return list;
    }

    public static void onRequestPermissionsResult(Activity context, int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults, Executor executor) {
        if (requestCode == Constants.ID_REQUEST_PERMISSIONS) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }

            if (allGranted) {
                if (executor != null) {
                    executor.execute();
                } else {
                    toast(context, R.string.message_permission_granted);
                }
            } else {
                toast(context, R.string.message_permissions_not_granted);
                ActivityCompat.requestPermissions(context, permissions, Constants.ID_REQUEST_PERMISSIONS);
            }
        }
    }

    protected static void toast(final Activity context, final int resId) {
        context.runOnUiThread(() -> Toast.makeText(context, resId, Toast.LENGTH_LONG).show());
    }
}


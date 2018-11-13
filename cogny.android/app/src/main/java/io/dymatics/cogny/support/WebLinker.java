package io.dymatics.cogny.support;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.BitmapFactory;
import android.net.Uri;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.dymatics.cogny.R;
import io.dymatics.cogny.ui.dialog.WebViewDialog;
import io.dymatics.cogny.ui.dialog.WebViewDialog_;


public class WebLinker {
    private static final String TAG = "WebLinker";
    private static final String STABLE_PACKAGE = "com.android.chrome";
    private static final String LOCAL_PACKAGE = "com.google.android.apps.chrome";
    private static final String ACTION_CUSTOM_TABS_CONNECTION = "android.support.customtabs.action.CustomTabsService";

    private static String packageNameToUse;
    private static WebViewDialog webViewDialog = WebViewDialog_.builder().build();

    public static void link(AppCompatActivity activity, Uri uri) {
        String packageName = getPackageNameToUse(activity);
        if (packageName == null) {
            webViewDialog.showUrl(uri.toString(), activity.getSupportFragmentManager());
        } else {
            CustomTabsIntent customTabsIntent = customTabsIntent(activity);
            customTabsIntent.intent.setPackage(packageName);
            customTabsIntent.launchUrl(activity, uri);
        }
    }

    private static CustomTabsIntent customTabsIntent(AppCompatActivity activity) {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setShowTitle(true);
        builder.enableUrlBarHiding();
        builder.setCloseButtonIcon(BitmapFactory.decodeResource(activity.getResources(), R.drawable.ic_arrow_back_black_24dp));

        return builder.build();
    }

    private static String getPackageNameToUse(Context context) {
        if (packageNameToUse != null) return packageNameToUse;

        PackageManager packageManager = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"));
        ResolveInfo resolveInfo = packageManager.resolveActivity(intent, 0);
        String defaultViewHandlerPackageName = null;
        if (resolveInfo != null) {
            defaultViewHandlerPackageName = resolveInfo.activityInfo.packageName;
        }

        List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(intent, 0);
        List<String> packagesSupportingCustomTabs = new ArrayList<>();
        for (ResolveInfo info : resolveInfos) {
            Intent serviceIntent = new Intent();
            serviceIntent.setAction(ACTION_CUSTOM_TABS_CONNECTION);
            serviceIntent.setPackage(info.activityInfo.packageName);
            if (packageManager.resolveService(serviceIntent, 0) != null) {
                packagesSupportingCustomTabs.add(info.activityInfo.packageName);
            }
        }

        if (packagesSupportingCustomTabs.isEmpty()) {
            packageNameToUse = null;

        } else if (packagesSupportingCustomTabs.size() == 1) {
            packageNameToUse = packagesSupportingCustomTabs.get(0);

        } else if (!TextUtils.isEmpty(defaultViewHandlerPackageName) && !hasSpecializedHandlerIntents(context, intent) && packagesSupportingCustomTabs.contains(defaultViewHandlerPackageName)) {
            packageNameToUse = defaultViewHandlerPackageName;

        } else if (packagesSupportingCustomTabs.contains(STABLE_PACKAGE)) {
            packageNameToUse = STABLE_PACKAGE;

        } else if (packagesSupportingCustomTabs.contains(LOCAL_PACKAGE)) {
            packageNameToUse = LOCAL_PACKAGE;

        }

        return packageNameToUse;
    }

    private static boolean hasSpecializedHandlerIntents(Context context, Intent intent) {
        try {
            PackageManager packageManager = context.getPackageManager();
            List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(intent, PackageManager.GET_RESOLVED_FILTER);
            if (resolveInfos == null || resolveInfos.size() == 0) {
                return false;
            }

            for (ResolveInfo resolveInfo : resolveInfos) {
                IntentFilter filter = resolveInfo.filter;
                if (filter == null) continue;
                if (filter.countDataAuthorities() == 0 || filter.countDataPaths() == 0) continue;
                if (resolveInfo.activityInfo == null) continue;
                return true;
            }
        } catch (RuntimeException e) {
            Log.e(TAG, "Runtime exception while getting specialized handlers");
        }

        return false;
    }
}

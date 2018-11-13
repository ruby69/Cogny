package io.dymatics.cogny.service;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.sharedpreferences.Pref;

import io.dymatics.cogny.Constants;
import io.dymatics.cogny.On;
import io.dymatics.cogny.domain.model.Meta;
import io.dymatics.cogny.support.PrefsService_;

@EBean(scope = EBean.Scope.Singleton)
public class MiscService {
    @RootContext Context context;

    @Bean RestClient restClient;
    @Pref PrefsService_ prefs;
    @SystemService WindowManager windowManager;

    public void applyFontScale(Context context) {
        applyFontScale(context, prefs.fontScale().getOr(1.30F));
    }

    public void applyFontScale(Context context, float scale) {
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.fontScale = scale;

        DisplayMetrics metrics = resources.getDisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        metrics.scaledDensity = configuration.fontScale * metrics.density;

        context.createConfigurationContext(configuration);
    }

    public void checkVersion(On<Void> on) {
        restClient.fetchMeta(new On<Meta>().addSuccessListener(meta -> {
            if (meta.getVersionCode() > versionCode()) {
                prefs.needUpdate().put(true);
                on.failure(new RuntimeException("Released new version."));
            } else {
                prefs.needUpdate().put(false);
                on.success(null);
            }
        }).addFailureListener(t -> on.success(null)));
    }

    private long versionCode() {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch(Exception e) {
            return 0;
        }
    }


//    private String getSizeName(Context context) {
//        int screenLayout = context.getResources().getConfiguration().screenLayout;
//        screenLayout &= Configuration.SCREENLAYOUT_SIZE_MASK;
//
//        switch (screenLayout) {
//            case Configuration.SCREENLAYOUT_SIZE_SMALL:
//                return "small";
//            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
//                return "normal";
//            case Configuration.SCREENLAYOUT_SIZE_LARGE:
//                return "large";
//            case Configuration.SCREENLAYOUT_SIZE_XLARGE:
//                return "xlarge";
//            default:
//                return "undefined";
//        }
//    }
//
//    private String getScreenLong(Context context) {
//        int screenLayout = context.getResources().getConfiguration().screenLayout;
//        screenLayout &= Configuration.SCREENLAYOUT_LONG_MASK;
//
//        switch (screenLayout) {
//            case Configuration.SCREENLAYOUT_LONG_YES:
//                // Long screens, such as WQVGA, WVGA, FWVGA
//                return "Long screens";
//            case Configuration.SCREENLAYOUT_LONG_NO:
//                // Not long screens, such as QVGA, HVGA, and VGA
//                return "Not long screens";
//            default:
//                return "undefined";
//        }
//    }
}

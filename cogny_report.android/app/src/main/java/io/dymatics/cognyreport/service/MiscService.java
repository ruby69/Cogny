package io.dymatics.cognyreport.service;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.sharedpreferences.Pref;

import io.dymatics.cognyreport.On;
import io.dymatics.cognyreport.domain.model.Meta;
import io.dymatics.cognyreport.support.PrefsService_;

@EBean(scope = EBean.Scope.Singleton)
public class MiscService {
    @RootContext Context context;

    @Bean RestClient restClient;
    @SystemService WindowManager windowManager;
    @Pref PrefsService_ prefs;

    public void applyFontScale(Context context) {
        applyFontScale(context, prefs.fontScale().getOr(1.30F));
    }

    public void applyFontScale(Context context, float fontScale) {
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.fontScale = fontScale;

        DisplayMetrics metrics = resources.getDisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        metrics.scaledDensity = configuration.fontScale * metrics.density;

        context.createConfigurationContext(configuration);
    }

    public void checkVersion(On<Void> on) {
        restClient.fetchMeta(new On<Meta>().addSuccessListener(meta -> {
            if (meta.getVersionCode() > versionCode()) {
                on.failure(new RuntimeException("Released new version."));
            } else {
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
}

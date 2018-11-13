package io.dymatics.cognyreport;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.greenrobot.eventbus.Subscribe;

import io.dymatics.cognyreport.domain.model.User;
import io.dymatics.cognyreport.event.OnCompleteOrmLite;
import io.dymatics.cognyreport.event.OnMainActivity;
import io.dymatics.cognyreport.service.CognyBean;
import io.dymatics.cognyreport.service.MiscService;
import io.dymatics.cognyreport.service.RestClient;
import io.dymatics.cognyreport.support.EventBusObserver;
import io.dymatics.cognyreport.support.PermissionChecker;
import io.dymatics.cognyreport.support.PrefsService_;
import io.dymatics.cognyreport.ui.view.ProgressView;
import io.dymatics.cognyreport.ui.view.ProgressView_;
import io.fabric.sdk.android.Fabric;

@EActivity(R.layout.activity_launcher)
public class LauncherActivity extends AppCompatActivity {
    @ViewById(R.id.permissionsLayer) View permissionsLayer;

    @Pref PrefsService_ prefs;
    @Bean CognyBean cognyBean;
    @Bean MiscService miscService;
    @Bean RestClient restClient;

    private ProgressView progressView;
    private AlertDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics());
        }
        getLifecycle().addObserver(new EventBusObserver.AtCreateDestroy(this));
    }

    @AfterInject
    void afterInject() {
        miscService.applyFontScale(this, 1.0F);
    }

    @AfterViews
    void afterViews() {
        initProgressDialog();
        progress(true, null);
        initialize();
    }

    private void initProgressDialog() {
        progressView = ProgressView_.build(this);
        progressDialog = new AlertDialog.Builder(this)
                .setCancelable(false)
                .setView(progressView)
                .create();
    }


    @UiThread
    void initialize() {
        if (prefs.initializedDbStatus().get() == 2) { // succeed
            postInitialize();

        } else if (prefs.initializedDbStatus().get() == 3) { // failed
            deleteDatabase(getString(R.string.db_name));
            Toast.makeText(getApplicationContext(), getString(R.string.message_common_db_fail), Toast.LENGTH_LONG).show();
            finish();
        } else {
            // nothing to do..
            // wait until receive on complete orm-lite event.
        }
    }

    @Subscribe
    public void onEvent(OnCompleteOrmLite event) {
        initialize();
    }

    private void postInitialize() {
        miscService.checkVersion(new On<Void>()
                .addSuccessListener(aVoid -> postVersionCheck())
                .addFailureListener(aVoid -> linkPlayStore())
        );
    }

    @UiThread
    void postVersionCheck() {
        progress(false);
        checkGrantedPermissions();
    }

    private void checkGrantedPermissions() {
        if (prefs.allGrantedPermissions().get()) {
            afterCheckingPermissions();
        } else {
            progress(false, null);
            setVisibilityPermissionsLayer(true);
        }
    }

    @Click(R.id.checkPermission)
    void onClickCheckPermission() {
        PermissionChecker.check(this, Constants.REQUEST_PERMISSIONS, () -> afterCheckingPermissions());
    }

    @UiThread
    void afterCheckingPermissions() {
        progress(true, null);
        setVisibilityPermissionsLayer(false);
        signin();
    }

    private void signin() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            signin(firebaseUser);
        } else {
            startSignActivity();
        }
    }

    private void signin(FirebaseUser firebaseUser) {
        progress(true, null);
        firebaseUser.getIdToken(true)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        restClient.signin(task.getResult().getToken(), new On<User>()
                                .addSuccessListener(result -> cognyBean.saveUser(result, new On<User>().addSuccessListener(user -> startMainActivity())))
                                .addFailureListener(t -> failSignin(R.string.message_sign_in_fail_net_error)));
                    } else {
                        startSignActivity();
                    }
                });
    }

    @UiThread
    void failSignin(int resId) {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setMessage(resId)
                .setCancelable(false)
                .setPositiveButton(R.string.label_common_confirm, (dialog, which) -> finish())
                .create();

        if (!isFinishing()) {
            alertDialog.show();
        }
    }

    @UiThread
    void startSignActivity() {
        SignActivity_.intent(this).start();
        finish();
    }

    @UiThread
    void startMainActivity() {
        MainActivity_.intent(LauncherActivity.this).flags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TOP).start();
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Constants.ID_REQUEST_PERMISSIONS) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }

            prefs.allGrantedPermissions().put(allGranted);
            if (allGranted) {
                afterCheckingPermissions();
            } else {
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setMessage(R.string.message_permissions_not_granted)
                        .setCancelable(false)
                        .setPositiveButton(R.string.label_permission_grant_set, (dialog1, which) -> onClickCheckPermission())
                        .setNegativeButton(R.string.label_common_exit, (dialog1, which) -> finish())
                        .create();

                if (!isFinishing()) {
                    dialog.show();
                }
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @UiThread
    void progress(boolean show, String message) {
        if (progressDialog != null) {
            if(show) {
                if (message != null) {
                    progressView.message(message);
                }
                if (!isFinishing() && !progressDialog.isShowing() && !isDestroyed()) {
                    progressDialog.show();
                }
            } else {
                progressView.message(R.string.message_common_wait);
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        }
    }

    private void progress(boolean show) {
        progress(show, null);
    }

    @UiThread
    void setVisibilityPermissionsLayer(boolean show) {
        permissionsLayer.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Subscribe
    public void onEvent(OnMainActivity event) {
        finish();
    }

    @UiThread
    void linkPlayStore() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(String.format(getString(R.string.url_store_app), getPackageName()))));
        } catch (Exception e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(String.format(getString(R.string.url_store_web), getPackageName()))));
        }
        finish();
    }

    @UiThread
    void toast(int resId) {
        Toast.makeText(this, resId, Toast.LENGTH_LONG).show();
    }

}

package io.dymatics.cogny;

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
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.greenrobot.eventbus.Subscribe;

import io.dymatics.cogny.domain.model.User;
import io.dymatics.cogny.event.OnCompleteOrmLite;
import io.dymatics.cogny.event.OnMainActivity;
import io.dymatics.cogny.service.CognyBean;
import io.dymatics.cogny.service.MiscService;
import io.dymatics.cogny.service.RestClient;
import io.dymatics.cogny.support.EventBusObserver;
import io.dymatics.cogny.support.PermissionChecker;
import io.dymatics.cogny.support.PrefsService_;
import io.dymatics.cogny.ui.view.ProgressView;
import io.dymatics.cogny.ui.view.ProgressView_;
import io.fabric.sdk.android.Fabric;

@EActivity(R.layout.activity_invitation)
public class InvitationActivity extends AppCompatActivity {
    @ViewById(R.id.permissionsLayer) View permissionsLayer;
    @ViewById(R.id.invitationLayer) View invitationLayer;

    @Pref PrefsService_ prefs;
    @Bean CognyBean cognyBean;
    @Bean MiscService miscService;
    @Bean RestClient restClient;

    private ProgressView progressView;
    private AlertDialog progressDialog;

    private static final String KEY_CODE = "code";

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
            handleInviteCode();
        }
    }

    private void signin(FirebaseUser firebaseUser) {
        progress(true, null);
        firebaseUser.getIdToken(true)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        restClient.signin(task.getResult().getToken(), new On<User>()
                                .addSuccessListener(result -> cognyBean.saveUser(result, new On<User>().addSuccessListener(u -> startMainActivity())))
                                .addFailureListener(t -> failSignin(R.string.message_sign_in_fail_net_error)));
                    } else {
                        handleInviteCode();
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
    void startSignActivity(User.Invitation invitation) {
        SignActivity_.intent(this).invitation(invitation).start();
        finish();
    }

    @UiThread
    void startMainActivity() {
        MainActivity_.intent(this).flags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TOP).start();
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

    private void handleInviteCode() {
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, pendingDynamicLinkData -> {
                    if (pendingDynamicLinkData == null) {
                        alertNoInvite();
                        return;
                    }
                    Uri deepLink = pendingDynamicLinkData.getLink();
                    String code = deepLink.getQueryParameter(KEY_CODE);
                    if (code != null && !code.isEmpty()) {
                        restClient.checkInvite(code, new On<User.Invitation>().addSuccessListener(invitation -> {
                            if (invitation == null) {
                                alertNoInvite();
                                return;
                            }
                            startSignActivity(invitation);
                        }).addFailureListener(e -> alertNoInvite()));

                    } else {
                        alertNoInvite();
                    }
                })
                .addOnFailureListener(this, e -> alertNoInvite())
        .addOnCompleteListener(task -> progress(false, null));
    }

    @UiThread
    void alertNoInvite() {
        invitationLayer.setVisibility(View.VISIBLE);
        permissionsLayer.setVisibility(View.GONE);
    }

    @Click(R.id.toCall)
    void onClickToCall() {
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(getString(R.string.tel_call_center))));
        finish();
    }

    @Click(R.id.toFinish)
    void onClickToFinish() {
        finish();
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////

    @UiThread
    void progress(boolean show, String message) {
        if (progressDialog != null) {
            if(show) {
                if (message != null) {
                    progressView.message(message);
                }
                if (!isFinishing() && !isDestroyed() && !progressDialog.isShowing()) {
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
    void toast(int resId) {
        Toast.makeText(this, resId, Toast.LENGTH_LONG).show();
    }
}

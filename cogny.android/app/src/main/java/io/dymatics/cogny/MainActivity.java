package io.dymatics.cogny;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.OptionsMenuItem;
import org.androidannotations.annotations.Receiver;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import io.dymatics.cogny.domain.model.User;
import io.dymatics.cogny.event.ChangeFontScale;
import io.dymatics.cogny.event.OnMainActivity;
import io.dymatics.cogny.event.OnNeedUpdate;
import io.dymatics.cogny.event.OnVehicleDetect;
import io.dymatics.cogny.event.OnVehicleUnknown;
import io.dymatics.cogny.event.obd.ObdConnStatus;
import io.dymatics.cogny.event.obd.ObdConnected;
import io.dymatics.cogny.event.obd.ObdDetected;
import io.dymatics.cogny.event.obd.ObdDisconnect;
import io.dymatics.cogny.event.obd.ObdDisconnected;
import io.dymatics.cogny.event.obd.ObdFotaAction;
import io.dymatics.cogny.event.obd.ObdFotaFin;
import io.dymatics.cogny.event.obd.ObdInvalid;
import io.dymatics.cogny.event.obd.ObdNotFound;
import io.dymatics.cogny.service.CognyBean;
import io.dymatics.cogny.service.MiscService;
import io.dymatics.cogny.service.RestClient;
import io.dymatics.cogny.support.CognyService_;
import io.dymatics.cogny.support.EventBusObserver;
import io.dymatics.cogny.support.PermissionChecker;
import io.dymatics.cogny.support.PrefsService_;
import io.dymatics.cogny.support.WebLinker;
import io.dymatics.cogny.ui.view.ProgressView;
import io.dymatics.cogny.ui.view.ProgressView_;
import io.dymatics.cogny.ui.view.VehicleInfoView;
import io.fabric.sdk.android.Fabric;

@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {
    @ViewById(R.id.drawerLayout) DrawerLayout drawerLayout;
    @ViewById(R.id.navigationView) NavigationView navigationView;
    @ViewById(R.id.toolbar) Toolbar toolbar;
    @ViewById(R.id.connect) View connect;
    @ViewById(R.id.disconnect) View disconnect;
    @ViewById(R.id.vehicleInfoView) VehicleInfoView vehicleInfoView;

    @OptionsMenuItem(R.id.menuConnect) MenuItem menuConnect;
    @OptionsMenuItem(R.id.menuDisconnect) MenuItem menuDisconnect;

    @Pref PrefsService_ prefs;
    @Bean CognyBean cognyBean;
    @Bean MiscService miscService;
    @Bean RestClient restClient;
    @SystemService BluetoothManager bluetoothManager;

    private FirebaseAnalytics firebaseAnalytics;
    private GoogleSignInClient mGoogleSignInClient;

    private BluetoothAdapter bluetoothAdapter;
    private boolean enabledBluetooth;

    private ProgressView progressView;
    private AlertDialog progressDialog;
    private boolean shown;

    @AfterInject
    void afterInject() {
        miscService.applyFontScale(this);
        cognyBean.pushUserMobile();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!BuildConfig.DEBUG) {
            firebaseAnalytics = FirebaseAnalytics.getInstance(this);
            Fabric.with(this, new Crashlytics());
        }
        getLifecycle().addObserver(new EventBusObserver.AtCreateDestroy(this));
        mGoogleSignInClient = GoogleSignIn.getClient(this, new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build());
    }

    @AfterViews
    void afterViews() {
        initProgressDialog();
        initNavigation();

//        MenuItem menuRevokeWithGoogle = navigationView.getMenu().findItem(R.id.menuRevokeWithGoogle);
//        boolean available = Constants.isSignedWithGoogle(cognyBean.currentSignProvider());
//        menuRevokeWithGoogle.setVisible(available);
//        menuRevokeWithGoogle.setEnabled(available);
    }

    private void initProgressDialog() {
        progressView = ProgressView_.build(this);
        progressDialog = new AlertDialog.Builder(this)
                .setCancelable(false)
                .setView(progressView)
                .create();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        CognyService_.start(this);
        EventBus.getDefault().post(new OnMainActivity());
        PermissionChecker.check(this, Constants.REQUEST_PERMISSIONS, () -> afterPermissionsCheck());
    }

    @Override
    protected void onResume() {
        super.onResume();
        shown = true;
        cognyBean.setOpenedMain(true);
        restClient.setOpenedMain(true);
    }

    @Override
    protected void onPause() {
        shown = false;
        cognyBean.setOpenedMain(false);
        restClient.setOpenedMain(false);
        super.onPause();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void initNavigation() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(navigationItemSelectedListener);
    }

    private NavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            int id = item.getItemId();

            if (id == R.id.menuUser) {
                UserActivity_.intent(MainActivity.this).start();
            } else if (id == R.id.menuAgreement) {
                WebLinker.link(MainActivity.this, Uri.parse(getString(R.string.url_agreement)));
            } else if (id == R.id.menuPrivacy) {
                WebLinker.link(MainActivity.this, Uri.parse(getString(R.string.url_privacy)));
            } else if (id == R.id.menuSettings) {
                SettingsActivity_.intent(MainActivity.this).start();
//            } else if (id == R.id.menuRevokeWithGoogle) {
//                revokeWithGoogle();
//            } else if (id == R.id.menuLogout) {
//                doSignOut();
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }
    };

    private void doSignOut() {
        if (Constants.isSignedWithGoogle(cognyBean.currentSignProvider())) {
            mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> finishSession());
        } else {
            finishSession();
        }
    }

    private void revokeWithGoogle() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (Constants.isSignedWithGoogle(cognyBean.currentSignProvider())) {
            progress(true);
            currentUser.getIdToken(true)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            restClient.revoke(task.getResult().getToken(), new On<User>()
                                    .addSuccessListener(result -> {
                                        mGoogleSignInClient.revokeAccess().addOnCompleteListener(MainActivity.this, task1 -> {
                                            currentUser.delete().addOnCompleteListener(MainActivity.this, task2 -> {
                                                finishSession();
                                            });
                                        });
                                    })
                                    .addCompleteListener(result -> progress(false)));

                        } else {
                            progress(false);
                        }
                    });

        } else {
            finishSession();
        }
    }

    @UiThread
    void finishSession() {
        cognyBean.clean();
        EventBus.getDefault().post(new ObdDisconnect());
        FirebaseAuth.getInstance().signOut();
        LauncherActivity_.intent(this).start();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        EventBus.getDefault().post(new ObdConnStatus());
        return super.onCreateOptionsMenu(menu);
    }

    @Click(R.id.connect)
    void menuConnect() {
        afterPermissionsCheck();
    }

    @Click(R.id.disconnect)
    void menuDisconnect() {
        EventBus.getDefault().post(new ObdDisconnect());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionChecker.onRequestPermissionsResult(this, requestCode, permissions, grantResults, () -> afterPermissionsCheck());
    }

    @UiThread
    void afterPermissionsCheck() {
        if (!cognyBean.isObdConnected()) {
            bluetoothAdapter = bluetoothManager.getAdapter();
            if (bluetoothAdapter == null) {
                toast(R.string.message_bt_not_supported);
                finish();
                return;
            }

            enableBluetooth();
            if (enabledBluetooth) {
                cognyBean.scanBle(
                        new ScanCallback() {
                            @Override
                            public void onScanResult(int callbackType, ScanResult result) {
                                BluetoothDevice device = result.getDevice();
                                String deviceName = device.getName();
                                if (device.getAddress().equals(prefs.deviceAddress().getOr(null))
                                        || (device.getType() == BluetoothDevice.DEVICE_TYPE_LE && result.getRssi() > -70 && deviceName != null && deviceName.length() > 0 && (deviceName.startsWith("COGNY") || deviceName.startsWith("SECO") || deviceName.startsWith("chipsen")))) {
                                    progress(false);
                                    EventBus.getDefault().post(new ObdDetected(result.getDevice()));
                                }
                            }
                        },
                        new On<Void>()
                                .addReadyListener(() -> progress(true))
                                .addCompleteListener(result -> {
                                    progress(false);
                                    if (prefs.deviceAddress().getOr(null) == null) {
                                        onEvent(new ObdNotFound());
                                    }
                                })
                );
            }
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void enableBluetooth() {
        if (bluetoothAdapter != null) {
            enabledBluetooth = bluetoothAdapter.isEnabled();
            if (!enabledBluetooth) {
                startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), Constants.REQUEST_ENABLE_BT);
            }
        }
    }

    @OnActivityResult(Constants.REQUEST_ENABLE_BT)
    void onResultEnableBluetooth(int resultCode) {
        enabledBluetooth = (resultCode != Activity.RESULT_CANCELED);
        if (enabledBluetooth) {
            afterPermissionsCheck();
        } else {
            askEnableBluetooth();
        }
    }

    @Receiver(actions = BluetoothAdapter.ACTION_STATE_CHANGED, registerAt = Receiver.RegisterAt.OnCreateOnDestroy)
    void onActionStateChanged(Intent intent) {
        int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF);
        if (state == BluetoothAdapter.STATE_OFF) {
            askEnableBluetooth();
        }
    }

    private void askEnableBluetooth() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.label_bt_required)
                .setMessage(R.string.message_bt_required)
                .setCancelable(false)
                .setPositiveButton(R.string.label_common_yes, (dialog1, which) -> enableBluetooth())
                .setNegativeButton(R.string.label_common_no, (dialog1, which) -> {})
                .create();

        if (!isFinishing()) {
            dialog.show();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Subscribe
    @UiThread
    public void onEvent(ObdConnected event) {
        connect.setVisibility(View.GONE);
        disconnect.setVisibility(View.VISIBLE);
    }

    @Subscribe
    @UiThread
    public void onEvent(ObdDisconnected event) {
        connect.setVisibility(View.VISIBLE);
        disconnect.setVisibility(View.GONE);
    }

    @Subscribe
    @UiThread
    public void onEvent(ObdInvalid event) {
        AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                .setTitle(R.string.label_obd_invalid)
                .setMessage(R.string.message_obd_invalid)
                .setPositiveButton(R.string.label_common_confirm, (dialog1, which) -> {})
                .create();

        if (!isFinishing()) {
            dialog.show();
        }
    }

    @Subscribe
    @UiThread
    public void onEvent(ObdNotFound event) {
        AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                .setTitle(R.string.label_obd_not_found)
                .setMessage(R.string.message_obd_not_found)
                .setNegativeButton(R.string.label_common_cancel, (dialog1, which) -> {})
                .setPositiveButton(R.string.label_obd_connect_menu, (dialog1, which) -> afterPermissionsCheck())
                .create();

        if (!isFinishing()) {
            dialog.show();
        }
    }

    @Subscribe
    @UiThread
    public void onEvent(OnVehicleDetect event) {
        vehicleInfoView.populateVehicle();
    }

    @Subscribe
    @UiThread
    public void onEvent(OnVehicleUnknown event) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage(R.string.message_vehicle_empty)
                .setPositiveButton(R.string.label_common_confirm, (dialog1, which) -> {})
                .create();

        if (!isFinishing()) {
            dialog.show();
        }
    }

    private AlertDialog fotaAlertDialog;

    @Subscribe
    @UiThread
    public void onEvent(ObdFotaAction event) {
        fotaAlertDialog = new AlertDialog.Builder(this)
                .setCancelable(false)
                .setView(ProgressView_.build(this).message(R.string.message_obd_fota))
                .create();

        if (!isFinishing()) {
            fotaAlertDialog.show();
        }
    }

    @Subscribe
    @UiThread
    public void onEvent(ObdFotaFin event) {
        if (fotaAlertDialog != null && fotaAlertDialog.isShowing()) {
            fotaAlertDialog.dismiss();
            fotaAlertDialog = null;
            toast(R.string.message_obd_fota_fin);
        }
    }

    @Subscribe
    @UiThread
    public void onEvent(OnNeedUpdate event) {
        if (shown) {
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setMessage(R.string.label_noti_update)
                    .setCancelable(false)
                    .setPositiveButton(R.string.label_common_confirm, (dialog1, which) -> {
                        linkPlayStore();
                        finish();
                    })
                    .create();

            if (!isFinishing() && !isDestroyed()) {
                alertDialog.show();
            }
        } else {
            if (!isFinishing() && !isDestroyed()) {
                finish();
            }
        }
    }

    @Subscribe
    public void onEvent(ChangeFontScale event) {
        LauncherActivity_.intent(this).start();
        finish();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
            return;
        }

        super.onBackPressed();
    }

    @UiThread
    void toast(int resId) {
        Toast.makeText(this, resId, Toast.LENGTH_LONG).show();
    }

    @UiThread
    void progress(boolean show, String message) {
        if (progressDialog != null) {
            if(show) {
                if (message != null) {
                    progressView.message(message);
                }
                if (!isFinishing() && !progressDialog.isShowing()) {
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

    void progress(boolean show) {
        progress(show, null);
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
}

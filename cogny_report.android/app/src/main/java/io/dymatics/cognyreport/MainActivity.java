package io.dymatics.cognyreport;

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
import android.view.MenuItem;
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
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import io.dymatics.cognyreport.domain.model.User;
import io.dymatics.cognyreport.event.ChangeFontScale;
import io.dymatics.cognyreport.event.OnMainActivity;
import io.dymatics.cognyreport.service.CognyBean;
import io.dymatics.cognyreport.service.MiscService;
import io.dymatics.cognyreport.service.RestClient;
import io.dymatics.cognyreport.support.EventBusObserver;
import io.dymatics.cognyreport.support.PrefsService_;
import io.dymatics.cognyreport.support.WebLinker;
import io.dymatics.cognyreport.ui.view.ProgressView;
import io.dymatics.cognyreport.ui.view.ProgressView_;
import io.fabric.sdk.android.Fabric;

@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {
    @ViewById(R.id.drawerLayout) DrawerLayout drawerLayout;
    @ViewById(R.id.navigationView) NavigationView navigationView;
    @ViewById(R.id.toolbar) Toolbar toolbar;

    @Pref PrefsService_ prefs;
    @Bean CognyBean cognyBean;
    @Bean MiscService miscService;
    @Bean RestClient restClient;

    private GoogleSignInClient googleSignInClient;

    private ProgressView progressView;
    private AlertDialog progressDialog;

    @AfterInject
    void afterInject() {
        miscService.applyFontScale(this);
        cognyBean.pushUserMobile();
        cognyBean.loadDiagnosisCategories();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!BuildConfig.DEBUG) {
            FirebaseAnalytics.getInstance(this);
            Fabric.with(this, new Crashlytics());
        }

        getLifecycle().addObserver(new EventBusObserver.AtCreateDestroy(this));
        googleSignInClient = GoogleSignIn.getClient(this, new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build());
    }

    @AfterViews
    void afterViews() {
        initProgressDialog();
        initNavigation();
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
        EventBus.getDefault().post(new OnMainActivity());

        int count = prefs.executedCount().get();
        prefs.executedCount().put(++count);
    }

    @Override
    protected void onResume() {
        super.onResume();
        restClient.setOpenedMain(true);
    }

    @Override
    protected void onPause() {
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


    ////////////////////////////////////////////////////////////////////////////////////////////////


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
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }
    };

    private void doSignOut() {
        if (Constants.isSignedWithGoogle(cognyBean.currentSignProvider())) {
            googleSignInClient.signOut().addOnCompleteListener(this, task -> finishSession());
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
                                        googleSignInClient.revokeAccess().addOnCompleteListener(MainActivity.this, task1 -> {
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
        FirebaseAuth.getInstance().signOut();
        LauncherActivity_.intent(this).start();
        finish();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

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
}

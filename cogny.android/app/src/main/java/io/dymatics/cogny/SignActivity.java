package io.dymatics.cogny;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.dymatics.cogny.domain.model.User;
import io.dymatics.cogny.event.OnMainActivity;
import io.dymatics.cogny.service.CognyBean;
import io.dymatics.cogny.service.MiscService;
import io.dymatics.cogny.service.RestClient;
import io.dymatics.cogny.support.EventBusObserver;
import io.dymatics.cogny.support.PrefsService_;
import io.dymatics.cogny.ui.frags.SignAgreementFragment_;
import io.dymatics.cogny.ui.frags.SignConfirmFragment_;
import io.dymatics.cogny.ui.frags.SignDenyFragment_;
import io.dymatics.cogny.ui.frags.SignFragment_;
import io.dymatics.cogny.ui.view.ProgressView;
import io.dymatics.cogny.ui.view.ProgressView_;

@EActivity(R.layout.activity_sign)
public class SignActivity extends AppCompatActivity {
    @ViewById(R.id.viewPager) ViewPager viewPager;

    @Pref PrefsService_ prefs;
    @Bean CognyBean cognyBean;
    @Bean MiscService miscService;
    @Bean RestClient restClient;

    @Extra("invitation") User.Invitation invitation;

    private PagerAdapter pagerAdapter;
    private ProgressView progressView;
    private AlertDialog progressDialog;

    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        getLifecycle().addObserver(new EventBusObserver.AtCreateDestroy(this));

        googleSignInClient = GoogleSignIn.getClient(this, new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build());
    }

    @AfterInject
    void afterInject() {
        miscService.applyFontScale(this, 1.0F);
    }

    @AfterViews
    void afterViews() {
        initProgressDialog();
        viewPager.setAdapter(pagerAdapter);
    }

    private void initProgressDialog() {
        progressView = ProgressView_.build(this);
        progressDialog = new AlertDialog.Builder(this)
                .setCancelable(false)
                .setView(progressView)
                .create();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void startMainActivity() {
        MainActivity_.intent(this).flags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TOP).start();
        finish();
    }

    public void startWithGoogle() {
        progress(true);
        startActivityForResult(googleSignInClient.getSignInIntent(), Constants.ID_REQUEST_SIGNIN_WITH_GOOGLE);
    }

    @OnActivityResult(Constants.ID_REQUEST_SIGNIN_WITH_GOOGLE)
    void onResultSigninWithGoogle(Intent data) {
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            GoogleSignInAccount googleSignInAccount = task.getResult(ApiException.class);
            AuthCredential credential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);
            FirebaseAuth.getInstance().signInWithCredential(credential)
                    .addOnSuccessListener(this, authResult -> checkUser(authResult))
                    .addOnFailureListener(this, ex -> {
                        progress(false);
                        failSignin();
                    });
        } catch (ApiException e) {
            progress(false);
            failSignin();
        }
    }

    private void checkUser(AuthResult authResult) {
        FirebaseUser firebaseUser = authResult.getUser();
        firebaseUser.getIdToken(true)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String token = task.getResult().getToken();
                        restClient.checkUser(token, new On<User>().addSuccessListener(result -> {
                            if (result != null) {
                                signin(token);
                            } else {
                                if (invitation != null) {
                                    signupCogny(token, firebaseUser);
                                } else {
                                    denySign();
                                }
                            }
                        }).addCompleteListener(result -> progress(false)));
                    }
                });
    }

    private void signin(String token) {
        restClient.signin(token, new On<User>()
                .addSuccessListener(result -> cognyBean.saveUser(result, new On<User>().addSuccessListener(user -> startMainActivity())))
                .addFailureListener((e) -> movePage(0)));
    }

    private void signupCogny(String token, FirebaseUser firebaseUser) {
        User user = new User();
        user.setToken(token);
        user.setHpNo(firebaseUser.getPhoneNumber());
        user.setEmail(firebaseUser.getEmail());
        user.setName(firebaseUser.getDisplayName());
        user.setSignProvider(Constants.SIGN_PROVIDER_GOOGLE);
        user.setPartnerNo(invitation.getPartnerNo());
        user.setRole("DRIVER");
        user.setUserInvitationNo(invitation.getUserInvitationNo());

        restClient.signup(user, new On<User>()
                .addReadyListener(() -> progress(true))
                .addSuccessListener(result -> cognyBean.saveUser(result, new On<User>().addSuccessListener(u -> movePage(1))))
                .addCompleteListener(result -> progress(false)));
    }

    private void failSignin() {
        failSignin(R.string.message_sign_in_fail);
    }

    @UiThread
    void failSignin(int resId) {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setMessage(resId)
                .setPositiveButton(R.string.label_common_confirm, (dialog, which) -> {})
                .create();

        if (!isFinishing()) {
            alertDialog.show();
        }
    }

    public void agree() {
        movePage(2);
    }

    public void disagree() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        progress(true);
        currentUser.getIdToken(true)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        currentUser.delete();
                        googleSignInClient.revokeAccess();
                        restClient.revoke(task.getResult().getToken(), new On<User>()
                                .addSuccessListener(result -> {})
                                .addCompleteListener(result -> {
                                    progress(false);
                                    movePage(0);
                                }));

                    } else {
                        progress(false);
                    }
                });
    }

    public void denySign() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        progress(true);
        currentUser.getIdToken(true)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        currentUser.delete();
                        googleSignInClient.revokeAccess();
                    }
                    movePage(4);
                    progress(false);
                });
    }

    @UiThread
    void movePage(int position) {
        viewPager.setCurrentItem(position);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private class PagerAdapter extends FragmentStatePagerAdapter {
        private List<Fragment> items;

        public PagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
            items = new ArrayList<>(Arrays.asList(
                    SignFragment_.builder().build(),
                    SignAgreementFragment_.builder().build(),
                    SignConfirmFragment_.builder().build(),
                    new Fragment(),
                    SignDenyFragment_.builder().build()
            ));
        }

        @Override
        public Fragment getItem(int position) {
            return items.get(position);
        }

        @Override
        public int getCount() {
            return items.size();
        }
    }

    @Override
    public void onBackPressed() {
        int currentPagePosition = viewPager.getCurrentItem();
        if (currentPagePosition > 0) {
            viewPager.setCurrentItem(0);
            return;
        }
        super.onBackPressed();
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

    public void progress(boolean show) {
        progress(show, null);
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

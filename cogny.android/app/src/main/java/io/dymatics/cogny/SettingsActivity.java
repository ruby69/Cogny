package io.dymatics.cogny;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.ColorRes;
import org.androidannotations.annotations.res.StringArrayRes;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.greenrobot.eventbus.EventBus;

import io.dymatics.cogny.domain.model.ObdDevice;
import io.dymatics.cogny.event.ChangeFontScale;
import io.dymatics.cogny.service.CognyBean;
import io.dymatics.cogny.service.MiscService;
import io.dymatics.cogny.support.PrefsService_;

@EActivity(R.layout.activity_settings)
public class SettingsActivity extends AppCompatActivity {
    @ViewById(R.id.toolbar) Toolbar toolbar;
    @ViewById(R.id.versionView) TextView versionView;
    @ViewById(R.id.versionMessage) View versionMessage;
    @ViewById(R.id.updateButton) View updateButton;
    @ViewById(R.id.spinner) Spinner spinner;
    @ViewById(R.id.obdSerial) TextView obdSerial;
    @ViewById(R.id.fwVersion) TextView fwVersion;

    @Bean MiscService miscService;
    @Bean CognyBean cognyBean;
    @Pref PrefsService_ prefs;

    @ColorRes(R.color.colorAccent) int accentColor;
    @StringArrayRes String[] fontScales;

    private int currentScaleIndex = 4;

    @AfterInject
    void afterInject() {
        float scale = prefs.fontScale().getOr(1.30F);
        if (scale == 0.70F) {
            currentScaleIndex = 0;
        } else if (scale == 0.85F) {
            currentScaleIndex = 1;
        } else if (scale == 1.00F) {
            currentScaleIndex = 2;
        } else if (scale == 1.15F) {
            currentScaleIndex = 3;
        } else {
            currentScaleIndex = 4;
        }
    }

    @AfterViews
    void afterViews() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.getNavigationIcon().setColorFilter(accentColor, PorterDuff.Mode.SRC_IN);
        toolbar.setTitleTextColor(accentColor);

        manipulate();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @UiThread
    void manipulate() {
        versionView.setText(versionName());
        miscService.checkVersion(new On<Void>()
                .addSuccessListener(aVoid -> manipulateVersion(false))
                .addFailureListener(aVoid -> manipulateVersion(true))
        );
        initSpinner();

        String firmwareVersion = prefs.firmwareVersion().get();
        if (firmwareVersion != null) {
            fwVersion.setText(firmwareVersion);
        }

        ObdDevice obdDevice = cognyBean.loadObdDevice();
        if (obdDevice != null) {
            obdSerial.setText(obdDevice.getObdSerial());
        }

    }

    @UiThread
    void initSpinner() {
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, fontScales);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setSelection(currentScaleIndex);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapter, View v, int position, long id) {
                if (position != currentScaleIndex) {

                    float scale = 1.30F;
                    String str = "가장 큰";
                    if (position == 0) {
                        scale = 0.70F;
                        str = "가장 작은";
                    } else if (position == 1) {
                        scale = 0.85F;
                        str = "작은";
                    } else if (position == 2) {
                        scale = 1.00F;
                        str = "보통";
                    } else if (position == 3) {
                        scale = 1.15F;
                        str = "큰";
                    } else {
                        scale = 1.30F;
                        str = "가장 큰";
                    }

                    str = String.format(getString(R.string.message_common_font_size), str);
                    alertFontSize(scale, str);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {}
        });
    }

    @UiThread
    void alertFontSize(float scale, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(SettingsActivity.this)
                .setMessage(message)
                .setNegativeButton(R.string.label_common_cancel, (dialog, which) -> spinner.setSelection(currentScaleIndex))
                .setPositiveButton(R.string.label_common_confirm, (dialog, which) -> {
                    prefs.fontScale().put(scale);
                    EventBus.getDefault().post(new ChangeFontScale());
                    finish();
                })
                .create();

        if (!isFinishing()) {
            alertDialog.show();
        }
    }

    @UiThread
    void manipulateVersion(boolean needUpdate) {
        if (needUpdate) {
            updateButton.setVisibility(View.VISIBLE);
            versionMessage.setVisibility(View.GONE);
        } else {
            updateButton.setVisibility(View.GONE);
            versionMessage.setVisibility(View.VISIBLE);
        }
    }

    @Click(R.id.updateButton)
    void onClickUpdateButton() {
        linkPlayStore();
    }

    @UiThread
    void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private String versionName() {
        try {
            return getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch(Exception e) {
            return "";
        }
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

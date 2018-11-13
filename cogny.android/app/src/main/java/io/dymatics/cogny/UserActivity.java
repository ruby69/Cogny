package io.dymatics.cogny;

import android.graphics.PorterDuff;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.ColorRes;

import io.dymatics.cogny.domain.model.User;
import io.dymatics.cogny.domain.model.UserMobileDevice;
import io.dymatics.cogny.service.CognyBean;

@EActivity(R.layout.activity_user)
public class UserActivity extends AppCompatActivity {
    @ViewById(R.id.toolbar) Toolbar toolbar;
    @ViewById(R.id.emailView) TextView emailView;
    @ViewById(R.id.nameView) TextView nameView;
    @ViewById(R.id.hpView) TextView hpView;
    @ViewById(R.id.partnerView) TextView partnerView;
    @ViewById(R.id.signupDateView) TextView signupDateView;

    @Bean CognyBean cognyBean;

    @ColorRes(R.color.colorAccent) int accentColor;

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
        User user = cognyBean.loadUser();
        emailView.setText(user.getEmail());
        nameView.setText(user.getName());
        partnerView.setText(user.getPartnerName());
        signupDateView.setText(Constants.FORMAT_DATE_YMD.format(user.getRegDate()));

        UserMobileDevice userMobileDevice = cognyBean.loadUserMobileDevice();
        hpView.setText(userMobileDevice.getMobileNumber());
    }

    @UiThread
    void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}

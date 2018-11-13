package io.dymatics.cogny.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import io.dymatics.cogny.Constants;
import io.dymatics.cogny.On;
import io.dymatics.cogny.R;
import io.dymatics.cogny.domain.model.DriveHistory;
import io.dymatics.cogny.domain.model.Vehicle;
import io.dymatics.cogny.event.OnDriveHistoryUpdated;
import io.dymatics.cogny.event.OnVehicleDetect;
import io.dymatics.cogny.service.CognyBean;

@EViewGroup(R.layout.view_vehicle_info)
public class VehicleInfoView extends RelativeLayout {
    @ViewById(R.id.license) TextView license;
    @ViewById(R.id.modelTitle) TextView modelTitle;
    @ViewById(R.id.mileage) TextView mileage;

    @Bean CognyBean cognyBean;

    public VehicleInfoView(Context context) {
        super(context);
    }

    public VehicleInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        EventBus.getDefault().unregister(this);
        super.onDetachedFromWindow();
    }

    @AfterViews
    void afterViews() {
        populateVehicle();
    }

    @UiThread
    public void populateVehicle() {
        if (cognyBean != null) {
            Vehicle vehicle = cognyBean.loadVehicle();
            if (vehicle != null && license != null) {
                license.setText(vehicle.getLicenseNo());
                modelTitle.setText(String.format(getContext().getString(R.string.format_model_title), vehicle.getModelName(), vehicle.getModelYear()));
            }
            cognyBean.loadLatestDriveHistory(new On<DriveHistory>().addSuccessListener(result -> populateDriveHistory(result)));
        }
    }

    @UiThread
    void populateDriveHistory(DriveHistory driveHistory) {
        if (driveHistory != null && mileage != null) {
            int endMileage = driveHistory.getEndMileage();
            if (endMileage > 0) {
                mileage.setText(String.format("%s", Constants.FORMAT_DISTANCE_3.format((int)(endMileage / 10F))));
                mileage.setVisibility(View.VISIBLE);
            } else {
                mileage.setVisibility(View.GONE);
            }
        }
    }

    @Subscribe
    public void onEvent(OnDriveHistoryUpdated event) {
        populateDriveHistory(event.getDriveHistory());
    }

    @Subscribe
    public void onEvent(OnVehicleDetect event) {
        populateVehicle();
    }
}
package io.dymatics.cognyreport.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import io.dymatics.cognyreport.Constants;
import io.dymatics.cognyreport.R;
import io.dymatics.cognyreport.domain.model.DtcReport;

@EViewGroup(R.layout.view_vehicle_info)
public class VehicleInfoView extends RelativeLayout {
    @ViewById(R.id.license) TextView license;
    @ViewById(R.id.model) TextView model;
    @ViewById(R.id.mileage) TextView mileage;

    public VehicleInfoView(Context context) {
        super(context);
    }

    public VehicleInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @UiThread
    public void populate(DtcReport dtcReport) {
        license.setText(dtcReport.getLicenseNo());
        model.setText(dtcReport.getModel());
        mileage.setText(Constants.FORMAT_DISTANCE_3.format((int)(dtcReport.getOdometer() / 10F)));
    }

}
package io.dymatics.cognyreport.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.ColorRes;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import io.dymatics.cognyreport.Constants;
import io.dymatics.cognyreport.R;
import io.dymatics.cognyreport.ReportActivity_;
import io.dymatics.cognyreport.domain.model.DtcReport;
import io.dymatics.cognyreport.domain.model.RepairMsg;
import io.dymatics.cognyreport.event.OnChangedDtcReport;

@EViewGroup(R.layout.view_dtc_report_item)
public class DtcReportItemView extends RelativeLayout {
    @ViewById(R.id.licenseNo) TextView licenseNo;
    @ViewById(R.id.drivingView) View drivingView;
    @ViewById(R.id.diagnosisCount) TextView diagnosisCount;
    @ViewById(R.id.repairMsgIcon) View repairMsgIcon;
    @ViewById(R.id.diagnosisIcon) View diagnosisIcon;
    @ViewById(R.id.model) TextView model;
    @ViewById(R.id.odometer) TextView odometer;

    @ColorRes(R.color.red) int red;
    @ColorRes(R.color.colorAccent) int colorAccent;
    @ColorRes(R.color.textColorPrimary) int textColorPrimary;

    private DtcReport dtcReport;

    public DtcReportItemView(Context context) {
        super(context);
    }

    public DtcReportItemView(Context context, AttributeSet attrs) {
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

    @UiThread
    public void setData(DtcReport dtcReport) {
        this.dtcReport = dtcReport;

        licenseNo.setText(dtcReport.getLicenseNo());
        drivingView.setVisibility(dtcReport.isDriving() ? View.VISIBLE : View.GONE);
        model.setText(dtcReport.getModel());
        odometer.setText(Constants.FORMAT_DISTANCE_3.format((int)(dtcReport.getOdometer() / 10F)));
        manipulateDiagnosisCount();
        manipulateRepairMsg();
    }

    @UiThread
    void manipulateDiagnosisCount() {
        int count = dtcReport.getDiagnosisAndDtcCount();
        if (count > 0) {
            diagnosisIcon.setVisibility(VISIBLE);
            diagnosisCount.setText(Constants.FORMAT_COUNT_ITEM.format(count));
            diagnosisCount.setTextColor(dtcReport.hasFatal() ? red : colorAccent);
        } else {
            diagnosisIcon.setVisibility(GONE);
            diagnosisCount.setText("-건");
            diagnosisCount.setTextColor(textColorPrimary);
        }
    }

    @UiThread
    void manipulateRepairMsg() {
        RepairMsg repairMsg = dtcReport.getRepairMsg();
        if (repairMsg == null || repairMsg.getMsgType().isEmpty()) {
            repairMsgIcon.setVisibility(View.GONE);
        } else {
            repairMsgIcon.setVisibility(View.VISIBLE);
        }
    }

    @Click(R.id.selectionLayer)
    void onClickSelectionLayer() {
        ReportActivity_.intent(getContext()).dtcReport(dtcReport).start();
    }

    @Subscribe
    public void onEvent(OnChangedDtcReport event) {
        DtcReport paramReport = event.getDtcReport();
        if (dtcReport != null && paramReport != null
                && dtcReport.getDriveDate().equals(paramReport.getDriveDate())
                && dtcReport.getVehicleNo().longValue() == paramReport.getVehicleNo().longValue()) {
            dtcReport.setRepairMsg(paramReport.getRepairMsg());
            dtcReport.setDiagnosis(paramReport.getDiagnosis());
            dtcReport.setDtcs(paramReport.getDtcs());
            manipulateDiagnosisCount();
            manipulateRepairMsg();
        }
    }
}
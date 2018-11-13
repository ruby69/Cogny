package io.dymatics.cogny.ui.frags;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.View;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.ColorRes;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import io.dymatics.cogny.Constants;
import io.dymatics.cogny.On;
import io.dymatics.cogny.R;
import io.dymatics.cogny.domain.model.DriveHistory;
import io.dymatics.cogny.domain.model.RepairMsg;
import io.dymatics.cogny.event.OnDriveFin;
import io.dymatics.cogny.event.OnDriveHistoryUpdated;
import io.dymatics.cogny.event.OnNotiMessage;
import io.dymatics.cogny.event.OnVehicleDetect;
import io.dymatics.cogny.service.CognyBean;
import io.dymatics.cogny.support.EventBusObserver;

@EFragment(R.layout.fragment_drive)
public class DriveFragment extends Fragment {
    @ViewById(R.id.lastDriveDate) TextView lastDriveDate;
    @ViewById(R.id.driveDurationTime) TextView driveDurationTime;
    @ViewById(R.id.driveDistance) TextView driveDistance;
    @ViewById(R.id.driveTime) TextView driveTime;
    @ViewById(R.id.driveNoneMessage) TextView driveNoneMessage;
    @ViewById(R.id.repairMsgView) TextView repairMsgView;
    @ViewById(R.id.repairMsgLayer) View repairMsgLayer;
    @ViewById(R.id.driveStatus) View driveStatus;

    @ViewById(R.id.driveStatusLayer) View driveStatusLayer;
    @ViewById(R.id.driveNoneLayer) View driveNoneLayer;

    @Bean CognyBean cognyBean;

    @ColorRes(R.color.red) int red;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLifecycle().addObserver(new EventBusObserver.AtStartStop(this));
    }

    @AfterViews
    void afterViews() {
        load();
    }

    private void load() {
        cognyBean.loadLatestDriveHistory(new On<DriveHistory>().addSuccessListener(result -> populateDriveHistory(result, false)));
        cognyBean.loadLatestRepairMsg(new On<RepairMsg>().addSuccessListener(this::populateRepairMsg));
    }

    @UiThread
    void populateDriveHistory(DriveHistory driveHistory, boolean driving) {
        if (driveHistory != null) {
            lastDriveDate.setVisibility(View.VISIBLE);
            driveStatusLayer.setVisibility(View.VISIBLE);
            driveNoneLayer.setVisibility(View.GONE);
            lastDriveDate.setText(Constants.FORMAT_DATE_YMD_KO.format(driveHistory.getStartDate()));

            int distance = driveHistory.getDriveDistance();
            if (distance > -1) {
                driveDistance.setText(String.format("%s", Constants.FORMAT_DISTANCE_2.format(distance / 10F)));
            }

            String driveDuration = Constants.FORMAT_TIME_HM.format(driveHistory.getStartDate()) + " 출발";
            if (driving) {
                driveTime.setText(Constants.diffTime(driveHistory.getStartDate()));
            } else {
                if (driveHistory.getEndTime() != null) {
                    driveTime.setText(Constants.diffTime(driveHistory.getStartDate(), driveHistory.getEndTime()));
                    driveDuration = driveDuration + " ~ " + Constants.FORMAT_TIME_HM.format(driveHistory.getEndTime()) + " 도착";

                    EventBus.getDefault().post(new OnDriveFin());
                }
            }

            driveDurationTime.setText(driveDuration);

        } else {
            lastDriveDate.setVisibility(View.GONE);
            driveStatusLayer.setVisibility(View.GONE);
            driveNoneMessage.setText(cognyBean.isObdConnected() ? R.string.label_drive_none1 : R.string.label_drive_none);
            driveNoneLayer.setVisibility(View.VISIBLE);
        }

        driveStatus.setVisibility(driving ? View.VISIBLE : View.GONE);
    }

    @UiThread
    void populateRepairMsg(RepairMsg repairMsg) {
        if (repairMsg != null && !repairMsg.getMsgType().isEmpty()) {
            manipulateRepairMsg(repairMsg.getMsgType());
        } else {
            repairMsgLayer.setVisibility(View.GONE);
        }
    }

    @UiThread
    void manipulateRepairMsg(RepairMsg.Msg msg) {
        if (msg.isEmpty() || msg.isComplete()) {
            repairMsgLayer.setVisibility(View.GONE);
        } else {
            repairMsgLayer.setVisibility(View.VISIBLE);
            repairMsgLayer.setBackgroundResource(msg.isEmergency() ? R.drawable.bg_box_red : R.drawable.bg_box_teal);
            repairMsgView.setText(msg.getMessage());
        }
    }

    @Subscribe
    public void onEvent(OnDriveHistoryUpdated event) {
        populateDriveHistory(event.getDriveHistory(), event.isDriving());
    }

    @Subscribe
    public void onEvent(OnVehicleDetect event) {
        load();
    }

    @Subscribe
    public void onEvent(OnNotiMessage event) {
        manipulateRepairMsg(event.getMsg());
    }

}

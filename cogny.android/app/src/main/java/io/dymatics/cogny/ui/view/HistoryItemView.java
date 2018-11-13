package io.dymatics.cogny.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.ColorRes;

import io.dymatics.cogny.Constants;
import io.dymatics.cogny.R;
import io.dymatics.cogny.domain.model.History;

@EViewGroup(R.layout.view_history_item)
public class HistoryItemView extends RelativeLayout {
    @ViewById(R.id.driveDateView) TextView driveDateView;
    @ViewById(R.id.messageIcon) View messageIcon;
    @ViewById(R.id.messageView) TextView messageView;
    @ViewById(R.id.driveInfoView) TextView driveInfoView;
    @ViewById(R.id.odometerView) TextView odometerView;

    @ColorRes(R.color.teal) int teal;
    @ColorRes(R.color.textColorPrimary) int textColorPrimary;

    private History history;

    public HistoryItemView(Context context) {
        super(context);
    }

    public HistoryItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @UiThread
    public void setData(History history) {
        this.history = history;

        driveDateView.setText(history.getDisplayIssuedDate());

        if (history.isHasRepair()) {
            messageIcon.setVisibility(VISIBLE);
            messageView.setText(R.string.message_diagnosis_msg1);
            messageView.setTextColor(teal);
        } else if (history.isHasRepairMsg()) {
            messageIcon.setVisibility(VISIBLE);
            messageView.setText(R.string.message_diagnosis_msg2);
            messageView.setTextColor(teal);
        } else {
            messageIcon.setVisibility(GONE);
            messageView.setText(R.string.message_diagnosis_msg3);
            messageView.setTextColor(textColorPrimary);
        }

        int driveTime = history.getDriveTime();
        if (driveTime > 0) {
            driveInfoView.setVisibility(VISIBLE);
            odometerView.setVisibility(VISIBLE);

            driveInfoView.setText(Constants.time(driveTime) + ", " + Constants.FORMAT_DISTANCE_2.format((int)(history.getDriveMileage() / 10F)) + " 운행");
            odometerView.setText(Constants.FORMAT_DISTANCE_3.format((int)(history.getTotalMileage() / 10F)));
        } else {
            driveInfoView.setVisibility(GONE);
            odometerView.setVisibility(GONE);
        }


    }
}
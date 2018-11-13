package io.dymatics.cognyreport.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.ColorRes;

import java.util.Date;

import io.dymatics.cognyreport.Constants;
import io.dymatics.cognyreport.R;
import io.dymatics.cognyreport.domain.model.PerformHistory;

@EViewGroup(R.layout.view_perform_history_item)
public class PerformHistoryItemView extends RelativeLayout {
    @ViewById(R.id.titleView) TextView titleView;
    @ViewById(R.id.bodyView) TextView bodyView;
    @ViewById(R.id.timeView) TextView timeView;

    @ColorRes(R.color.colorAccent) int colorAccent;
    @ColorRes(R.color.textColorPrimary) int textColorPrimary;

    private PerformHistory performHistory;

    public PerformHistoryItemView(Context context) {
        super(context);
    }

    public PerformHistoryItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @UiThread
    public void setData(PerformHistory performHistory) {
        this.performHistory = performHistory;

        titleView.setText(performHistory.getTitle());
        String body = performHistory.getBody();
        if (body == null) {
            bodyView.setText(null);
            bodyView.setVisibility(GONE);
        } else {
            bodyView.setText(body);
            bodyView.setVisibility(VISIBLE);
        }

        boolean diagnosisOrDtc = performHistory.getRef().isDiagnosisOrDtc();
        titleView.setTextColor(diagnosisOrDtc ? colorAccent : textColorPrimary);
        Date date = diagnosisOrDtc ? performHistory.getIssuedTime() : performHistory.getRegDate();
        if (date != null) {
            timeView.setText(Constants.FORMAT_TIME_HM.format(date));
        }
    }
}
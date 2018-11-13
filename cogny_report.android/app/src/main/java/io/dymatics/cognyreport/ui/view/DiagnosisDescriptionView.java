package io.dymatics.cognyreport.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.ColorRes;

import io.dymatics.cognyreport.R;
import io.dymatics.cognyreport.domain.model.Diagnosis;

@EViewGroup(R.layout.view_diagnosis_description)
public class DiagnosisDescriptionView extends FrameLayout {
    @ViewById(R.id.titleView) TextView titleView;
    @ViewById(R.id.subtitleView) TextView subtitleView;
    @ViewById(R.id.descriptionView) TextView descriptionView;
    @ViewById(R.id.divider) View divider;

    @ColorRes(R.color.red) int red;
    @ColorRes(R.color.colorAccent) int colorAccent;
    @ColorRes(R.color.textColorPrimary) int textColorPrimary;

    public DiagnosisDescriptionView(Context context) {
        super(context);
    }

    public DiagnosisDescriptionView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @UiThread
    public void setData(Diagnosis.Summary summary, boolean existNext) {
        divider.setVisibility(existNext ? VISIBLE : GONE);

        titleView.setText(summary.getName());
        if (summary.isTireNormal()) {
            descriptionView.setText(null);
            descriptionView.setVisibility(GONE);
        } else {
            descriptionView.setText(summary.getServiceMsg());
        }

        String diagnosisMsg = summary.getDiagnosisMsg();
        if (diagnosisMsg != null) {
            subtitleView.setVisibility(VISIBLE);

            if (summary.getDiagnosisResult().isCaution()) {
                subtitleView.setText(diagnosisMsg);
                subtitleView.setTextColor(colorAccent);

            } else if (summary.getDiagnosisResult().isFatal()) {
                subtitleView.setText(diagnosisMsg);
                subtitleView.setTextColor(red);

            } else if (summary.getDiagnosisResult().isNormal()) {
                subtitleView.setText(diagnosisMsg);
                subtitleView.setTextColor(textColorPrimary);

            } else if (summary.getDiagnosisResult().isNotAvailable()) {
                subtitleView.setText(diagnosisMsg);
            }

        } else {
            subtitleView.setVisibility(GONE);
        }



    }
}
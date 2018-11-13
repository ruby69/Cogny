package io.dymatics.cognyreport.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.ColorRes;

import java.util.Iterator;
import java.util.List;

import io.dymatics.cognyreport.R;
import io.dymatics.cognyreport.domain.model.Diagnosis;

@EViewGroup(R.layout.view_diagnosis_item)
public class DiagnosisItemView extends FrameLayout {
    @ViewById(R.id.iconView) ImageView iconView;
    @ViewById(R.id.nameView) TextView nameView;
    @ViewById(R.id.noticeView) TextView noticeView;
    @ViewById(R.id.arrowView) ImageView arrowView;
    @ViewById(R.id.descriptionLayer) LinearLayout descriptionLayer;

    @ColorRes(R.color.red) int red;
    @ColorRes(R.color.colorAccent) int colorAccent;
    @ColorRes(R.color.textColorPrimary) int textColorPrimary;

    public DiagnosisItemView(Context context) {
        super(context);
    }

    public DiagnosisItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @UiThread
    public void setData(List<Diagnosis.Summary> list, int iconResId, int nameResId) {
        iconView.setImageResource(iconResId);
        nameView.setText(nameResId);

        if (list != null && !list.isEmpty()) {
            Diagnosis.Summary titleSummary = getTitleSummary(list);
            if (titleSummary.getDiagnosisResult().isCaution()) {
                noticeView.setText(R.string.label_status_diagnosis_caution);
                noticeView.setTextColor(colorAccent);
            } else if (titleSummary.getDiagnosisResult().isFatal()) {
                noticeView.setText(R.string.label_status_diagnosis_fatal);
                noticeView.setTextColor(red);
            } else if (titleSummary.getDiagnosisResult().isNormal()) {
                noticeView.setText(R.string.label_status_diagnosis_normal);
                noticeView.setTextColor(textColorPrimary);
            } else if (titleSummary.getDiagnosisResult().isNotAvailable()) {
                noticeView.setText(R.string.label_status_diagnosis_not_available);
                noticeView.setTextColor(textColorPrimary);
            }

            descriptionLayer.removeAllViews();
            if (titleSummary.isShowDescription()) {
                arrowView.setVisibility(View.VISIBLE);
                for (Iterator<Diagnosis.Summary> it = list.iterator(); it.hasNext(); ) {
                    Diagnosis.Summary summary = it.next();
                    if (summary.isShowDescription()) {
                        DiagnosisDescriptionView descriptionView = DiagnosisDescriptionView_.build(getContext());
                        descriptionView.setData(summary, it.hasNext());
                        descriptionLayer.addView(descriptionView);
                    }
                }
            } else {
                arrowView.setVisibility(View.GONE);
            }

        } else {
            arrowView.setVisibility(View.GONE);
            noticeView.setText(R.string.label_status_diagnosis_empty);
            noticeView.setTextColor(textColorPrimary);
            descriptionLayer.setVisibility(View.GONE);
        }
    }

    private Diagnosis.Summary getTitleSummary(List<Diagnosis.Summary> list) {
        for (Diagnosis.Summary summary : list) {
            if (summary.getDiagnosisResult().isFatal()) {
                return summary;
            }
        }

        for (Diagnosis.Summary summary : list) {
            if (summary.getDiagnosisResult().isCaution()) {
                return summary;
            }
        }

        return list.get(0);
    }

    @Click(R.id.diagnosisLayer)
    void onClickDiagnosisLayer() {
        if (arrowView.getVisibility() == VISIBLE) {
            if (descriptionLayer.getVisibility() == View.VISIBLE) {
                descriptionLayer.setVisibility(View.GONE);
                arrowView.setImageResource(R.drawable.ic_arrow_drop_down_black_24dp);
            } else {
                descriptionLayer.setVisibility(View.VISIBLE);
                arrowView.setImageResource(R.drawable.ic_arrow_drop_up_black_24dp);
            }
        }
    }

    @Click(R.id.descriptionLayer)
    void onClickDescriptionLayer() {

    }
}
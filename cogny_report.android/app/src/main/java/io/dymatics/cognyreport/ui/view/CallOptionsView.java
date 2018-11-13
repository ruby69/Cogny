package io.dymatics.cognyreport.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import io.dymatics.cognyreport.R;
import io.dymatics.cognyreport.domain.model.RepairMsg;
import lombok.Getter;

@EViewGroup(R.layout.view_call_options)
public class CallOptionsView extends RelativeLayout {
    @ViewById(R.id.callOptions) RadioGroup callOptions;

    @Getter RepairMsg.Msg msg = RepairMsg.Msg.NORMAL;

    public CallOptionsView(Context context) {
        super(context);
    }

    public CallOptionsView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @AfterViews
    void afterViews() {
    }

    @Click({R.id.option1, R.id.option2, R.id.option0})
    void onClick(View view) {
        try {
            String tag = (String) view.getTag();
            msg = RepairMsg.Msg.valueOf(tag);
        } catch (Exception e) {
            msg = RepairMsg.Msg.EMPTY;
        }
    }
}

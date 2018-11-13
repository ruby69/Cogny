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

@EViewGroup(R.layout.view_dtc_info_item)
public class DtcInfoItemView extends RelativeLayout {
    @ViewById(R.id.dtcCode) TextView dtcCode;
    @ViewById(R.id.dtcDesc) TextView dtcDesc;
    @ViewById(R.id.issuedTime) TextView issuedTime;

    private DtcReport.DtcInfo dtcInfo;

    public DtcInfoItemView(Context context) {
        super(context);
    }

    public DtcInfoItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @UiThread
    public void setData(DtcReport.DtcInfo dtcInfo) {
        this.dtcInfo = dtcInfo;

        dtcCode.setText(dtcInfo.getDtcCode());
        dtcDesc.setText(dtcInfo.getDesc());
        issuedTime.setText(Constants.FORMAT_DATETIME_DTC_ISSUED.format(dtcInfo.getIssuedTime()));
    }

}
package io.dymatics.cognyreport.ui.view;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import io.dymatics.cognyreport.Constants;
import io.dymatics.cognyreport.R;
import io.dymatics.cognyreport.domain.model.DtcReport;
import io.dymatics.cognyreport.ui.adapter.DtcReportRecyclerViewAdapter;

@EViewGroup(R.layout.view_dtc_report_group_item)
public class DtcReportGroupItemView extends RelativeLayout {
    @ViewById(R.id.driveDate) TextView driveDate;
    @ViewById(R.id.recyclerView) RecyclerView recyclerView;

    @Bean DtcReportRecyclerViewAdapter recyclerViewAdapter;

    private DtcReport.Group reportGroup;

    public DtcReportGroupItemView(Context context) {
        super(context);
    }

    public DtcReportGroupItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @AfterViews
    void afterViews() {
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    @UiThread
    public void setData(DtcReport.Group reportGroup) {
        this.reportGroup = reportGroup;

        driveDate.setText(Constants.convertDate(reportGroup.getDriveDate()));
        recyclerViewAdapter.reset(reportGroup.getReports());
    }

}
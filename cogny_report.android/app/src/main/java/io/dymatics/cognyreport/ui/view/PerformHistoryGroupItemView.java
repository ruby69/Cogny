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
import io.dymatics.cognyreport.domain.model.PerformHistory;
import io.dymatics.cognyreport.ui.adapter.ReportHistoryRecyclerViewAdapter;

@EViewGroup(R.layout.view_perform_history_group_item)
public class PerformHistoryGroupItemView extends RelativeLayout {
    @ViewById(R.id.driveDate) TextView driveDate;
    @ViewById(R.id.recyclerView) RecyclerView recyclerView;

    @Bean ReportHistoryRecyclerViewAdapter recyclerViewAdapter;

    private PerformHistory.Group performHistoryGroup;

    public PerformHistoryGroupItemView(Context context) {
        super(context);
    }

    public PerformHistoryGroupItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @AfterViews
    void afterViews() {
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    @UiThread
    public void setData(PerformHistory.Group performHistoryGroup) {
        this.performHistoryGroup = performHistoryGroup;

        driveDate.setText(Constants.convertDate(performHistoryGroup.getIssuedDate()));
        recyclerViewAdapter.reset(performHistoryGroup.getList());
    }

}
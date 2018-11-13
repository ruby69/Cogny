package io.dymatics.cognyreport.ui.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import org.androidannotations.annotations.EBean;

import io.dymatics.cognyreport.domain.model.DtcReport;
import io.dymatics.cognyreport.ui.view.DtcReportGroupItemView;
import io.dymatics.cognyreport.ui.view.DtcReportGroupItemView_;

@EBean
public class DtcReportGroupRecyclerViewAdapter extends CommonRecyclerViewAdapter<DtcReport.Group, DtcReportGroupRecyclerViewAdapter.ViewHolder> {

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.dtcReportGroupItemView.setData(items.get(position));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        return new ViewHolder(DtcReportGroupItemView_.build(viewGroup.getContext()));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        DtcReportGroupItemView dtcReportGroupItemView;
        public ViewHolder(View itemView) {
            super(itemView);
            this.dtcReportGroupItemView = (DtcReportGroupItemView) itemView;
        }
    }

}
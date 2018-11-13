package io.dymatics.cognyreport.ui.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import org.androidannotations.annotations.EBean;

import io.dymatics.cognyreport.domain.model.DtcReport;
import io.dymatics.cognyreport.ui.view.DtcReportItemView;
import io.dymatics.cognyreport.ui.view.DtcReportItemView_;

@EBean
public class DtcReportRecyclerViewAdapter extends CommonRecyclerViewAdapter<DtcReport, DtcReportRecyclerViewAdapter.ViewHolder> {

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.dtcReportItemView.setData(items.get(position));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        return new ViewHolder(DtcReportItemView_.build(viewGroup.getContext()));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        DtcReportItemView dtcReportItemView;
        public ViewHolder(View itemView) {
            super(itemView);
            this.dtcReportItemView = (DtcReportItemView) itemView;
        }
    }
}
package io.dymatics.cognyreport.ui.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import org.androidannotations.annotations.EBean;

import io.dymatics.cognyreport.domain.model.DtcReport;
import io.dymatics.cognyreport.ui.view.DtcInfoItemView;
import io.dymatics.cognyreport.ui.view.DtcInfoItemView_;

@EBean
public class DtcInfoRecyclerViewAdapter extends CommonRecyclerViewAdapter<DtcReport.DtcInfo, DtcInfoRecyclerViewAdapter.ViewHolder> {

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.dtcInfoItemView.setData(items.get(position));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        return new ViewHolder(DtcInfoItemView_.build(viewGroup.getContext()));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        DtcInfoItemView dtcInfoItemView;
        public ViewHolder(View itemView) {
            super(itemView);
            this.dtcInfoItemView = (DtcInfoItemView) itemView;
        }
    }
}
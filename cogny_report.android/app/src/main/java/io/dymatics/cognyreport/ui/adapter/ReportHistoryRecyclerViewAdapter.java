package io.dymatics.cognyreport.ui.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import org.androidannotations.annotations.EBean;

import io.dymatics.cognyreport.domain.model.PerformHistory;
import io.dymatics.cognyreport.ui.view.PerformHistoryItemView;
import io.dymatics.cognyreport.ui.view.PerformHistoryItemView_;

@EBean
public class ReportHistoryRecyclerViewAdapter extends CommonRecyclerViewAdapter<PerformHistory, ReportHistoryRecyclerViewAdapter.ViewHolder> {

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.performHistoryItemView.setData(items.get(position));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        return new ViewHolder(PerformHistoryItemView_.build(viewGroup.getContext()));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        PerformHistoryItemView performHistoryItemView;
        public ViewHolder(View itemView) {
            super(itemView);
            this.performHistoryItemView = (PerformHistoryItemView) itemView;
        }
    }
}
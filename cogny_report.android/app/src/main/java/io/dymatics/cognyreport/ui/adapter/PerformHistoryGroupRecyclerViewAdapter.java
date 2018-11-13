package io.dymatics.cognyreport.ui.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import org.androidannotations.annotations.EBean;

import io.dymatics.cognyreport.domain.model.Loadable;
import io.dymatics.cognyreport.domain.model.PerformHistory;
import io.dymatics.cognyreport.ui.view.PerformHistoryGroupItemView;
import io.dymatics.cognyreport.ui.view.PerformHistoryGroupItemView_;
import lombok.Setter;

@EBean
public class PerformHistoryGroupRecyclerViewAdapter extends CommonRecyclerViewAdapter<PerformHistory.Group, PerformHistoryGroupRecyclerViewAdapter.ViewHolder> {
    @Setter private Loadable pageLoader;
    private static final int pageCheckCount = 10;

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        if (pageLoader != null) {
            int itemCount = this.getItemCount();
            if(itemCount > pageCheckCount && position == itemCount - pageCheckCount) {
                pageLoader.load();
            }
        }

        viewHolder.performHistoryGroupItemView.setData(items.get(position));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        return new ViewHolder(PerformHistoryGroupItemView_.build(viewGroup.getContext()));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        PerformHistoryGroupItemView performHistoryGroupItemView;
        public ViewHolder(View itemView) {
            super(itemView);
            this.performHistoryGroupItemView = (PerformHistoryGroupItemView) itemView;
        }
    }
}
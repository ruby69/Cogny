package io.dymatics.cogny.ui.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import org.androidannotations.annotations.EBean;

import io.dymatics.cogny.domain.model.History;
import io.dymatics.cogny.domain.model.Loadable;
import io.dymatics.cogny.ui.view.HistoryItemView;
import io.dymatics.cogny.ui.view.HistoryItemView_;
import lombok.Setter;

@EBean
public class HistoryRecyclerViewAdapter extends CommonRecyclerViewAdapter<History, HistoryRecyclerViewAdapter.ViewHolder> {
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

        viewHolder.historyItemView.setData(items.get(position));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        return new ViewHolder(HistoryItemView_.build(viewGroup.getContext()));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        HistoryItemView historyItemView;
        public ViewHolder(View itemView) {
            super(itemView);
            this.historyItemView = (HistoryItemView) itemView;
        }
    }
}
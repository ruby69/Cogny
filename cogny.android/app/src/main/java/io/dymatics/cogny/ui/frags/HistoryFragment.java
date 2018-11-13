package io.dymatics.cogny.ui.frags;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import io.dymatics.cogny.On;
import io.dymatics.cogny.R;
import io.dymatics.cogny.domain.model.History;
import io.dymatics.cogny.domain.model.Loadable;
import io.dymatics.cogny.event.OnDriveFin;
import io.dymatics.cogny.event.OnDriveHistoryUpdated;
import io.dymatics.cogny.event.OnNotiMessage;
import io.dymatics.cogny.event.OnVehicleDetect;
import io.dymatics.cogny.service.CognyBean;
import io.dymatics.cogny.service.RestClient;
import io.dymatics.cogny.support.EventBusObserver;
import io.dymatics.cogny.ui.adapter.HistoryRecyclerViewAdapter;

@EFragment(R.layout.fragment_history)
public class HistoryFragment extends Fragment implements Loadable {
    @ViewById(R.id.historyLayer) View historyLayer;
    @ViewById(R.id.recyclerView) RecyclerView recyclerView;
    @ViewById(R.id.more) View more;
    @ViewById(R.id.historyNoneLayer) View historyNoneLayer;
    @ViewById(R.id.historyNoneMessage) TextView historyNoneMessage;

    @Bean CognyBean cognyBean;
    @Bean HistoryRecyclerViewAdapter recyclerViewAdapter;
    @Bean RestClient restClient;

    private History.Page lastPage = null;
    private On<History.Page> on;

    @AfterInject
    void afterInject() {
        recyclerViewAdapter.setPageLoader(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLifecycle().addObserver(new EventBusObserver.AtStartStop(this));
    }

    @AfterViews
    void afterViews() {
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setNestedScrollingEnabled(false);

        on = new On<History.Page>().addSuccessListener(this::manipulate);
        load();
    }

    @UiThread
    void manipulate(History.Page response) {
        if (recyclerView != null && recyclerViewAdapter != null && response != null) {
            List<History> contents = response.getContents();

            if (contents == null || contents.isEmpty()) {
                historyLayer.setVisibility(View.GONE);
                historyNoneLayer.setVisibility(View.VISIBLE);
                historyNoneMessage.setText(R.string.label_drive_history_empty1);
            } else {
                historyLayer.setVisibility(View.VISIBLE);
                historyNoneLayer.setVisibility(View.GONE);

                if (response.getPage() == 0) {
                    recyclerViewAdapter.reset(contents);
                    recyclerView.scrollToPosition(0);
                } else {
                    recyclerViewAdapter.add(contents);
                }

                more.setVisibility(response.isHasMore() ? View.VISIBLE : View.GONE);
                lastPage = response;
            }
        }
    }

    @Override
    public void load() {
        if (restClient != null) {
            long vehicleNo = cognyBean.getVehicleNo();
            if (vehicleNo > 0L) {
                if (lastPage == null) {
                    restClient.fetchHistories(vehicleNo, 0, on);
                    return;
                }

                if (lastPage.isHasMore()) {
                    restClient.fetchHistories(vehicleNo, lastPage.getPage() + 1, on);
                }
            } else {
                historyLayer.setVisibility(View.GONE);
                historyNoneLayer.setVisibility(View.VISIBLE);
                historyNoneMessage.setText(R.string.label_drive_history_empty);
            }
        }
    }

    @Click(R.id.historyHelp)
    void onClickHistoryHelp() {
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setMessage(R.string.message_drive_history_help)
                .setPositiveButton(R.string.label_common_confirm, (d, w) -> {})
                .create();

        if (!getActivity().isFinishing()) {
            dialog.show();
        }
    }

    @Click(R.id.more)
    void onClickMore() {
        more.setVisibility(View.GONE);
        load();
    }

    @Click(R.id.refreshHistories)
    void onClickRefreshHistories() {
        more.setVisibility(View.GONE);
        refresh();
    }

    @Subscribe
    public void onEvent(OnVehicleDetect event) {
        refresh();
    }

    @Subscribe
    public void onEvent(OnDriveFin event) {
        refresh();
    }

    @Subscribe
    public void onEvent(OnNotiMessage event) {
        refresh();
    }

    private void refresh() {
        lastPage = null;
        load();
    }

    private int updatedCount;
    @Subscribe
    public void onEvent(OnDriveHistoryUpdated event) {
        if (++updatedCount % 300 == 0) {
            refresh();
        }
    }
}

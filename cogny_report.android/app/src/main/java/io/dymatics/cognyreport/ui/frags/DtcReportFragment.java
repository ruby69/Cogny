package io.dymatics.cognyreport.ui.frags;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import io.dymatics.cognyreport.On;
import io.dymatics.cognyreport.R;
import io.dymatics.cognyreport.domain.model.DtcReport;
import io.dymatics.cognyreport.domain.model.RepairForm;
import io.dymatics.cognyreport.domain.model.RepairMsg;
import io.dymatics.cognyreport.event.OnCallToDriver;
import io.dymatics.cognyreport.event.OnChangedDtcReport;
import io.dymatics.cognyreport.event.OnCompleteHelp;
import io.dymatics.cognyreport.event.OnCompleteRepair;
import io.dymatics.cognyreport.service.RestClient;
import io.dymatics.cognyreport.support.EventBusObserver;
import io.dymatics.cognyreport.ui.adapter.DtcReportGroupRecyclerViewAdapter;
import io.dymatics.cognyreport.ui.view.CallOptionsView;
import io.dymatics.cognyreport.ui.view.CallOptionsView_;

@EFragment(R.layout.fragment_dtc_report)
public class DtcReportFragment extends Fragment {
    @ViewById(R.id.recyclerView) RecyclerView recyclerView;

    @Bean RestClient restClient;
    @Bean DtcReportGroupRecyclerViewAdapter recyclerViewAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLifecycle().addObserver(new EventBusObserver.AtStartStop(this));
    }

    @AfterViews
    void afterViews() {
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setNestedScrollingEnabled(false);
        load();
    }

    private void load() {
        restClient.fetchDtcReports(new On<DtcReport.Groups>().addSuccessListener(result -> refresh(result)));
    }

    @UiThread
    void refresh(DtcReport.Groups dtcReportGroups) {
        recyclerViewAdapter.reset(dtcReportGroups);

        if (recyclerView != null) {
            recyclerView.scrollToPosition(0);
        }
    }

    @Subscribe
    @UiThread
    public void onEvent(OnCallToDriver event) {
        DtcReport dtcReport = event.getDtcReport();
        String mobileNumber = dtcReport.getUserMobileDevice().getMobileNumber();
        CallOptionsView callOptionsView = CallOptionsView_.build(getActivity());

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setView(callOptionsView);

        if (mobileNumber != null) {
            builder.setMessage(R.string.label_contact_options)
                    .setPositiveButton(R.string.label_contact_call, (dialog1, which) -> {
                        restClient.postRepairNoti(RepairMsg.Form.instance(dtcReport, callOptionsView.getMsg(), RepairMsg.Call.MSG),
                                new On<RepairMsg>().addSuccessListener(repairMsg -> {
                                    dtcReport.setRepairMsg(repairMsg);
                                    EventBus.getDefault().post(new OnChangedDtcReport(dtcReport));
                                }));
                        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+mobileNumber)));
                    })
                    .setNegativeButton(R.string.label_contact_message_no_call, (dialog1, which) -> {
                        restClient.postRepairNoti(RepairMsg.Form.instance(dtcReport, callOptionsView.getMsg(), RepairMsg.Call.MSG),
                                new On<RepairMsg>().addSuccessListener(repairMsg -> {
                                    dtcReport.setRepairMsg(repairMsg);
                                    EventBus.getDefault().post(new OnChangedDtcReport(dtcReport));
                                }));
                    })
                    .setNeutralButton(R.string.label_common_cancel, (dialog1, which) -> {
                    });
        } else {
            builder.setMessage(R.string.label_contact_options_no_call)
                    .setPositiveButton(R.string.label_contact_message, (dialog1, which) -> {
                        restClient.postRepairNoti(RepairMsg.Form.instance(dtcReport, callOptionsView.getMsg(), RepairMsg.Call.MSG),
                                new On<RepairMsg>().addSuccessListener(repairMsg -> {
                                    dtcReport.setRepairMsg(repairMsg);
                                    EventBus.getDefault().post(new OnChangedDtcReport(dtcReport));
                                }));
                    })
                    .setNegativeButton(R.string.label_common_cancel, (dialog1, which) -> {
                    });
        }

        if (!getActivity().isFinishing()) {
            builder.create().show();
        }
    }

    @Subscribe
    @UiThread
    public void onEvent(OnCompleteRepair event) {
        DtcReport dtcReport = event.getDtcReport();
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setMessage(R.string.message_dtc_repair_complete)
                .setPositiveButton(R.string.label_common_confirm, (d, w) -> {
                    restClient.postRepairs(RepairForm.instance(dtcReport),
                            new On<Void>().addSuccessListener(aVoid -> {
                                dtcReport.setRepairMsg(null);
                                EventBus.getDefault().post(new OnChangedDtcReport(dtcReport));
                            })
                    );
                })
                .setNegativeButton(R.string.label_common_cancel, (d, w) -> {})
                .create();

        if (!getActivity().isFinishing()) {
            dialog.show();
        }
    }

    @Subscribe
    @UiThread
    public void onEvent(OnCompleteHelp event) {
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setMessage(R.string.message_dtc_repair_complete_help)
                .setPositiveButton(R.string.label_common_confirm, (d, w) -> {})
                .create();

        if (!getActivity().isFinishing()) {
            dialog.show();
        }
    }

    @Click(R.id.refresh)
    void onClickRefresh() {
        load();
    }
}

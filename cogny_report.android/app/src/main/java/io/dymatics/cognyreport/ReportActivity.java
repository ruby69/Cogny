package io.dymatics.cognyreport;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.ColorRes;
import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import io.dymatics.cognyreport.domain.model.Diagnosis;
import io.dymatics.cognyreport.domain.model.DtcReport;
import io.dymatics.cognyreport.domain.model.RepairForm;
import io.dymatics.cognyreport.domain.model.RepairMsg;
import io.dymatics.cognyreport.domain.model.UserMobileDevice;
import io.dymatics.cognyreport.event.OnChangedDtcReport;
import io.dymatics.cognyreport.service.CognyBean;
import io.dymatics.cognyreport.service.RestClient;
import io.dymatics.cognyreport.ui.adapter.DtcInfoRecyclerViewAdapter;
import io.dymatics.cognyreport.ui.view.CallOptionsView;
import io.dymatics.cognyreport.ui.view.CallOptionsView_;
import io.dymatics.cognyreport.ui.view.DiagnosisItemView;
import io.dymatics.cognyreport.ui.view.VehicleInfoView;

@EActivity(R.layout.activity_report)
public class ReportActivity extends AppCompatActivity {
    @ViewById(R.id.toolbar) Toolbar toolbar;
    @ViewById(R.id.vehicleInfoView) VehicleInfoView vehicleInfoView;
    @ViewById(R.id.drivingView) View drivingView;
    @ViewById(R.id.recyclerView) RecyclerView recyclerView;
    @ViewById(R.id.repairMsgView) TextView repairMsgView;
    @ViewById(R.id.dtcNoneLayer) View dtcNoneLayer;

    @ViewById(R.id.ignitionView) DiagnosisItemView ignitionView;
    @ViewById(R.id.fuelView) DiagnosisItemView fuelView;
    @ViewById(R.id.engineMachineView) DiagnosisItemView engineMachineView;
    @ViewById(R.id.engineControlView) DiagnosisItemView engineControlView;
    @ViewById(R.id.tireView) DiagnosisItemView tireView;
    @ViewById(R.id.generatorView) DiagnosisItemView generatorView;
    @ViewById(R.id.coolantView) DiagnosisItemView coolantView;

    @Bean DtcInfoRecyclerViewAdapter recyclerViewAdapter;
    @Bean RestClient restClient;
    @Bean CognyBean cognyBean;

    @ColorRes(R.color.colorAccent) int accentColor;
    @ColorRes(R.color.textColorPrimary) int textColorPrimary;
    @ColorRes(R.color.grey2) int grey;
    @ColorRes(R.color.red) int red;

    @Extra("dtcReport") DtcReport dtcReport;

    @AfterViews
    void afterViews() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.getNavigationIcon().setColorFilter(accentColor, PorterDuff.Mode.SRC_IN);

        vehicleInfoView.populate(dtcReport);
        drivingView.setVisibility(dtcReport.isDriving() ? View.VISIBLE : View.GONE);
        manipulateRepairMsg();
        manipulateDiagnosis();
        manipulateDtcList();

        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setNestedScrollingEnabled(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @UiThread
    void manipulateRepairMsg() {
        RepairMsg repairMsg = dtcReport.getRepairMsg();
        if (repairMsg == null || repairMsg.getMsgType().isEmpty()) {
            repairMsgView.setText(R.string.label_dtc_repair_msg_none);
            repairMsgView.setTextColor(grey);
        } else {
            RepairMsg.Msg msgType = repairMsg.getMsgType();
            repairMsgView.setText(msgType.getMessage());
            if (msgType.isEmergency()) {
                repairMsgView.setTextColor(red);
            } else if (msgType.isNormal()) {
                repairMsgView.setTextColor(accentColor);
            } else {
                repairMsgView.setTextColor(grey);
            }
        }
    }

    @UiThread
    void manipulateDiagnosis() {
        Map<Long, List<Diagnosis.Summary>> map = dtcReport.getDiagnosis();
        ignitionView.setData(map.get(Diagnosis.CATE_IGNITION), R.drawable.ic_dia_o_08, R.string.label_status_cate_engine_ignite);
        fuelView.setData(map.get(Diagnosis.CATE_FUEL), R.drawable.ic_dia_o_09, R.string.label_status_cate_engine_fuel);
        engineMachineView.setData(map.get(Diagnosis.CATE_ENGINE), R.drawable.ic_dia_o_06, R.string.label_status_cate_engine_machine);
        engineControlView.setData(map.get(Diagnosis.CATE_ENGINE_CONTROL), R.drawable.ic_dia_o_07, R.string.label_status_cate_engine_control);
        tireView.setData(map.get(Diagnosis.CATE_TIRE), R.drawable.ic_dia_o_03, R.string.label_status_cate_parts_tire);
        generatorView.setData(map.get(Diagnosis.CATE_GENERATOR), R.drawable.ic_dia_o_10, R.string.label_status_cate_parts_generator);
        coolantView.setData(map.get(Diagnosis.CATE_COOLANT), R.drawable.ic_dia_o_02, R.string.label_status_cate_parts_coolant);

        List<Diagnosis.Category> categories = cognyBean.getDiagnosisCategories();
        if (categories != null && !categories.isEmpty()) {
            for (Diagnosis.Category category : categories) {
                if (!category.isEnabled()) {
                    hideDiagnosisView(category);
                }
            }
        }
    }

    private void hideDiagnosisView(Diagnosis.Category category) {
        Long diagnosisCateNo = category.getDiagnosisCateNo();
        if (diagnosisCateNo.longValue() == Diagnosis.CATE_IGNITION.longValue()) {
            ignitionView.setVisibility(View.GONE);
        } else if (diagnosisCateNo.longValue() == Diagnosis.CATE_FUEL.longValue()) {
            fuelView.setVisibility(View.GONE);
        } else if (diagnosisCateNo.longValue() == Diagnosis.CATE_ENGINE.longValue()) {
            engineMachineView.setVisibility(View.GONE);
        } else if (diagnosisCateNo.longValue() == Diagnosis.CATE_ENGINE_CONTROL.longValue()) {
            engineControlView.setVisibility(View.GONE);
        } else if (diagnosisCateNo.longValue() == Diagnosis.CATE_TIRE.longValue()) {
            tireView.setVisibility(View.GONE);
        } else if (diagnosisCateNo.longValue() == Diagnosis.CATE_GENERATOR.longValue()) {
            generatorView.setVisibility(View.GONE);
        } else if (diagnosisCateNo.longValue() == Diagnosis.CATE_COOLANT.longValue()) {
            coolantView.setVisibility(View.GONE);
        }
    }

    @UiThread
    void manipulateDtcList() {
        ArrayList<DtcReport.DtcInfo> dtcs = dtcReport.getDtcs();
        if (dtcs == null || dtcs.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            dtcNoneLayer.setVisibility(View.VISIBLE);
        } else {
            recyclerViewAdapter.reset(dtcs);
            recyclerView.setVisibility(View.VISIBLE);
            dtcNoneLayer.setVisibility(View.GONE);
        }
    }

    @Click(R.id.showReport)
    void onClickShowReport() {
        PerformHistoryActivity_.intent(this).dtcReport(dtcReport).start();
    }

    @Click(R.id.completeHelp)
    void onClickCompleteHelp() {
        RepairMsg repairMsg = dtcReport.getRepairMsg();
        int msgResId = (repairMsg == null || repairMsg.getMsgType().isEmpty()) ? R.string.message_dtc_repair_complete_help2 : R.string.message_dtc_repair_complete_help;
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage(msgResId)
                .setPositiveButton(R.string.label_common_confirm, (d, w) -> {})
                .create();

        if (!isFinishing()) {
            dialog.show();
        }
    }

    @Click(R.id.complete)
    void onClickComplete() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage(R.string.message_dtc_repair_complete)
                .setPositiveButton(R.string.label_common_confirm, (d, w) -> {
                    restClient.postRepairs(RepairForm.instance(dtcReport),
                            new On<Void>().addSuccessListener(aVoid -> {
                                dtcReport.setRepairMsg(null);
                                dtcReport.setDtcs(null);
                                Map<Long, List<Diagnosis.Summary>> diagnosis = dtcReport.getDiagnosis();
                                diagnosis.clear();
                                dtcReport.setDiagnosis(diagnosis);

                                manipulateRepairMsg();
                                manipulateDiagnosis();
                                manipulateDtcList();
                                EventBus.getDefault().post(new OnChangedDtcReport(dtcReport));
                            })
                    );
                })
                .setNegativeButton(R.string.label_common_cancel, (d, w) -> {})
                .create();

        if (!isFinishing()) {
            dialog.show();
        }
    }

    @Click(R.id.callToDriver)
    void onClickCallToDriver() {
        showCallOptions();
    }

    @UiThread
    void showCallOptions() {
        UserMobileDevice userMobileDevice = dtcReport.getUserMobileDevice();
        String mobileNumber = userMobileDevice.getMobileNumber();
        CallOptionsView callOptionsView = CallOptionsView_.build(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(this).setView(callOptionsView);

        if (mobileNumber != null) {
            builder.setMessage(R.string.label_contact_options)
                    .setPositiveButton(R.string.label_contact_call, (dialog1, which) -> {
                        confirmCallAndPush(String.format(getString(R.string.message_call_to_user), userMobileDevice.getName()), (d, w) -> {
                            restClient.postRepairNoti(RepairMsg.Form.instance(dtcReport, callOptionsView.getMsg(), RepairMsg.Call.CALL),
                                    new On<RepairMsg>().addSuccessListener(repairMsg -> {
                                        dtcReport.setRepairMsg(repairMsg);
                                        manipulateRepairMsg();
                                        EventBus.getDefault().post(new OnChangedDtcReport(dtcReport));
                                    }));
                            startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mobileNumber)));
                        });
                    })
                    .setNegativeButton(R.string.label_contact_message_no_call, (dialog1, which) -> {
                        confirmCallAndPush(String.format(getString(R.string.message_push_to_user), userMobileDevice.getName()), (d, w) -> {
                            restClient.postRepairNoti(RepairMsg.Form.instance(dtcReport, callOptionsView.getMsg(), RepairMsg.Call.MSG),
                                    new On<RepairMsg>().addSuccessListener(repairMsg -> {
                                        dtcReport.setRepairMsg(repairMsg);
                                        manipulateRepairMsg();
                                        EventBus.getDefault().post(new OnChangedDtcReport(dtcReport));
                                    }));
                        });
                    })
                    .setNeutralButton(R.string.label_common_cancel, (dialog1, which) -> {});
        } else {
            builder.setMessage(R.string.label_contact_options_no_call)
                    .setPositiveButton(R.string.label_contact_message, (dialog1, which) -> {
                        confirmCallAndPush(String.format(getString(R.string.message_push_to_user), userMobileDevice.getName()), (d, w) -> {
                            restClient.postRepairNoti(RepairMsg.Form.instance(dtcReport, callOptionsView.getMsg(), RepairMsg.Call.MSG),
                                    new On<RepairMsg>().addSuccessListener(repairMsg -> {
                                        dtcReport.setRepairMsg(repairMsg);
                                        manipulateRepairMsg();
                                        EventBus.getDefault().post(new OnChangedDtcReport(dtcReport));
                                    }));
                        });
                    })
                    .setNegativeButton(R.string.label_common_cancel, (dialog1, which) -> {});
        }

        if (!isFinishing()) {
            builder.create().show();
        }
    }

    @UiThread
    void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @UiThread
    void confirmCallAndPush(String message, DialogInterface.OnClickListener confirmListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setMessage(message)
                .setCancelable(false)
                .setNegativeButton(R.string.label_common_cancel, (d, w) -> {})
                .setPositiveButton(R.string.label_common_confirm, confirmListener);

        if (!isFinishing()) {
            builder.create().show();
        }
    }
}

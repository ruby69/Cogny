package io.dymatics.cognyreport;

import android.graphics.PorterDuff;
import android.view.MenuItem;
import android.view.View;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.ColorRes;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import io.dymatics.cognyreport.domain.model.DtcReport;
import io.dymatics.cognyreport.domain.model.Loadable;
import io.dymatics.cognyreport.domain.model.PerformHistory;
import io.dymatics.cognyreport.service.RestClient;
import io.dymatics.cognyreport.ui.adapter.PerformHistoryGroupRecyclerViewAdapter;
import io.dymatics.cognyreport.ui.view.VehicleInfoView;

@EActivity(R.layout.activity_perform_history)
public class PerformHistoryActivity extends AppCompatActivity implements Loadable {
    @ViewById(R.id.toolbar) Toolbar toolbar;
    @ViewById(R.id.vehicleInfoView) VehicleInfoView vehicleInfoView;
    @ViewById(R.id.recyclerView) RecyclerView recyclerView;
    @ViewById(R.id.emptyLayer) View emptyLayer;

    @Bean PerformHistoryGroupRecyclerViewAdapter recyclerViewAdapter;
    @Bean RestClient restClient;

    @ColorRes(R.color.colorAccent) int accentColor;
    @Extra("dtcReport") DtcReport dtcReport;

    private PerformHistory.Page lastPage = null;
    private On<PerformHistory.Page> on;

    @AfterInject
    void afterInject() {
        recyclerViewAdapter.setPageLoader(this);
    }

    @AfterViews
    void afterViews() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.getNavigationIcon().setColorFilter(accentColor, PorterDuff.Mode.SRC_IN);

        vehicleInfoView.populate(dtcReport);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setNestedScrollingEnabled(false);

        on = new On<PerformHistory.Page>().addSuccessListener(this::manipulate);
        load();
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
    void manipulate(PerformHistory.Page response) {
        if (recyclerView != null && recyclerViewAdapter != null && response != null) {
            List<PerformHistory.Group> contents = response.getContents();

            if (contents == null || contents.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                emptyLayer.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                emptyLayer.setVisibility(View.GONE);

                if (response.getPage() == 0) {
                    recyclerViewAdapter.reset(contents);
                    recyclerView.scrollToPosition(0);
                } else {
                    recyclerViewAdapter.add(contents);
                }
                lastPage = response;
            }
        }
    }

    @Override
    public void load() {
        if (restClient != null) {
            if (lastPage == null) {
                restClient.fetchPerforms(dtcReport.getVehicleNo(), 0, on);
                return;
            }

            if (lastPage.isHasMore()) {
                restClient.fetchPerforms(dtcReport.getVehicleNo(), lastPage.getPage() + 1, on);
            }
        }
    }

    @Click(R.id.refresh)
    void onClickRefresh() {
        lastPage = null;
        load();
    }

    @Override
    public void onDestroy() {
        lastPage = null;
        on = null;

        if (recyclerViewAdapter != null) {
            recyclerViewAdapter.clear();
            recyclerViewAdapter.setPageLoader(null);
            recyclerViewAdapter = null;
        }

        if (recyclerView != null) {
            recyclerView.setAdapter(null);
        }
        super.onDestroy();
    }
}

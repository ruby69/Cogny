package io.dymatics.cognyreport.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;

import io.dymatics.cognyreport.Constants;
import io.dymatics.cognyreport.On;
import io.dymatics.cognyreport.R;
import io.dymatics.cognyreport.domain.model.Partner;
import io.dymatics.cognyreport.event.OnPartner;
import io.dymatics.cognyreport.service.RestClient;

@EViewGroup(R.layout.view_partner_info)
public class PartnerInfoView extends RelativeLayout {
    @ViewById(R.id.vehicleCount) TextView vehicleCount;
    @ViewById(R.id.companyName) TextView companyName;

    @Bean RestClient restClient;

    public PartnerInfoView(Context context) {
        super(context);
    }

    public PartnerInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @AfterViews
    void afterViews() {
        if (restClient != null && vehicleCount != null) {
            restClient.fetchPartner(new On<Partner>().addSuccessListener(result -> populate(result)));
        }
    }

    @UiThread
    public void populate(Partner partner) {
        if (partner != null) {
            vehicleCount.setText(Constants.FORMAT_COUNT_ITEM2.format(partner.getVehicleCount()));
            companyName.setText(partner.getCompanyName());
            EventBus.getDefault().post(new OnPartner(partner));
        }
    }

}
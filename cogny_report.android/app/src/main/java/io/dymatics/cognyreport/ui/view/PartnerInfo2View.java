package io.dymatics.cognyreport.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import io.dymatics.cognyreport.R;
import io.dymatics.cognyreport.domain.model.Partner;
import io.dymatics.cognyreport.event.OnPartner;

@EViewGroup(R.layout.view_partner_info2)
public class PartnerInfo2View extends RelativeLayout {
    @ViewById(R.id.companyName) TextView companyName;

    public PartnerInfo2View(Context context) {
        super(context);
    }

    public PartnerInfo2View(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        EventBus.getDefault().unregister(this);
        super.onDetachedFromWindow();
    }

    @UiThread
    public void populate(Partner partner) {
        companyName.setText(partner.getCompanyName());
    }

    @Subscribe
    public void onEvent(OnPartner event) {
        Partner partner = event.getPartner();
        if (partner != null) {
            populate(partner);
        }
    }
}
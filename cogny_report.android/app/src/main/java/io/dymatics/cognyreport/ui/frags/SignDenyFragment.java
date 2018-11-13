package io.dymatics.cognyreport.ui.frags;

import android.content.Intent;
import android.net.Uri;
import androidx.fragment.app.Fragment;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;

import io.dymatics.cognyreport.R;
import io.dymatics.cognyreport.service.MiscService;

@EFragment(R.layout.fragment_sign_deny)
public class SignDenyFragment extends Fragment {

    @Bean MiscService miscService;

    @AfterInject
    void afterInject() {
        miscService.applyFontScale(getActivity(), 1.0F);
    }

    @Click(R.id.toCall)
    void onClickToCall() {
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(getString(R.string.tel_call_center))));
        getActivity().finish();
    }

    @Click(R.id.toFinish)
    void onClickToFinish() {
        getActivity().finish();
    }
}

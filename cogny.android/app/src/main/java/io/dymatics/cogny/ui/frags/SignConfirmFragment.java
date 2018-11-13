package io.dymatics.cogny.ui.frags;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.widget.TextView;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import io.dymatics.cogny.R;
import io.dymatics.cogny.SignActivity;
import io.dymatics.cogny.domain.model.User;
import io.dymatics.cogny.service.CognyBean;
import io.dymatics.cogny.service.MiscService;

@EFragment(R.layout.fragment_sign_confirm)
public class SignConfirmFragment extends Fragment {
    @ViewById(R.id.nameView) TextView nameView;

    @Bean CognyBean cognyBean;
    @Bean MiscService miscService;

    private SignActivity signActivity;

    @AfterInject
    void afterInject() {
        miscService.applyFontScale(getActivity(), 1.0F);
    }

    @AfterViews
    void afterViews() {
        User user = cognyBean.loadUser();
        if (user != null) {
            nameView.setText(user.getName() + "님,");
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        signActivity = (SignActivity) getActivity();
    }

    @Click(R.id.confirm)
    void onClickConfirm() {
        signActivity.startMainActivity();
    }

}

package io.dymatics.cogny.ui.frags;

import androidx.fragment.app.Fragment;
import android.view.View;
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
import io.dymatics.cogny.service.MiscService;

@EFragment(R.layout.fragment_sign)
public class SignFragment extends Fragment {
    @ViewById(R.id.nameView) TextView nameView;
    @Bean MiscService miscService;

    @AfterInject
    void afterInject() {
        miscService.applyFontScale(getActivity(), 1.0F);
    }

    @AfterViews
    void afterViews() {
        User.Invitation invitation = (User.Invitation) getActivity().getIntent().getSerializableExtra("invitation");
        if (invitation != null) {
            nameView.setVisibility(View.VISIBLE);
            nameView.setText(invitation.getName() + "님,");
        } else {
            nameView.setVisibility(View.GONE);
        }
    }

    @Click(R.id.startWithGoogle)
    void onClickStartWithGoogle() {
        ((SignActivity) getActivity()).startWithGoogle();
    }

}

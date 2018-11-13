package io.dymatics.cogny.ui.frags;

import androidx.fragment.app.Fragment;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

import io.dymatics.cogny.R;

@EFragment(R.layout.fragment_dummy)
public class DummyFragment extends Fragment {
    @ViewById(R.id.titleView) TextView titleView;

    @FragmentArg("title") String title;

    @AfterViews
    void afterViews() {
        titleView.setText(title);
    }
}

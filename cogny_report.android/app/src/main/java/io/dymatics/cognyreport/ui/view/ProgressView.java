package io.dymatics.cognyreport.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import io.dymatics.cognyreport.R;

@EViewGroup(R.layout.view_progress)
public class ProgressView extends RelativeLayout {
    @ViewById(R.id.progressMessage) TextView progressMessage;

    public ProgressView(Context context) {
        super(context);
    }

    public ProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ProgressView message(String message) {
        setProgressMessage(message);
        return this;
    }

    public ProgressView message(int message) {
        setProgressMessage(message);
        return this;
    }

    @UiThread
    void setProgressMessage(String message) {
        if (progressMessage != null) {
            progressMessage.setText(message);
        }
    }

    @UiThread
    void setProgressMessage(int message) {
        if (progressMessage != null) {
            progressMessage.setText(message);
        }
    }
}
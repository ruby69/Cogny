package io.dymatics.cognyreport.ui.dialog;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import io.dymatics.cognyreport.R;
import lombok.Setter;

@EFragment(R.layout.dialog_web_view)
public class WebViewDialog extends DialogFragment {
    private static final String TAG = "WebViewDialog";
    private String url;

    @ViewById(R.id.webView) WebView webView;
    @ViewById(R.id.progressView) ProgressBar progressView;

    private CognyWebViewClient webViewClient = new CognyWebViewClient();
    private WebChromeClient webChromeClient = new WebChromeClient();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.DialogFragmentTheme);
    }

    @AfterViews
    void afterViews() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(webViewClient);
        webView.setWebChromeClient(webChromeClient);
        webView.requestFocus();
        webViewClient.setProgressView(progressView);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (url != null) {
            webView.loadUrl(url);
        }
    }

    public void showUrl(String url, FragmentManager fragmentManager) {
        this.url = url;
        show(fragmentManager, TAG);
    }

    private static class CognyWebViewClient extends WebViewClient {
        @Setter private ProgressBar progressView;

        @Override
        public void onPageFinished(WebView view, String url) {
            if (progressView != null) {
                progressView.setVisibility(View.GONE);
            }
            super.onPageFinished(view, url);
        }
    }
}

package io.dymatics.cognyreport.ui.frags;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import io.dymatics.cognyreport.R;
import io.dymatics.cognyreport.SignActivity;
import io.dymatics.cognyreport.service.MiscService;
import io.dymatics.cognyreport.ui.dialog.WebViewDialog;
import lombok.Setter;

@EFragment(R.layout.fragment_sign_agreement)
public class SignAgreementFragment extends Fragment {

    @ViewById(R.id.webView1) WebView webView1;
    @ViewById(R.id.webView2) WebView webView2;

    @Bean MiscService miscService;

    private SignActivity signActivity;

    private CognyWebViewClient webViewClient = new CognyWebViewClient();
    private WebChromeClient webChromeClient = new WebChromeClient();

    @AfterInject
    void afterInject() {
        miscService.applyFontScale(getActivity(), 1.0F);
    }

    @AfterViews
    void afterViews() {
        webView1.getSettings().setJavaScriptEnabled(true);
        webView1.setWebViewClient(webViewClient);
        webView1.setWebChromeClient(webChromeClient);
        webView1.requestFocus();

        webView2.getSettings().setJavaScriptEnabled(true);
        webView2.setWebViewClient(webViewClient);
        webView2.setWebChromeClient(webChromeClient);
        webView2.requestFocus();

        webView1.loadUrl(getString(R.string.url_agreement));
        webView2.loadUrl(getString(R.string.url_privacy));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        signActivity = (SignActivity) getActivity();
    }

    @Click(R.id.agree)
    void onClickAgree() {
        signActivity.agree();
    }

    @Click(R.id.disagree)
    void onClickDisagree() {
        signActivity.disagree();
    }

    private static class CognyWebViewClient extends WebViewClient {
    }
}

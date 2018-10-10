package com.echoesnet.eatandmeet.views.widgets.ProgressBridgeWebView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.VideoView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.BridgeWebViewClient;
import com.orhanobut.logger.Logger;

import java.util.Map;

/**
 * Created by ben on 2017/2/24.
 */
public class ProgressEnableVideoWebView extends BridgeWebView
{
    private ProgressBar progressbar;
    private LoadingProgressListener pListener;


    private EnableVideoWebChromeClient videoEnabledWebChromeClient;
    private boolean addedJavascriptInterface;

    public ProgressEnableVideoWebView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        addedJavascriptInterface = false;
    }

    /**
     * Pass only a VideoEnabledWebChromeClient instance.
     */
    @Override @SuppressLint("SetJavaScriptEnabled")
    public void setWebChromeClient(WebChromeClient client)
    {
        getSettings().setJavaScriptEnabled(true);

        if (client instanceof EnableVideoWebChromeClient)
        {
            this.videoEnabledWebChromeClient = (EnableVideoWebChromeClient) client;
        }

        super.setWebChromeClient(client);
    }

    @Override
    public void loadData(String data, String mimeType, String encoding)
    {
        addJavascriptInterface();
        super.loadData(data, mimeType, encoding);
    }

    @Override
    public void loadDataWithBaseURL(String baseUrl, String data, String mimeType, String encoding, String historyUrl)
    {
        addJavascriptInterface();
        super.loadDataWithBaseURL(baseUrl, data, mimeType, encoding, historyUrl);
    }

    @Override
    public void loadUrl(String url)
    {
        addJavascriptInterface();
        super.loadUrl(url);
    }

    @Override
    public void loadUrl(String url, Map<String, String> additionalHttpHeaders)
    {
        addJavascriptInterface();
        super.loadUrl(url, additionalHttpHeaders);
    }

    private void addJavascriptInterface()
    {
        if (!addedJavascriptInterface)
        {
            // Add javascript interface to be called when the video ends (must be done before page load)
            addJavascriptInterface(new Object()
            {
                @JavascriptInterface
                @SuppressWarnings("unused")
                public void notifyVideoEnd() // Must match Javascript interface method of VideoEnabledWebChromeClient
                {
                    // This code is not executed in the UI thread, so we must force it to happen
                    new Handler(Looper.getMainLooper()).post(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            if (videoEnabledWebChromeClient != null)
                            {
                                videoEnabledWebChromeClient.onHideCustomView();
                            }
                        }
                    });
                }
            }, "_VideoEnabledWebView"); // Must match Javascript interface name of VideoEnabledWebChromeClient

            addedJavascriptInterface = true;
        }
    }


    public ProgressEnableVideoWebView(Context context)
    {
        super(context);
        addedJavascriptInterface = false;
    }

    public ProgressEnableVideoWebView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        addedJavascriptInterface = false;
        progressbar = new ProgressBar(context, null,
                android.R.attr.progressBarStyleHorizontal);
        progressbar.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                CommonUtils.dp2px(context,2.5f), 0, 0));

        Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.progress_bar_states);
        progressbar.setProgressDrawable(drawable);
        progressbar.setVisibility(GONE);
        addView(progressbar);
        setWebChromeClient(new EnableVideoWebChromeClient());
        setWebViewClient(new MyWebViewClient(this));
        //是否可以缩放
        getSettings().setSupportZoom(false);
        getSettings().setBuiltInZoomControls(true);
        if (Build.VERSION.SDK_INT >= 20) // KITKAT
        {
            setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }
        setBackgroundColor(ContextCompat.getColor(context,R.color.main_background_color));
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt)
    {
        LayoutParams lp = (LayoutParams) progressbar.getLayoutParams();
        lp.x = l;
        lp.y = t;
        progressbar.setLayoutParams(lp);
        super.onScrollChanged(l, t, oldl, oldt);
    }
    public interface ToggledFullscreenCallback
    {
        public void toggledFullscreen(boolean fullscreen);
    }
    public void setProgressListener(LoadingProgressListener listener)
    {
        this.pListener = listener;
    }

    public static class EnableVideoWebChromeClient extends WebChromeClient implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener
    {

        private View activityNonVideoView;
        private ViewGroup activityVideoView;
        private View loadingView;
        private ProgressEnableVideoWebView webView;

        private boolean isVideoFullscreen; // Indicates if the video is being displayed using a custom view (typically full-screen)
        private FrameLayout videoViewContainer;
        private CustomViewCallback videoViewCallback;

        private ToggledFullscreenCallback toggledFullscreenCallback;

        /**
         * Never use this constructor alone.
         * This constructor allows this class to be defined as an inline inner class in which the user can override methods
         */
        public EnableVideoWebChromeClient()
        {
        }

        /**
         * Builds a video enabled WebChromeClient.
         * @param activityNonVideoView A View in the activity's layout that contains every other view that should be hidden when the video goes full-screen.
         * @param activityVideoView A ViewGroup in the activity's layout that will display the video. Typically you would like this to fill the whole layout.
         */
        public EnableVideoWebChromeClient(View activityNonVideoView, ViewGroup activityVideoView)
        {
            this.activityNonVideoView = activityNonVideoView;
            this.activityVideoView = activityVideoView;
            this.loadingView = null;
            this.webView = null;
            this.isVideoFullscreen = false;
        }

        /**
         * Builds a video enabled WebChromeClient.
         * @param activityNonVideoView A View in the activity's layout that contains every other view that should be hidden when the video goes full-screen.
         * @param activityVideoView A ViewGroup in the activity's layout that will display the video. Typically you would like this to fill the whole layout.
         * @param loadingView A View to be shown while the video is loading (typically only used in API level <11). Must be already inflated and without a parent view.
         */
        public EnableVideoWebChromeClient(View activityNonVideoView, ViewGroup activityVideoView, View loadingView)
        {
            this.activityNonVideoView = activityNonVideoView;
            this.activityVideoView = activityVideoView;
            this.loadingView = loadingView;
            this.webView = null;
            this.isVideoFullscreen = false;
        }

        /**
         * Builds a video enabled WebChromeClient.
         * @param activityNonVideoView A View in the activity's layout that contains every other view that should be hidden when the video goes full-screen.
         * @param activityVideoView A ViewGroup in the activity's layout that will display the video. Typically you would like this to fill the whole layout.
         * @param loadingView A View to be shown while the video is loading (typically only used in API level <11). Must be already inflated and without a parent view.
         * @param webView The owner VideoEnabledWebView. Passing it will enable the VideoEnabledWebChromeClient to detect the HTML5 video ended event and exit full-screen.
         * Note: The web page must only contain one video tag in order for the HTML5 video ended event to work. This could be improved if needed (see Javascript code).
         */
        public EnableVideoWebChromeClient(View activityNonVideoView, ViewGroup activityVideoView, View loadingView, ProgressEnableVideoWebView webView)
        {
            this.activityNonVideoView = activityNonVideoView;
            this.activityVideoView = activityVideoView;
            this.loadingView = loadingView;
            this.webView = webView;
            this.isVideoFullscreen = false;
        }

        /**
         * Indicates if the video is being displayed using a custom view (typically full-screen)
         * @return true it the video is being displayed using a custom view (typically full-screen)
         */
        public boolean isVideoFullscreen()
        {
            return isVideoFullscreen;
        }

        /**
         * Set a callback that will be fired when the video starts or finishes displaying using a custom view (typically full-screen)
         * @param callback A VideoEnabledWebChromeClient.ToggledFullscreenCallback callback
         */
        public void setOnToggledFullscreen(ToggledFullscreenCallback callback)
        {
            this.toggledFullscreenCallback = callback;
        }

        @Override
        public void onShowCustomView(View view, CustomViewCallback callback)
        {
            if (view instanceof FrameLayout)
            {
                // A video wants to be shown
                FrameLayout frameLayout = (FrameLayout) view;
                View focusedChild = frameLayout.getFocusedChild();

                // Save video related variables
                this.isVideoFullscreen = true;
                this.videoViewContainer = frameLayout;
                this.videoViewCallback = callback;

                // Hide the non-video view, add the video view, and show it
                activityNonVideoView.setVisibility(View.INVISIBLE);
                activityVideoView.addView(videoViewContainer, new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
                activityVideoView.setVisibility(View.VISIBLE);

                if (focusedChild instanceof VideoView)
                {
                    // VideoView (typically API level <11)
                    VideoView videoView = (VideoView) focusedChild;
                    // Handle all the required events
                    videoView.setOnPreparedListener(this);
                    videoView.setOnCompletionListener(this);
                    videoView.setOnErrorListener(this);
                }
                else // Usually android.webkit.HTML5VideoFullScreen$VideoSurfaceView, sometimes android.webkit.HTML5VideoFullScreen$VideoTextureView
                {
                    // HTML5VideoFullScreen (typically API level 11+)
                    // Handle HTML5 video ended event
                    if (webView != null && webView.getSettings().getJavaScriptEnabled())
                    {
                        // Run javascript code that detects the video end and notifies the interface
                        String js = "javascript:";
                        js += "_ytrp_html5_video = document.getElementsByTagName('video')[0];";
                        js += "if (_ytrp_html5_video !== undefined) {";
                        {
                            js += "function _ytrp_html5_video_ended() {";
                            {
                                js += "_ytrp_html5_video.removeEventListener('ended', _ytrp_html5_video_ended);";
                                js += "_VideoEnabledWebView.notifyVideoEnd();"; // Must match Javascript interface name and method of VideoEnableWebView
                            }
                            js += "}";
                            js += "_ytrp_html5_video.addEventListener('ended', _ytrp_html5_video_ended);";
                        }
                        js += "}";
                        webView.loadUrl(js);
                    }
                }

                // Notify full-screen change
                if (toggledFullscreenCallback != null)
                {
                    toggledFullscreenCallback.toggledFullscreen(true);
                }
            }
        }

        @Override
        public void onShowCustomView(View view, int requestedOrientation, CustomViewCallback callback) // Only available in API level 14+
        {
            onShowCustomView(view, callback);
        }

        @Override
        public void onHideCustomView()
        {
            // This method must be manually (internally) called on video end in the case of VideoView (typically API level <11)
            // This method must be manually (internally) called on video end in the case of HTML5VideoFullScreen (typically API level 11+) because it's not always called automatically
            // This method must be manually (internally) called on back key press (from this class' onBackPressed() method)

            if (isVideoFullscreen)
            {
                // Hide the video view, remove it, and show the non-video view
                activityVideoView.setVisibility(View.INVISIBLE);
                activityVideoView.removeView(videoViewContainer);
                activityNonVideoView.setVisibility(View.VISIBLE);

                // Call back
                if (videoViewCallback != null) videoViewCallback.onCustomViewHidden();

                // Reset video related variables
                isVideoFullscreen = false;
                videoViewContainer = null;
                videoViewCallback = null;

                // Notify full-screen change
                if (toggledFullscreenCallback != null)
                {
                    toggledFullscreenCallback.toggledFullscreen(false);
                }
            }
        }

        @Override
        public View getVideoLoadingProgressView() // Video will start loading, only called in the case of VideoView (typically API level <11)
        {
            if (loadingView != null)
            {
                loadingView.setVisibility(View.VISIBLE);
                return loadingView;
            }
            else
            {
                return super.getVideoLoadingProgressView();
            }
        }

        @Override
        public void onPrepared(MediaPlayer mp) // Video will start playing, only called in the case of VideoView (typically API level <11)
        {
            if (loadingView != null)
            {
                loadingView.setVisibility(View.GONE);
            }
        }

        @Override
        public void onCompletion(MediaPlayer mp) // Video finished playing, only called in the case of VideoView (typically API level <11)
        {
            onHideCustomView();
        }

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) // Error while playing video, only called in the case of VideoView (typically API level <11)
        {
            return false; // By returning false, onCompletion() will be called
        }

        /**
         * Notifies the class that the back key has been pressed by the user.
         * This must be called from the Activity's onBackPressed(), and if it returns false, the activity itself should handle it. Otherwise don't do anything.
         * @return Returns true if the event was handled, and false if it is not (video view is not visible)
         */
        public boolean onBackPressed()
        {
            if (isVideoFullscreen)
            {
                onHideCustomView();
                return true;
            }
            else
            {
                return false;
            }
        }
//        @Override
//        public void onProgressChanged(WebView view, int newProgress)
//        {
//            if (pListener != null)
//                pListener.onProgressChanged(newProgress);
//            if (newProgress == 100)
//            {
//                progressbar.setVisibility(GONE);
//            }
//            else
//            {
//                if (progressbar.getVisibility() == GONE)
//                    progressbar.setVisibility(VISIBLE);
//                progressbar.setProgress(newProgress);
//            }
//            super.onProgressChanged(view, newProgress);
//        }
    }

    public class MyWebViewClient extends BridgeWebViewClient
    {
        public MyWebViewClient(BridgeWebView webView)
        {
            super(webView);
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error)
        {
            super.onReceivedError(view, request, error);
            if (Build.VERSION.SDK_INT >= 24)
            {
                Logger.t("ProgressBridgeWebView").d(request.getUrl().toString() + " | " +
                        request.getRequestHeaders().toString() + " | " + request.getMethod() + " error>" + error.getDescription());
            }
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl)
        {
            super.onReceivedError(view, errorCode, description, failingUrl);
            Logger.t("ProgressBridgeWebView").d("ProgressBridgeWebView--error");
            loadUrl("file:///android_asset/404.html");
        }
    }

    public interface LoadingProgressListener
    {
        void onProgressChanged(int progress);
    }
}

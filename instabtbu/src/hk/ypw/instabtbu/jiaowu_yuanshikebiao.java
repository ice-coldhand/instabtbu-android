package hk.ypw.instabtbu;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class jiaowu_yuanshikebiao extends SwipeBackActivity {
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_jiaowu_yuanshikebiao);

		WebView webView = (WebView) findViewById(R.id.jiaowu_yuanshikebiao);

		WebSettings ws = webView.getSettings();

		ws.setSupportZoom(true);
		ws.setUseWideViewPort(true);
		ws.setBuiltInZoomControls(true);
		if (android.os.Build.VERSION.SDK_INT > 15)
			ws.setDisplayZoomControls(false);
		/**
		 * ��׿4.0�����ǲ�������������(setDisplayZoomControls)
		 */

		webView.post(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				WebView webView = (WebView) findViewById(R.id.jiaowu_yuanshikebiao);
				SharedPreferences sp = getSharedPreferences("data", 0);
				String kebiaopath = sp.getString("lixiankebiao", "");
				webView.loadUrl("file://" + kebiaopath);
			}
		});
	}
}

package hk.ypw.instabtbu;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.MenuItem;

import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.FeedbackAgent;
import com.umeng.fb.model.UserInfo;

public class Public_menu {
	Activity thisActivity;

	@SuppressLint("WorldReadableFiles")
	@SuppressWarnings("deprecation")
	public void select(MenuItem item) {
		try {
			if (item.getItemId() == R.id.about) {
				Intent intent = new Intent();
				intent.setClass(thisActivity, About.class);
				thisActivity.startActivity(intent);
				MobclickAgent.onEvent(thisActivity, "about");
			} else if (item.getItemId() == R.id.feedback) {
				FeedbackAgent agent = new FeedbackAgent(thisActivity);
				UserInfo info = agent.getUserInfo();
				if (info == null)
					info = new UserInfo();
				Map<String, String> contact = info.getContact();
				if (contact == null)
					contact = new HashMap<String, String>();
				SharedPreferences sp = thisActivity.getSharedPreferences(
						"data", 0);
				String num = sp.getString("num", "");
				contact.put("num", num);
				info.setContact(contact);
				agent.setUserInfo(info);
				agent.startFeedbackActivity();
			} else if (item.getItemId() == R.id.addqun) {
				joinQQGroup("aU9Sag6d1GjA3Z3l1kOeHQ-plxiEk1wc");
			} else if (item.getItemId() == R.id.exit) {
				stopService();
				System.exit(0);
			} else if (item.getItemId() == R.id.fenxiang) {
				try {
					Drawable drawable = thisActivity.getResources()
							.getDrawable(R.drawable.fenxiang);
					FileOutputStream Os = thisActivity.openFileOutput(
							"share.jpg", Context.MODE_WORLD_READABLE);
					Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					bitmap.compress(CompressFormat.JPEG, 100, bos);
					byte[] bitmapdata = bos.toByteArray();
					Os.write(bitmapdata);
					Os.close();

					File F = thisActivity.getFileStreamPath("share.jpg");
					Uri U = Uri.fromFile(F);
					String fenxiang = thisActivity.getString(R.string.share);
					Intent sendIntent = new Intent();
					ComponentName cn = new ComponentName("com.tencent.mm",
							"com.tencent.mm.ui.tools.ShareToTimeLineUI");
					sendIntent.setComponent(cn);
					sendIntent.setAction(Intent.ACTION_SEND);
					sendIntent.putExtra(Intent.EXTRA_SUBJECT, "instabtbu");
					sendIntent.putExtra(Intent.EXTRA_STREAM, U);
					sendIntent.putExtra(Intent.EXTRA_TEXT, fenxiang);
					sendIntent.putExtra("Kdescription", fenxiang);
					sendIntent.setType("image/*");
					thisActivity.startActivity(sendIntent);
				} catch (Exception e) {
				}
			}

		} catch (Exception ex) {
		}
	}

	protected void stopService() {
		Intent intent = new Intent(thisActivity, ser.class);
		thisActivity.stopService(intent);
	}

	/****************
	 * 
	 * �������Ⱥ���̡�Ⱥ�ţ�instabtbu�û�����Ⱥ(99254687) �� key Ϊ��
	 * UU3oQFVAdguz3DtqKNAHjMsbYo9Hy__1 ����
	 * joinQQGroup(UU3oQFVAdguz3DtqKNAHjMsbYo9Hy__1) ���ɷ�����Q�ͻ��������Ⱥ
	 * instabtbu�û�����Ⱥ(99254687)
	 * 
	 * @param key
	 *            �ɹ������ɵ�key
	 * @return ����true��ʾ������Q�ɹ�������fals��ʾ����ʧ��
	 ******************/
	public boolean joinQQGroup(String key) {
		Intent intent = new Intent();
		intent.setData(Uri
				.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D"
						+ key));
		// ��Flag�ɸ��ݾ����Ʒ��Ҫ�Զ��壬�����ã����ڼ�Ⱥ���水���أ�������Q�����棬�����ã������ػ᷵�ص������Ʒ����
		// //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
		try {
			thisActivity.startActivity(intent);
			return true;
		} catch (Exception e) {
			// δ��װ��Q��װ�İ汾��֧��
			return false;
		}
	}

}

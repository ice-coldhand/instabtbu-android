package hk.ypw.instabtbu;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.umeng.analytics.MobclickAgent;

@SuppressLint("HandlerLeak")
public class xiaoyuanka_2 extends Activity {

	leftmenu leftmenu;
	SlidingMenu menu;
	long uiId = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_xiaoyuanka_2);
		MobclickAgent.updateOnlineConfig(this);
		uiId = Thread.currentThread().getId();
		leftmenu = new leftmenu(thisActivity, 4);
		menu = leftmenu.menu;
		now = Calendar.getInstance();
		xinxiList = xiaoyuanka.xinxiList;
		Message message = new Message();
		message.what = 3;
		handler.sendMessage(message);

		SharedPreferences sp = getSharedPreferences("data", 0);
		String num = sp.getString("num_xiaoyuanka", "");
		String psw = sp.getString("psw_xiaoyuanka", "");

		out("校园卡配置读取:" + num + "," + psw);

		numString = num;
		pswString = psw;
		myui();
		dialog2 = ProgressDialog.show(thisActivity, "正在获取数据", "正在获取数据中……",
				true, true);
		executorService.submit(huoquxiaofeiRunnable);
		out("开始加载数据,huoquxiaofeiRunnable");

		final PullToRefreshListView list = (PullToRefreshListView) findViewById(R.id.xiaoyuanka_list);
		list.setMode(Mode.PULL_FROM_END);
		list.getLoadingLayoutProxy().setPullLabel("上拉加载更多");
		list.getLoadingLayoutProxy().setReleaseLabel("释放加载更多");
		list.getLoadingLayoutProxy().setRefreshingLabel("正在加载");
		list.setPullToRefreshOverScrollEnabled(false);
		list.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView arg0, int arg1) {
				// TODO Auto-generated method stub
				if (yeshu >= zongyeshu && zongyeshu != 0) {
					out("已经滑动到最后一页," + yeshu + "/" + zongyeshu);
					now.add(Calendar.MONTH, -1);
					yeshu = 0;
					zongyeshu = 0;
				}
				if (listView.getCount() - listView.getLastVisiblePosition() < 8) {

					if (!working)
						executorService.submit(huoquxiaofeiRunnable);
				}
				out(working + "滑动到底部,开始获取数据,获取第" + (yeshu + 1) + "页");
			}

			@Override
			public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub

			}
		});
		list.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				out("上拉加载事件");
				executorService.submit(huoquxiaofeiRunnable);
			}
		});
	}

	boolean working = false;
	String numString = "", pswString = "";
	int yeshu = 0;
	int zongyeshu = 0;
	Calendar now;
	Runnable huoquxiaofeiRunnable = new Runnable() {
		@Override
		public void run() {
			if (!working) {
				working = true;
				String result = "";
				yeshu++;
				Calendar early = (Calendar) now.clone();
				early.add(Calendar.MONTH, -1);
				String postdata = "start=" + early.get(Calendar.YEAR) + "-"
						+ (early.get(Calendar.MONTH) + 1) + "-"
						+ early.get(Calendar.DAY_OF_MONTH) + "&endto="
						+ now.get(Calendar.YEAR) + "-"
						+ (now.get(Calendar.MONTH) + 1) + "-"
						+ now.get(Calendar.DAY_OF_MONTH) + "&offset=" + yeshu;

				result = POST("http://card.btbu.edu.cn/CardWeb/finance.asp",
						postdata);

				if (find(result, "没有相关信息")) {
					out("没有相关信息,提前一个月继续寻找");
					now.add(Calendar.MONTH, -1);
				}
				out(postdata);
				mainresult = result;
				p = Pattern.compile("<TD align=right>(.*?)<",
						Pattern.CASE_INSENSITIVE);
				m = p.matcher(mainresult);
				xiaofeiList = new ArrayList<String>();
				while (m.find()) {
					xiaofeiList.add(m.group(1));
					adapterList2.add(m.group(1));
				}

				if (zongyeshu == 0) {
					p = Pattern.compile("共有.*?(\\d)");
					m = p.matcher(result);
					if (m.find()) {
						try {
							String tmp = m.group(1);
							zongyeshu = Integer.valueOf(tmp);
							System.out.println("总页数:" + tmp + "," + zongyeshu);
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else
						out("寻找总页数失败");
				}

				if (xiaofeiList.size() != 0) {
					Message message = new Message();
					message.what = 4;
					handler.sendMessage(message);
				} else
					yeshu--;
				if (dialog2.isShowing())
					dialog2.dismiss();
				working = false;
			}
		}
	};

	float money = 0;

	String mainresult = "";

	public void dengchu(View v) {
		SharedPreferences.Editor editor = getSharedPreferences("data", 0)
				.edit();
		editor.remove("num_xiaoyuanka");
		editor.remove("psw_xiaoyuanka");
		editor.commit();
		Intent intent = new Intent();
		intent.setClass(thisActivity, xiaoyuanka.class);
		startActivity(intent);
		show("已退出帐号。");
	}

	@SuppressWarnings("unused")
	public void myui() {
		try {
			out("设置校园卡数据UI");
			DisplayMetrics mDisplayMetrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
			int width = mDisplayMetrics.widthPixels;
			int height = mDisplayMetrics.heightPixels;
			float density = mDisplayMetrics.density;
			double w = width / 700.0;

			mypoint head = setView(R.id.xiaoyuanka_head, 0, 0, width,
					width * 404 / 719);

			mypoint xiaofei = setView(R.id.xiaoyuanka_xiaofei,
					(int) (0.04 * width), head.height + (int) (0.04 * width),
					(int) (0.46 * width), (int) (0.46 * width * 56 / 288));

			findViewById(R.id.xiaoyuanka_xiaofei).setVisibility(View.INVISIBLE);

			TextView left_userTextView = (TextView) thisActivity
					.findViewById(R.id.leftmenu_textview_user);
			TextView left_user2TextView = (TextView) thisActivity
					.findViewById(R.id.leftmenu_textview_dianzheli);
			left_userTextView.setText(numString);
			left_user2TextView.setText("登录成功");

			// mypoint list = setView(R.id.xiaoyuanka_list,
			// 0,xiaofei.y+xiaofei.height+(int)(0.04*width),width,LayoutParams.WRAP_CONTENT);
			out("设置校园卡数据UI完毕");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void out(String o) {
		System.out.println(o);
	}

	public mypoint setView(int id, int x, int y, int wid, int hei) {
		View myView = findViewById(id);
		LayoutParams myParams = new LayoutParams(wid, hei);
		myParams.setMargins(x, y, 0, 0);
		myView.setLayoutParams(myParams);
		return new mypoint(x, y, wid, hei);
	}

	Activity thisActivity = this;

	Toast toast;
	final Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			try {
				super.handleMessage(msg);
				if (msg.what == 1) {
					if (dialog2.isShowing()) {
						dialog2.setMessage(gengxinString);
					}
				} else if (msg.what == 2) {
					if (toast == null)
						toast = Toast.makeText(getApplicationContext(),
								showString, Toast.LENGTH_SHORT);
					else
						toast.setText(showString);
					toast.show();
				} else if (msg.what == 3) {
					/**
					 * [杨培文, 男, 计算机与信息工程学院2013级, 学生, 43060219950421451X,
					 * 10011130401033400100, 1304010334, 暂无信息, 2013-10-24,
					 * 2017-7-31, 在用] 0名字 2学院,年级 6学号
					 */
					out("开始获取数据");
					System.out.println(xinxiList);
					if (xinxiList.size() == 11) {
						out("正在更新UI");
						TextView number = (TextView) findViewById(R.id.xiaoyuanka_number);
						number.setText(xinxiList.get(6));

						TextView name = (TextView) findViewById(R.id.xiaoyuanka_name);
						name.setText(xinxiList.get(0));

						String xueyuanString = xinxiList.get(2);
						xueyuanString = xueyuanString.replaceAll("计算机与信息工程学院",
								"计信学院");
						xueyuanString = xueyuanString.replaceAll("材料与机械工程学院",
								"材机学院");
						xueyuanString = xueyuanString.replaceAll("艺术与传媒学院",
								"艺传学院");
						xueyuanString = xueyuanString.replaceAll(
								"\\(马克思主义学院\\)", "");

						TextView xueyuan = (TextView) findViewById(R.id.xiaoyuanka_xueyuan);
						xueyuan.setText(xueyuanString);

						out("更新UI完毕");
					}

					// TextView xinxiTextView =
					// (TextView)findViewById(R.id.xiaoyuanka_xinxi);
					// xinxiTextView.setText(xinxiList.toString());

				} else if (msg.what == 4) {
					out("更新列表UI开始");
					PullToRefreshListView list = (PullToRefreshListView) findViewById(R.id.xiaoyuanka_list);
					if (money == 0) {
						money = 1;
						TextView yueTextView = (TextView) findViewById(R.id.xiaoyuanka_yue);
						yueTextView.setText("￥ " + xiaofeiList.get(3));
					}
					if (adapterList == null) {
						adapterList = new ArrayList<String>();
						out("创建新的list");
					}
					if (xiaofeiList.size() % 5 == 0) {
						int i;
						for (i = 0; i < xiaofeiList.size() / 5; i++) {
							String tmp = xiaofeiList.get(i * 5) + "在"
									+ xiaofeiList.get(i * 5 + 4)
									+ xiaofeiList.get(i * 5 + 1) + "了"
									+ xiaofeiList.get(i * 5 + 2) + "元";
							adapterList.add(tmp);
							try {
								if (find(xiaofeiList.get(i * 5 + 1), "消费")) {
									zonge += Double.valueOf(xiaofeiList
											.get(i * 5 + 2));
									out("总额:" + zonge);
									// TextView zongeTextView =
									// (TextView)findViewById(R.id.xiaoyuanka_zonge);
									// zongeTextView.setText("总额:￥ "+((int)(zonge*100))/100);
								}
							} catch (Exception e) {
							}

						}
					} else {
					}
					System.out.println(xiaofeiList.size());

					listView = list.getRefreshableView();
					if (adapter == null) {
						adapter = new myAdapter(thisActivity, adapterList);
						listView.setAdapter(adapter);
						out("创建新的adapter");
					} else {
						adapter.notifyDataSetChanged();
						out("更新adapter");
					}

					list.onRefreshComplete();
					out("更新列表UI完毕");
				}
			} catch (Exception e) {
			}
		}
	};
	double zonge = 0;

	private class myAdapter extends BaseAdapter {
		private List<String> mData;
		private LayoutInflater mInflater;

		public myAdapter(Context context, List<String> data) {
			mData = data;
			mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			if (mData == null || mData.size() <= 0) {
				return 0;
			}
			return mData.size();
		}

		@Override
		public Object getItem(int position) {
			if (mData == null || mData.size() <= 0 || position < 0
					|| position >= mData.size()) {
				return null;
			}
			return mData.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@SuppressLint("InflateParams")
		@Override
		public View getView(int position, View convertView, ViewGroup arg2) {
			if (convertView == null)
				convertView = mInflater.inflate(R.layout.item_xiaofei, null);
			try {
				String time = adapterList2.get(position * 5);
				String type = adapterList2.get(position * 5 + 1);
				String jine = adapterList2.get(position * 5 + 2);
				String didian = adapterList2.get(position * 5 + 4);

				TextView jineTextView = (TextView) convertView
						.findViewById(R.id.xiaoyuanka_list_jine);
				jineTextView.setText("￥ " + jine);

				ImageView typeImageView = (ImageView) convertView
						.findViewById(R.id.xiaoyuanka_list_typeimg);
				if (find(type, "消费")) {
					typeImageView
							.setBackgroundResource(R.drawable.xiaoyuanka_xiaofei);
					jineTextView.setTextColor(0xFF000000);
				} else if (find(type, "充值")) {
					typeImageView
							.setBackgroundResource(R.drawable.xiaoyuanka_chongzhi);
					jineTextView.setTextColor(0xFF39B54A);
					jineTextView.setText("＋￥ " + jine);
				}

				TextView typeTextView = (TextView) convertView
						.findViewById(R.id.xiaoyuanka_list_type);
				typeTextView.setText(type);

				TextView didianTextView = (TextView) convertView
						.findViewById(R.id.xiaoyuanka_list_didian);

				String replaceString = MobclickAgent.getConfigParams(
						thisActivity, "replace");

				// out("replaceString:"+replaceString);

				String[] replaceStrings = replaceString.split("\n");

				int i;
				for (i = 0; i < replaceStrings.length; i++) {
					String[] replaceStrings2 = replaceStrings[i].split("----");
					if (replaceStrings2.length == 2) {
						// out("replace"+i+":"+replaceStrings2[0]+","+replaceStrings2[1]);
						if (didian.equals(replaceStrings2[0]))
							didian = replaceStrings2[1];
					}
				}

				if (find(didian, "组团")) {
					didianTextView.setText("浴室圈存");
				} else
					didianTextView.setText(didian);

				TextView timeTextView = (TextView) convertView
						.findViewById(R.id.xiaoyuanka_list_time);
				timeTextView.setText(time);
			} catch (Exception e) {

			}
			return convertView;
		}
	};

	myAdapter adapter;
	List<String> adapterList;
	List<String> adapterList2 = new ArrayList<String>();
	ListView listView;

	void gengxin(String gx) {
		gengxinString = gx;
		Message message = new Message();
		message.what = 1;
		handler.sendMessage(message);
	}

	public boolean find(String text, String w) {
		if (text.indexOf(w) == -1)
			return false;
		else
			return true;
	}

	public String zhongjian(String text, String textl, String textr) {
		return zhongjian(text, textl, textr, 0);
	}

	public String zhongjian(String text, String textl, String textr, int start) {
		try {
			int left = text.indexOf(textl, start);
			int right = text.indexOf(textr, left + textl.length());
			return text.substring(left + textl.length(), right);
		} catch (Exception e) {
			System.out.println("zhongjian:error:" + e);
			return "";
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		leftmenu.leftmenu_ui(3);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.btbu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Public_menu menu = new Public_menu();
		menu.thisActivity = thisActivity;
		menu.select(item);
		return false;
	}

	List<String> xiaofeiList = new ArrayList<String>();
	List<String> xinxiList = new ArrayList<String>();

	private ExecutorService executorService = Executors.newCachedThreadPool();// 线程池
	private ProgressDialog dialog2;
	Pattern p;
	Matcher m;
	String gengxinString = "";

	public void show(String str) {
		show(str, 0);
	}

	String showString = "";

	public void show(String str, int d) {
		System.out.println(str);
		if (d > 0)
			str = str.substring(0, str.length() - 1);
		showString = str;

		if (Thread.currentThread().getId() == uiId) {
			if (toast == null)
				toast = Toast.makeText(getApplicationContext(), showString,
						Toast.LENGTH_SHORT);
			else
				toast.setText(showString);
			toast.show();
		} else {
			Message message = new Message();
			message.what = 2;
			handler.sendMessage(message);
		}
	}

	public class mypoint {
		int x;
		int y;
		int width;
		int height;

		public mypoint(int x, int y, int width, int height) {
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}
	}

	public String POST(String url, String postdata) {
		String result = "";
		System.out.println(url);
		HttpPost hPost = new HttpPost(url);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		String posts[] = postdata.split("&");
		String posts2[];
		int i;
		for (i = 0; i < posts.length; i++) {
			posts2 = posts[i].split("=");
			if (posts2.length == 2)
				params.add(new BasicNameValuePair(posts2[0], posts2[1]));
			else
				params.add(new BasicNameValuePair(posts2[0], ""));
		}
		try {
			HttpEntity hen = new UrlEncodedFormEntity(params, "gb2312");
			hPost.setEntity(hen);
			xiaoyuanka.myClient.getParams().setParameter(
					CoreConnectionPNames.CONNECTION_TIMEOUT, 30000);
			// 请求超时
			xiaoyuanka.myClient.getParams().setParameter(
					CoreConnectionPNames.SO_TIMEOUT, 30000);
			// 读取超时
			HttpResponse hResponse;
			hResponse = xiaoyuanka.myClient.execute(hPost);
			if (hResponse.getStatusLine().getStatusCode() == 200) {
				result = EntityUtils.toString(hResponse.getEntity());
				result = new String(result.getBytes("ISO_8859_1"), "gbk");
				// 转码
			}
		} catch (Exception e) {
			if (dialog2.isShowing())
				dialog2.dismiss();
			e.printStackTrace();
		}
		return (result);
	}
}

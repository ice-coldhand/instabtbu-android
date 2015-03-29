package hk.ypw.instabtbu;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.umeng.analytics.MobclickAgent;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputFilter;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;

@SuppressLint({ "HandlerLeak", "WorldReadableFiles" })
public class xiaoyuanka extends Activity{
	
	leftmenu leftmenu;
	SlidingMenu menu;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_xiaoyuanka);
		myui_tianjia();
		leftmenu = new leftmenu(thisActivity,4);
		menu=leftmenu.menu;
		MobclickAgent.updateOnlineConfig(this);
		uiId = Thread.currentThread().getId();
		
		SharedPreferences sp = getSharedPreferences("data", 0);
		String num = sp.getString("num_xiaoyuanka","");
		String psw = sp.getString("psw_xiaoyuanka","");
		
		out("У԰�����ö�ȡ:"+num+","+psw);
		
		if(num.length()==0)
		{
			
		}else {
			numString=num;
			pswString=psw;
			
			dialog2 = ProgressDialog.show(thisActivity, "���ڵ�¼", "���ڵ�¼�С���",true,true);
			executorService.submit(dengluRunnable);
		}

	}
	
	public void guashi(View v)
	{
    	Intent intent = new Intent();  
		intent.setClass(thisActivity, xiaoyuanka_guashi.class);
        startActivity(intent);
	}
	
	public void out(String o)
	{
		System.out.println(o);
	}
	
	@SuppressWarnings("unused")
	public void myui_tianjia() {
		out("����У԰����¼UI");
		try{
		DisplayMetrics mDisplayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
		int width = mDisplayMetrics.widthPixels;
		int height = mDisplayMetrics.heightPixels;
		float density = mDisplayMetrics.density;
		double w=width/700.0;
		
//		findViewById(R.id.xiaoyuanka_tianjia1).setBackgroundResource(R.drawable.xiaoyuanka_tianjia);
		findViewById(R.id.xiaoyuanka_tianjia2).setBackgroundResource(R.drawable.xiaoyuanka_tianjia2);
		
		mypoint tianjia1 = setView(R.id.xiaoyuanka_dise1,0,0,width,width*404/719);
		mypoint tianjia2 = setView(R.id.xiaoyuanka_tianjia2,0,tianjia1.height,width,width*712/719);
		
		out("����У԰����¼UI���");
		}catch(Exception e)
		{
			e.printStackTrace();
		}

	}
	
	public void tianjia(View v)
	{
		LinearLayout layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);
		TextView numTextView = new TextView(this);
		numTextView.setText("�����������ʺ�(ѧ��):");
		numTextView.setTextSize(18f);

		final EditText numEditText = new EditText(this);
		numEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
		SharedPreferences sp = getSharedPreferences("data", 0);
		String num = sp.getString("num","");
		numEditText.setText(num);
		TextView pswTextView = new TextView(this);
		pswTextView.setText("��������������(6λ����):");
		pswTextView.setTextSize(18f);
		final EditText pswEditText = new EditText(this);
		pswEditText.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_VARIATION_PASSWORD);
		pswEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
		layout.addView(numTextView);
		layout.addView(numEditText);
		layout.addView(pswTextView);
		layout.addView(pswEditText);
    	new AlertDialog.Builder(this)
    	.setTitle("���¼")  
        .setIcon(R.drawable.ser_logo)
        .setView(layout)
        .setNegativeButton("ȡ��", null)
        .setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {  
            @Override  
            public void onClick(DialogInterface arg0, int arg1) {
            	numString=numEditText.getText().toString();
            	pswString=pswEditText.getText().toString();
    			dialog2 = ProgressDialog.show(thisActivity, "���ڵ�¼", "���ڵ�¼�С���",true,true);
    			executorService.submit(dengluRunnable);
            }
        }).show();
	}
	
	long uiId = 0;
	
	String numString = "";
	String pswString = "";
	
	
	Runnable dengluRunnable = new Runnable() {
		@Override
		public void run(){
			String result = "";
			result=GET("http://card.btbu.edu.cn/CardWeb/queryresult.asp?cardno="+numString+"&password="+pswString+"&Submit=%B2%E9%D1%AF");
			if(find(result,numString)&&numString.length()>0)
			{
				System.out.println("У԰����¼�ɹ�"+numString);
				
			xinxiList = new ArrayList<String>();
			p=Pattern.compile("<div align=\"center\">(.+)</div></td>\r\n +</tr>");
			m=p.matcher(result);
			while(m.find()){
				xinxiList.add(m.group(1));
			}
				gengxin("��¼�ɹ�,���ڻ�ȡ���Ѽ�¼����");
				SharedPreferences.Editor editor = getSharedPreferences("data", 0).edit();
				editor.putString("num_xiaoyuanka",numString);
				editor.putString("psw_xiaoyuanka",pswString);
		        editor.commit();
		        out("�ѱ���У԰����Ϣ");
		        
				Message message = new Message();
				message.what=3;
				handler.sendMessage(message);
				
			}else if(find(result,"���벻��"))
			{
				System.out.println(result);
				System.out.println(zhongjian(result, "alert('","'"));
				if(find(result, "alert('"))show(zhongjian(result, "alert('","'"));else show("��¼ʧ��");
				SharedPreferences.Editor editor = getSharedPreferences("data", 0).edit();
				editor.remove("num_xiaoyuanka");
				editor.remove("psw_xiaoyuanka");
		        editor.commit();
			}
			else{
				System.out.println(result);
				System.out.println(zhongjian(result, "alert('","'"));
				if(find(result, "alert('"))show(zhongjian(result, "alert('","'"));else show("��¼ʧ��");
			}
			
			if(dialog2.isShowing())dialog2.dismiss();
		}
	};
	
	Toast toast;
	final Handler handler = new Handler(){
	@Override
	public void handleMessage(Message msg){
		super.handleMessage(msg);
		try{
			if(msg.what == 1){
				if(dialog2.isShowing())dialog2.setMessage(gengxinString);
			}else if(msg.what==2)
			{
				if(toast==null)toast=Toast.makeText(getApplicationContext(), showString, Toast.LENGTH_SHORT);
				else toast.setText(showString);
				toast.show();
			}else if(msg.what==3)
			{
				if(xinxiList.size()==14)
				{
					xinxiList.remove(11);xinxiList.remove(11);xinxiList.remove(11);
				}
				System.out.println(xinxiList.size());
				
				Intent intent = new Intent();  
				intent.setClass(thisActivity, xiaoyuanka_2.class);
				startActivity(intent);
				finish();
			}
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	};
	ListView listView;
	void gengxin(String gx)
	{
		gengxinString=gx;
	Message message = new Message();
	message.what=1;
	handler.sendMessage(message);
	}
	
	public boolean find(String text,String w){
		if(text.indexOf(w)==-1)return false;
		else return true;
	}
	public String zhongjian(String text,String textl,String textr){
		return zhongjian(text,textl,textr,0);
	}
	public String zhongjian(String text,String textl,String textr,int start){
		try{
		int left = text.indexOf(textl,start);
		int right = text.indexOf(textr, left+textl.length());
		return text.substring(left+textl.length(),right);
		}catch(Exception e)
		{
			System.out.println("zhongjian:error:"+e);
			return "";
		}
	}
	
	public mypoint setView(int id,int x,int y,int wid,int hei)
	{
			View myView = findViewById(id);
		LayoutParams myParams = new LayoutParams(wid,hei);
		myParams.setMargins(x, y, 0, 0);
		myView.setLayoutParams(myParams);
		return new mypoint(x, y, wid, hei);
	}
	Activity thisActivity = this;

	@Override
	public void onResume()
	{
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
	static List<String> xinxiList = new ArrayList<String>();

	private ExecutorService executorService = Executors.newCachedThreadPool();//�̳߳�
	private ProgressDialog dialog2;
	Pattern p;Matcher m;
	String gengxinString = "";

	public void show(String str){
		show(str,0);
	}
	String showString = "";
	public void show(String str,int d){
		System.out.println(str);
		if(d>0)
		str=str.substring(0,str.length()-1);
		showString = str;
		
		if(Thread.currentThread().getId()==uiId)Toast.makeText(getApplicationContext(), showString, Toast.LENGTH_SHORT).show();
		else {
			Message message = new Message();
			message.what=2;
			handler.sendMessage(message);
		}
	}
		public class mypoint{
			int x;
			int y;
			int width;
			int height;
			public mypoint(int x, int y,int width,int height){
				this.x=x;
				this.y=y;
				this.width=width;
				this.height=height;
				 }
			
			public int getX()
			{
				return x;
			}
			
			public int getY()
			{
				return y;
			}
		}
	static HttpClient myClient = new DefaultHttpClient();
	public String POST(String url,String postdata)
	{
		String result = "";
		System.out.println(url);
			HttpPost hPost=new HttpPost(url);
			List <NameValuePair> params=new ArrayList<NameValuePair>();
			String posts[]=postdata.split("&");
			String posts2[];
			int i;
				for(i=0;i<posts.length;i++)
				{
					posts2=posts[i].split("=");
					if(posts2.length==2)
					params.add(new BasicNameValuePair (posts2[0],posts2[1]));
					else params.add(new BasicNameValuePair (posts2[0],""));
				}
			try{
				HttpEntity hen=new UrlEncodedFormEntity(params,"gb2312");
				hPost.setEntity(hen);
				myClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 30000);
				//����ʱ
				myClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 30000);
				//��ȡ��ʱ
				HttpResponse hResponse;
				hResponse = myClient.execute(hPost);
				if(hResponse.getStatusLine().getStatusCode()==200)
					{
						result = EntityUtils.toString(hResponse.getEntity());
						result = new String(result.getBytes("ISO_8859_1"),"gbk");
						//ת��
					}
		
			}catch(Exception e){
				if(dialog2.isShowing())dialog2.dismiss();
				show("����BTBUʧ�ܡ�\n��ȷ���ź������ٲ�����");
				e.printStackTrace();
			}
		return(result);
	}
	
	public String GET(String url)
	{
		String result = "";
		System.out.println(url);
			HttpGet hGet = new HttpGet(url);
			try{
				myClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 30000);
				//����ʱ
				myClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 30000);
				//��ȡ��ʱ
				HttpResponse hResponse;
				hResponse = myClient.execute(hGet);
				if(hResponse.getStatusLine().getStatusCode()==200)
					{
						result = EntityUtils.toString(hResponse.getEntity());
						result = new String(result.getBytes("ISO_8859_1"),"gbk");
						//ת��
					}
		
			}catch(Exception e){
				if(dialog2.isShowing())dialog2.dismiss();
				show("����BTBUʧ�ܡ�\n��ȷ���ź������ٲ�����");
				e.printStackTrace();
			}
		return(result);
	}
}

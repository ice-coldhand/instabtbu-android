package hk.ypw.instabtbu;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class First_jieshao extends Activity implements OnClickListener, OnPageChangeListener{
	
	private ViewPager vp;
	private ViewPagerAdapter vpAdapter;
	private List<View> views;
	
	//����ͼƬ��Դ
	private static final int[] pics = {R.drawable.first1,R.drawable.first2,R.drawable.first3};
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_first_jieshao);

        
        views = new ArrayList<View>();
       
        LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
        		LinearLayout.LayoutParams.WRAP_CONTENT);
        
        //��ʼ������ͼƬ�б�
        for(int i=0; i<pics.length; i++) {
        	ImageView iv = new ImageView(this);
        	iv.setLayoutParams(mParams);
        	iv.setImageResource(pics[i]);
        	iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        	views.add(iv);
        }
        
        vp = (ViewPager) findViewById(R.id.viewpager);
        //��ʼ��Adapter
        vpAdapter = new ViewPagerAdapter(views);
        vp.setAdapter(vpAdapter);
        //�󶨻ص�
        vp.setOnPageChangeListener(this);
    }
    

    /**
     *���õ�ǰ������ҳ 
     */
    private void setCurView(int position)
    {
		if (position < 0 || position >= pics.length) {
			return;
		}

		vp.setCurrentItem(position);
    }


    //������״̬�ı�ʱ����
	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
//		System.out.println("onPageScrollStateChanged:"+arg0);
		if(arg0==0)
			{
			System.out.println(now);
			if(now>6)finish();
			}
	}
	int now = 0;
	//��ǰҳ�汻����ʱ����
	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
		if(arg0==pics.length-1)now++;
		else now=0;
	}


	@Override
	public void onClick(View v) {
		int position = (Integer)v.getTag();
		setCurView(position);
	}


	@Override
	public void onPageSelected(int arg0) {
		// TODO Auto-generated method stub
	}
}
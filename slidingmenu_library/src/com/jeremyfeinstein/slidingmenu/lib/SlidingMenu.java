package com.jeremyfeinstein.slidingmenu.lib;

import java.lang.reflect.Method;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.jeremyfeinstein.slidingmenu.lib.CustomViewAbove.OnPageChangeListener;

public class SlidingMenu extends RelativeLayout {

	private static final String TAG = "SlidingMenu";

	public static final int SLIDING_WINDOW = 0;
	public static final int SLIDING_CONTENT = 1;
	private boolean mActionbarOverlay = false;

	/**
	 * ΪsetTouchModeAbove()��������һ������ֵ���������˵�ͨ��������Ļ�ı�Ե���� 
	 */
	public static final int TOUCHMODE_MARGIN = 0;
	/**
	 * �ữЧ��(��˹ģ��)(�Ż�������������)
	 * @param bmp
	 * @return
	 */
	public static Bitmap blur(Bitmap sentBitmap, int radius, boolean canReuseInBitmap) {
		 
	    // Stack Blur v1.0 from
	    // http://www.quasimondo.com/StackBlurForCanvas/StackBlurDemo.html
	    //
	    // Java Author: Mario Klingemann <mario at="" quasimondo.com="">
	    // http://incubator.quasimondo.com
	    // created Feburary 29, 2004
	    // Android port : Yahel Bouaziz <yahel at="" kayenko.com="">
	    // http://www.kayenko.com
	    // ported april 5th, 2012
	 
	    // This is a compromise between Gaussian Blur and Box blur
	    // It creates much better looking blurs than Box Blur, but is
	    // 7x faster than my Gaussian Blur implementation.
	    //
	    // I called it Stack Blur because this describes best how this
	    // filter works internally: it creates a kind of moving stack
	    // of colors whilst scanning through the image. Thereby it
	    // just has to add one new block of color to the right side
	    // of the stack and remove the leftmost color. The remaining
	    // colors on the topmost layer of the stack are either added on
	    // or reduced by one, depending on if they are on the right or
	    // on the left side of the stack.
	    //
	    // If you are using this algorithm in your code please add
	    // the following line:
	    //
	    // Stack Blur Algorithm by Mario Klingemann <mario@quasimondo.com>
	 
	    Bitmap bitmap;
	    if (canReuseInBitmap) {
	        bitmap = sentBitmap;
	    } else {
	        bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
	    }
	 
	    if (radius < 1) {
	        return (null);
	    }
	 
	    int w = bitmap.getWidth();
	    int h = bitmap.getHeight();
	 
	    int[] pix = new int[w * h];
	    bitmap.getPixels(pix, 0, w, 0, 0, w, h);
	 
	    int wm = w - 1;
	    int hm = h - 1;
	    int wh = w * h;
	    int div = radius + radius + 1;
	 
	    int r[] = new int[wh];
	    int g[] = new int[wh];
	    int b[] = new int[wh];
	    int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
	    int vmin[] = new int[Math.max(w, h)];
	 
	    int divsum = (div + 1) >> 1;
	    divsum *= divsum;
	    int dv[] = new int[256 * divsum];
	    for (i = 0; i < 256 * divsum; i++) {
	        dv[i] = (i / divsum);
	    }
	 
	    yw = yi = 0;
	 
	    int[][] stack = new int[div][3];
	    int stackpointer;
	    int stackstart;
	    int[] sir;
	    int rbs;
	    int r1 = radius + 1;
	    int routsum, goutsum, boutsum;
	    int rinsum, ginsum, binsum;
	 
	    for (y = 0; y < h; y++) {
	        rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
	        for (i = -radius; i <= radius; i++) {
	            p = pix[yi + Math.min(wm, Math.max(i, 0))];
	            sir = stack[i + radius];
	            sir[0] = (p & 0xff0000) >> 16;
	            sir[1] = (p & 0x00ff00) >> 8;
	            sir[2] = (p & 0x0000ff);
	            rbs = r1 - Math.abs(i);
	            rsum += sir[0] * rbs;
	            gsum += sir[1] * rbs;
	            bsum += sir[2] * rbs;
	            if (i > 0) {
	                rinsum += sir[0];
	                ginsum += sir[1];
	                binsum += sir[2];
	            } else {
	                routsum += sir[0];
	                goutsum += sir[1];
	                boutsum += sir[2];
	            }
	        }
	        stackpointer = radius;
	 
	        for (x = 0; x < w; x++) {
	 
	            r[yi] = dv[rsum];
	            g[yi] = dv[gsum];
	            b[yi] = dv[bsum];
	 
	            rsum -= routsum;
	            gsum -= goutsum;
	            bsum -= boutsum;
	 
	            stackstart = stackpointer - radius + div;
	            sir = stack[stackstart % div];
	 
	            routsum -= sir[0];
	            goutsum -= sir[1];
	            boutsum -= sir[2];
	 
	            if (y == 0) {
	                vmin[x] = Math.min(x + radius + 1, wm);
	            }
	            p = pix[yw + vmin[x]];
	 
	            sir[0] = (p & 0xff0000) >> 16;
	            sir[1] = (p & 0x00ff00) >> 8;
	            sir[2] = (p & 0x0000ff);
	 
	            rinsum += sir[0];
	            ginsum += sir[1];
	            binsum += sir[2];
	 
	            rsum += rinsum;
	            gsum += ginsum;
	            bsum += binsum;
	 
	            stackpointer = (stackpointer + 1) % div;
	            sir = stack[(stackpointer) % div];
	 
	            routsum += sir[0];
	            goutsum += sir[1];
	            boutsum += sir[2];
	 
	            rinsum -= sir[0];
	            ginsum -= sir[1];
	            binsum -= sir[2];
	 
	            yi++;
	        }
	        yw += w;
	    }
	    for (x = 0; x < w; x++) {
	        rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
	        yp = -radius * w;
	        for (i = -radius; i <= radius; i++) {
	            yi = Math.max(0, yp) + x;
	 
	            sir = stack[i + radius];
	 
	            sir[0] = r[yi];
	            sir[1] = g[yi];
	            sir[2] = b[yi];
	 
	            rbs = r1 - Math.abs(i);
	 
	            rsum += r[yi] * rbs;
	            gsum += g[yi] * rbs;
	            bsum += b[yi] * rbs;
	 
	            if (i > 0) {
	                rinsum += sir[0];
	                ginsum += sir[1];
	                binsum += sir[2];
	            } else {
	                routsum += sir[0];
	                goutsum += sir[1];
	                boutsum += sir[2];
	            }
	 
	            if (i < hm) {
	                yp += w;
	            }
	        }
	        yi = x;
	        stackpointer = radius;
	        for (y = 0; y < h; y++) {
	            // Preserve alpha channel: ( 0xff000000 & pix[yi] )
	            pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];
	 
	            rsum -= routsum;
	            gsum -= goutsum;
	            bsum -= boutsum;
	 
	            stackstart = stackpointer - radius + div;
	            sir = stack[stackstart % div];
	 
	            routsum -= sir[0];
	            goutsum -= sir[1];
	            boutsum -= sir[2];
	 
	            if (x == 0) {
	                vmin[y] = Math.min(y + r1, hm) * w;
	            }
	            p = x + vmin[y];
	 
	            sir[0] = r[p];
	            sir[1] = g[p];
	            sir[2] = b[p];
	 
	            rinsum += sir[0];
	            ginsum += sir[1];
	            binsum += sir[2];
	 
	            rsum += rinsum;
	            gsum += ginsum;
	            bsum += binsum;
	 
	            stackpointer = (stackpointer + 1) % div;
	            sir = stack[stackpointer];
	 
	            routsum += sir[0];
	            goutsum += sir[1];
	            boutsum += sir[2];
	 
	            rinsum -= sir[0];
	            ginsum -= sir[1];
	            binsum -= sir[2];
	 
	            yi += w;
	        }
	    }
	 
	    bitmap.setPixels(pix, 0, w, 0, 0, w, h);
	 
	    return (bitmap);
	}
	/** 
	 * ΪsetTouchModeAbove()��������һ������ֵ���������˵�ͨ��������Ļ���κεط�����
	 */
	public static final int TOUCHMODE_FULLSCREEN = 1;

	/** 
	 * ΪsetTouchModeAbove()��������һ������ֵ�����������˵�ͨ��������Ļ����
	 */
	public static final int TOUCHMODE_NONE = 2;

	/** 
	 * ΪsetMode()��������һ������ֵ���ѻ����˵��������
	 */
	public static final int LEFT = 0;

	/** 
	 * ΪsetMode()��������һ������ֵ���ѻ����˵������ұ�
	 */
	public static final int RIGHT = 1;

	/** 
	 * ΪsetMode()��������һ������ֵ���ѻ����˵�������������
	 */
	public static final int LEFT_RIGHT = 2;

	/**
	 * �����Ϸ���ͼ����
	 */
	private CustomViewAbove mViewAbove;

	/**
	 * �����·���ͼ����
	 */
	private CustomViewBehind mViewBehind;

	/**
	 * ���廬���˵��򿪵ļ�������
	 */
	private OnOpenListener mOpenListener;

	/**
	 * ���廬���˵��رյļ�������
	 */
	private OnCloseListener mCloseListener;

	/**
	 * �����˵���ʱ�ļ����¼�
	 */
	public interface OnOpenListener {	
		public void onOpen();
	}

	/**
	 * ��⻬���˵��Ƿ��Ѿ��򿪵ļ����¼�
	 */
	public interface OnOpenedListener {
		public void onOpened();
	}

	/**
	 * �����˵��ر�ʱ�ļ����¼�
	 */
	public interface OnCloseListener {
		public void onClose();
	}

	/**
	 * ��⻬���˵��Ƿ��Ѿ��رյļ����¼�
	 */
	public interface OnClosedListener {
		public void onClosed();
	}

	/**
	 * The Interface CanvasTransformer.
	 */
	public interface CanvasTransformer {

		/**
		 * Transform canvas.
		 *
		 * @param canvas the canvas
		 * @param percentOpen the percent open
		 */
		public void transformCanvas(Canvas canvas, float percentOpen);
	}

	/**
	 * ��ʼ�������˵�
	 *
	 * @param context the associated Context
	 */
	public SlidingMenu(Context context) {
		this(context, null);
	}

	/**
	 * ��ʼ�������˵�
	 *
	 * @param activity the activity to attach slidingmenu
	 * @param slideStyle the slidingmenu style
	 */
	public SlidingMenu(Activity activity, int slideStyle) {
		this(activity, null);
		this.attachToActivity(activity, slideStyle);
	}

	public float getPercent()
	{
		return mViewAbove.getPercentOpen();
	}
	
	/**
	 * ��ʼ�������˵�
	 *
	 * @param context the associated Context
	 * @param attrs the attrs
	 */
	public SlidingMenu(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	/**
	 * ��ʼ�������˵�
	 *
	 * @param context the associated Context
	 * @param attrs the attrs
	 * @param defStyle the def style
	 */
	public SlidingMenu(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		LayoutParams behindParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		mViewBehind = new CustomViewBehind(context);
		addView(mViewBehind, behindParams);
		LayoutParams aboveParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		mViewAbove = new CustomViewAbove(context);
		addView(mViewAbove, aboveParams);
		// register the CustomViewBehind with the CustomViewAbove
		mViewAbove.setCustomViewBehind(mViewBehind);
		mViewBehind.setCustomViewAbove(mViewAbove);
		mViewAbove.setOnPageChangeListener(new OnPageChangeListener() {
			public static final int POSITION_OPEN = 0;
			public static final int POSITION_CLOSE = 1;

			public void onPageScrolled(int position, float positionOffset,
					int positionOffsetPixels) { }

			public void onPageSelected(int position) {
				if (position == POSITION_OPEN && mOpenListener != null) {
					mOpenListener.onOpen();
				} else if (position == POSITION_CLOSE && mCloseListener != null) {
					mCloseListener.onClose();
				}
			}
		});

		// now style everything!
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SlidingMenu);
		// set the above and behind views if defined in xml
		int mode = ta.getInt(R.styleable.SlidingMenu_mode, LEFT);
		setMode(mode);
		int viewAbove = ta.getResourceId(R.styleable.SlidingMenu_viewAbove, -1);
		if (viewAbove != -1) {
			setContent(viewAbove);
		} else {
			setContent(new FrameLayout(context));
		}
		int viewBehind = ta.getResourceId(R.styleable.SlidingMenu_viewBehind, -1);
		if (viewBehind != -1) {
			setMenu(viewBehind); 
		} else {
			setMenu(new FrameLayout(context));
		}
		int touchModeAbove = ta.getInt(R.styleable.SlidingMenu_touchModeAbove, TOUCHMODE_MARGIN);
		setTouchModeAbove(touchModeAbove);
		int touchModeBehind = ta.getInt(R.styleable.SlidingMenu_touchModeBehind, TOUCHMODE_MARGIN);
		setTouchModeBehind(touchModeBehind);

		int offsetBehind = (int) ta.getDimension(R.styleable.SlidingMenu_behindOffset, -1);
		int widthBehind = (int) ta.getDimension(R.styleable.SlidingMenu_behindWidth, -1);
		if (offsetBehind != -1 && widthBehind != -1)
			throw new IllegalStateException("Cannot set both behindOffset and behindWidth for a SlidingMenu");
		else if (offsetBehind != -1)
			setBehindOffset(offsetBehind);
		else if (widthBehind != -1)
			setBehindWidth(widthBehind);
		else
			setBehindOffset(0);
		float scrollOffsetBehind = ta.getFloat(R.styleable.SlidingMenu_behindScrollScale, 0.33f);
		setBehindScrollScale(scrollOffsetBehind);
		int shadowRes = ta.getResourceId(R.styleable.SlidingMenu_shadowDrawable, -1);
		if (shadowRes != -1) {
			setShadowDrawable(shadowRes);
		}
		int shadowWidth = (int) ta.getDimension(R.styleable.SlidingMenu_shadowWidth, 0);
		setShadowWidth(shadowWidth);
		boolean fadeEnabled = ta.getBoolean(R.styleable.SlidingMenu_fadeEnabled, true);
		setFadeEnabled(fadeEnabled);
		float fadeDeg = ta.getFloat(R.styleable.SlidingMenu_fadeDegree, 0.33f);
		setFadeDegree(fadeDeg);
		boolean selectorEnabled = ta.getBoolean(R.styleable.SlidingMenu_selectorEnabled, false);
		setSelectorEnabled(selectorEnabled);
		int selectorRes = ta.getResourceId(R.styleable.SlidingMenu_selectorDrawable, -1);
		if (selectorRes != -1)
			setSelectorDrawable(selectorRes);
		ta.recycle();
	}

	/**
	 * �ѻ����˵���ӽ����е�Activity��
	 * 
	 * @param activity the Activity
	 * @param slideStyle either SLIDING_CONTENT or SLIDING_WINDOW
	 */
	public void attachToActivity(Activity activity, int slideStyle) {
		attachToActivity(activity, slideStyle, false);
	}

	/**
	 * �ѻ����˵���ӽ����е�Activity��
	 * 
	 * @param activity the Activity
	 * @param slideStyle either SLIDING_CONTENT or SLIDING_WINDOW
	 * @param actionbarOverlay whether or not the ActionBar is overlaid
	 */
	public void attachToActivity(Activity activity, int slideStyle, boolean actionbarOverlay) {
		if (slideStyle != SLIDING_WINDOW && slideStyle != SLIDING_CONTENT)
			throw new IllegalArgumentException("slideStyle must be either SLIDING_WINDOW or SLIDING_CONTENT");

		if (getParent() != null)
			throw new IllegalStateException("This SlidingMenu appears to already be attached");

		// get the window background
		TypedArray a = activity.getTheme().obtainStyledAttributes(new int[] {android.R.attr.windowBackground});
		int background = a.getResourceId(0, 0);
		a.recycle();

		switch (slideStyle) {
		case SLIDING_WINDOW:
			mActionbarOverlay = false;
			ViewGroup decor = (ViewGroup) activity.getWindow().getDecorView();
			ViewGroup decorChild = (ViewGroup) decor.getChildAt(0);
			// save ActionBar themes that have transparent assets
			decorChild.setBackgroundResource(background);
			decor.removeView(decorChild);
			decor.addView(this);
			setContent(decorChild);
			break;
		case SLIDING_CONTENT:
			mActionbarOverlay = actionbarOverlay;
			// take the above view out of
			ViewGroup contentParent = (ViewGroup)activity.findViewById(android.R.id.content);
			View content = contentParent.getChildAt(0);
			contentParent.removeView(content);
			contentParent.addView(this);
			setContent(content);
			// save people from having transparent backgrounds
			if (content.getBackground() == null)
				content.setBackgroundResource(background);
			break;
		}
	}

	/**
	 * �Ӳ�����Դ�ļ��������Ϸ�����ͼ���ݣ�������ֻᱻ�����ӵ�����ͼ������Ϸ�
	 */
	public void setContent(int res) {
		setContent(LayoutInflater.from(getContext()).inflate(res, null));
	}

	/**
	 * ͨ��View�������Ϸ�����ͼ����
	 */
	public void setContent(View view) {
		mViewAbove.setContent(view);
		showContent();
	}

	/**
	 * �õ��Ϸ�����ͼ����
	 */
	public View getContent() {
		return mViewAbove.getContent();
	}

	/**
	 * �Ӳ�����Դ�ļ��������·��������˵�������ͼ���ݣ�������ֻᱻ�����ӵ�����ͼ������·�
	 * 
	 * @param res the new content
	 */
	public void setMenu(int res) {
		setMenu(LayoutInflater.from(getContext()).inflate(res, null));
	}

	/**
	 * �õ��·��������˵�������ͼ����
	 *
	 * @param view The desired content to display.
	 */
	public void setMenu(View v) {
		mViewBehind.setContent(v);
	}

	/**
	 * �õ��·��������˵�������ͼ����
	 */
	public View getMenu() {
		return mViewBehind.getContent();
	}

	/**
	 * �Ӳ�����Դ�ļ��������·����ұ߻����˵�������ͼ���ݣ�������ֻᱻ�����ӵ�����ͼ������·�
	 */
	public void setSecondaryMenu(int res) {
		setSecondaryMenu(LayoutInflater.from(getContext()).inflate(res, null));
	}

	/**
	 * �����·����ұ߻����˵�������ͼ����
	 */
	public void setSecondaryMenu(View v) {
		mViewBehind.setSecondaryContent(v);
	}

	/**
	 * �õ��·����ұ߻����˵�������ͼ����
	 */
	public View getSecondaryMenu() {
		return mViewBehind.getSecondaryContent();
	}

	/**
	 * �����Ϸ���ͼ�Ƿ��ܹ�����
	 */
	public void setSlidingEnabled(boolean b) {
		mViewAbove.setSlidingEnabled(b);
	}

	/**
	 * ����Ϸ���ͼ�Ƿ��ܹ�����
	 */
	public boolean isSlidingEnabled() {
		return mViewAbove.isSlidingEnabled();
	}

	/**
	 * ���û����˵���������ͼ�е�λ��
	 * 
	 * @param mode must be either SlidingMenu.LEFT or SlidingMenu.RIGHT
	 */
	public void setMode(int mode) {
		if (mode != LEFT && mode != RIGHT && mode != LEFT_RIGHT) {
			throw new IllegalStateException("SlidingMenu mode must be LEFT, RIGHT, or LEFT_RIGHT");
		}
		mViewBehind.setMode(mode);
	}

	/**
	 * �õ������˵�����ͼ�е�λ��
	 * 
	 * @return the current mode, either SlidingMenu.LEFT or SlidingMenu.RIGHT
	 */
	public int getMode() {
		return mViewBehind.getMode();
	}

	/**
	 * ���û����˵��Ƿ��Ǿ�̬ģʽ(���ܹ�ʹ�û����˵�)
	 */
	public void setStatic(boolean b) {
		if (b) {
			setSlidingEnabled(false);
			mViewAbove.setCustomViewBehind(null);
			mViewAbove.setCurrentItem(1);
			//			mViewBehind.setCurrentItem(0);	
		} else {
			mViewAbove.setCurrentItem(1);
			//			mViewBehind.setCurrentItem(1);
			mViewAbove.setCustomViewBehind(mViewBehind);
			setSlidingEnabled(true);
		}
	}

	/**
	 * �򿪻����˵�����ʾ�˵�����ͼ
	 */
	public void showMenu() {
		showMenu(true);
	}

	/**
	 * �Ƿ�ʹ�ö���Ч���򿪻����˵�����ʾ�˵�����ͼ
	 */
	public void showMenu(boolean animate) {
		mViewAbove.setCurrentItem(0, animate);
	}

	/**
	 * ���ұߵĻ����˵�����ʾ�˵�����ͼ 
	 */
	public void showSecondaryMenu() {
		showSecondaryMenu(true);
	}

	/**
	 * �Ƿ�ʹ�ö���Ч�����ұߵĻ����˵�����ʾ�˵�����ͼ
	 */
	public void showSecondaryMenu(boolean animate) {
		mViewAbove.setCurrentItem(2, animate);
	}

	/**
	 * �رղ˵�����ʾ�Ϸ�����ͼ
	 */
	public void showContent() {
		showContent(true);
	}

	/**
	 * �Ƿ�ʹ�ö���Ч���رղ˵�����ʾ�Ϸ�����ͼ
	 */
	public void showContent(boolean animate) {
		mViewAbove.setCurrentItem(1, animate);
	}

	/**
	 * �����˵��Ŀ���
	 */
	public void toggle() {
		toggle(true);
	}

	/**
	 * �Ƿ�ʹ�ö���Ч���򿪻�رջ����˵�
	 */
	public void toggle(boolean animate) {
		if (isMenuShowing()) {
			showContent(animate);
		} else {
			showMenu(animate);
		}
	}

	/**
	 * ��⻬���˵��Ƿ����ڱ���ʾ
	 */
	public boolean isMenuShowing() {
		return mViewAbove.getCurrentItem() == 0 || mViewAbove.getCurrentItem() == 2;
	}
	
	/**
	 * ����ұ߻����˵��Ƿ����ڱ���ʾ
	 */
	public boolean isSecondaryMenuShowing() {
		return mViewAbove.getCurrentItem() == 2;
	}

	/**
	 * �õ��·���ͼ��ƫ����
	 */
	public int getBehindOffset() {
		return ((RelativeLayout.LayoutParams)mViewBehind.getLayoutParams()).rightMargin;
	}

	/**
	 * �������ص�ֵ�������·���ͼ��ƫ����
	 *
	 * @param i The margin, in pixels, on the right of the screen that the behind view scrolls to.
	 */
	public void setBehindOffset(int i) {		
		mViewBehind.setWidthOffset(i);
	}

	/**
	 * ����dimension��Դ�ļ���ID�������·���ͼ��ƫ����
	 *
	 * @param resID The dimension resource id to be set as the behind offset.
	 * The menu, when open, will leave this width margin on the right of the screen.
	 */
	public void setBehindOffsetRes(int resID) {
		int i = (int) getContext().getResources().getDimension(resID);
		setBehindOffset(i);
	}

	/**
	 * �������ص�ֵ�������Ϸ���ͼ��ƫ����
	 *
	 * @param i the new above offset, in pixels
	 */
	public void setAboveOffset(int i) {
		mViewAbove.setAboveOffset(i);
	}

	/**
	 * ����dimension��Դ�ļ���ID�������Ϸ���ͼ��ƫ����
	 *
	 * @param resID The dimension resource id to be set as the above offset.
	 */
	public void setAboveOffsetRes(int resID) {
		int i = (int) getContext().getResources().getDimension(resID);
		setAboveOffset(i);
	}

	/**
	 * �������ص�ֵ�������·���ͼ�Ŀ��
	 *
	 * @param i The width the Sliding Menu will open to, in pixels
	 */
	@SuppressWarnings("deprecation")
	public void setBehindWidth(int i) {
		int width;
		Display display = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE))
				.getDefaultDisplay();
		try {
			Class<?> cls = Display.class;
			Class<?>[] parameterTypes = {Point.class};
			Point parameter = new Point();
			Method method = cls.getMethod("getSize", parameterTypes);
			method.invoke(display, parameter);
			width = parameter.x;
		} catch (Exception e) {
			width = display.getWidth();
		}
		setBehindOffset(width-i);
	}

	/**
	 * ����dimension��Դ�ļ���ID�������·���ͼ�Ŀ��
	 *
	 * @param res The dimension resource id to be set as the behind width offset.
	 * The menu, when open, will open this wide.
	 */
	public void setBehindWidthRes(int res) {
		int i = (int) getContext().getResources().getDimension(res);
		setBehindWidth(i);
	}

	/**
	 * �õ��·���ͼ���ڹ���ʱ�����ű���
	 *
	 * @return The scale of the parallax scroll
	 */
	public float getBehindScrollScale() {
		return mViewBehind.getScrollScale();
	}
	
	/**
	 * �����·���ͼ���ڹ���ʱ�����ű���
	 *
	 * @param f The scale of the parallax scroll (i.e. 1.0f scrolls 1 pixel for every
	 * 1 pixel that the above view scrolls and 0.0f scrolls 0 pixels)
	 */
	public void setBehindScrollScale(float f) {
		if (f < 0 && f > 1)
			throw new IllegalStateException("ScrollScale must be between 0 and 1");
		mViewBehind.setScrollScale(f);
	}
	
	/**
	 * �õ���Ե�������ٽ�ֵ
	 */
	public int getTouchmodeMarginThreshold() {
		return mViewBehind.getMarginThreshold();
	}
	
	/**
	 * �������ĵ�ģʽΪ��Ե����ʱ�����ñ�Ե�������ٽ�ֵ
	 */
	public void setTouchmodeMarginThreshold(int touchmodeMarginThreshold) {
		mViewBehind.setMarginThreshold(touchmodeMarginThreshold);
	}

	/**
	 * Sets the behind canvas transformer.
	 *
	 * @param t the new behind canvas transformer
	 */
	public void setBehindCanvasTransformer(CanvasTransformer t) {
		mViewBehind.setCanvasTransformer(t);
	}

	/**
	 * �õ��Ϸ���ͼ�Ĵ���ģʽ��ֵ
	 */
	public int getTouchModeAbove() {
		return mViewAbove.getTouchMode();
	}


	/**
	 * �����Ϸ���ͼ�Ĵ���ģʽ��ֵ
	 */
	public void setTouchModeAbove(int i) {
		if (i != TOUCHMODE_FULLSCREEN && i != TOUCHMODE_MARGIN
				&& i != TOUCHMODE_NONE) {
			throw new IllegalStateException("TouchMode must be set to either" +
					"TOUCHMODE_FULLSCREEN or TOUCHMODE_MARGIN or TOUCHMODE_NONE.");
		}
		mViewAbove.setTouchMode(i);
	}

	/**
	 * �����·���ͼ�Ĵ���ģʽ��ֵ
	 */
	public void setTouchModeBehind(int i) {
		if (i != TOUCHMODE_FULLSCREEN && i != TOUCHMODE_MARGIN
				&& i != TOUCHMODE_NONE) {
			throw new IllegalStateException("TouchMode must be set to either" +
					"TOUCHMODE_FULLSCREEN or TOUCHMODE_MARGIN or TOUCHMODE_NONE.");
		}
		mViewBehind.setTouchMode(i);
	}

	/**
	 * ������Դ�ļ�ID�����û����˵�����ӰЧ��
	 *
	 * @param resId the resource ID of the new shadow drawable
	 */
	public void setShadowDrawable(int resId) {
		setShadowDrawable(getContext().getResources().getDrawable(resId));
	}

	/**
	 * ����Drawable�����û����˵�����ӰЧ��
	 *
	 * @param d the new shadow drawable
	 */
	public void setShadowDrawable(Drawable d) {
		mViewBehind.setShadowDrawable(d);
	}

	/**
	 * ������Դ�ļ�ID�������ұ߻����˵�����ӰЧ��
	 *
	 * @param resId the resource ID of the new shadow drawable
	 */
	public void setSecondaryShadowDrawable(int resId) {
		setSecondaryShadowDrawable(getContext().getResources().getDrawable(resId));
	}

	/**
	 * ����Drawable�����û����˵�����ӰЧ��
	 *
	 * @param d the new shadow drawable
	 */
	public void setSecondaryShadowDrawable(Drawable d) {
		mViewBehind.setSecondaryShadowDrawable(d);
	}

	/**
	 * ����dimension��Դ�ļ���ID��������Ӱ�Ŀ��
	 *
	 * @param resId The dimension resource id to be set as the shadow width.
	 */
	public void setShadowWidthRes(int resId) {
		setShadowWidth((int)getResources().getDimension(resId));
	}

	/**
	 * �������ص�ֵ��������Ӱ�Ŀ��
	 *
	 * @param pixels the new shadow width, in pixels
	 */
	public void setShadowWidth(int pixels) {
		mViewBehind.setShadowWidth(pixels);
	}

	/**
	 * �����Ƿ��ܹ�ʹ�û����˵����뽥����Ч��
	 **/
	public void setFadeEnabled(boolean b) {
		mViewBehind.setFadeEnabled(b);
	}

	/**
	 * ���ý��뽥��Ч����ֵ
	 *
	 * @param f the new fade degree, between 0.0f and 1.0f
	 */
	public void setFadeDegree(float f) {
		mViewBehind.setFadeDegree(f);
	}

	/**
	 * Enables or disables whether the selector is drawn
	 *
	 * @param b true to draw the selector, false to not draw the selector
	 */
	public void setSelectorEnabled(boolean b) {
		mViewBehind.setSelectorEnabled(true);
	}

	/**
	 * Sets the selected view. The selector will be drawn here
	 *
	 * @param v the new selected view
	 */
	public void setSelectedView(View v) {
		mViewBehind.setSelectedView(v);
	}

	/**
	 * Sets the selector drawable.
	 *
	 * @param res a resource ID for the selector drawable
	 */
	public void setSelectorDrawable(int res) {
		mViewBehind.setSelectorBitmap(BitmapFactory.decodeResource(getResources(), res));
	}

	/**
	 * Sets the selector drawable.
	 *
	 * @param b the new selector bitmap
	 */
	public void setSelectorBitmap(Bitmap b) {
		mViewBehind.setSelectorBitmap(b);
	}

	/**
	 * ��ӱ����Ե���ͼ
	 */
	public void addIgnoredView(View v) {
		mViewAbove.addIgnoredView(v);
	}

	/**
	 * �Ƴ������Ե���ͼ
	 */
	public void removeIgnoredView(View v) {
		mViewAbove.removeIgnoredView(v);
	}

	/**
	 * ��ģʽΪFullscreenģʽʱ��������Ļ������б����Ե���ͼ
	 */
	public void clearIgnoredViews() {
		mViewAbove.clearIgnoredViews();
	}

	/**
	 * ���ô򿪼����¼����������˵�����ʱ����
	 */
	public void setOnOpenListener(OnOpenListener listener) {
		mOpenListener = listener;
	}

	/**
	 * ���ùرռ����¼����������˵����ر�ʱ����
	 */
	public void setOnCloseListener(OnCloseListener listener) {
		//mViewAbove.setOnCloseListener(listener);
		mCloseListener = listener;
	}

	/**
	 * ���ô򿪼����¼����������˵����򿪹�֮�����
	 */
	public void setOnOpenedListener(OnOpenedListener listener) {
		mViewAbove.setOnOpenedListener(listener);
	}

	/**
	 * ���ùرռ����¼����������˵����رչ�֮�����
	 */
	public void setOnClosedListener(OnClosedListener listener) {
		mViewAbove.setOnClosedListener(listener);
	}

	/**
	 * ��������������״̬���࣬�̳���BaseSavedState
	 */
	public static class SavedState extends BaseSavedState {
		private final int mItem;

		public SavedState(Parcelable superState, int item) {
			super(superState);
			mItem = item;
		}

		private SavedState(Parcel in) {
			super(in);
			mItem = in.readInt();
		}

		public int getItem() {
			return mItem;
		}

		/* (non-Javadoc)
		 * @see android.view.AbsSavedState#writeToParcel(android.os.Parcel, int)
		 */
		public void writeToParcel(Parcel out, int flags) {
			super.writeToParcel(out, flags);
			out.writeInt(mItem);
		}

		public static final Parcelable.Creator<SavedState> CREATOR =
				new Parcelable.Creator<SavedState>() {
			public SavedState createFromParcel(Parcel in) {
				return new SavedState(in);
			}

			public SavedState[] newArray(int size) {
				return new SavedState[size];
			}
		};
	}

	/* (non-Javadoc)
	 * @see android.view.View#onSaveInstanceState()
	 */
	@Override
	protected Parcelable onSaveInstanceState() {
		Parcelable superState = super.onSaveInstanceState();
		SavedState ss = new SavedState(superState, mViewAbove.getCurrentItem());
		return ss;
	}

	/* (non-Javadoc)
	 * @see android.view.View#onRestoreInstanceState(android.os.Parcelable)
	 */
	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		SavedState ss = (SavedState)state;
		super.onRestoreInstanceState(ss.getSuperState());
		mViewAbove.setCurrentItem(ss.getItem());
	}

	/* (non-Javadoc)
	 * @see android.view.ViewGroup#fitSystemWindows(android.graphics.Rect)
	 */
	@SuppressLint("NewApi")
	@Override
	protected boolean fitSystemWindows(Rect insets) {
		int leftPadding = insets.left;
		int rightPadding = insets.right;
		int topPadding = insets.top;
		int bottomPadding = insets.bottom;
		if (!mActionbarOverlay) {
			Log.v(TAG, "setting padding!");
			setPadding(leftPadding, topPadding, rightPadding, bottomPadding);
		}
		return true;
	}
	
	private Handler mHandler = new Handler();

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void manageLayers(float percentOpen) {
		if (Build.VERSION.SDK_INT < 11) return;

		boolean layer = percentOpen > 0.0f && percentOpen < 1.0f;
		final int layerType = layer ? View.LAYER_TYPE_HARDWARE : View.LAYER_TYPE_NONE;

		if (layerType != getContent().getLayerType()) {
			mHandler.post(new Runnable() {
				public void run() {
					Log.v(TAG, "changing layerType. hardware? " + (layerType == View.LAYER_TYPE_HARDWARE));
					getContent().setLayerType(layerType, null);
					getMenu().setLayerType(layerType, null);
					if (getSecondaryMenu() != null) {
						getSecondaryMenu().setLayerType(layerType, null);
					}
				}
			});
		}
	}

}
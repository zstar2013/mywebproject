package com.tony.viewpager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import cn.w.song.widget.navigation.RollNavigationBar;
import cn.w.song.widget.navigation.adapter.RollNavigationBarAdapter;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

/**
 * 仿优酷Android客户端图片左右滑动
 * 
 */
public class MyViewPagerActivity extends Activity {
	private ViewPager viewPager; // android-support-v4中的滑动组件
	private List<ImageView> imageViews; // 滑动的图片集合
	KeywordsFlow keywordsFlow;
	private int MAX_WORLDS = 20;
	private int WORLDS;
	int aa = 2;
	private String[] title = { "主页", "时讯", "养生", "设置" };
	public static String[] Namekeywords = { "速生鸡", "话梅", "膨化食品", "麻花卷", "豆腐干",
			"酸菜鱼", "糖果", "食用调和油", "矿泉水"};

	private String[] titles; // 图片标题
	private int[] imageResId; // 图片ID
	private List<View> dots; // 图片标题正文的那些点

	private TextView tv_title;
	private int currentItem = 0; // 当前图片的索引号

	// An ExecutorService that can schedule commands to run after a given delay,
	// or to execute periodically.
	private ScheduledExecutorService scheduledExecutorService;

	// 切换当前显示的图片
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			viewPager.setCurrentItem(currentItem);// 切换当前显示的图片
		};
	};

	Handler nameshandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 2:

				keywordsFlow.rubKeywords();
				feedKeywordsFlow(keywordsFlow, Namekeywords);
				keywordsFlow.removeAllViews();
				Random random = new Random();
				int ran = random.nextInt(10);
				if (ran % 2 == 0) {
					keywordsFlow.go2Show(KeywordsFlow.ANIMATION_OUT);
				} else {
					keywordsFlow.go2Show(KeywordsFlow.ANIMATION_IN);
				}
				break;

			default:
				break;
			}

		}
	};
	boolean isshowkeyflower = true;
	showThread thread;

	class showThread extends Thread {
		@Override
		public void run() {
			while (isshowkeyflower) {
				try {
					if (aa == 2) {
						nameshandler.sendEmptyMessage(2);
					}
					sleep(4000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	Thread myThread;

	@Override
	protected void onResume() {
		super.onResume();

		// 启动更新进度条的线程
		isshowkeyflower = true;
		thread = new showThread();
		thread.start();
	}

	private void feedKeywordsFlow(KeywordsFlow keywordsFlow, String[] arr) {
		Random random = new Random();
		if (arr.length > MAX_WORLDS) {
			WORLDS = MAX_WORLDS;
		} else {
			WORLDS = arr.length;
		}
		for (int i = 0; i < WORLDS; i++) {
			int ran = random.nextInt(arr.length);
			String tmp = arr[ran];
			keywordsFlow.feedKeyword(tmp);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		imageResId = new int[] { R.drawable.a, R.drawable.b, R.drawable.c,
				R.drawable.d, R.drawable.e };
		titles = new String[imageResId.length];
		titles[0] = "巩俐不低俗，我就不能低俗";
		titles[1] = "扑树又回来啦！再唱经典老歌引万人大合唱";
		titles[2] = "揭秘北京电影如何升级";
		titles[3] = "乐视网TV版大派送";
		titles[4] = "热血屌丝的反杀";

		imageViews = new ArrayList<ImageView>();

		// 初始化图片资源
		for (int i = 0; i < imageResId.length; i++) {
			ImageView imageView = new ImageView(this);
			imageView.setImageResource(imageResId[i]);
			imageView.setScaleType(ScaleType.CENTER_CROP);
			imageViews.add(imageView);
		}

		dots = new ArrayList<View>();
		dots.add(findViewById(R.id.v_dot0));
		dots.add(findViewById(R.id.v_dot1));
		dots.add(findViewById(R.id.v_dot2));
		dots.add(findViewById(R.id.v_dot3));
		dots.add(findViewById(R.id.v_dot4));

		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText(titles[0]);//

		viewPager = (ViewPager) findViewById(R.id.vp);
		viewPager.setAdapter(new MyAdapter());// 设置填充ViewPager页面的适配器
		// 设置一个监听器，当ViewPager中的页面改变时调用
		viewPager.setOnPageChangeListener(new MyPageChangeListener());
		keywordsFlow = (KeywordsFlow) findViewById(R.id.frameLayout1);

		keywordsFlow.setOnItemClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String keyword = ((TextView) v).getText().toString();
				Toast.makeText(MyViewPagerActivity.this, keyword,
						Toast.LENGTH_SHORT).show();

			}
		});
		final RollNavigationBar rnb = (RollNavigationBar) findViewById(R.id.uiademo_ui_RollNavigationBar);
		/* 设置滑动条的滑动时间，时间范围在0.1~1s，不在范围则默认0.1s */
		rnb.setSelecterMoveContinueTime(0.1f);// 可以不设置，默认0.1s
		/* 设置滑动条样式（图片） */
		rnb.setSelecterDrawableSource(R.drawable.navi_menu);// 必须
		/* 设置导航栏的被选位置 */
		rnb.setSelectedChildPosition(0);// 可以不设置
		/* 定制动态数据 */
		List<Map<String, Object>> list = new LinkedList<Map<String, Object>>();
		for (int i = 0; i < title.length; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("title", title[i]);
			list.add(map);
		}
		/* 导航栏扩展 */
		final MyNavigationBarAdapter adapter = new MyNavigationBarAdapter(this,
				list);
		rnb.setAdapter(adapter);// 必须
		rnb.setNavigationBarListener(new RollNavigationBar.NavigationBarListener() {
			/**
			 * position 被选位置 view 为导航栏 event 移动事件
			 */
			@Override
			public void onNavigationBarClick(int position, View view,
					MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:// 按下去时

					break;
				case MotionEvent.ACTION_MOVE:// 移动中

					break;
				case MotionEvent.ACTION_UP:// 抬手时

					break;
				}

			}

		});

	}
	
	
	/**
	 * 导航栏扩展
	 * 
	 * @author w.song
	 * @version 1.0.1
	 * @date 2012-4-22
	 */
	class MyNavigationBarAdapter extends RollNavigationBarAdapter {
		private List<Map<String, Object>> list;
		private LayoutInflater mInflater;

		public MyNavigationBarAdapter(Activity activity,
				List<Map<String, Object>> list) {
			mInflater = LayoutInflater.from(activity);
			this.list = list;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		/**
		 * 获取每个组件
		 * 
		 * @param position
		 *            组件的位置
		 * @param contextView
		 *            组件
		 * @param parent
		 *            上层组件
		 */
		@Override
		public View getView(int position, View contextView, ViewGroup parent) {
			mInflater.inflate(R.layout.item, (ViewGroup) contextView);
			RollNavigationBar rollNavigationBar = (RollNavigationBar) parent;
			/* 获取组件 */
			TextView titleView = (TextView) contextView
					.findViewById(R.id.title_view);

			/* 获取参数 */
			String title = "" + list.get(position).get("title");
		/*	int photo = (Integer) list.get(position).get("photo");
			int photoSelected = (Integer) list.get(position).get(
					"photoSelected");*/

			/* 组件设置参数 */
			// 区分选择与被选择图片
			/*if (position == rollNavigationBar.getSelectedChildPosition()) {// 被选择
				imageView.setBackgroundResource(photoSelected);
			} else {// 没被选择
				imageView.setBackgroundResource(photo);
			}*/
			if (position == rollNavigationBar.getSelectedChildPosition()) {// 被选择
				titleView.setTextColor(Color.parseColor("#000000"));
			} else {// 没被选择
				titleView.setTextColor(Color.parseColor("#FFFFFF"));
			}
			titleView.setText(title);
			return contextView;
		}

	}

	@Override
	protected void onStart() {
		scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
		// 当Activity显示出来后，每两秒钟切换一次图片显示
		scheduledExecutorService.scheduleAtFixedRate(new ScrollTask(), 1, 2,
				TimeUnit.SECONDS);
		super.onStart();
	}

	@Override
	protected void onStop() {
		// 当Activity不可见的时候停止切换
		scheduledExecutorService.shutdown();
		super.onStop();
	}

	/**
	 * 换行切换任务
	 * 
	 * @author Administrator
	 * 
	 */
	private class ScrollTask implements Runnable {

		public void run() {
			synchronized (viewPager) {
				System.out.println("currentItem: " + currentItem);
				currentItem = (currentItem + 1) % imageViews.size();
				handler.obtainMessage().sendToTarget(); // 通过Handler切换图片
			}
		}

	}

	/**
	 * 当ViewPager中页面的状态发生改变时调用
	 * 
	 * @author Administrator
	 * 
	 */
	private class MyPageChangeListener implements OnPageChangeListener {
		private int oldPosition = 0;

		/**
		 * This method will be invoked when a new page becomes selected.
		 * position: Position index of the new selected page.
		 */
		public void onPageSelected(int position) {
			currentItem = position;
			tv_title.setText(titles[position]);
			dots.get(oldPosition).setBackgroundResource(R.drawable.dot_normal);
			dots.get(position).setBackgroundResource(R.drawable.dot_focused);
			oldPosition = position;
		}

		public void onPageScrollStateChanged(int arg0) {

		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}
	}

	/**
	 * 填充ViewPager页面的适配器
	 * 
	 * @author Administrator
	 * 
	 */
	private class MyAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return imageResId.length;
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			((ViewPager) arg0).addView(imageViews.get(arg1));
			return imageViews.get(arg1);
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView((View) arg2);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {

		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {

		}

		@Override
		public void finishUpdate(View arg0) {

		}
	}
}
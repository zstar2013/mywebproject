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
 * ���ſ�Android�ͻ���ͼƬ���һ���
 * 
 */
public class MyViewPagerActivity extends Activity {
	private ViewPager viewPager; // android-support-v4�еĻ������
	private List<ImageView> imageViews; // ������ͼƬ����
	KeywordsFlow keywordsFlow;
	private int MAX_WORLDS = 20;
	private int WORLDS;
	int aa = 2;
	private String[] title = { "��ҳ", "ʱѶ", "����", "����" };
	public static String[] Namekeywords = { "������", "��÷", "��ʳƷ", "�黨��", "������",
			"�����", "�ǹ�", "ʳ�õ�����", "��Ȫˮ"};

	private String[] titles; // ͼƬ����
	private int[] imageResId; // ͼƬID
	private List<View> dots; // ͼƬ�������ĵ���Щ��

	private TextView tv_title;
	private int currentItem = 0; // ��ǰͼƬ��������

	// An ExecutorService that can schedule commands to run after a given delay,
	// or to execute periodically.
	private ScheduledExecutorService scheduledExecutorService;

	// �л���ǰ��ʾ��ͼƬ
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			viewPager.setCurrentItem(currentItem);// �л���ǰ��ʾ��ͼƬ
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

		// �������½��������߳�
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
		titles[0] = "���������ף��ҾͲ��ܵ���";
		titles[1] = "�����ֻ��������ٳ������ϸ������˴�ϳ�";
		titles[2] = "���ر�����Ӱ�������";
		titles[3] = "������TV�������";
		titles[4] = "��Ѫ��˿�ķ�ɱ";

		imageViews = new ArrayList<ImageView>();

		// ��ʼ��ͼƬ��Դ
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
		viewPager.setAdapter(new MyAdapter());// �������ViewPagerҳ���������
		// ����һ������������ViewPager�е�ҳ��ı�ʱ����
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
		/* ���û������Ļ���ʱ�䣬ʱ�䷶Χ��0.1~1s�����ڷ�Χ��Ĭ��0.1s */
		rnb.setSelecterMoveContinueTime(0.1f);// ���Բ����ã�Ĭ��0.1s
		/* ���û�������ʽ��ͼƬ�� */
		rnb.setSelecterDrawableSource(R.drawable.navi_menu);// ����
		/* ���õ������ı�ѡλ�� */
		rnb.setSelectedChildPosition(0);// ���Բ�����
		/* ���ƶ�̬���� */
		List<Map<String, Object>> list = new LinkedList<Map<String, Object>>();
		for (int i = 0; i < title.length; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("title", title[i]);
			list.add(map);
		}
		/* ��������չ */
		final MyNavigationBarAdapter adapter = new MyNavigationBarAdapter(this,
				list);
		rnb.setAdapter(adapter);// ����
		rnb.setNavigationBarListener(new RollNavigationBar.NavigationBarListener() {
			/**
			 * position ��ѡλ�� view Ϊ������ event �ƶ��¼�
			 */
			@Override
			public void onNavigationBarClick(int position, View view,
					MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:// ����ȥʱ

					break;
				case MotionEvent.ACTION_MOVE:// �ƶ���

					break;
				case MotionEvent.ACTION_UP:// ̧��ʱ

					break;
				}

			}

		});

	}
	
	
	/**
	 * ��������չ
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
		 * ��ȡÿ�����
		 * 
		 * @param position
		 *            �����λ��
		 * @param contextView
		 *            ���
		 * @param parent
		 *            �ϲ����
		 */
		@Override
		public View getView(int position, View contextView, ViewGroup parent) {
			mInflater.inflate(R.layout.item, (ViewGroup) contextView);
			RollNavigationBar rollNavigationBar = (RollNavigationBar) parent;
			/* ��ȡ��� */
			TextView titleView = (TextView) contextView
					.findViewById(R.id.title_view);

			/* ��ȡ���� */
			String title = "" + list.get(position).get("title");
		/*	int photo = (Integer) list.get(position).get("photo");
			int photoSelected = (Integer) list.get(position).get(
					"photoSelected");*/

			/* ������ò��� */
			// ����ѡ���뱻ѡ��ͼƬ
			/*if (position == rollNavigationBar.getSelectedChildPosition()) {// ��ѡ��
				imageView.setBackgroundResource(photoSelected);
			} else {// û��ѡ��
				imageView.setBackgroundResource(photo);
			}*/
			if (position == rollNavigationBar.getSelectedChildPosition()) {// ��ѡ��
				titleView.setTextColor(Color.parseColor("#000000"));
			} else {// û��ѡ��
				titleView.setTextColor(Color.parseColor("#FFFFFF"));
			}
			titleView.setText(title);
			return contextView;
		}

	}

	@Override
	protected void onStart() {
		scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
		// ��Activity��ʾ������ÿ�������л�һ��ͼƬ��ʾ
		scheduledExecutorService.scheduleAtFixedRate(new ScrollTask(), 1, 2,
				TimeUnit.SECONDS);
		super.onStart();
	}

	@Override
	protected void onStop() {
		// ��Activity���ɼ���ʱ��ֹͣ�л�
		scheduledExecutorService.shutdown();
		super.onStop();
	}

	/**
	 * �����л�����
	 * 
	 * @author Administrator
	 * 
	 */
	private class ScrollTask implements Runnable {

		public void run() {
			synchronized (viewPager) {
				System.out.println("currentItem: " + currentItem);
				currentItem = (currentItem + 1) % imageViews.size();
				handler.obtainMessage().sendToTarget(); // ͨ��Handler�л�ͼƬ
			}
		}

	}

	/**
	 * ��ViewPager��ҳ���״̬�����ı�ʱ����
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
	 * ���ViewPagerҳ���������
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
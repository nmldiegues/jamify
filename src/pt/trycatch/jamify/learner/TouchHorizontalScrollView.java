package pt.trycatch.jamify.learner;

import java.util.ArrayList;

import pt.trycatch.jamify.R;
import pt.trycatch.jamify.SongComparisonActivity;
import pt.trycatch.jamify.jammer.SongChord;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


public class TouchHorizontalScrollView extends SurfaceView implements SurfaceHolder.Callback {

	private ArrayList<ChordItem> originalItems = new ArrayList<TouchHorizontalScrollView.ChordItem>();
	private ArrayList<ChordItem> userItems = new ArrayList<TouchHorizontalScrollView.ChordItem>();
	public static final int TIMER_PERIOD = 500;
	private static int SCREEN_WIDTH = 0;
	private static int ORIGINALSONG_Y = 200;
	private static int USERSONG_Y = 400;
	private static int ITEM_WIDTH = 0;
	private static int ITEM_HEIGHT = 100;
	private static int VIEW_WIDTH_ORIGINAL = 0;
	private static int VIEW_WIDTH_USER = 0;
	private static float DENSITY = 0;

	public DrawThread mThread;
	private static int[] BAR_LIST = new int[]{R.drawable.large_bar_blue,  R.drawable.large_bar_green, R.drawable.large_bar_brown, R.drawable.large_bar_grey, R.drawable.large_bar_orange, R.drawable.large_bar_red, R.drawable.large_bar_violet};
	private boolean isTopThread = false;
	private Context mCtx;


	public TouchHorizontalScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mCtx = context;
	}

	public TouchHorizontalScrollView(Activity activity) {
		super(activity);		
		DisplayMetrics displaymetrics = new DisplayMetrics();
		(activity).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		SCREEN_WIDTH = displaymetrics.widthPixels;
		//SCREEN_HEIGHT = displaymetrics.heightPixels;
		DENSITY = displaymetrics.density;
		ITEM_WIDTH = SCREEN_WIDTH / 5;
		getHolder().addCallback(this);
		this.setFocusable(true);
		mCtx = activity;
	}


	public int getChordWidthByDuration(double d)
	{
		int res = 0;
		res = (int) (d*358);
		return res;
	}

	public int getBarByChordName(String chordname)
	{
		return (chordname.charAt(0)%BAR_LIST.length);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	public Bitmap createScaleBitmap(Bitmap b, int width, int height) {
		return Bitmap.createScaledBitmap(b, width, height, false);
	}
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// init data
		originalItems.clear();
		userItems.clear();

		VIEW_WIDTH_ORIGINAL = 0;
		for(SongChord ch : SongComparisonActivity.originalSong.getChords())
		{
			ChordItem tmp = new ChordItem();
			tmp.chord = ch;
			tmp.cWidth = getChordWidthByDuration((ch.getEnd()-ch.getInit())/1000);
			tmp.bitmap = createScaleBitmap(BitmapFactory.decodeResource(getResources(), BAR_LIST[getBarByChordName(ch.getChordName())]), tmp.cWidth, ITEM_HEIGHT);
			tmp.x = VIEW_WIDTH_ORIGINAL;
			tmp.y = ORIGINALSONG_Y;
			VIEW_WIDTH_ORIGINAL += tmp.cWidth;
			originalItems.add(tmp);
		}
		
		VIEW_WIDTH_USER = 0;
		double emptyTime = 0;
		for(SongChord ch : SongComparisonActivity.userSong.getChords())
		{
			ChordItem tmp = new ChordItem();
			tmp.chord = ch;
			tmp.cWidth = getChordWidthByDuration((ch.getEnd()-ch.getInit())/1000);
			tmp.bitmap = createScaleBitmap(BitmapFactory.decodeResource(getResources(), BAR_LIST[getBarByChordName(ch.getChordName())]), tmp.cWidth, ITEM_HEIGHT);
			emptyTime = getChordWidthByDuration(ch.getInit()-VIEW_WIDTH_USER);
			tmp.x = (VIEW_WIDTH_USER);//+emptyTime);
			tmp.y = USERSONG_Y;
			VIEW_WIDTH_USER += tmp.cWidth;//(emptyTime+tmp.cWidth);
			userItems.add(tmp);
		}

		mThread = new DrawThread();
		mThread.start();
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		System.out.println("==> TOUCHEVENT");

		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mThread.xDistance = mThread.yDistance = 0f;
			mThread.lastX = ev.getX();
			mThread.lastY = ev.getY();
			mThread.isTouched = true;
			break;
		case MotionEvent.ACTION_MOVE:
			final float curX = ev.getX();
			final float curY = ev.getY();
			mThread.xDistance += 0.1*(curX - mThread.lastX);
			mThread.yDistance += 0.1*(curY - mThread.lastY);
			System.out.println("==> TOUCH ACTION_MOVE xDistance: "+mThread.xDistance+"\tcurX: "+curX+"\tlastX:"+mThread.lastX);
			mThread.lastX = curX;
			mThread.lastY = curY;
			mThread.handle_movement(mThread.xDistance);
			if(mThread.xDistance > mThread.yDistance)
				return false;
			break;
		case MotionEvent.ACTION_UP:
			System.out.println("==> TOUCH ACTION_UP xDistance: "+mThread.xDistance+"\tlastX:"+mThread.lastX);
			mThread.isTouched = false;
			//mThread.handle_movement(mThread.xDistance);
		}
		return true;//super.onTouchEvent(ev);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		if (mThread != null) {
			isTopThread = true;
		}
	}

	private class ChordItem {	
		public float x;
		public float y;
		SongChord chord;
		public int cWidth;
		public Bitmap bitmap;


		public void drawItem(Canvas canvas, boolean isUser) 
		{
			canvas.save();

			if (isUser) {
				Paint paint = new Paint();
				
					Bitmap alpha = bitmap.extractAlpha();
					BlurMaskFilter blurMaskFilter = new BlurMaskFilter(40, BlurMaskFilter.Blur.OUTER);
					paint.setMaskFilter(blurMaskFilter);
					if (chord.score == SongChord.SCORE_BAD) {
						paint.setColor(0xffff0000);
					} else if (chord.score == SongChord.SCORE_MEDIUM) {
						paint.setColor(0xffADD8E6);
					} else {
						paint.setColor(0xff9ACD32);
					}
					canvas.drawBitmap(alpha, x, y, paint);
				
			}
			
			Paint paint = new Paint();
			paint.setAntiAlias(true);
			paint.setFilterBitmap(true);
			paint.setAntiAlias(true);

			Typeface tf = Typeface.create("Helvetica",Typeface.BOLD);
			paint.setTypeface(tf);						//textfont
			paint.setColor(Color.YELLOW); 				//textcolor
			paint.setTextSize((int) (20 * DENSITY));	//textsize

			canvas.drawBitmap(bitmap, x, y, paint);

			String chordname = chord.getChordName();
			int DELTA = 70;
			float textwidth = paint.measureText(chordname);
			if(x > 0)
			{
				if(isUser)
					canvas.drawText(chordname, x+20, USERSONG_Y+DELTA, paint);
				else
					canvas.drawText(chordname, x+20, ORIGINALSONG_Y+DELTA, paint);

			}
			else if(x+cWidth < textwidth)
			{
				if(isUser)
					canvas.drawText(chordname, x+cWidth-textwidth, USERSONG_Y+DELTA, paint);
				else
					canvas.drawText(chordname, x+cWidth-textwidth, ORIGINALSONG_Y+DELTA, paint);

			}
			else
			{
				if(isUser)
					canvas.drawText(chordname, 0, USERSONG_Y+DELTA, paint);
				else
					canvas.drawText(chordname, 0, ORIGINALSONG_Y+DELTA, paint);
			}
			
			paint.setColor(Color.WHITE); 				//textcolor
			if(isUser)
				canvas.drawText("User Song:", 20, USERSONG_Y-20, paint);
			else
				canvas.drawText("Original Song:", 20, ORIGINALSONG_Y-20, paint);
			canvas.restore();
		}
	}

	//..................................................
	public class DrawThread extends Thread {

		public float yDistance = 0;
		public float lastY = 0;
		public float lastX = 0;
		public float xDistance = 0;
		public boolean isTouched = false;

		@Override
		public void run() {

			while (!isTopThread) {
				draw();
			}
		}


		public void draw() {
			if (isTopThread) {
				return;
			}

			Canvas canvas = getHolder().lockCanvas();
			if (canvas == null) {
				return;
			}
			canvas.save();
			canvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);

			for (int i = 0; i < originalItems.size(); i++) {	
				ChordItem item = originalItems.get(i);
				item.drawItem(canvas, false);
			}
			
			for (int i = 0; i < userItems.size(); i++) {	
				ChordItem item = userItems.get(i);
				item.drawItem(canvas, true);
			}

			Typeface tfLob = Typeface.createFromAsset(mCtx.getAssets(),"fonts/Lobster_1.3.otf");
			Paint paint = new Paint();
			paint.setAntiAlias(true);
			paint.setFilterBitmap(true);
			paint.setAntiAlias(true);
			paint.setTypeface(tfLob);						//textfont
			paint.setColor(Color.WHITE); 				//textcolor
			paint.setTextSize((int) (30 * DENSITY));	//textsize
			
			canvas.drawText("Score: "+String.valueOf(SongComparisonActivity.score), 30, 700, paint);
			
			canvas.restore();
			getHolder().unlockCanvasAndPost(canvas);
		}

		public void handle_movement(float xdist)
		{
			//prevents the first bar to go off the right side of the screen
			if((originalItems.get(0).x+xdist > SCREEN_WIDTH) || (originalItems.get(originalItems.size()-1).x+xdist < 0))
				return;

			for (int i = 0; i < originalItems.size(); i++) {	
				ChordItem item = originalItems.get(i);					
				item.x += xdist;
			}
			
			for (int i = 0; i < userItems.size(); i++) {	
				ChordItem item = userItems.get(i);					
				item.x += xdist;
			}
		}
	}
}

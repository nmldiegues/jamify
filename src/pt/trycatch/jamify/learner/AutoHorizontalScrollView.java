package pt.trycatch.jamify.learner;

import java.util.ArrayList;

import pt.trycatch.jamify.BackendRequestThread;
import pt.trycatch.jamify.R;
import pt.trycatch.jamify.RecordService;
import pt.trycatch.jamify.SongComparisonActivity;
import pt.trycatch.jamify.jammer.Song;
import pt.trycatch.jamify.jammer.SongChord;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * @author huyletran84@gmail.com
 */
public class AutoHorizontalScrollView extends SurfaceView implements SurfaceHolder.Callback {

	private ArrayList<ChordItem> items = new ArrayList<AutoHorizontalScrollView.ChordItem>();
	public String hardcodedSong = "54;244;4;4;0:B Major;9.65951:C# Minor;11.8886:G# Minor;13.2818:E Major;15.7896:G# Minor;15.8825:B Major;17.5543:C# Minor;19.7835:G# Minor;21.1767:E Major;23.22:C# Minor;24.3346:B Major;25.6349:C# Major;26.8424:C# Major;27.3996:C# Major"; 
	
	public static final int TIMER_PERIOD = 500;
	private static int SCREEN_WIDTH = 0;
	//private static int SCREEN_HEIGHT = 0;
	private static int ITEM_HEIGHT = 200;
	private static int VIEW_WIDTH = 0;
	private static float DENSITY = 0;

	private Thread mThread;
	private static int[] BAR_LIST = new int[]{R.drawable.large_bar_blue,  R.drawable.large_bar_green, R.drawable.large_bar_brown, R.drawable.large_bar_grey, R.drawable.large_bar_orange, R.drawable.large_bar_red, R.drawable.large_bar_violet};
	private long startDisplayTime = System.currentTimeMillis();
	private boolean isTopThread = false;
	private boolean songHasFinished = false;
	private Song song;
	private RecordService mRecord;
	public static String songName;
	private Context mCtx;
	
	private final Activity activity;
	
	public AutoHorizontalScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.activity = null;
		mCtx = context;
	}

	public AutoHorizontalScrollView(Song s, Activity activity) {
		super(activity);
		this.song = s;
		DisplayMetrics displaymetrics = new DisplayMetrics();
		(activity).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		SCREEN_WIDTH = displaymetrics.widthPixels;
		//SCREEN_HEIGHT = displaymetrics.heightPixels;
		DENSITY = displaymetrics.density;
		getHolder().addCallback(this);
		this.setFocusable(true);
		this.activity = activity;
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
		items.clear();

		VIEW_WIDTH = 100;
		for(SongChord ch : song.getChords())
		{
			ChordItem tmp = new ChordItem();
			tmp.chord = ch;
			tmp.cWidth = getChordWidthByDuration((ch.getEnd()-ch.getInit())/1000);
			tmp.bitmap = createScaleBitmap(BitmapFactory.decodeResource(getResources(), BAR_LIST[getBarByChordName(ch.getChordName())]), tmp.cWidth, ITEM_HEIGHT);
			tmp.x = VIEW_WIDTH;
			tmp.y = ITEM_HEIGHT;
			VIEW_WIDTH += tmp.cWidth;
			items.add(tmp);
		}
		
		startDisplayTime = System.currentTimeMillis();
		  
		mThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (!isTopThread) {
					draw();
				}
			}
		});
		mThread.start();
		mRecord = new RecordService();
		mRecord.startRecording();		
	}
	protected void draw() {
		if (isTopThread) {
			return;
		}

		boolean glow = false;
		if (System.currentTimeMillis() - startDisplayTime >= TIMER_PERIOD) 
		{
			// update view by animation.
			startDisplayTime = System.currentTimeMillis();
			for (int i = 0; i < items.size(); i++) 
			{
				ChordItem item = items.get(i);

				//loop
				/*if (item.x <= -item.cWidth) {
					item.x = VIEW_WIDTH - item.cWidth;
				}*/

				TranslateXAnimation translateX = new TranslateXAnimation();
				translateX.mStartTime = System.currentTimeMillis();
				translateX.mDuration = 500;
				translateX.mEndTime = System.currentTimeMillis() + 500;
				translateX.mStartValue = item.x;
				translateX.mEndValue = item.x - 179;
				item.translateX = translateX;

				if(item.x < 0 && (item.x+item.cWidth) > 0)
					glow = true;
				
				//the song has finished: stop recording
				if(i==(items.size()-1) && (item.x+item.cWidth) < 0)
				{
					synchronized(this){
						if(!songHasFinished)
						{
							this.activity.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AutoHorizontalScrollView.this.activity);
									alertDialogBuilder.setTitle("Processing your performance");
									alertDialogBuilder.setMessage("Please hold");
									AlertDialog alertDialog = alertDialogBuilder.create();
									alertDialog.show();
								}
							});

							System.out.println("===> Song has finished!");
							songHasFinished = true;
							String filename = mRecord.stopRecording();
							final Song songInfo = BackendRequestThread.getAllInfoOnFile(filename);

							SongComparisonActivity.userSong = songInfo;
							SongComparisonActivity.originalSong = song;
							
							Intent intent = new Intent(AutoHorizontalScrollView.this.getContext(), SongComparisonActivity.class);
							AutoHorizontalScrollView.this.getContext().startActivity(intent);
							((Activity) AutoHorizontalScrollView.this.getContext()).finish();
						}
					}
				}					
			}
		} 
		
		Canvas canvas = getHolder().lockCanvas();
		if (canvas == null) {
			return;
		}
		canvas.save();
		canvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);

		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);
		paint.setAntiAlias(true);
		
		if(!songHasFinished)
		{
			if(glow)
			{
				Bitmap buttonbmp = createScaleBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.vectormetalbutton_glow), 300, 350);
				canvas.drawBitmap(buttonbmp, -130, 135, paint);
			}
			else
			{
				Bitmap buttonbmp = createScaleBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.vectormetalbutton), 300, 350);
				canvas.drawBitmap(buttonbmp, -130, 135, paint);
			}


			for (int i = 0; i < items.size(); i++) {	
				ChordItem item = items.get(i);
				item.draw(canvas);
			}
		}
		else
		{
			Typeface tf = Typeface.create("Roboto",Typeface.BOLD);
			paint.setTypeface(tf);						//textfont
			paint.setColor(Color.YELLOW); 				//textcolor
			paint.setTextSize((int) (20 * DENSITY));	//textsize
		}

		canvas.restore();
		getHolder().unlockCanvasAndPost(canvas);
	}

	public static class FireMissilesDialogFragment extends DialogFragment {
	    @Override
	    public Dialog onCreateDialog(Bundle savedInstanceState) {
	        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	        builder.setMessage("Processing your performance. Please hold");
	        return builder.create();
	    }
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
		public TranslateXAnimation translateX;

		public void draw(Canvas canvas) 
		{
			canvas.save();
			if (translateX != null && !translateX.isEnded()) 
			{
				x = translateX.getCurrentValue(System.currentTimeMillis()
						- translateX.mStartTime);
			}
			
			Paint paint = new Paint();
			if(this.x < 0 && (this.x+this.cWidth) > 0) {
				Bitmap alpha = bitmap.extractAlpha();
				BlurMaskFilter blurMaskFilter = new BlurMaskFilter(40, BlurMaskFilter.Blur.OUTER);
				paint.setMaskFilter(blurMaskFilter);
				paint.setColor(0xffFF4D00);
				canvas.drawBitmap(alpha, x, y, paint);
			}
			
			
			paint = new Paint();
			paint.setAntiAlias(true);
			paint.setFilterBitmap(true);
			paint.setAntiAlias(true);

			Typeface tf = Typeface.create("Roboto",Typeface.BOLD);
			paint.setTypeface(tf);						//textfont
			paint.setColor(Color.YELLOW); 				//textcolor
			paint.setTextSize((int) (30 * DENSITY));	//textsize

			canvas.drawBitmap(bitmap, x, y, paint);

			String chordName = chord.getChordName();
			float textwidth = paint.measureText(chordName);
			if(x > 0)
			{
				canvas.drawText(chordName, x+20, 330, paint);

			}
			else if(x+cWidth < textwidth)
			{
				canvas.drawText(chordName, x+cWidth-textwidth-20, 330, paint);
			}
			else
			{
				canvas.drawText(chordName, 0, 330, paint);
			}

			
			Typeface tfLob = Typeface.createFromAsset(mCtx.getAssets(),"fonts/Lobster_1.3.otf");
			paint.setTypeface(tfLob);						//textfont
			paint.setColor(Color.WHITE); 				//textcolor
			paint.setTextSize((int) (30 * DENSITY));	//textsize
			
			canvas.drawText(songName, 30, 700, paint);
			canvas.restore();
		}
	}
}

package pl.kamituel.nfc_qr_alarm;

import java.util.Calendar;
import java.util.TimeZone;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;


public class ClockSurfaceView extends SurfaceView implements SurfaceHolder.Callback, SurfaceView.OnTouchListener {
	private final static String TAG = ClockSurfaceView.class.getSimpleName();
	
	private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private float mAlarmAngle = 0;
	
	private int mCanvasWidth = 0;
	private int mCanvasHeight = 0;
	private final static int MARGIN = 80;
	private int x, y, w, h, cx, cy;
	
	private RectF mOuterCircle = null;
	private RectF mInnerCircle = null;
	private RectF mAlarmBullet = null;
	private RectF mCenterPoint = null;
	private RadialGradient mAlarmGradient = null;
	private RadialGradient mInnerCircleGradient = null;
	private RadialGradient mAlarmDueInGradient = null;
	private RectF mInnerRing = null;

	private boolean mIsRunning = true;
	private boolean mRepaint = true;
	
	private Calendar mAlarmTime = null;
	
	private boolean mGrabScreenshot = false;

	public ClockSurfaceView(Context context) {
		super(context);
		_ClockSurfaceView();
	}

	public ClockSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		_ClockSurfaceView();
	}
	
	private void _ClockSurfaceView () {
		if ( isInEditMode() ) return;
		
		//this.setDrawingCacheEnabled(true);
		this.setZOrderOnTop(true);
		this.getHolder().setFormat(PixelFormat.TRANSPARENT);
		setWillNotDraw(false);
		
		getHolder().addCallback(this);
		
		setOnTouchListener(this);
		
		Log.d(TAG, "ClockSurface created");		
	}
	
	public void setAlarmTimeOrNone (Calendar c) {
		mAlarmTime = c;
	}

	public void setAngle (double a) {
		mAlarmAngle = (float) a;
		mRepaint = true;
	}

	@Override
	public void onDraw(Canvas c) {
		super.onDraw(c);
		
//		Bitmap screenshot = null;
//		if ( mGrabScreenshot ) {
//			screenshot = Bitmap.createBitmap(mCanvasWidth, mCanvasHeight, Bitmap.Config.ARGB_8888);
//			c = new Canvas(screenshot);
//		}
		
		if ( isInEditMode() ) return;
		
		drawClockBase(c);
		drawAlarm(c);
		drawClockCurrentTime(c);
		
		drawInnerRing(c);
		
		drawAlarmDueIn(c);
		
		
//		if ( mGrabScreenshot ) {
//			mGrabScreenshot = false;
//			try {
//				OutputStream out = new FileOutputStream("/storage/sdcard0/Pictures/Screenshots/canvas.png");
//				screenshot.compress(CompressFormat.PNG, 100, out);
//				out.close();
//				Log.i(TAG, "Screenshot saved");
//			} catch (Exception e) {
//				Log.e(TAG, "Screenshot error", e);
//			}
//		}
		
	}
	
	private void drawAlarm (Canvas c) {
		if ( mAlarmTime != null ) return; 
		
		c.save();
		
		c.rotate(-90+mAlarmAngle, mCanvasWidth/2, mCanvasHeight/2);
		
		mPaint.setColor(Color.parseColor("#ee3333"));
		mPaint.setStrokeWidth(5);
		mPaint.setStyle(Paint.Style.STROKE);
		
		c.drawLine(cx+w/30, cy, x+w+w*5/100, cy, mPaint);
		
		mPaint.setStyle(Paint.Style.FILL);
		mPaint.setShader(mAlarmGradient);
		c.drawOval(mAlarmBullet, mPaint);
		mPaint.setShader(null);
		
		c.restore();
	}
	
	private void drawClockBase (Canvas c) {
		int size;
		
		mPaint.setStyle(Paint.Style.FILL);
		
		//mPaint.setColor(Color.YELLOW);
		//c.drawRect(0, 0, mCanvasWidth, mCanvasHeight, mPaint);
		
		// outer circle
		if ( mAlarmTime == null ) mPaint.setColor(Color.parseColor("#eeeeee"));
		else mPaint.setColor(Color.parseColor("#999999"));
		c.drawOval(mOuterCircle, mPaint);
		
		// inner circle
		mPaint.setColor(Color.parseColor("#003a51"));
		mPaint.setShader(mInnerCircleGradient);
		c.drawOval(mInnerCircle, mPaint);
		mPaint.setShader(null);
		
		size = (int)(0.012 * w);
		for ( int hour = 0; hour < 12; hour++ ) {
			double px, py;
			
			switch (hour) {
			case 0: px = 0; py = -1; break;
			case 1: px = Math.sin(Math.PI/6); 		py = -Math.cos(Math.PI/6); break;
			case 2: px = Math.sin(Math.PI/6*2); 	py = -Math.cos(Math.PI/6*2); break;
			case 3: px = 1; py = 0; break;
			case 4: px = Math.sin(Math.PI/6); 		py = Math.cos(Math.PI/6); break;
			case 5: px = Math.sin(Math.PI/6*2); 	py = Math.cos(Math.PI/6*2); break;
			case 6: px = 0; py = 1; break;
			case 7: px = -Math.sin(Math.PI/6); 		py = Math.cos(Math.PI/6); break;
			case 8: px = -Math.sin(Math.PI/6*2); 	py = Math.cos(Math.PI/6*2); break;
			case 9: px = -1; py = 0; break;
			case 10: px = -Math.sin(Math.PI/6); 	py = -Math.cos(Math.PI/6); break;
			case 11: px = -Math.sin(Math.PI/6*2); 	py = -Math.cos(Math.PI/6*2); break;
			default: px = -700; py = -700;
			}
			
			
			
			int centerX = cx;
			int centerY = cy;
			int marginH = size*5;
			
			int left = (int)(centerX + px * (w/2-marginH) - size);
			int top = (int)(centerY + py * (h/2-marginH) - size);
			int right = (int)(centerX + px * (w/2-marginH) + size);
			int bottom = (int)(centerY + py * (h/2-marginH) + size);
			
			//Log.d(TAG, String.format("px %f py %f l %d t %d r %d b %d", px, py, left, top, right, bottom));
			
			if ( mAlarmTime == null )	mPaint.setColor(Color.parseColor("#ffffff"));
			else mPaint.setColor(Color.parseColor("#aaaaaa"));
			c.drawOval(new RectF(left, top, right, bottom), mPaint);
		}
		
	}
	
	private void drawInnerRing (Canvas c) {
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeWidth((int)(0.01*w));
		c.drawOval(mInnerRing, mPaint);
		
		// center point
		mPaint.setStyle(Paint.Style.FILL);
		mPaint.setColor(Color.WHITE);
		c.drawOval(mCenterPoint, mPaint);
	}
	
	private void drawClockCurrentTime (Canvas c) {
		drawClockHour(c, Calendar.getInstance());
		drawClockMinute(c, Calendar.getInstance());
	}
	
	private void drawClockMinute (Canvas c, Calendar time) {
		c.save();
		
		float angle = -90+360*((float)time.get(Calendar.MINUTE)*60+time.get(Calendar.SECOND))/(3600f);
		//Log.d(TAG, "minute rotate "+angle);
		c.rotate(angle, mCanvasWidth/2, mCanvasHeight/2);
		
		mPaint.setStyle(Paint.Style.STROKE);
		
//		mPaint.setColor(Color.parseColor("#333333"));
//		mPaint.setStrokeWidth(7);
//		c.drawLine(cx+w/20, cy, x+w-w*10/100+2, cy, mPaint);
		
		mPaint.setColor(Color.parseColor("#ffffff"));
		mPaint.setStrokeWidth(3);
		c.drawLine(cx+w/30, cy, x+w-w*10/100, cy, mPaint);
		
		c.restore();
	}
	
	private void drawClockHour (Canvas c, Calendar time) {
		c.save();
		float angle = -90+360*((float)time.get(Calendar.HOUR_OF_DAY)*60*60+time.get(Calendar.MINUTE)*60+time.get(Calendar.SECOND))/(3600f*12);
		//Log.d(TAG, "hour rotate "+angle);
		c.rotate(angle, mCanvasWidth/2, mCanvasHeight/2);
		
		mPaint.setStyle(Paint.Style.STROKE);
		
//		mPaint.setColor(Color.parseColor("#333333"));
//		mPaint.setStrokeWidth(9);
//		c.drawLine(cx+w/20, cy, x+w-w*25/100+2, cy, mPaint);
		
		mPaint.setColor(Color.parseColor("#ffffff"));
		mPaint.setStrokeWidth(5);
		c.drawLine(cx+w/30, cy, x+w-w*25/100, cy, mPaint);
		
//		int thick = 3;
//		
//		Path p = new Path();
//		p.setLastPoint(cx+w*3/100, cy-thick);
//		p.lineTo(x+w-w*25/100, cy-thick);
//		p.setLastPoint(x+w-w*25/100, cy-thick);
//		RectF arc = new RectF(x+w-w*25/100, cy-thick, x+w-w*25/100+thick*2, cy+thick);
//		p.arcTo(arc, 270, 180);
//		//p.lineTo(x+w-w*15/100, cy+thick);
//		p.setLastPoint(x+w-w*25/100, cy+thick);
//		p.lineTo(cx+w*3/100, cy+thick);
//		p.setLastPoint(cx+w*3/100, cy+thick);
//		p.lineTo(cx+w*3/100, cy-thick);
//		
//		c.drawPath(p, mPaint);
		
		c.restore();
	}
	
	private void drawAlarmDueIn (Canvas c) {
		if ( mAlarmTime == null ) return;
		
		// inner circle
		mPaint.setColor(Color.parseColor("#003a51"));
		mPaint.setShader(mAlarmDueInGradient);
		c.drawOval(mInnerCircle, mPaint);
		mPaint.setShader(null);
		
		int txtLarge = w/10;
		int txtSmall = w/15;
		
		Calendar cnow = Calendar.getInstance();
		Calendar diff = Calendar.getInstance(TimeZone.getTimeZone("UCT"));
		diff.setTimeInMillis(mAlarmTime.getTimeInMillis()-cnow.getTimeInMillis());
		
		int hours = diff.get(Calendar.HOUR_OF_DAY);
		int mins = diff.get(Calendar.MINUTE);
		
		mPaint.setColor(Color.parseColor("#bbbbbb"));
		mPaint.setTextSize(txtSmall);
		String text = getResources().getString(R.string.alarm_due_in);
		float textW = mPaint.measureText(text);
		c.drawText(text, cx-textW/2, cy-2*txtSmall, mPaint);
		
		String hText = hours+"";
		String mText = ""+mins;
		String hLabel = getResources().getString(R.string.hrs)+" ";
		String mLabel = getResources().getString(R.string.min);
		
		mPaint.setTextSize(txtLarge);
		float hTextW = mPaint.measureText(hText);
		float mTextW = mPaint.measureText(mText);
		mPaint.setTextSize(txtSmall);
		float hLabelW = mPaint.measureText(hLabel);
		float mLabelW = mPaint.measureText(mLabel);

		float totalW = hTextW + hLabelW + mTextW + mLabelW;
		
		mPaint.setColor(Color.parseColor("#eeeeee"));
		mPaint.setTextSize(txtLarge);
		c.drawText(hText, cx-totalW/2, cy+txtLarge/2, mPaint);
		c.drawText(mText, cx-totalW/2+hTextW+hLabelW, cy+txtLarge/2, mPaint);
		
		mPaint.setColor(Color.parseColor("#bbbbbb"));
		mPaint.setTextSize(w/15);
		c.drawText(hLabel, cx-totalW/2+hTextW, cy+txtLarge/2, mPaint);
		c.drawText(mLabel, cx-totalW/2+hTextW+hLabelW+mTextW, cy+txtLarge/2, mPaint);
	}

	private class AnimateThread extends Thread {
		public void run () {
			int i = 0;
			while ( mIsRunning ) {
				try {
					if ( i++ > 30*1000/20 || mRepaint ) {
						i = 0;
						mRepaint = false;
						postInvalidate();
					}
					Thread.sleep(20);
				} catch (InterruptedException e) {
					Log.e(TAG, "", e);
				}
			}
		}

	}



	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int cw, int ch) {
		Log.d(TAG, "surfaceChanged "+cw+","+ch);
		//mClockBounds = new Rect(MARGIN, MARGIN, w-MARGIN, w-MARGIN);
		mCanvasWidth = cw;
		mCanvasHeight = ch;
		w = mCanvasWidth - 2*MARGIN;
		h = mCanvasHeight - 2*MARGIN;
		x = MARGIN;
		y = MARGIN;
		cx = x + w/2;
		cy = y + h/2;
		
		int size = 20;
		mAlarmBullet = new RectF(x+w+size*3/4, cy-size, x+w+size*3/4+2*size, cy+size);
		
		mOuterCircle = new RectF(x, y, w+x, h+y);
		
		size = (int)(0.007 * w);
		mInnerCircle = new RectF(x+size, y+size, w+x-size, h+y-size);
		
		size = (int)(0.008 * w);
		mCenterPoint = new RectF(cx-size, cy-size, cx+size, cy+size);
		mInnerRing = new RectF(cx-w/30, cy-h/30, cx+w/30, cy+h/30);
		
		mAlarmGradient = new RadialGradient(mAlarmBullet.centerX(), mAlarmBullet.centerY(), mAlarmBullet.width()/2, Color.parseColor("#ee3333"), Color.parseColor("#bb0000"), Shader.TileMode.CLAMP);
		mInnerCircleGradient = new RadialGradient(cx, cy, w/2, Color.parseColor("#104a6a"), Color.parseColor("#001a31"), Shader.TileMode.CLAMP);
		mAlarmDueInGradient = new RadialGradient(cx, cy, w/2, Color.parseColor("#ee000000"), Color.parseColor("#aa000000"), Shader.TileMode.CLAMP);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mIsRunning = true;
		new AnimateThread().start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		mIsRunning = false;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		Log.d(TAG, "onMeasure");
		super.onMeasure(widthMeasureSpec, widthMeasureSpec);
	}
	


	@Override
	public boolean onTouch(View arg0, MotionEvent ev) {
		if ( mAlarmTime != null ) return false;
		//Log.d(TAG, "action "+ev.getAction());
		
		int cx = mCanvasWidth/2;
		int cy = mCanvasHeight/2;
		
		float x = ev.getX();
		float y = ev.getY();

		double angle = 0;
		float dx = x - cx;
		float dy = y - cy;
		if ( dx >= 0 && dy < 0) {
			angle = -Math.atan(dx/dy);
		} else if ( dx >= 0 && dy > 0 ) {
			angle = Math.PI-Math.atan(dx/dy);
		} else if ( dx >= 0 && dy == 0 ) {
			angle = Math.PI;
		} else if ( dx <= 0 && dy > 0 ) {
			angle = Math.PI + -Math.atan(dx/dy);
		} else if ( dx <= 0 && dy < 0 ) {
			angle = 2*Math.PI - Math.atan(dx/dy);
		} else if ( dx <= 0 && dy == 0 ) {
			angle = 3/2*Math.PI;
		} else {
			Log.e(TAG, "Could not compute angle for "+dx+","+dy);
			return false;
		}
		angle = Math.toDegrees(angle);

		int secondsTillMidnight = TimeUtils.getSecondsFromMidnight(angle);
		secondsTillMidnight = secondsTillMidnight - (secondsTillMidnight % (5*60));
		angle = TimeUtils.getAngleFromSecondsFromMidnight(secondsTillMidnight);
		setAngle(angle);
		
		//Date sanitized = TimeUtils.sanitizeDate(TimeUtils.getTimeFromAngle(angle));
		//setAngle(TimeUtils.getAngleFromTime(sanitized));
		//if ( mAlarmListener != null ) mAlarmListener.onAlarmChanged(sanitized);
		if ( mAlarmListener != null ) mAlarmListener.onAlarmChanged(secondsTillMidnight);
		
		Integer prevSecondsTillMidnight = (Integer) getTag(R.id.clock_time_on_previous_touch);
		if ( prevSecondsTillMidnight != null ) {
			switch ( ev.getAction() ) {
			case MotionEvent.ACTION_DOWN:
				Log.d(TAG, "Clock presseed");
				break;
			case MotionEvent.ACTION_MOVE:
				//Log.d(TAG, "prevSec "+prevSecondsTillMidnight+" sec "+secondsTillMidnight);
				
				if ( prevSecondsTillMidnight > 11*3600 && secondsTillMidnight < 1*3600 ) {
					Log.d(TAG, "Change forward "+secondsTillMidnight+" prev="+prevSecondsTillMidnight);
					if ( mAlarmListener != null ) mAlarmListener.onTimeOfDayChanged();
				} else if ( prevSecondsTillMidnight < 1*3600 && secondsTillMidnight > 11*3600 ) {
					Log.d(TAG, "Change backward "+secondsTillMidnight+" prev="+prevSecondsTillMidnight);
					if ( mAlarmListener != null ) mAlarmListener.onTimeOfDayChanged();
				}
				
				break;
			}
		} 
		
		if ( ev.getAction() == MotionEvent.ACTION_UP && mAlarmListener != null ) mAlarmListener.commit();
		
		setTag(R.id.clock_time_on_previous_touch, secondsTillMidnight);
		return true;
	}

	public static interface AlarmListener {
		public void onAlarmChanged (int seconsTill12);
		public void onTimeOfDayChanged();
		public void commit();
	}
	
	private AlarmListener mAlarmListener = null;
	public void setAlarmListener (AlarmListener l) {
		mAlarmListener = l;
	}

	public void grabScreenshot () {
		mGrabScreenshot = true; 
	}

//	@Override
//	protected void onAttachedToWindow() {
//		Log.d(TAG, "onAttachedToWindow");
//		super.onAttachedToWindow();
//	}
//
//	@Override
//	protected void onDetachedFromWindow() {
//		Log.d(TAG, "onDetachedFromWindow");
//		super.onDetachedFromWindow();
//	}
//
//	@Override
//	protected void onWindowVisibilityChanged(int visibility) {
//		Log.d(TAG, "onWindowVisibilityChanged");
//		super.onWindowVisibilityChanged(visibility);
//	}
//
//	@Override
//	protected void onAnimationEnd() {
//		Log.d(TAG, "onAnimationEnd");
//		super.onAnimationEnd();
//	}
//
//	@Override
//	protected void onAnimationStart() {
//		Log.d(TAG, "onAnimationStart");
//		super.onAnimationStart();
//	}
//
//	@Override
//	protected void onConfigurationChanged(Configuration newConfig) {
//		Log.d(TAG, "onConfigurationChanged");
//		super.onConfigurationChanged(newConfig);
//	}
//
//	@Override
//	protected int[] onCreateDrawableState(int extraSpace) {
//		Log.d(TAG, "onCreateDrawableState");
//		return super.onCreateDrawableState(extraSpace);
//	}
//
//	@Override
//	protected void onFinishInflate() {
//		Log.d(TAG, "onFinishInflate");
//		super.onFinishInflate();
//	}
//
//	@Override
//	public void onFinishTemporaryDetach() {
//		Log.d(TAG, "onFinishTemporaryDetach");
//		super.onFinishTemporaryDetach();
//	}
//
//	@Override
//	protected void onLayout(boolean changed, int left, int top, int right,
//			int bottom) {
//		Log.d(TAG, "onLayout");
//		super.onLayout(changed, left, top, right, bottom);
//	}
//
//	@Override
//	protected Parcelable onSaveInstanceState() {
//		Log.d(TAG, "onSaveInstanceState");
//		return super.onSaveInstanceState();
//	}
//
//	@Override
//	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//		Log.d(TAG, "onSizeChanged");
//		super.onSizeChanged(w, h, oldw, oldh);
//	}
//
//	@Override
//	public void onStartTemporaryDetach() {
//		Log.d(TAG, "onStartTemporaryDetach");
//		super.onStartTemporaryDetach();
//	}
//
//	@Override
//	protected void onVisibilityChanged(View changedView, int visibility) {
//		Log.d(TAG, "onVisibilityChanged");
//		super.onVisibilityChanged(changedView, visibility);
//	}
//	
//	
}

package pl.kamituel.nfc_qr_alarm;

import java.util.Calendar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;


public class ClockSurfaceView extends SurfaceView implements SurfaceHolder.Callback, SurfaceView.OnTouchListener {
	private final static String TAG = ClockSurfaceView.class.getSimpleName();
	
	private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	
	private int mCanvasWidth = 0;
	private int mCanvasHeight = 0;
	private int x, y, w, h, cx, cy;
	
	/**
	 * Theese are here to avoid instantiating them
	 * over and over again in onDraw().
	 */
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
	
	private AlarmDataProvider mAlarmData = null;
	
	public ClockSurfaceView(Context context) {
		super(context);
		_ClockSurfaceView(context);
	}

	public ClockSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		_ClockSurfaceView(context);
	}
	
	private void _ClockSurfaceView (Context context) {
		if ( isInEditMode() ) return;
		
		//this.setDrawingCacheEnabled(true);
		this.setZOrderOnTop(true);
		this.getHolder().setFormat(PixelFormat.TRANSPARENT);
		setWillNotDraw(false);
		
		getHolder().addCallback(this);
		
		setOnTouchListener(this);

		Log.d(TAG, "ClockSurface created");		
	}
	
	private float getAlarmHandAngle () {
		return TimeUtils.secondsToAngle(mAlarmData.getSelectedAlarm().get());
	}

	@Override
	public void onDraw(Canvas c) {
		super.onDraw(c);
		
		AlarmTime alarm = mAlarmData.getSelectedAlarm();
		
		if ( isInEditMode() ) return;
		
		drawClockBase(c, alarm);
		
		if ( !alarm.getEnabled() ) drawAlarm(c);
		if ( !alarm.getEnabled() ) drawClockCurrentTime(c);
		
		drawInnerRing(c);
		
		if ( alarm.getEnabled() ) drawAlarmDueIn(c, alarm);
	}
	
	private void drawAlarm (Canvas c) {
		c.save();
		
		c.rotate(-90+getAlarmHandAngle(), mCanvasWidth/2, mCanvasHeight/2);
		
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
	
	private void drawClockBase (Canvas c, AlarmTime alarm) {
		int size;
		
		mPaint.setStyle(Paint.Style.FILL);
		
		// outer circle
		mPaint.setColor(Color.parseColor(alarm.getEnabled() ? "#999999" : "#EEEEEE"));
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
			
			mPaint.setColor(Color.parseColor(alarm.getEnabled() ? "#AAAAAA" : "#FFFFFF"));
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
		mPaint.setColor(Color.parseColor("#ffffff"));
		mPaint.setStrokeWidth(5);
		c.drawLine(cx+w/30, cy, x+w-w*25/100, cy, mPaint);
		
		c.restore();
	}
	
	private void drawAlarmDueIn (Canvas c, AlarmTime alarm) {
		// inner circle
		mPaint.setColor(Color.parseColor("#003a51"));
		mPaint.setShader(mAlarmDueInGradient);
		c.drawOval(mInnerCircle, mPaint);
		mPaint.setShader(null);
		
		int txtLarge = w/10;
		int txtSmall = w/15;
		
		int hours = alarm.getCountdownHours();
		int mins = alarm.getCountdownMinutes();

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
		int margin = cw * 1 / 7;
		w = mCanvasWidth - 2*margin;
		h = mCanvasHeight - 2*margin;
		x = margin;
		y = margin;
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
		//Log.d(TAG, "onMeasure");
		super.onMeasure(widthMeasureSpec, widthMeasureSpec);
	}
	
	private float getAngleFromTouch (int cx, int cy, float x, float y) {
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
			return 0;
		}
		return (float) Math.toDegrees(angle);
	}
	
	@Override
	public boolean onTouch(View arg0, MotionEvent ev) {
		AlarmTime alarm = mAlarmData.getSelectedAlarm();
		if ( alarm.getEnabled() ) return false;
		
		int cx = mCanvasWidth/2;
		int cy = mCanvasHeight/2;
		float x = ev.getX();
		float y = ev.getY();
		float angle = getAngleFromTouch(cx, cy, x, y);
		
		int seconds = TimeUtils.angleToSeconds(angle, true);

		switch ( ev.getAction() ) {
		case MotionEvent.ACTION_DOWN:
			Log.d(TAG, "Clock presseed");
			break;
		case MotionEvent.ACTION_MOVE:			
			int H = TimeUtils.HOUR;
			int prevSeconds = (alarm.get() % TimeUtils.TWELVE_HOUR);
			boolean crossedTwelveOclock = ((prevSeconds > 11*H && seconds < H) 
					|| (prevSeconds < H && seconds > 11*H));
			
			if ( crossedTwelveOclock) Log.d(TAG, "Twelve o'clock crossed");
			
			if ( alarm.get() >= 12*H ) {
				if ( crossedTwelveOclock ) alarm.set(seconds);
				else alarm.set(12*H+seconds);
			} else {
				if ( crossedTwelveOclock ) alarm.set(12*H+seconds);
				else alarm.set(seconds);	
			}
			
			//if ( crossedTwelveOclock ) mAlarmData.onTimeOfDayChanged();
			
			//if ( crossedTwelveOclock ) Log.d(TAG, "touch seconds "+seconds+", prev "+prevSeconds*1.0/H+", res "+mAlarm.get()*1.0/H);

			break;
		}

		mRepaint = true;
		//mAlarmData.onAlarmChanged(mAlarm.get());
		
		if (ev.getAction() == MotionEvent.ACTION_UP ) mAlarmData.commit();
		
		//if ( ev.getAction() == MotionEvent.ACTION_UP && mAlarmListener != null ) mAlarmListener.commit();

		return true;
	}
	
	public void setAlarmDataProvider (AlarmDataProvider l) {
		mAlarmData = l;
	}
	
	public void forceRepaint () {
		mRepaint = true;
	}
}

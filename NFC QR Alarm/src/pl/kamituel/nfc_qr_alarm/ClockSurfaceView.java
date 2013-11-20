package pl.kamituel.nfc_qr_alarm;

import java.util.Calendar;

import pl.kamituel.nfc_qr_alarm.alarm.Alarm;
import pl.kamituel.nfc_qr_alarm.time.ClockFaceTime;
import pl.kamituel.nfc_qr_alarm.time.CountdownDecorator;
import pl.kamituel.nfc_qr_alarm.time.Time;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
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
	private RectF mPmIndicator360 = null;
	//private RectF mPmIndicator720 = null;
	private RectF mAlarmBullet = null;
	private RectF mCenterPoint = null;
	private RadialGradient mAlarmGradient = null;
	private RadialGradient mInnerCircleGradient = null;
	private RadialGradient mAlarmDueInGradient = null;
	private RectF mInnerRing = null;

	private boolean mIsRunning = true;
	private boolean mRepaint = true;
	
	private Alarm mAlarm = null;
	private ClockFaceTime mAlarmClockFace = null;
	private CountdownDecorator mAlarmCountdown = null;
	
	private boolean mTouchDown = false;

	/**** PRECACHED COLORS ****/
	// Inner circle gradient
	private int cInnerCircleLight = Color.parseColor("#104a6a");
	private int cInnerCircleDark = Color.parseColor("#001a31");
	
	// Inner circle gradient when showing "alarm due in" message
	private int cAlarmDueInLight = Color.parseColor("#ee000000");
	private int cAlarmDueInDark = Color.parseColor("#aa000000");
	
	// Outer circle
	private int cOuterCircleWhenEnabled = Color.parseColor("#999999");
	private int cOuterCircleWhenDisabled = Color.parseColor("#EEEEEE");
	
	// Alarm arrow gradient
	private int cAlarmLight = Color.parseColor("#ee3333");
	private int cAlarmDark = Color.parseColor("#bb0000");
	
	// "Due in" rings color
	private int cDueIn360 = Color.parseColor("#104a6a");
	private int cDueIn720 = Color.parseColor("#508aaa");
	
	/**** PRECACHED PAINTS ****/
	// Outer circle
	private Paint pOuterCircleWhenEnabled = new Paint(Paint.ANTI_ALIAS_FLAG);
	private Paint pOuterCircleWhenDisabled = new Paint(Paint.ANTI_ALIAS_FLAG);
	
	// Inner circle
	private Paint pInnerCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
	
	// Inner ring
	private Paint pInnerRing = new Paint(Paint.ANTI_ALIAS_FLAG);
	private Paint pCenterPoint = new Paint(Paint.ANTI_ALIAS_FLAG);
	
	// "Due in" rings
	private Paint pDueIn360 = new Paint(Paint.ANTI_ALIAS_FLAG);
	private Paint pDueIn720 = new Paint(Paint.ANTI_ALIAS_FLAG);
	
	// Alarm arrow
	private Paint pAlarmLine = new Paint(Paint.ANTI_ALIAS_FLAG);
	private Paint pAlarmBullet = new Paint(Paint.ANTI_ALIAS_FLAG);
	
	// Clock arrows
	private Paint pClockHour = new Paint(Paint.ANTI_ALIAS_FLAG);
	private Paint pClockMinute = new Paint(Paint.ANTI_ALIAS_FLAG);

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
	
	public void setAlarm(Alarm alarm) {
		mAlarm = alarm;
		mAlarmClockFace = new ClockFaceTime(mAlarm.getTime());
		mAlarmCountdown = new CountdownDecorator(mAlarm.getTime());
	}
	
	private float getAlarmHandAngle () {
		return (float) mAlarmClockFace.getAngle();
	}

	@Override
	public void onDraw(Canvas c) {
		super.onDraw(c);
		if ( isInEditMode() ) return;
		
		Calendar calendar = Calendar.getInstance();
		
		if ( !mAlarm.getEnabled() && mTouchDown ) drawPmIndicator(c, calendar); 

		drawClockBase(c);
		
		if ( !mAlarm.getEnabled() ) drawAlarm(c);
		if ( !mAlarm.getEnabled() ) drawClockCurrentTime(c, calendar);		
		drawInnerRing(c);
		
		if ( mAlarm.getEnabled() ) drawAlarmDueIn(c);
	}
	
	private void drawAlarm (Canvas c) {
		c.save();		
		c.rotate(-90+getAlarmHandAngle(), mCanvasWidth/2, mCanvasHeight/2);
		
		c.drawLine(cx+w/30, cy, x+w+w*5/100, cy, pAlarmLine);
		c.drawOval(mAlarmBullet, pAlarmBullet);

		c.restore();
	}
	
	private void drawClockBase (Canvas c) {
		int size;
		
		c.drawOval(mOuterCircle, mAlarm.getEnabled() ? pOuterCircleWhenEnabled : pOuterCircleWhenDisabled);
		c.drawOval(mInnerCircle, pInnerCircle);
		
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
			
			mPaint.setColor(Color.parseColor(mAlarm.getEnabled() ? "#AAAAAA" : "#FFFFFF"));
			c.drawOval(new RectF(left, top, right, bottom), mPaint);
		}
		
	}
	
	private void drawInnerRing (Canvas c) {
		c.drawOval(mInnerRing, pInnerRing);
		c.drawOval(mCenterPoint, pCenterPoint);
	}
	
	private void drawClockCurrentTime (Canvas c, Calendar calendar) {
		drawClockHour(c, calendar);
		drawClockMinute(c, calendar);
	}
	
	private void drawClockMinute (Canvas c, Calendar time) {
		float angle = -90 + 360 * ((float) time.get(Calendar.MINUTE) * 60 + time.get(Calendar.SECOND))/(3600f);

		c.save();
		c.rotate(angle, mCanvasWidth/2, mCanvasHeight/2);
		c.drawLine(cx+w/30, cy, x+w-w*10/100, cy, pClockMinute);	
		c.restore();
	}
	
	private void drawClockHour (Canvas c, Calendar time) {
		float angle = timeAngle(time);
		
		c.save();
		c.rotate(angle, mCanvasWidth/2, mCanvasHeight/2);
		c.drawLine(cx+w/30, cy, x+w-w*25/100, cy, pClockHour);
		c.restore();
	}
	
	private float timeAngle (Calendar time) {
		return -90 + 360 * (
				(float)time.get(Calendar.HOUR_OF_DAY) * 60 * 60
				+ time.get(Calendar.MINUTE) * 60
				+ time.get(Calendar.SECOND)
				) / (3600f * 12);
	}
	
	private void drawAlarmDueIn (Canvas c) {
		// inner circle
		mPaint.setStyle(Paint.Style.FILL);
		mPaint.setColor(Color.parseColor("#003a51"));
		mPaint.setShader(mAlarmDueInGradient);
		c.drawOval(mInnerCircle, mPaint);
		mPaint.setShader(null);
		
		int txtLarge = w/10;
		int txtSmall = w/15;
		
		int hours = mAlarmCountdown.getHours();
		int mins = mAlarmCountdown.getMinutes();

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

	private void drawPmIndicator (Canvas c, Calendar calendar) {
		long milliseconds = mAlarm.getTime().getAlarmCountdown();
		
		float countdownAngle = (float) ((milliseconds > 12 * Time.HOUR ? 360 : 0) 
				+ mAlarmClockFace.getAngle());
		
		float angle = timeAngle(calendar);
		
		c.save();
		c.translate(cx, cx);
		c.rotate(angle, 0, 0);

		c.drawArc(mPmIndicator360, 0, countdownAngle, true, pDueIn360);

		if (countdownAngle > 360) {
			c.drawArc(mPmIndicator360, 0, countdownAngle % 360, true, pDueIn720);
		}
		
		c.restore();
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
		
		size = (int)(0.01 * w);
		mPmIndicator360 = new RectF(-w/2-size, -h/2-size, w/2+size, h/2+size);
		//size = (int)(0.06 * w);
		//mPmIndicator720 = new RectF(-w/2-size, -h/2-size, w/2+size, h/2+size);
		
		size = (int)(0.008 * w);
		mCenterPoint = new RectF(cx-size, cy-size, cx+size, cy+size);
		mInnerRing = new RectF(cx-w/30, cy-h/30, cx+w/30, cy+h/30);
		
		mAlarmGradient = new RadialGradient(mAlarmBullet.centerX(), mAlarmBullet.centerY(), mAlarmBullet.width()/2, cAlarmLight, cAlarmDark, Shader.TileMode.CLAMP);
		mInnerCircleGradient = new RadialGradient(cx, cy, w/2, cInnerCircleLight, cInnerCircleDark, Shader.TileMode.CLAMP);
		mAlarmDueInGradient = new RadialGradient(cx, cy, w/2, cAlarmDueInLight, cAlarmDueInDark, Shader.TileMode.CLAMP);
		
		precomputePaints();
	}
	
	private void precomputePaints () {
		pDueIn360.setStyle(Paint.Style.STROKE);
		pDueIn360.setStrokeWidth((int)(0.04 * w));
		pDueIn360.setColor(cDueIn360);
		
		pDueIn720.setStyle(Paint.Style.STROKE);
		pDueIn720.setStrokeWidth((int)(0.04 * w));
		pDueIn720.setColor(cDueIn720);
		pDueIn720.setPathEffect(new DashPathEffect(new float[] {5, 25}, 0));
		
		pAlarmLine.setColor(Color.parseColor("#ee3333"));
		pAlarmLine.setStrokeWidth(5);
		pAlarmLine.setStyle(Paint.Style.STROKE);

		pAlarmBullet.setStyle(Paint.Style.FILL);
		pAlarmBullet.setShader(mAlarmGradient);
		
		pOuterCircleWhenEnabled.setStyle(Paint.Style.STROKE);
		pOuterCircleWhenEnabled.setColor(cOuterCircleWhenEnabled);
		
		pOuterCircleWhenDisabled.setStyle(Paint.Style.STROKE);
		pOuterCircleWhenDisabled.setColor(cOuterCircleWhenDisabled);
		
		pInnerCircle.setStyle(Paint.Style.FILL);
		pInnerCircle.setColor(Color.parseColor("#003a51"));
		pInnerCircle.setShader(mInnerCircleGradient);
		
		pInnerRing.setStyle(Paint.Style.STROKE);
		pInnerRing.setColor(Color.WHITE);
		pInnerRing.setStrokeWidth((int)(0.01*w));
		
		pCenterPoint.setStyle(Paint.Style.FILL);
		pCenterPoint.setColor(Color.WHITE);
		
		pClockMinute.setStyle(Paint.Style.STROKE);
		pClockMinute.setColor(Color.parseColor("#ffffff"));
		pClockMinute.setStrokeWidth(3);
		
		pClockHour.setStyle(Paint.Style.STROKE);
		pClockHour.setColor(Color.parseColor("#ffffff"));
		pClockHour.setStrokeWidth(5);
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
		if (mAlarm.getEnabled()) {
			return false;
		}
		
		int cx = mCanvasWidth/2;
		int cy = mCanvasHeight/2;
		float x = ev.getX();
		float y = ev.getY();
		float angle = getAngleFromTouch(cx, cy, x, y);
		
		long milliseconds = ClockFaceTime.millisecondsFromAngle(angle, true);

		switch ( ev.getAction() ) {
		case MotionEvent.ACTION_DOWN:
			Log.d(TAG, "Clock presseed");
			mTouchDown = true;
			break;
		case MotionEvent.ACTION_UP:
			mTouchDown = false;
			break;
		case MotionEvent.ACTION_MOVE:			
			long H = Time.HOUR;
			long alarmValue = mAlarm.getTime().getAbsolute();
			long prevMilliseconds = (alarmValue % (12 * H));
			boolean crossedTwelveOclock = ((prevMilliseconds > 11*H && milliseconds < H) 
					|| (prevMilliseconds < H && milliseconds > 11*H));
			
			if (crossedTwelveOclock) {
				Log.d(TAG, "Twelve o'clock crossed");
			}
			
			if ( alarmValue >= 12*H ) {
				if (crossedTwelveOclock) {
					mAlarm.getTime().setAbsolute(milliseconds);
				} else {
					mAlarm.getTime().setAbsolute(12*H + milliseconds);
				}
			} else {
				if (crossedTwelveOclock) {
					mAlarm.getTime().setAbsolute(12*H + milliseconds);
				} else {
					mAlarm.getTime().setAbsolute(milliseconds);	
				}
			}

			break;
		}

		mRepaint = true;		
		if (ev.getAction() == MotionEvent.ACTION_UP ) {
			// TODO: commit needed?
			// mAlarmData.commit();
		}

		return true;
	}
	
	public void forceRepaint () {
		mRepaint = true;
	}
}

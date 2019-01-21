package com.scwang.wave;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.v4.graphics.ColorUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * 多重水波纹
 * Created by SCWANG on 2017/12/11.
 */
@SuppressWarnings("unused")
public class WaterWave extends ViewGroup {

    private final String TAG = "WaterWave";

    private Paint mPaint = new Paint();
    private Paint mPaint2 = new Paint();
    private Paint mPaint3 = new Paint();
    private Paint waterPaint = new Paint();
    private Matrix mMatrix = new Matrix();
    private Water water = new Water(0xFF065986, this.getWidth(), this.getHeight(), 120);
    private List<WaveNew> mltWave = new ArrayList<>();
    private int mAmplitude;
    private int mStartColor;
    private int mCloseColor;
    private int mStartColor2;
    private int mCloseColor2;
    private int mGradientAngle;
    private boolean mIsRunning;
    private float mVelocity;
    private float mColorAlpha;
    private float mProgress;
    private long mLastTime = 0;
    private int crest = 2;

    private int waterHeight;

    private int colorCambridgeBlue = Color.parseColor("#7affff");
    private int colorBlue = Color.parseColor("#0ae1fb");
    private int colorOrange = Color.parseColor("#f9ab87");


    public WaterWave(Context context) {
        this(context, null, 0);
    }

    public WaterWave(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaterWave(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mPaint.setAntiAlias(true);
        mPaint2.setAntiAlias(true);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.MultiWaveHeader);

        mAmplitude = ta.getDimensionPixelOffset(R.styleable.MultiWaveHeader_mwhWaveHeight, Util.dp2px(50));
        mStartColor = ta.getColor(R.styleable.MultiWaveHeader_mwhStartColor, 0xFF056CD0);
        mCloseColor = ta.getColor(R.styleable.MultiWaveHeader_mwhCloseColor, 0xFF31AFFE);
        mStartColor2 = ta.getColor(R.styleable.MultiWaveHeader_mwhStartColor, 0xFF065986);
        mCloseColor2 = ta.getColor(R.styleable.MultiWaveHeader_mwhCloseColor, 0xFF065986);
        mColorAlpha = ta.getFloat(R.styleable.MultiWaveHeader_mwhColorAlpha, 0.45f);
        mProgress = ta.getFloat(R.styleable.MultiWaveHeader_mwhProgress, 1f);
        mVelocity = ta.getFloat(R.styleable.MultiWaveHeader_mwhVelocity, 1f);
        mGradientAngle = ta.getInt(R.styleable.MultiWaveHeader_mwhGradientAngle, 45);
        mIsRunning = ta.getBoolean(R.styleable.MultiWaveHeader_mwhIsRunning, true);

        ta.recycle();

    }

    private void initWaves(int w, int h) {
        WaveNew waveNew1 = new WaveNew(0, 5, w, h,
                (int) (mAmplitude * 0.8f), crest, true, colorOrange, mColorAlpha);
        WaveNew waveNew2 = new WaveNew(0, 5, w, h,
                (int) (mAmplitude * 0.8f), crest, false, colorBlue, mColorAlpha);
        WaveNew waveNew3 = new WaveNew(200, 8, w, h,
                mAmplitude, crest, false, colorCambridgeBlue, mColorAlpha);
        WaveNew waveNew4 = new WaveNew(200, 8, w, h,
                (int) (mAmplitude * 0.3f), crest, false, Color.WHITE, mColorAlpha);
        mltWave.add(waveNew1);
        mltWave.add(waveNew2);
        mltWave.add(waveNew3);
        mltWave.add(waveNew4);
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        updateWavePath(w, h);
        updateLinearGradient(w, h);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        if (mltWave.size() > 0 && mPaint != null) {
            View thisView = this;
            int height = getWaterHeight();
            long thisTime = System.currentTimeMillis();
            long diffTime = thisTime - mLastTime;
            Log.d(TAG, String.format("diffTime:%d", diffTime));
            canvas.save();
            canvas.drawPath(water.getPath(), waterPaint);
            canvas.restore();
            for (int i = 0; i < mltWave.size(); i++) {
                WaveNew wave = mltWave.get(i);
                canvas.save();
                if (mLastTime > 0 && wave.getVelocity() != 0) {
                    float offsetXd = wave.getVelocity() * mVelocity * diffTime / 1000f;
                    wave.moveX(offsetXd);
                    //canvas.translate(0, 0 - (1 - mProgress) * height);
                } else {
                    //canvas.translate(0, -wave.getOffsetX() - (1 - mProgress) * height);
                }
                canvas.drawPath(wave.getPath(), wave.getPaint());
                canvas.restore();
            }
            mLastTime = thisTime;
        }
        if (mIsRunning) {
            invalidate();
            //Log.d(TAG, String.format("call invalidate %d", System.currentTimeMillis()));
        }
    }

    private void updateLinearGradient(int width, int height) {
        int startColor = ColorUtils.setAlphaComponent(mStartColor, (int) (mColorAlpha * 255));
        int startColor2 = Color.parseColor("#eff7b6");
        startColor2 = ColorUtils.setAlphaComponent(startColor2, (int) (mColorAlpha * 255));
        int closeColor2 = Color.parseColor("#885353");
        closeColor2 = ColorUtils.setAlphaComponent(closeColor2, (int) (mColorAlpha * 255));

        int startColor3 = Color.parseColor("#eff7b6");
        startColor3 = ColorUtils.setAlphaComponent(startColor3, (int) (mColorAlpha * 255));

        int closeColor3 = Color.parseColor("#00ff00");
        closeColor3 = ColorUtils.setAlphaComponent(closeColor3, (int) (mColorAlpha * 255));

        int closeColor = ColorUtils.setAlphaComponent(mCloseColor, (int) (mColorAlpha * 255));

        for (WaveNew waveNew : mltWave) {
            waveNew.setAlpha(mColorAlpha);
        }
        //noinspection UnnecessaryLocalVariable
        double w = width;
        double h = height * mProgress;
        double r = Math.sqrt(w * w + h * h) / 2;
        double y = r * Math.sin(2 * Math.PI * mGradientAngle / 360);
        double x = r * Math.cos(2 * Math.PI * mGradientAngle / 360);
        mPaint.setShader(new LinearGradient((int) (w / 2 - x), (int) (h / 2 - y), (int) (w / 2 + x), (int) (h / 2 + y), startColor, closeColor, Shader.TileMode.CLAMP));
        mPaint2.setShader(new LinearGradient((int) (w / 2 - x), (int) (h / 2 - y), (int) (w / 2 + x), (int) (h / 2 + y), startColor2, closeColor2, Shader.TileMode.CLAMP));
        mPaint3.setShader(new LinearGradient((int) (w / 2 - x), (int) (h / 2 - y), (int) (w / 2 + x), (int) (h / 2 + y), startColor3, closeColor3, Shader.TileMode.CLAMP));
        waterPaint.setShader(new LinearGradient(0, getWaterHeight(), 0, getWaterHeight() - 120, new int[]{mStartColor2, Color.BLACK}, null, Shader.TileMode.CLAMP));
    }

    private void updateWavePath(int w, int h) {
        water.createPath(w, h);
        mltWave.clear();
        initWaves(w, h);
    }

    public void setWaves(String waves) {
        setTag(waves);
        if (mLastTime > 0) {
            View thisView = this;
            updateWavePath(thisView.getWidth(), getWaterHeight());
        }
    }

    public int getAmplitude() {
        return mAmplitude;
    }

    public void setAmplitude(int amplitude) {
        this.mAmplitude = Util.dp2px(amplitude);
        if (!mltWave.isEmpty()) {
            View thisView = this;
            updateWavePath(thisView.getWidth(), getWaterHeight());
        }
    }

    public float getVelocity() {
        return mVelocity;
    }

    public void setVelocity(float velocity) {
        this.mVelocity = velocity;
    }

    public float getProgress() {
        return mProgress;
    }

    public void setProgress(float progress) {
        waterHeight = (int) ((1 - mProgress) * getHeight());
        this.mProgress = progress;
        if (mPaint != null) {
            View thisView = this;
            updateLinearGradient(thisView.getWidth(), getWaterHeight());
            updateWavePath(getWidth(), getWaterHeight());
        }
    }

    public int getGradientAngle() {
        return mGradientAngle;
    }

    public void setGradientAngle(int angle) {
        this.mGradientAngle = angle;
        if (!mltWave.isEmpty()) {
            View thisView = this;
            updateLinearGradient(thisView.getWidth(), getWaterHeight());
        }
    }

    public int getStartColor() {
        return mStartColor;
    }

    public void setStartColor(int color) {
        this.mStartColor = color;
        if (!mltWave.isEmpty()) {
            View thisView = this;
            updateLinearGradient(thisView.getWidth(), getWaterHeight());
        }
    }

    public void setStartColorId(@ColorRes int colorId) {
        final View thisView = this;
        setStartColor(Util.getColor(thisView.getContext(), colorId));
    }

    public int getCloseColor() {
        return mCloseColor;
    }

    public void setCloseColor(int color) {
        this.mCloseColor = color;
        if (!mltWave.isEmpty()) {
            View thisView = this;
            updateLinearGradient(thisView.getWidth(), getWaterHeight());
        }
    }

    public void setCloseColorId(@ColorRes int colorId) {
        final View thisView = this;
        setCloseColor(Util.getColor(thisView.getContext(), colorId));
    }

    public float getColorAlpha() {
        return mColorAlpha;
    }

    public void setColorAlpha(float alpha) {
        this.mColorAlpha = alpha;
        if (!mltWave.isEmpty()) {
            View thisView = this;
            updateLinearGradient(thisView.getWidth(), getWaterHeight());
        }
    }

    public void start() {
        if (!mIsRunning) {
            mIsRunning = true;
            mLastTime = System.currentTimeMillis();
            invalidate();
        }
    }

    public void stop() {
        mIsRunning = false;
    }

    public boolean isRunning() {
        return mIsRunning;
    }

    public int getCrest() {
        return crest;
    }

    public void setCrest(int crest) {
        this.crest = crest;
        if (!mltWave.isEmpty()) {
            updateWavePath(this.getWidth(), getWaterHeight());
        }
    }

    public int getWaterHeight() {
        return waterHeight;
    }
}

package com.scwang.wave;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.support.v4.graphics.ColorUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Float.parseFloat;

/**
 * 多重水波纹
 * Created by SCWANG on 2017/12/11.
 */
@SuppressWarnings("unused")
public class MultiWaveHeader extends ViewGroup {

    private Paint mPaint = new Paint();
    private Matrix mMatrix = new Matrix();
    private List<Wave> mltWave = new ArrayList<>();
    private int mWaveHeight;
    private int mStartColor;
    private int mCloseColor;
    private float mAlphaColor;
    private long mLastTime = 0;

    public MultiWaveHeader(Context context) {
        this(context, null, 0);
    }

    public MultiWaveHeader(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MultiWaveHeader(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mPaint.setAntiAlias(true);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.MultiWaveHeader);

        mWaveHeight = ta.getDimensionPixelOffset(R.styleable.MultiWaveHeader_mwhWaveHeight, Util.dp2px(50));
        mStartColor = ta.getColor(R.styleable.MultiWaveHeader_mwhStartColor, 0xff1372CF);
        mCloseColor = ta.getColor(R.styleable.MultiWaveHeader_mwhCloseColor, 0xFF40B5FF);
        mAlphaColor = ta.getFloat(R.styleable.MultiWaveHeader_mwhAlphaColor, 0.3f);

        if (ta.hasValue(R.styleable.MultiWaveHeader_mwhWaves)) {
            setTag(ta.getString(R.styleable.MultiWaveHeader_mwhWaves));
        } else if (getTag() == null) {
            setTag("70,25,1.4,1.4,-26\n" +
                    "100,5,1.4,1.2,15\n" +
                    "420,0,1.15,1,-10\n" +
                    "520,10,1.7,1.5,20\n" +
                    "220,0,1,1,-15");
        }

        ta.recycle();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        int count = getChildCount();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                View child = getChildAt(i);
                if (child instanceof Wave) {
                    child.setVisibility(GONE);
                } else {
                    throw new RuntimeException("只能用Wave作为子视图，You can only use Wave as a subview.");
                }
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        updateWavePath(w,h);
    }

    private void updateWavePath(int w, int h) {
        int waveHeight = mWaveHeight;

        int startColor = ColorUtils.setAlphaComponent(mStartColor, (int)(mAlphaColor*255));
        int closeColor = ColorUtils.setAlphaComponent(mCloseColor, (int)(mAlphaColor*255));
        mPaint.setShader(new LinearGradient(0, 0, w, w, startColor, closeColor, Shader.TileMode.CLAMP));

        mltWave.clear();

        int count = getChildCount();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                Wave wave = (Wave) getChildAt(i);
                wave.updateWavePath(w, h, waveHeight);
                mltWave.add(wave);
            }
        } else if (getTag() instanceof String) {
            String[] waves = getTag().toString().split("\\s+");
            for (String twave : waves) {
                String[] args = twave.split ("\\s*,\\s*");
                if (args.length == 5) {
                    mltWave.add(new Wave(getContext(),Util.dp2px(parseFloat(args[0])), Util.dp2px(parseFloat(args[1])), Util.dp2px(parseFloat(args[4])), parseFloat(args[2]), parseFloat(args[3]), w, h, waveHeight/2));
                }
            }
        } else {
            mltWave.add(new Wave(getContext(),Util.dp2px(50), Util.dp2px(0), Util.dp2px(5), 1.7f, 2f, w, h, waveHeight/2));
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (mltWave.size() > 0) {
            long thisTime = System.currentTimeMillis();
            for (Wave wave : mltWave) {
                mMatrix.reset();
                canvas.save();
                if (mLastTime > 0 && wave.velocity != 0) {
                    int offsetX = (wave.offsetX + (int) (wave.velocity * (thisTime - mLastTime) / 1000f));
                    if (wave.velocity > 0) {
                        offsetX %= wave.width / 2;
                    } else {
                        while (offsetX < 0) {
                            offsetX += (wave.width / 2);
                        }
                    }
                    mMatrix.setTranslate(offsetX, 0);
                    canvas.translate(-offsetX, -wave.offsetY);
                } else{
                    mMatrix.setTranslate(wave.offsetX, 0);
                    canvas.translate(-wave.offsetX, -wave.offsetY);
                }
                mPaint.getShader().setLocalMatrix(mMatrix);
                canvas.drawPath(wave.path, mPaint);
                canvas.restore();
            }
            if (mLastTime == 0) {
                mLastTime = thisTime;
            }
            invalidate();
        }
    }

}
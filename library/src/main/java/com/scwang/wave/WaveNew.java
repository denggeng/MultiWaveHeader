package com.scwang.wave;

import android.graphics.Paint;
import android.graphics.Path;
import android.support.v4.graphics.ColorUtils;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * 水波对象
 * Created by Kenny on 2017/12/11.
 */
public class WaveNew {

    private final String TAG = "WaveNew";

    private Path path;          //水波路径
    private int width;          //画布宽度
    private int height;
    private int amplitude;           //波幅（振幅）
    private boolean moveRight;    //是否向右移动
    private float offsetX;        //水波的水平偏移量
    private float offsetY;        //水波的竖直偏移量
    private float velocity;       //水波移动速度（像素/秒）
    private float periodNumber = 1; //宽度内的周期个数

    private float alpha;

    //颜色
    private int color;

    //步长，以减少计算量
    private int stepSize = 2;

    /**
     * 使用一个波峰的正弦波压缩
     */
    private boolean compress = true;

    private float omega;

    private Paint paint = new Paint();

    /**
     * 缓存压缩函数的点阵比例，比如在x=20的点，对y的压缩比例
     */
    private Map<Integer, Float> compressPointMap = new HashMap<>();


    /**
     * 通过参数构造一个水波对象
     *
     * @param offsetX   水平偏移量
     * @param velocity  移动速度（像素/秒）
     * @param w         波长
     * @param h         画布高度
     * @param amplitude 振幅（波高度）
     */
    WaveNew(int offsetX, int velocity, int w, int h,
            int amplitude, float periodNumber, boolean moveRight, int color, float alpha) {
        this.width = w; //画布宽度
        this.height = h;
        this.amplitude = amplitude; //振幅
        this.offsetX = offsetX;     //水平偏移量
        this.velocity = velocity;   //移动速度（像素/秒）
        this.periodNumber = periodNumber;
        this.moveRight = moveRight;
        this.color = color;
        this.omega = (float) (2 * Math.PI / (width / periodNumber));
        this.alpha = alpha;
        this.paint.setAntiAlias(true);
        this.paint.setColor(ColorUtils.
                setAlphaComponent(color, (int) (alpha * 255)));
        initCompressingMap();
        buildWavePath();
    }

    private void buildWavePath() {
        //一个dp在当前设备表示的像素量（水波的绘制精度设为一个dp单位）
        path = new Path();
        path.moveTo(0, height);
        for (int x = 0; x <= width; x += stepSize) {
            float oldY = calcNormalY(x);
            float compressedY = oldY;
            if (compress) {
                compressedY = compressing(x, (int) oldY);
            }
            //Log.d(TAG, String.format("width:%d,height:%d,amplitude:%d,oldY:%f,comY:%f", width, height, amplitude, oldY, compressedY));
            path.lineTo(x, compressedY);
        }
        //处理最后一点,防止因为步长而导致最后一点不能到达width的情况
        float oldY = calcNormalY(width);
        float compressedY = oldY;
        if (compress) {
            compressedY = compressing(width, (int) oldY);
        }
        path.lineTo(width, compressedY);
        //回到起点
        path.lineTo(0, height);
        path.close();
    }

    /**
     * 计算x对应的y点
     * 正弦型函数解析式：y=Asin(ωx+φ)+b
     * 各常数值对函数图像的影响：
     * φ：决定波形与X轴位置关系或横向移动距离（左加右减）->offsetX
     * ω：决定周期（最小正周期T=2π/∣ω∣） ->omega
     * A：决定峰值（即纵向拉伸压缩的倍数） -> amplitude
     * b：表示波形在Y轴的位置关系或纵向移动距离（上加下减） ->  height
     *
     * @param x
     * @return
     */
    private float calcNormalY(int x) {
        return (float) (-amplitude * Math.sin(omega * x + offsetX) + height);
    }

    public void moveX(float dx) {
        if (moveRight) {
            offsetX += Math.abs(dx);
        } else {
            offsetX -= Math.abs(dx);
        }
        if (Math.abs(offsetX) > width) {
            offsetX = offsetY % width;
        }
        //Log.d(TAG, String.format("wave:%s,moveRight:%s,offsetX:%f", this, moveRight, offsetX));
        buildWavePath();
    }

    private float compressing(int x, int y) {
        //y减去高度后计算再加回来
        int usedY = y - height;
        //大于width一半的x,映射为左边的x
        int usedX = 2 * x > width ? width - x : x;
        float outY = compressPointMap.get(usedX) * usedY + height;
        //Log.d(TAG, String.format("map.size:%d,x:%d,usedX:%d,outY:%f", compressPointMap.size(), x, usedX, outY));
        return outY;
    }

    private void initCompressingMap() {
        //使用只有一个波峰的sin函数sin
        float compressOmega = (float) (2 * Math.PI / (width * 2));
        for (int i = 0; i <= width / 2; i++) {
            float outerSinY = (float) (-amplitude * Math.sin(compressOmega * i));
            float rateY = -outerSinY / amplitude;
            Log.d(TAG, String.format("x:%d,rateY:%f", i, rateY));
            compressPointMap.put(i, rateY);
        }
    }

    public Path getPath() {
        return path;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getAmplitude() {
        return amplitude;
    }

    public boolean isMoveRight() {
        return moveRight;
    }

    public float getOffsetX() {
        return offsetX;
    }

    public float getOffsetY() {
        return offsetY;
    }

    public float getVelocity() {
        return velocity;
    }

    public float getPeriodNumber() {
        return periodNumber;
    }

    public void setOffsetX(float offsetX) {
        this.offsetX = offsetX;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }

    public float getAlpha() {
        return alpha;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
        this.paint.setColor(ColorUtils.
                setAlphaComponent(color, (int) (alpha * 255)));
        initCompressingMap();
    }
}
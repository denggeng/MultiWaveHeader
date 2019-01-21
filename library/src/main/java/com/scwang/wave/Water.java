package com.scwang.wave;

import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;

/**
 * 水波背景的水
 */
public class Water {

    private Path path;

    private int color;

    private int deep = 120;

    private Paint paint = new Paint();

    public Water(int color, int width, int height, int deep) {
        this.color = color;
        this.deep = deep;
        paint.setShader(new LinearGradient(0, height, 0, height - deep,
                color, Color.BLACK, Shader.TileMode.CLAMP));
        createPath(width, height);
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getDeep() {
        return deep;
    }

    public void setDeep(int deep) {
        this.deep = deep;
    }

    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }

    public void createPath(int width, int height) {
        path = new Path();
        int startY = height - deep;
        path.moveTo(0, startY);
        path.lineTo(0, height);
        path.lineTo(width, height);
        path.lineTo(width, startY);
        path.lineTo(0, startY);
        path.close();
    }
}

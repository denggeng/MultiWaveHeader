package com.scwang.wave;

import android.graphics.Path;

/**
 * 水波背景的水
 */
public class Water {

    private Path path;

    private int color;

    private int deep = 120;

    public Water(int color, int width, int height, int deep) {
        this.color = color;
        this.deep = deep;
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

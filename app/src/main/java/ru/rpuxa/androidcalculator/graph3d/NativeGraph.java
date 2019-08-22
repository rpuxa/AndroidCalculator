package ru.rpuxa.androidcalculator.graph3d;

import android.graphics.Canvas;
import android.graphics.Paint;

public final class NativeGraph {

    static {
        System.loadLibrary("graph");
    }
/*

    private static float[] vertices;
    private static native void draw(
            float cameraX,
            float cameraY,
            float cameraZ,
            float horizontalAngle,
            float verticalAngle,
            float cameraWidth,
            float cameraHeight,
            float screenWidth,
            float screenHeight,
            float focus,
            int polygonsAxisCount,
            float polygonSize,
            float polygonStart,
            float[] graph,
            float[] vertices);*/

    public static native void draw(Canvas canvas, Paint paint, Camera camera, float[] graph, Graph3dParams params, Canvas.VertexMode mode);

}
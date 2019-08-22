#include <math.h>
#include <malloc.h>
#include <jni.h>
#include <time.h>

#define NaN 0.0f / 0.0f

struct polygon {
    short x;
    short y;
    float distanceToCamera;
};

float *mult_matrix(float *first, float *second) {
    float *matrix = (float *) malloc(9 * sizeof(float));
    for (int i = 0; i < 9; ++i) {
        int row = i / 3;
        int column = i % 3;
        float sum = 0;
        for (int j = 0; j < 3; ++j) {
            sum += first[3 * row + j] * second[column + 3 * j];
        }
        matrix[i] = sum;
    }

    return matrix;
}


void project_points(
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
        float *points,
        float pointsCount) {

    float cosTmp, sinTmp;
    float halfScreenWidth = screenWidth / 2;
    float halfScreenHeight = screenHeight / 2;

    cosTmp = cosf(horizontalAngle);
    sinTmp = sinf(horizontalAngle);
    float rotateZ[9] = {
            cosTmp, -sinTmp, 0,
            sinTmp, cosTmp, 0,
            0, 0, 1
    };

    cosTmp = cosf(verticalAngle);
    sinTmp = sinf(verticalAngle);
    float rotateX[9] = {
            1, 0, 0,
            0, cosTmp, -sinTmp,
            0, sinTmp, cosTmp
    };


    float *resultMatrix = mult_matrix(rotateZ, rotateX);

    float resizeWidth = screenWidth / cameraWidth;
    float resizeHeight = -screenHeight / cameraHeight;

    float a11 = resultMatrix[0] * resizeWidth,
            a12 = resultMatrix[1],
            a13 = resultMatrix[2] * resizeHeight,

            a21 = resultMatrix[3] * resizeWidth,
            a22 = resultMatrix[4],
            a23 = resultMatrix[5] * resizeHeight,

            a31 = resultMatrix[6] * resizeWidth,
            a32 = resultMatrix[7],
            a33 = resultMatrix[8] * resizeHeight;

    free(resultMatrix);


    for (int i = 0; i < pointsCount; ++i) {
        float x = points[0] - cameraX;
        float y = points[1] - cameraY;
        float z = points[2] - cameraZ;
        float newX = a11 * x + a21 * y + a31 * z;
        float newY = a12 * x + a22 * y + a32 * z;
        float newZ = a13 * x + a23 * y + a33 * z;

        if (newY < 0) {
              points[0] = NaN;
              points[1] = NaN;
              points[2] = NaN;
        } else {
            float tmp = focus / (newY + focus);
            float __x = newX * tmp + halfScreenWidth;
            float __y = newZ * tmp + halfScreenHeight;
            points[0] = __x;
            points[1] = __y;
        }

        points += 3;
    }

}


void merge_sort(struct polygon *array, int size) {
    if (size == 1)
        return;

    int firstSize = size >> 1;
    int secondSize = size - firstSize;
    struct polygon *first = array;
    struct polygon *second = array + firstSize;
    merge_sort(first, firstSize);
    merge_sort(second, secondSize);

    int index = 0;
    struct polygon result[size];

    while (firstSize != 0 || secondSize != 0) {
        struct polygon firstValue = *first, secondValue = *second;
        if (secondSize == 0 ||
            firstSize != 0 && firstValue.distanceToCamera > secondValue.distanceToCamera) {
            first++;
            firstSize--;
            result[index++] = firstValue;
            continue;
        }
        second++;
        secondSize--;
        result[index++] = secondValue;
    }

    for (int i = 0; i < size; ++i) {
        array[i] = result[i];
    }
}

float sqr(float x) {
    return x * x;
}

/*
 *float array[48];
for (int i = 0; i < 48; ++i) {
    array[i] = newPoints[i];
}
array;
 */

JNIEXPORT jdouble JNICALL
Java_ru_rpuxa_androidcalculator_graph3d_NativeGraph_draw(
        JNIEnv *env,
        jclass type,
        jobject canvas,
        jobject paint,
        jobject camera,
        jfloatArray graph,
        jobject params,
        jobject vertexMode) {
    JNIEnv e = *env;


    // Canvas
    jclass canvasClazz = e->FindClass(env, "android/graphics/Canvas");
    jmethodID drawARGB = e->GetMethodID(env, canvasClazz, "drawARGB", "(IIII)V");
    jmethodID drawVertices = e->GetMethodID(env, canvasClazz, "drawVertices",
                                            "(Landroid/graphics/Canvas$VertexMode;I[FI[FI[II[SIILandroid/graphics/Paint;)V");

    //Camera
    jclass cameraClazz = e->FindClass(env, "ru/rpuxa/androidcalculator/graph3d/Camera");
    jmethodID getX = e->GetMethodID(env, cameraClazz, "getX", "()F");
    jmethodID getY = e->GetMethodID(env, cameraClazz, "getY", "()F");
    jmethodID getZ = e->GetMethodID(env, cameraClazz, "getZ", "()F");
    jmethodID getHorizontalAngle = e->GetMethodID(env, cameraClazz, "getHorizontalAngle",
                                                  "()F");
    jmethodID getVerticalAngle = e->GetMethodID(env, cameraClazz, "getVerticalAngle", "()F");
    jmethodID getCameraWidth = e->GetMethodID(env, cameraClazz, "getCameraWidth", "()F");
    jmethodID getCameraHeight = e->GetMethodID(env, cameraClazz, "getCameraHeight", "()F");
    jmethodID getScreenWidth = e->GetMethodID(env, cameraClazz, "getScreenWidth", "()F");
    jmethodID getScreenHeight = e->GetMethodID(env, cameraClazz, "getScreenHeight", "()F");
    jmethodID getFocus = e->GetMethodID(env, cameraClazz, "getFocus", "()F");

    float cameraX = e->CallFloatMethod(env, camera, getX);
    float cameraY = e->CallFloatMethod(env, camera, getY);
    float cameraZ = e->CallFloatMethod(env, camera, getZ);
    float horizontalAngle = e->CallFloatMethod(env, camera, getHorizontalAngle);
    float verticalAngle = e->CallFloatMethod(env, camera, getVerticalAngle);
    float cameraWidth = e->CallFloatMethod(env, camera, getCameraWidth);
    float cameraHeight = e->CallFloatMethod(env, camera, getCameraHeight);
    float screenWidth = e->CallFloatMethod(env, camera, getScreenWidth);
    float screenHeight = e->CallFloatMethod(env, camera, getScreenHeight);
    float focus = e->CallFloatMethod(env, camera, getFocus);

    // Graph Params
    jclass paramsClass = e->FindClass(env, "ru/rpuxa/androidcalculator/graph3d/Graph3dParams");
    jmethodID getPolygonsCount = e->GetMethodID(env, paramsClass, "getPolygonsCount", "()I");
    jmethodID getPolygonSize = e->GetMethodID(env, paramsClass, "getPolygonSize", "()F");
    jmethodID getPolygonStart = e->GetMethodID(env, paramsClass, "getPolygonStart", "()F");

    int polygonsAxisCount = e->CallIntMethod(env, params, getPolygonsCount);
    float polygonSize = e->CallFloatMethod(env, params, getPolygonSize);
    float polygonStart = e->CallFloatMethod(env, params, getPolygonStart);

    //////////////////////////////////////////////

    int totalPolygonsCount = polygonsAxisCount * polygonsAxisCount;
    struct polygon polygons[totalPolygonsCount];

    for (short x = 0; x < polygonsAxisCount; ++x) {
        for (short y = 0; y < polygonsAxisCount; ++y) {
            struct polygon *p = polygons + (polygonsAxisCount * x + y);
            p->x = x;
            p->y = y;
            p->distanceToCamera = sqr((x + 0.5f) * polygonSize + polygonStart - cameraX) +
                                  sqr((y + 0.5f) * polygonSize + polygonStart - cameraY);
        }
    }

    merge_sort(polygons, totalPolygonsCount);


    jfloat *points = e->GetFloatArrayElements(env, graph, NULL);
    jint pointsCount = e->GetArrayLength(env, graph);


    project_points(cameraX, cameraY, cameraZ, horizontalAngle, verticalAngle, cameraWidth,
                   cameraHeight, screenWidth, screenHeight, focus, points, pointsCount / 3);


    int newPointsIndex = 0;
    int newPointsSize = totalPolygonsCount * 12;
    jfloatArray jNewPoints = e->NewFloatArray(env, newPointsSize);
    float *newPoints = e->GetFloatArrayElements(env, jNewPoints, NULL);

    int colorsSize = newPointsSize;
    jfloatArray jColors = e->NewIntArray(env, colorsSize);
    int *colorsPointer = e->GetIntArrayElements(env, jColors, NULL);
    int *colors = colorsPointer;

    float minZ = MAXFLOAT;
    float maxZ = -MAXFLOAT;
    for (int i = 2; i < pointsCount / 3; i += 3) {
        float z = points[i];
        if (z < minZ) minZ = z;
        if (z > maxZ) maxZ = z;
    }

    maxZ -= minZ;


    for (int i = 0; i < totalPolygonsCount; ++i) {
        struct polygon polygon = polygons[i];
        int x = polygon.x;
        int y = polygon.y;

        int point = (y + (polygonsAxisCount + 1) * x) * 3;

        float x1 = points[point];
        float y1 = points[point + 1];
        float z1 = points[point + 2];

        float x2 = points[point + 3];
        float y2 = points[point + 4];
        float z2 = points[point + 5];

        point += (polygonsAxisCount + 1) * 3;
        float x3 = points[point];
        float y3 = points[point + 1];
        float z3 = points[point + 2];

        float x4 = points[point + 3];
        float y4 = points[point + 4];
        float z4 = points[point + 5];

        newPoints[newPointsIndex] = x1;
        newPoints[newPointsIndex + 1] = y1;
        newPoints[newPointsIndex + 2] = x2;
        newPoints[newPointsIndex + 3] = y2;
        newPoints[newPointsIndex + 4] = x3;
        newPoints[newPointsIndex + 5] = y3;

        newPoints[newPointsIndex + 6] = x2;
        newPoints[newPointsIndex + 7] = y2;
        newPoints[newPointsIndex + 8] = x3;
        newPoints[newPointsIndex + 9] = y3;
        newPoints[newPointsIndex + 10] = x4;
        newPoints[newPointsIndex + 11] = y4;
        newPointsIndex += 12;


        // Color


        float zArray[6] = {z1, z2, z3, z2, z3, z4};
        for (int k = 0; k < 6; ++k) {
            float z = zArray[k];
            float percentFloat = 255 * (z - minZ) / maxZ;
            int percent;
            if (percentFloat < 0) {
                percent = 0;
            } else if (percentFloat > 255) {
                percent = 255;
            } else {
                percent = (int) percentFloat;
            }
            int color = (255 << 24) | (percent << 16) | (0 << 8) | (255 - percent);
            colors[k] = color;
        }
        colors += 6;
    }

    e->ReleaseFloatArrayElements(env, jNewPoints, newPoints, 0);
    e->ReleaseIntArrayElements(env, jColors, colorsPointer, 0);

    // Draw canvas
    e->CallVoidMethod(env, canvas, drawARGB, 255, 255, 255, 255);
    auto startTime = clock();

    e->CallVoidMethod(env, canvas, drawVertices,
                      vertexMode, // mode
                      newPointsSize, // vertexCount
                      jNewPoints, // verts
                      0, //vertOffset
                      NULL, // texs
                      0, // texOffset
                      jColors, // colors
                      0, // colorOffset
                      NULL, // indices
                      0, // indexOffset
                      0, // indexCount
                      paint // paint
    );
    auto endTime = clock();


    return (((double) (endTime - startTime)) / CLOCKS_PER_SEC);
}
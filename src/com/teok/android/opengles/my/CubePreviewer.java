package com.teok.android.opengles.my;

import android.app.Activity;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import com.teok.android.R;
import com.teok.android.common.ULog;
import com.teok.android.opengles.common.RawResourceReader;
import com.teok.android.opengles.common.ShaderHelper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class CubePreviewer extends GLSurfaceViewActivity implements View.OnTouchListener {

    private static final String TAG = "CubePreviewer";

    private CubeRenderer mRenderer;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGLSurfaceView.setOnTouchListener(this);
    }

    @Override
    protected GLSurfaceView.Renderer getRenderer() {
        mRenderer = new CubeRenderer(this);
        return mRenderer;
    }

    class CubeRenderer implements GLSurfaceView.Renderer {
        /** How many bytes per float. */
        private final int BYTES_PER_FLOAT = 4;

        /** Size of the position data in elements. */
        private final int POSITION_DATA_SIZE = 3;

        /** Size of the color data in elements. */
        private final int COLOR_DATA_SIZE = 4;

        /** Store our model data in a float buffer. */
        private final FloatBuffer mCubePositions;
        private final FloatBuffer mCubeColors;

        /**
         * Store the model matrix. This matrix is used to move models from object space (where each model can be thought
         * of being located at the center of the universe) to world space.
         */
        private float[] mModelMatrix = new float[16];

        /**
         * Store the view matrix. This can be thought of as our camera. This matrix transforms world space to eye space;
         * it positions things relative to our eye.
         */
        private float[] mViewMatrix = new float[16];

        /** Store the projection matrix. This is used to project the scene onto a 2D viewport. */
        private float[] mProjectionMatrix = new float[16];

        /** Allocate storage for the final combined matrix. This will be passed into the shader program. */
        private float[] mMVPMatrix = new float[16];

        // Pointers
        /** This will be used to pass in the transformation matrix. */
        private int mMVPMatrixHandle;

        /** This will be used to pass in the modelview matrix. */
        private int mMVMatrixHandle;

        /** This will be used to pass in model position information. */
        private int mPositionHandle;

        /** This will be used to pass in model color information. */
        private int mColorHandle;

        /** This is a handle to our cube shading program. */
        private int mProgramHandle;

        private Activity mActivityContext;

        private ScaleGestureDetector mScaleDetector;

        private float mScaleFactor = 1.f;

        public CubeRenderer(Activity activityContext) {
            mActivityContext = activityContext;

            // Define points for a cube.

            // X, Y, Z
            final float[] cubePositionData =
                    {
                            // In OpenGL counter-clockwise winding is default. This means that when we look at a triangle,
                            // if the points are counter-clockwise we are looking at the "front". If not we are looking at
                            // the back. OpenGL has an optimization where all back-facing triangles are culled, since they
                            // usually represent the backside of an object and aren't visible anyways.

                            // Front face
                            -1.0f, 1.0f, 1.0f,
                            -1.0f, -1.0f, 1.0f,
                            1.0f, 1.0f, 1.0f,
                            -1.0f, -1.0f, 1.0f,
                            1.0f, -1.0f, 1.0f,
                            1.0f, 1.0f, 1.0f,

                            // Right face
                            1.0f, 1.0f, 1.0f,
                            1.0f, -1.0f, 1.0f,
                            1.0f, 1.0f, -1.0f,
                            1.0f, -1.0f, 1.0f,
                            1.0f, -1.0f, -1.0f,
                            1.0f, 1.0f, -1.0f,

                            // Back face
                            1.0f, 1.0f, -1.0f,
                            1.0f, -1.0f, -1.0f,
                            -1.0f, 1.0f, -1.0f,
                            1.0f, -1.0f, -1.0f,
                            -1.0f, -1.0f, -1.0f,
                            -1.0f, 1.0f, -1.0f,

                            // Left face
                            -1.0f, 1.0f, -1.0f,
                            -1.0f, -1.0f, -1.0f,
                            -1.0f, 1.0f, 1.0f,
                            -1.0f, -1.0f, -1.0f,
                            -1.0f, -1.0f, 1.0f,
                            -1.0f, 1.0f, 1.0f,

                            // Top face
                            -1.0f, 1.0f, -1.0f,
                            -1.0f, 1.0f, 1.0f,
                            1.0f, 1.0f, -1.0f,
                            -1.0f, 1.0f, 1.0f,
                            1.0f, 1.0f, 1.0f,
                            1.0f, 1.0f, -1.0f,

                            // Bottom face
                            1.0f, -1.0f, -1.0f,
                            1.0f, -1.0f, 1.0f,
                            -1.0f, -1.0f, -1.0f,
                            1.0f, -1.0f, 1.0f,
                            -1.0f, -1.0f, 1.0f,
                            -1.0f, -1.0f, -1.0f,
                    };

            // R, G, B, A
            final float[] cubeColorData =
                    {
                            // Front face (red)
                            1.0f, 0.0f, 0.0f, 1.0f,
                            1.0f, 0.0f, 0.0f, 1.0f,
                            1.0f, 0.0f, 0.0f, 1.0f,
                            1.0f, 0.0f, 0.0f, 1.0f,
                            1.0f, 0.0f, 0.0f, 1.0f,
                            1.0f, 0.0f, 0.0f, 1.0f,

                            // Right face (green)
                            0.0f, 1.0f, 0.0f, 1.0f,
                            0.0f, 1.0f, 0.0f, 1.0f,
                            0.0f, 1.0f, 0.0f, 1.0f,
                            0.0f, 1.0f, 0.0f, 1.0f,
                            0.0f, 1.0f, 0.0f, 1.0f,
                            0.0f, 1.0f, 0.0f, 1.0f,

                            // Back face (blue)
                            0.0f, 0.0f, 1.0f, 1.0f,
                            0.0f, 0.0f, 1.0f, 1.0f,
                            0.0f, 0.0f, 1.0f, 1.0f,
                            0.0f, 0.0f, 1.0f, 1.0f,
                            0.0f, 0.0f, 1.0f, 1.0f,
                            0.0f, 0.0f, 1.0f, 1.0f,

                            // Left face (yellow)
                            1.0f, 1.0f, 0.0f, 1.0f,
                            1.0f, 1.0f, 0.0f, 1.0f,
                            1.0f, 1.0f, 0.0f, 1.0f,
                            1.0f, 1.0f, 0.0f, 1.0f,
                            1.0f, 1.0f, 0.0f, 1.0f,
                            1.0f, 1.0f, 0.0f, 1.0f,

                            // Top face (cyan)
                            0.0f, 1.0f, 1.0f, 1.0f,
                            0.0f, 1.0f, 1.0f, 1.0f,
                            0.0f, 1.0f, 1.0f, 1.0f,
                            0.0f, 1.0f, 1.0f, 1.0f,
                            0.0f, 1.0f, 1.0f, 1.0f,
                            0.0f, 1.0f, 1.0f, 1.0f,

                            // Bottom face (magenta)
                            1.0f, 0.0f, 1.0f, 1.0f,
                            1.0f, 0.0f, 1.0f, 1.0f,
                            1.0f, 0.0f, 1.0f, 1.0f,
                            1.0f, 0.0f, 1.0f, 1.0f,
                            1.0f, 0.0f, 1.0f, 1.0f,
                            1.0f, 0.0f, 1.0f, 1.0f
                    };

            // Initialize the buffers.
            mCubePositions = ByteBuffer.allocateDirect(cubePositionData.length * BYTES_PER_FLOAT)
                    .order(ByteOrder.nativeOrder()).asFloatBuffer();
            mCubePositions.put(cubePositionData).position(0);

            mCubeColors = ByteBuffer.allocateDirect(cubeColorData.length * BYTES_PER_FLOAT)
                    .order(ByteOrder.nativeOrder()).asFloatBuffer();
            mCubeColors.put(cubeColorData).position(0);

            mScaleDetector = new ScaleGestureDetector(activityContext, new ScaleListener());
        }

        protected String getVertexShader() {
            return RawResourceReader.readTextFileFromRawResource(mActivityContext, R.raw.per_pixel_vertex_shader2);
        }

        protected String getFragmentShader() {
            return RawResourceReader.readTextFileFromRawResource(mActivityContext, R.raw.per_pixel_fragment_shader2);
        }


        float xOffset = 0.0f;
        float yOffset = 0.0f;
        float zOffset = 0.0f;
        private final float TOUCH_SCALE_FACTOR = 180.0f / 320 / 2;

        private void updateLookAtM() {
            // Position the eye in front of the origin.
            final float eyeX = 1.0f;
            final float eyeY = 0.0f;
            final float eyeZ = -0.5f;

            // We are looking toward the distance
            final float lookX = 0.0f;
            final float lookY = 0.0f;
            final float lookZ = -5.0f;

            // Set our up vector. This is where our head would be pointing were we holding the camera.
            final float upX = 0.0f;
            final float upY = 1.0f;
            final float upZ = 0.0f;

            // Set the view matrix. This matrix can be said to represent the camera position.
            // NOTE: In OpenGL 1, a ModelView matrix is used, which is a combination of a model and
            // view matrix. In OpenGL 2, we can keep track of these matrices separately if we choose.
            Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);
        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            // Set the background clear color to black.
            GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

            // Use culling to remove back faces.
            GLES20.glEnable(GLES20.GL_CULL_FACE);

            // Enable depth testing
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);

            updateLookAtM();

            final String vertexShader = getVertexShader();
            final String fragmentShader = getFragmentShader();

            final int vertexShaderHandle = ShaderHelper.compileShader(GLES20.GL_VERTEX_SHADER, vertexShader);
            final int fragmentShaderHandle = ShaderHelper.compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader);

            mProgramHandle = ShaderHelper.createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle,
                    new String[] {"a_Position",  "a_Color"});
        }

        /**
         * Draws a cube.
         */
        private void drawCube()
        {
            // Pass in the position information
            mCubePositions.position(0);
            GLES20.glVertexAttribPointer(mPositionHandle, POSITION_DATA_SIZE, GLES20.GL_FLOAT, false,
                    0, mCubePositions);

            GLES20.glEnableVertexAttribArray(mPositionHandle);

            // Pass in the color information
            mCubeColors.position(0);
            GLES20.glVertexAttribPointer(mColorHandle, COLOR_DATA_SIZE, GLES20.GL_FLOAT, false,
                    0, mCubeColors);

            GLES20.glEnableVertexAttribArray(mColorHandle);

            // This multiplies the view matrix by the model matrix, and stores the result in the MVP matrix
            // (which currently contains model * view).
            Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);

            // Pass in the modelview matrix.
            GLES20.glUniformMatrix4fv(mMVMatrixHandle, 1, false, mMVPMatrix, 0);

            // This multiplies the modelview matrix by the projection matrix, and stores the result in the MVP matrix
            // (which now contains model * view * projection).
            Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);

            // Pass in the combined matrix.
            GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

            // Draw the cube.
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 36);
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            // Set the OpenGL viewport to the same size as the surface.
            GLES20.glViewport(0, 0, width, height);

            // Create a new perspective projection matrix. The height will stay the same
            // while the width will vary as per aspect ratio.
            final float ratio = (float) width / height;
            final float left = -ratio;
            final float right = ratio;
            final float bottom = -1.0f;
            final float top = 1.0f;
            final float near = 1.0f;
            final float far = 10.0f;

            Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
        }

        float mCubeX = 0.0f;
        float mCubeY = 0.0f;
        float mCubeZ = -7.0f;

        @Override
        public void onDrawFrame(GL10 gl) {
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

            // Do a complete rotation every 10 seconds.
            long time = SystemClock.uptimeMillis() % 10000L;
            float angleInDegrees = (360.0f / 10000.0f) * ((int) time);

            // Set our per-vertex lighting program.
            GLES20.glUseProgram(mProgramHandle);

            // Set program handles for cube drawing.
            mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_MVPMatrix");
            mMVMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_MVMatrix");
            mPositionHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Position");
            mColorHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Color");

            Matrix.setIdentityM(mModelMatrix, 0);
            Matrix.translateM(mModelMatrix, 0, mCubeX, mCubeY, mCubeZ * mScaleFactor);
            Matrix.rotateM(mModelMatrix, 0, angleInDegrees, 1.0f, 0.0f, 0.7f);
            drawCube();
        }

        /* Pointer coords */
        MotionEvent.PointerCoords mP0Coords = new MotionEvent.PointerCoords();
        MotionEvent.PointerCoords mP1Coords = new MotionEvent.PointerCoords();
        MotionEvent.PointerCoords mP2Coords = new MotionEvent.PointerCoords();

        /* Historical coords */
        MotionEvent.PointerCoords mH0Coords = new MotionEvent.PointerCoords();
        MotionEvent.PointerCoords mH1Coords = new MotionEvent.PointerCoords();
        MotionEvent.PointerCoords mH2Coords = new MotionEvent.PointerCoords();

        public void onTouchEvent(View v, MotionEvent event) {
            // Debug
//            ULog.d(TAG, "PointerCount=%s", event.getPointerCount());

            if (event.getAction() != MotionEvent.ACTION_MOVE) {
                return;
            }

            switch (event.getPointerCount()) {
                case 1:
                    // get coord of first pointer
                    event.getPointerCoords(0, mP0Coords);
                    // get last coord of first pointer
                    if (event.getHistorySize() != 0) {
                        int historyPos = Math.min(Math.abs(event.getHistorySize() - 1), event.getHistorySize());
                        event.getHistoricalPointerCoords(0, historyPos, mH0Coords);

                        // Deltas
                        float dX = mP0Coords.x - mH0Coords.x;
                        // Android screen coords is converse with OpenGL
                        float dY = mH0Coords.y - mP0Coords.y;

                        xOffset = dX;
                        yOffset = dY;

                        mCubeX += xOffset * TOUCH_SCALE_FACTOR;
                        mCubeY += yOffset * TOUCH_SCALE_FACTOR;

                        ULog.d(TAG, "posOffset(%f, %f, %f)", xOffset, yOffset, mCubeZ);
                    }
                    break;
                case 2:
                    mScaleDetector.onTouchEvent(event);
                    break;
                case 3:
                    break;
                default:
                    // Not support
                    break;
            }
        }

        class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                mScaleFactor *= detector.getScaleFactor();

                // Don't let the object get too small or too large.
                mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 2.0f));

                ULog.d(TAG, "mScaleFactor=%s, mCubeZ=%s", mScaleFactor, mCubeZ);
                return true;
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
//        ULog.d(TAG, String.format("x=%f, y=%f", event.getRawX(), event.getRawY()));
        if (mRenderer != null) {
            mRenderer.onTouchEvent(v, event);
        }
        return true;
    }

}
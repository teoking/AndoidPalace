package com.teok.android.opengles.my;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Bundle;
import com.teok.android.R;
import com.teok.android.opengles.common.RawResourceReader;
import com.teok.android.opengles.common.ShaderHelper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Show a 3D boing ball.
 * <a href="https://github.com/glfw/glfw/blob/master/examples/boing.c">Boing C implementation</a>
 */
public class BoingBall extends GLSurfaceViewActivity {

    /**
     * Store the view matrix. This can be thought of as our camera. This matrix transforms world space to eye space;
     * it positions things relative to our eye.
     */
    private float[] mViewMatrix = new float[16];

    /** Store the projection matrix. This is used to project the scene onto a 2D viewport. */
    private float[] mProjectionMatrix = new float[16];

    /** Allocate storage for the final combined matrix. This will be passed into the shader program. */
    private float[] mMVPMatrix = new float[16];

    /**
     * Store the model matrix. This matrix is used to move models from object space (where each model can be thought
     * of being located at the center of the universe) to world space.
     */
    private float[] mModelMatrix = new float[16];

    /** This will be used to pass in the transformation matrix. */
    private int mMVPMatrixHandle;

    /** This will be used to pass in model position information. */
    private int mPositionHandle;

    /** This will be used to pass in model color information. */
    private int mColorHandle;

    private int mProgramHandle;

    private FloatBuffer mGridCoordinates;
    private FloatBuffer mColor;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    protected GLSurfaceView.Renderer getRenderer() {
        return new BoingRenderer();
    }

    String getVertexShader() {
        return RawResourceReader.readTextFileFromRawResource(BoingBall.this, R.raw.basic_vertex_shader);
    }

    String getFragmentShader() {
        return  RawResourceReader.readTextFileFromRawResource(BoingBall.this, R.raw.basic_fragment_shader);
    }

    class BoingRenderer implements GLSurfaceView.Renderer {

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            GLES20.glClearColor(0.55f, 0.55f, 0.55f, 0.f);

            // Position the eye behind the origin.
            final float eyeX = 0.0f;
            final float eyeY = 0.0f;
            final float eyeZ = 1.5f;

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

            final String vertexShader = getVertexShader();
            final String fragmentShader = getFragmentShader();

            int vertexShaderHandle = ShaderHelper.compileShader(GLES20.GL_VERTEX_SHADER, vertexShader);
            int fragmentShaderHandle = ShaderHelper.compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader);

            mProgramHandle = ShaderHelper.createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle,
                    new String[] {"a_Position", "a_Color", "v_Color"});

            genGrid();
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

        public void genGrid() {
            float RADIUS = 0.5f;
            float STEP_LONGITUDE = 22.5f;
            float STEP_LATITUDE = 22.5f;
            float DIST_BALL = RADIUS * 1.0f + RADIUS * 0.1f;
            float VIEW_SCENE_DIST = DIST_BALL * 3.f + 200.f;
            float GRID_SIZE = RADIUS * 4.5f;
            float BOUNCE_HEIGHT = RADIUS * 2.1f;
            float BOUNCE_WIDTH = RADIUS * 2.1f;

            float SHADOW_OFFSET_X = -20.f;
            float SHADOW_OFFSET_Y = 10.f;
            float SHADOW_OFFSET_Z = 0.f;

            float WALL_L_OFFSET = 0.f;
            float WALL_R_OFFSET = 5.f;

            float ANIMATION_SPEED = 50.f;
            float MAX_DELTA_T = 0.02f;

            final int BYTES_PER_FLOAT = 4;

            int row, col;
            int rowTotal = 12;
            int colTotal = 12;
            float widthLine = 0.2f;
            float sizeCell = GRID_SIZE / rowTotal;
            float z_offset = 1.f;
            float xl, xr;
            float yt, yb;

            int coLen = colTotal * 3* 3 * 2;
            float[] gridCoordinates = new float[coLen];
            int idx = 0;

            for (col = 0; col < colTotal; col++) {
                xl = -GRID_SIZE / 2 + col * sizeCell;
                xr = xl + widthLine;

                yt =  GRID_SIZE / 2;
                yb = -GRID_SIZE / 2 - widthLine;

                idx = put(gridCoordinates, idx, xr, yt, z_offset);
                idx = put(gridCoordinates, idx, xl, yt, z_offset);
                idx = put(gridCoordinates, idx, xl, yb, z_offset);

                idx = put(gridCoordinates, idx, xr, yt, z_offset);
                idx = put(gridCoordinates, idx, xl, yb, z_offset);
                idx = put(gridCoordinates, idx, xr, yb, z_offset);
            }

            mGridCoordinates = ByteBuffer.allocateDirect(coLen * BYTES_PER_FLOAT)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer();
            mGridCoordinates.put(gridCoordinates).position(0);

            mColor = ByteBuffer.allocateDirect(4 * BYTES_PER_FLOAT)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer();
            mColor.put(new float[]{
                    CommonColors.RED[0],
                    CommonColors.RED[0],
                    CommonColors.RED[0],
                    1.0f
            }).position(0);
        }

        public void drawGrid() {
            GLES20.glUseProgram(mProgramHandle);

            //mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_MVPMatrix");
            mColorHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Color");
            mPositionHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Position");

            mGridCoordinates.position(0);
            GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false,
                    0, mGridCoordinates);

            GLES20.glEnableVertexAttribArray(mPositionHandle);

            // Pass in the color information
            mColor.position(0);
            GLES20.glVertexAttribPointer(mColorHandle, 4, GLES20.GL_FLOAT, false,
                    0, mColor);

            GLES20.glEnableVertexAttribArray(mColorHandle);

            //Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);

            //GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 24);
//            GLES20.glDrawElements(GLES20.GL_TRIANGLES, 12, GLES20.GL_UNSIGNED_BYTE, mPositionHandle);

            // Debug
            int status = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);
            switch (status) {
                case GLES20.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT:
                    System.out.println("11111111111");
                    break;
                case GLES20.GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS:
                    System.out.println("2222222222");
                    break;
                case GLES20.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT:
                    System.out.println("333333333333");
                    break;
                case GLES20.GL_FRAMEBUFFER_UNSUPPORTED:
                    System.out.println("444444444444");
                    break;
                default:
                    System.out.println("555555555555");
                    break;
            }
        }

        int put(float[] t, int idx, float x, float y, float z) {
            t[idx  ] = x;
            t[idx++] = y;
            t[idx++] = z;
//            System.out.println(String.format(">>>>>>>>   (%f, %f, %f)", x, y, z));
            return idx++;
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

            drawGrid();
        }
    }
}
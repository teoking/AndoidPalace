package com.teok.android.opengles.my;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
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

    private static String TAG = "BoingBall";

    /**
     * Store the view matrix. This can be thought of as our camera. This matrix transforms world space to eye space;
     * it positions things relative to our eye.
     */
    private float[] mViewMatrix = new float[16];

    /**
     * Store the projection matrix. This is used to project the scene onto a 2D viewport.
     */
    private float[] mProjectionMatrix = new float[16];

    /**
     * Allocate storage for the final combined matrix. This will be passed into the shader program.
     */
    private float[] mMVPMatrix = new float[16];

    /**
     * Store the model matrix. This matrix is used to move models from object space (where each model can be thought
     * of being located at the center of the universe) to world space.
     */
    private float[] mModelMatrix = new float[16];

    /**
     * This will be used to pass in the transformation matrix.
     */
    private int mMVPMatrixHandle;

    /**
     * This will be used to pass in model position information.
     */
    private int mPositionHandle;

    /**
     * This will be used to pass in model color information.
     */
    private int mColorHandle;

    private int mProgramHandle;

    private FloatBuffer mGridCoordinates;
    private FloatBuffer mBoingCoordinates;

    @Override
    protected GLSurfaceView.Renderer getRenderer() {
        return new BoingRenderer();
    }

    String getVertexShader() {
        return RawResourceReader.readTextFileFromRawResource(BoingBall.this, R.raw.basic_vertex_shader);
    }

    String getFragmentShader() {
        return RawResourceReader.readTextFileFromRawResource(BoingBall.this, R.raw.basic_fragment_shader);
    }

    enum DRAW_BALL_ENUM {
        DRAW_BALL, DRAW_BALL_SHADOW
    }

    class BoingRenderer implements GLSurfaceView.Renderer {

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            // Set the background clear color to black.
            GLES20.glClearColor(0.55f, 0.55f, 0.55f, 0.f);

            // Use culling to remove back faces.
            GLES20.glEnable(GLES20.GL_CULL_FACE);

//            GLES20.glCullFace(GLES20.GL_FRONT);

            // Enable depth testing
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);

            // The below glEnable() call is a holdover from OpenGL ES 1, and is not needed in OpenGL ES 2.
            // Enable texture mapping
            // GLES20.glEnable(GLES20.GL_TEXTURE_2D);

            // Position the eye in front of the origin.
            final float eyeX = 0.0f;
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

            final String vertexShader = getVertexShader();
            final String fragmentShader = getFragmentShader();

            int vertexShaderHandle = ShaderHelper.compileShader(GLES20.GL_VERTEX_SHADER, vertexShader);
            int fragmentShaderHandle = ShaderHelper.compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader);

            mProgramHandle = ShaderHelper.createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle,
                    new String[]{"a_Position", "a_Color", "v_Color"});

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

        final int ROW_TOTAL = 12;
        final int COL_TOTAL = ROW_TOTAL;
        final float RADIUS = 0.9f;
        final float STEP_LONGITUDE = 22.5f;
        final float STEP_LATITUDE = 22.5f;
        final float DIST_BALL = RADIUS * 1.0f + RADIUS * 0.1f;
        final float VIEW_SCENE_DIST = DIST_BALL * 3.f + 200.f;
        final float GRID_SIZE = RADIUS * 4.5f;
        final float BOUNCE_HEIGHT = RADIUS * 1.1f;
        final float BOUNCE_WIDTH = RADIUS * 1.1f;

        final float SHADOW_OFFSET_X = -20.f;
        final float SHADOW_OFFSET_Y = 10.f;
        final float SHADOW_OFFSET_Z = 0.f;

        final float WALL_L_OFFSET = 0.f;
        final float WALL_R_OFFSET = 5.f;

        final float ANIMATION_SPEED = 50.f;
        final float MAX_DELTA_T = 0.02f;

        final int BYTES_PER_FLOAT = 4;

        public void genGrid() {
            int row, col;
            float widthLine = 0.05f;
            float sizeCell = GRID_SIZE / ROW_TOTAL;
            float z_offset = 1.f;
            float xl, xr;
            float yt, yb;

            int coLen = 3 * 3 * (ROW_TOTAL + 1) * 2 * 2;
            float[] gridCoordinates = new float[coLen];

            int idx = 0;

            for (col = 0; col <= COL_TOTAL; col++) {
                xl = -GRID_SIZE / 2 + col * sizeCell;
                xr = xl + widthLine;

                yt = GRID_SIZE / 2;
                yb = -GRID_SIZE / 2 - widthLine;

//                ULog.d(TAG, String.format("idx=%d (xr=%f, xl=%f, yt=%f, yb=%f)", col, xr, xl, yt, yb));

                idx = putf3(gridCoordinates, idx, xr, yt, z_offset);
                idx = putf3(gridCoordinates, idx, xl, yt, z_offset);
                idx = putf3(gridCoordinates, idx, xl, yb, z_offset);

                idx = putf3(gridCoordinates, idx, xr, yt, z_offset);
                idx = putf3(gridCoordinates, idx, xl, yb, z_offset);
                idx = putf3(gridCoordinates, idx, xr, yb, z_offset);
            }

            for (row = 0; row <= ROW_TOTAL; row++) {
                /*
                 * Compute co-ords of line.
                 */
                yt = GRID_SIZE / 2 - row * sizeCell;
                yb = yt - widthLine;

                xl = -GRID_SIZE / 2;
                xr = GRID_SIZE / 2 + widthLine;

                // gen
                idx = putf3(gridCoordinates, idx, xr, yt, z_offset);
                idx = putf3(gridCoordinates, idx, xl, yt, z_offset);
                idx = putf3(gridCoordinates, idx, xl, yb, z_offset);

                idx = putf3(gridCoordinates, idx, xr, yt, z_offset);
                idx = putf3(gridCoordinates, idx, xl, yb, z_offset);
                idx = putf3(gridCoordinates, idx, xr, yb, z_offset);
            }

            mGridCoordinates = ByteBuffer.allocateDirect(gridCoordinates.length * BYTES_PER_FLOAT)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer();
            mGridCoordinates.put(gridCoordinates).position(0);

            mBoingCoordinates = ByteBuffer.allocateDirect(12 * BYTES_PER_FLOAT)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer();

            // Debug
            /*
            for (int i = 0, j; i < coLen / 3; i++) {
                j = i * 3;
                System.out.print(gridCoordinates[j  ] + ", ");
                System.out.print(gridCoordinates[++j] + ", ");
                System.out.println(gridCoordinates[++j]);
                ++j;
            }
            */
        }

        public void drawGrid() {
            Matrix.setIdentityM(mModelMatrix, 0);
            Matrix.translateM(mModelMatrix, 0, 0.0f, 0.0f, -5.0f);

            mGridCoordinates.position(0);
            GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false,
                    0, mGridCoordinates);

            GLES20.glEnableVertexAttribArray(mPositionHandle);

            // Pass in the color as uniform
            GLES20.glUniform4f(mColorHandle, 1.0f, 0.0f, 0.0f, 1.0f);

            Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
            // This multiplies the modelview matrix by the projection matrix, and stores the result in the MVP matrix
            // (which now contains model * view * projection).
            Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);

            GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, (ROW_TOTAL + 1) * 6 * 2);
        }

        /* Global vars */
        float deg_rot_y = 0.f;
        float deg_rot_y_inc = 2.f;
        float ball_x = -RADIUS;
        float ball_y = -RADIUS;
        float ball_x_inc = 1.f;
        float ball_y_inc = 2.f;
        DRAW_BALL_ENUM drawBallHow;
        double t;
        double t_old = 0.f;
        double dt;
        final int RAND_MAX = 4095;
        final double M_PI = 3.1415926535897932384626433832795;

        /**
         * **************************************************************************
         * Truncate a degree.
         * ***************************************************************************
         */
        float TruncateDeg(float deg) {
            if (deg >= 360.f)
                return (deg - 360.f);
            else
                return deg;
        }

        /**
         * **************************************************************************
         * Convert a degree (360-based) into a radian.
         * 360' = 2 * PI
         * ***************************************************************************
         */
        double deg2rad(double deg) {
            return deg / 360 * (2 * M_PI);
        }

        /**
         * **************************************************************************
         * 360' sin().
         * ***************************************************************************
         */
        double sin_deg(double deg) {
            return Math.sin(deg2rad(deg));
        }

        /**
         * **************************************************************************
         * 360' cos().
         * ***************************************************************************
         */
        double cos_deg(double deg) {
            return Math.cos(deg2rad(deg));
        }

        /**
         * **************************************************************************
         * Compute a cross product (for a normal vector).
         * <p/>
         * c = a x b
         * ***************************************************************************
         */
        void CrossProduct(vertex_t a, vertex_t b, vertex_t c, vertex_t n) {
            float u1, u2, u3;
            float v1, v2, v3;

            u1 = b.x - a.x;
            u2 = b.y - a.y;
            u3 = b.y - a.z;

            v1 = c.x - a.x;
            v2 = c.y - a.y;
            v3 = c.z - a.z;

            n.x = u2 * v3 - v2 * v3;
            n.y = u3 * v1 - v3 * u1;
            n.z = u1 * v2 - v1 * u2;
        }

        public void drawBoingBall() {
            /* degree of longitude */
            float lon_deg;
            double dt_total, dt2;

            /*
             * Another relative Z translation to separate objects.
             */
            Matrix.setIdentityM(mModelMatrix, 0);
            Matrix.translateM(mModelMatrix, 0, 0.0f, 0.0f, -5.0f);

               /* Update ball position and rotation (iterate if necessary) */
            dt_total = dt;
            while (dt_total > 0.0) {
                dt2 = dt_total > MAX_DELTA_T ? MAX_DELTA_T : dt_total;
                dt_total -= dt2;
                BounceBall(dt2);
                deg_rot_y = TruncateDeg(deg_rot_y + deg_rot_y_inc * ((float) dt2 * ANIMATION_SPEED));
            }

            Matrix.translateM(mModelMatrix, 0, ball_x, ball_y, 0.0f);

            if (drawBallHow == DRAW_BALL_ENUM.DRAW_BALL_SHADOW) {
                Matrix.translateM(mModelMatrix, 0, SHADOW_OFFSET_X, SHADOW_OFFSET_Y, SHADOW_OFFSET_Z);
            }

            Matrix.rotateM(mModelMatrix, 0, deg_rot_y, 0.0f, 1.0f, 0.0f);

            Matrix.rotateM(mModelMatrix, 0, -2.0f, 0.0f, 0.0f, 1.0f);

            /*
             * Build a faceted latitude slice of the Boing ball,
             * stepping same-sized vertical bands of the sphere.
             */
            for (lon_deg = 0; lon_deg < 180; lon_deg += STEP_LONGITUDE) {
             /*
              * Draw a latitude circle at this longitude.
              */
               DrawBoingBallBand(10, 10 + STEP_LONGITUDE);
            }

        }

        /**
         * **************************************************************************
         * Bounce the ball.
         * ***************************************************************************
         */
        void BounceBall(double delta_t) {
            float sign;
            float deg;

            /* Bounce on walls */
            if (ball_x > (BOUNCE_WIDTH / 2 + WALL_R_OFFSET)) {
                ball_x_inc = -0.5f - 0.75f * (float) Math.random() / (float) RAND_MAX;
                deg_rot_y_inc = -deg_rot_y_inc;
            }
            if (ball_x < -(BOUNCE_HEIGHT / 2 + WALL_L_OFFSET)) {
                ball_x_inc = 0.5f + 0.75f * (float) Math.random() / (float) RAND_MAX;
                deg_rot_y_inc = -deg_rot_y_inc;
            }

            /* Bounce on floor / roof */
            if (ball_y > BOUNCE_HEIGHT / 2) {
                ball_y_inc = -0.75f - 1.f * (float) Math.random() / (float) RAND_MAX;
            }
            if (ball_y < -BOUNCE_HEIGHT / 2 * 0.85) {
                ball_y_inc = 0.75f + 1.f * (float) Math.random() / (float) RAND_MAX;
            }

            /* Update ball position */
            ball_x += ball_x_inc * ((float) delta_t * ANIMATION_SPEED);
            ball_y += ball_y_inc * ((float) delta_t * ANIMATION_SPEED);

          /*
           * Simulate the effects of gravity on Y movement.
           */
            if (ball_y_inc < 0) sign = -1.0f;
            else sign = 1.0f;

            deg = (ball_y + BOUNCE_HEIGHT / 2) * 90 / BOUNCE_HEIGHT;
            if (deg > 80) deg = 80;
            if (deg < 10) deg = 10;

            ball_y_inc = sign * 4.f * (float) sin_deg(deg);
        }

        vertex_t vert_ne = new vertex_t();            /* "ne" means south-east, so on */
        vertex_t vert_nw = new vertex_t();
        vertex_t vert_sw = new vertex_t();
        vertex_t vert_se = new vertex_t();
        vertex_t vert_norm = new vertex_t();
        float[] vert_arr = new float[12];

        boolean colorToggle = true;

        /**
         * **************************************************************************
         * Draw a faceted latitude band of the Boing ball.
         * <p/>
         * Parms:   long_lo, long_hi
         * Low and high longitudes of slice, resp.
         * ***************************************************************************
         */
        void DrawBoingBallBand(float long_lo,
                               float long_hi) {

            float lat_deg;
          /*
           * Iterate thru the points of a latitude circle.
           * A latitude circle is a 2D set of X,Z points.
           */
            for (lat_deg = 0;
                 lat_deg <= (360 - STEP_LATITUDE);
                 lat_deg += STEP_LATITUDE) {
             /*
              * Color this polygon with red or white.
              */

                if (colorToggle)
                    GLES20.glUniform4f(mColorHandle, 0.8f, 0.1f, 0.1f, 1.0f);
                else
                    GLES20.glUniform4f(mColorHandle, 0.95f, 0.95f, 0.95f, 1.0f);

                colorToggle = !colorToggle;
             /*
              * Change color if drawing shadow.
              */
                if (drawBallHow == DRAW_BALL_ENUM.DRAW_BALL_SHADOW)
                    GLES20.glUniform4f(mColorHandle, 0.35f, 0.35f, 0.35f, 1.0f);

             /*
              * Assign each Y.
              */
                vert_ne.y = vert_nw.y = (float) cos_deg(long_hi) * RADIUS;
                vert_sw.y = vert_se.y = (float) cos_deg(long_lo) * RADIUS;

             /*
              * Assign each X,Z with sin,cos values scaled by latitude radius indexed by longitude.
              * Eg, long=0 and long=180 are at the poles, so zero scale is sin(longitude),
              * while long=90 (sin(90)=1) is at equator.
              */
                vert_ne.x = (float) cos_deg(lat_deg) * (RADIUS * (float) sin_deg(long_lo + STEP_LONGITUDE));
                vert_se.x = (float) cos_deg(lat_deg) * (RADIUS * (float) sin_deg(long_lo));
                vert_nw.x = (float) cos_deg(lat_deg + STEP_LATITUDE) * (RADIUS * (float) sin_deg(long_lo + STEP_LONGITUDE));
                vert_sw.x = (float) cos_deg(lat_deg + STEP_LATITUDE) * (RADIUS * (float) sin_deg(long_lo));

                vert_ne.z = (float) sin_deg(lat_deg) * (RADIUS * (float) sin_deg(long_lo + STEP_LONGITUDE)) - 5.0f;
                vert_se.z = (float) sin_deg(lat_deg) * (RADIUS * (float) sin_deg(long_lo)) - 5.0f;
                vert_nw.z = (float) sin_deg(lat_deg + STEP_LATITUDE) * (RADIUS * (float) sin_deg(long_lo + STEP_LONGITUDE)) - 5.0f;
                vert_sw.z = (float) sin_deg(lat_deg + STEP_LATITUDE) * (RADIUS * (float) sin_deg(long_lo)) - 5.0f;

             /*
              * Draw the facet.
              */
//                glBegin(GL_POLYGON);

                CrossProduct(vert_ne, vert_nw, vert_sw, vert_norm);
//                glNormal3f(vert_norm.x, vert_norm.y, vert_norm.z);
//
//                glVertex3f(vert_ne.x, vert_ne.y, vert_ne.z);
//                glVertex3f(vert_nw.x, vert_nw.y, vert_nw.z);
//                glVertex3f(vert_sw.x, vert_sw.y, vert_sw.z);
//                glVertex3f(vert_se.x, vert_se.y, vert_se.z);

                int idx = 0;
                idx = putf3(vert_arr, idx, vert_ne.x, vert_ne.y, vert_ne.z);
                idx = putf3(vert_arr, idx, vert_nw.x, vert_nw.y, vert_nw.z);
                idx = putf3(vert_arr, idx, vert_sw.x, vert_sw.y, vert_sw.z);
                idx = putf3(vert_arr, idx, vert_se.x, vert_se.y, vert_se.z);

                mBoingCoordinates.put(vert_arr).position(0);

                mBoingCoordinates.position(0);

                GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false,
                        0, mBoingCoordinates);

//                GLES20.glVertexAttrib3fv(mPositionHandle, vert_arr, 0);

                GLES20.glEnableVertexAttribArray(mPositionHandle);

                Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
                // This multiplies the modelview matrix by the projection matrix, and stores the result in the MVP matrix
                // (which now contains model * view * projection).
                Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);

                GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

                GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 4 * 6);
//                glEnd();


//                System.out.println("-----------------------------------------------------------");
//                System.out.println(String.format("lat = %f  long_lo = %f  long_hi = %f \n", lat_deg, long_lo, long_hi));
//                System.out.println(String.format("vert_ne  x = %.8f  y = %.8f  z = %.8f \n", vert_ne.x, vert_ne.y, vert_ne.z));
//                System.out.println(String.format("vert_nw  x = %.8f  y = %.8f  z = %.8f \n", vert_nw.x, vert_nw.y, vert_nw.z));
//                System.out.println(String.format("vert_se  x = %.8f  y = %.8f  z = %.8f \n", vert_se.x, vert_se.y, vert_se.z));
//                System.out.println(String.format("vert_sw  x = %.8f  y = %.8f  z = %.8f \n", vert_sw.x, vert_sw.y, vert_sw.z));
            }

          /*
           * Toggle color so that next band will opposite red/white colors than this one.
           */
            colorToggle = !colorToggle;

          /*
           * This circular band is done.
           */
        }

        /**
         * Put 3 floats to t while index starts at idx.
         *
         * @param t
         * @param idx
         * @param x
         * @param y
         * @param z
         * @return the current index of the float array
         */
        int putf3(float[] t, int idx, float x, float y, float z) {
            t[idx] = x;
            t[++idx] = y;
            t[++idx] = z;
            return ++idx;
        }

        /**
         * Put 4 floats to t while index starts at idx.
         *
         * @param t
         * @param idx
         * @param r
         * @param g
         * @param b
         * @param a
         * @return the current index of the float array
         */
        int putf4(float[] t, int idx, float r, float g, float b, float a) {
            t[idx] = r;
            t[++idx] = g;
            t[++idx] = b;
            t[++idx] = a;
            return ++idx;
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

            GLES20.glUseProgram(mProgramHandle);

            mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_MVPMatrix");
            mColorHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_Color");
            mPositionHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Position");

            drawBallHow = DRAW_BALL_ENUM.DRAW_BALL_SHADOW;
            drawBoingBall();

//            drawGrid();
//
//            drawBallHow = DRAW_BALL_ENUM.DRAW_BALL;
//            drawBoingBall();
        }
    }

    class vertex_t {
        float x;
        float y;
        float z;
    }
}
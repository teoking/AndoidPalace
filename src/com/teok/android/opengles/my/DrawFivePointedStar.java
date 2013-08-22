package com.teok.android.opengles.my;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import com.teok.android.common.ULog;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.opengles.GL10;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Random;

/**
 */
public class DrawFivePointedStar extends GLSurfaceViewActivity{

    private static final String TAG = "DrawFivePointedStar";

    /** How many bytes per float. */
    static final int BYTES_PER_FLOAT = 4;

    /** How many elements per vertex. */
    static final int STRIDE_BYTES = 7 * BYTES_PER_FLOAT;

    /** Offset of the position data. */
    static final int POSITION_OFFSET = 0;

    /** Size of the position data in elements. */
    static final int POSITION_DATA_SIZE = 3;

    /** Offset of the color data. */
    static final int COLOR_OFFSET = 3;

    /** Size of the color data in elements. */
    static final int COLOR_DATA_SIZE = 4;

    /** The radius of the bigger circle. */
    static final float RADIUS = 0.5f;

    static final float TH = 3.1415926f / 180;

    private FloatBuffer mTriangleFloatBuffer;

    /** This will be used to pass in the transformation matrix. */
    private int mMVPMatrixHandle;

    /** This will be used to pass in model position information. */
    private int mPositionHandle;

    /** This will be used to pass in model color information. */
    private int mColorHandle;

    /**
     * Store the view matrix. This can be thought of as our camera. This matrix transforms world space to eye space;
     * it positions things relative to our eye.
     */
    private float[] mViewMatrix = new float[16];

    /**
     * Store the model matrix. This matrix is used to move models from object space (where each model can be thought
     * of being located at the center of the universe) to world space.
     */
    private float[] mModelMatrix = new float[16];

    /** Store the projection matrix. This is used to project the scene onto a 2D viewport. */
    private float[] mProjectionMatrix = new float[16];

    /** Allocate storage for the final combined matrix. This will be passed into the shader program. */
    private float[] mMVPMatrix = new float[16];


    @Override
    protected GLSurfaceView.Renderer getRenderer() {
        return new StarRender();
    }

    /**
     * generate a 2d vertex array with the center of (x, y) and radius r. the z value should be always 0.0f.
     * @param x x of the center point
     * @param y y of the center point
     * @param r radius of the bigger circle
     * @return the 2d vertex array
     */
    static float[] generateStarVertex(float x, float y, float r) {
        float[] vertices = new float[10 * (POSITION_DATA_SIZE + COLOR_DATA_SIZE)];


        final Random ran = new Random(1);

        // Inner circle radius
        float r0 = (float) (r * Math.sin(18 * TH) / Math.cos(36 * TH));
        ULog.d(TAG, "r0 = " + r0);

        for (int i = 0, k = 0; i < 5; i++) {
            // point on bigger circle
            // X, Y, Z
            // R, G, B, A

            // point on inner circle
            vertices[k++] = (float) (x + r0 * Math.cos((54 + i * 72) * TH));    // x
            vertices[k++] = (float) (y - r0 * Math.sin((54 + i * 72) * TH));    // y
            vertices[k++] = 0.0f;       // z
            vertices[k++] = ran.nextFloat();    // R
            vertices[k++] = ran.nextFloat();    // G
            vertices[k++] = ran.nextFloat();    // B
            vertices[k++] = 1.0f;

            vertices[k++] = (float) (x + r * Math.cos((90 + i * 72) * TH));     // x
            vertices[k++] = (float) (y - r * Math.sin((90 + i * 72) * TH));     // y
            vertices[k++] = 0.0f;       // z value
            vertices[k++] = ran.nextFloat();    // R
            vertices[k++] = ran.nextFloat();    // G
            vertices[k++] = ran.nextFloat();    // B
            vertices[k++] = 1.0f;               // A
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0, p = 0, c = 0; i < vertices.length; i++) {
//            if (p < POSITION_DATA_SIZE) {
//                sb.append(vertices[i]).append(", ");
//                p++;
//            } else {
//                if (c == 0) {
//                    sb.append("\n");
//                }
//
//                if (c < COLOR_DATA_SIZE) {
//                    sb.append(vertices[i]).append(", ");
//                    c++;
//                } else {
//                    sb.append("\n");
//                    p = 0;
//                    c = 0;
//                }
//            }
            sb.append(vertices[i]).append(", ").append("\n");
        }

        ULog.d(TAG, "@@ = " + sb.toString());

        return vertices;
    }

    class StarRender implements GLSurfaceView.Renderer {

        public StarRender() {

            // A triangle is blue, green, red
            final float[] starVerticesData = {
                    // X, Y, Z,
                    // R, G, B, A
                    0.112257004f, -0.1545085f, 0.0f,
                    0.40743977f, 0.2077148f, 0.036235332f, 1.0f,

                    -2.1855694E-8f, -0.5f, 0.0f,
                    0.7308782f, 0.100473166f, 0.4100808f, 1.0f,

                    -0.11225699f, -0.15450852f, 0.0f,
                    0.7107396f, 0.006117165f, 0.15273619f, 1.0f,

                    -0.11225699f, -0.15450852f, 0.0f,
                    0.7107396f, 0.006117165f, 0.15273619f, 1.0f,

                    -0.47552824f, -0.15450852f, 0.0f,
                    0.332717f, 0.6588672f, 0.96775585f, 1.0f,

                    -0.18163563f, 0.05901699f, 0.0f,
                    0.55407226f, 0.9471949f, 0.9109504f, 1.0f,

                    -0.18163563f, 0.05901699f, 0.0f,
                    0.55407226f, 0.9471949f, 0.9109504f, 1.0f,

                    -0.2938927f, 0.4045084f, 0.0f,
                    0.96370476f, 0.15957803f, 0.93986535f, 1.0f,

                    2.2774496E-9f, 0.19098301f, 0.0f,
                    0.91396284f, 0.34751803f, 0.15933955f, 1.0f,

                    2.2774496E-9f, 0.19098301f, 0.0f,
                    0.91396284f, 0.34751803f, 0.15933955f, 1.0f,

                    0.29389253f, 0.40450856f, 0.0f,
                    0.9370821f, 0.48708737f, 0.3971743f, 1.0f,

                    0.18163565f, 0.059016988f, 0.0f,
                    0.864463f, 0.115967035f, 0.5392596f, 1.0f,

                    0.18163565f, 0.059016988f, 0.0f,
                    0.864463f, 0.115967035f, 0.5392596f, 1.0f,

                    0.4755283f, -0.15450841f, 0.0f,
                    0.294057f, 0.36900252f, 0.5064836f, 1.0f,

                    0.112257004f, -0.1545085f, 0.0f,
                    0.40743977f, 0.2077148f, 0.036235332f, 1.0f,
            };

//            final float[] starVerticesData = generateStarVertex(0.0f, 0.0f, RADIUS);

            mTriangleFloatBuffer = ByteBuffer.allocateDirect(starVerticesData.length * BYTES_PER_FLOAT)
                    .order(ByteOrder.nativeOrder()).asFloatBuffer();

            mTriangleFloatBuffer.put(starVerticesData).position(0);
        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);

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

            final String vertexShader =
                    "uniform mat4 u_MVPMatrix;      \n"		// A constant representing the combined model/view/projection matrix.

                            + "attribute vec4 a_Position;     \n"		// Per-vertex position information we will pass in.
                            + "attribute vec4 a_Color;        \n"		// Per-vertex color information we will pass in.

                            + "varying vec4 v_Color;          \n"		// This will be passed into the fragment shader.

                            + "void main()                    \n"		// The entry point for our vertex shader.
                            + "{                              \n"
                            + "   v_Color = a_Color;          \n"		// Pass the color through to the fragment shader.
                            // It will be interpolated across the triangle.
                            + "   gl_Position = u_MVPMatrix   \n" 	// gl_Position is a special variable used to store the final position.
                            + "               * a_Position;   \n"     // Multiply the vertex by the matrix to get the final point in
                            + "}                              \n";    // normalized screen coordinates.

            final String fragmentShader =
                    "precision mediump float;       \n"		// Set the default precision to medium. We don't need as high of a
                            // precision in the fragment shader.
                            + "varying vec4 v_Color;          \n"		// This is the color from the vertex shader interpolated across the
                            // triangle per fragment.
                            + "void main()                    \n"		// The entry point for our fragment shader.
                            + "{                              \n"
                            + "   gl_FragColor = v_Color;     \n"		// Pass the color directly through the pipeline.
                            + "}                              \n";

            // Load in the vertex shader.
            int vertexShaderHandle = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);

            if (vertexShaderHandle != 0)
            {
                // Pass in the shader source.
                GLES20.glShaderSource(vertexShaderHandle, vertexShader);

                // Compile the shader.
                GLES20.glCompileShader(vertexShaderHandle);

                // Get the compilation status.
                final int[] compileStatus = new int[1];
                GLES20.glGetShaderiv(vertexShaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

                // If the compilation failed, delete the shader.
                if (compileStatus[0] == 0)
                {
                    GLES20.glDeleteShader(vertexShaderHandle);
                    vertexShaderHandle = 0;
                }
            }

            if (vertexShaderHandle == 0)
            {
                throw new RuntimeException("Error creating vertex shader.");
            }

            // Load in the fragment shader shader.
            int fragmentShaderHandle = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);

            if (fragmentShaderHandle != 0)
            {
                // Pass in the shader source.
                GLES20.glShaderSource(fragmentShaderHandle, fragmentShader);

                // Compile the shader.
                GLES20.glCompileShader(fragmentShaderHandle);

                // Get the compilation status.
                final int[] compileStatus = new int[1];
                GLES20.glGetShaderiv(fragmentShaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

                // If the compilation failed, delete the shader.
                if (compileStatus[0] == 0)
                {
                    GLES20.glDeleteShader(fragmentShaderHandle);
                    fragmentShaderHandle = 0;
                }
            }

            if (fragmentShaderHandle == 0)
            {
                throw new RuntimeException("Error creating fragment shader.");
            }

            // Create a program object and store the handle to it.
            int programHandle = GLES20.glCreateProgram();

            if (programHandle != 0)
            {
                // Bind the vertex shader to the program.
                GLES20.glAttachShader(programHandle, vertexShaderHandle);

                // Bind the fragment shader to the program.
                GLES20.glAttachShader(programHandle, fragmentShaderHandle);

                // Bind attributes
                GLES20.glBindAttribLocation(programHandle, 0, "a_Position");
                GLES20.glBindAttribLocation(programHandle, 1, "a_Color");

                // Link the two shaders together into a program.
                GLES20.glLinkProgram(programHandle);

                // Get the link status.
                final int[] linkStatus = new int[1];
                GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, linkStatus, 0);

                // If the link failed, delete the program.
                if (linkStatus[0] == 0)
                {
                    GLES20.glDeleteProgram(programHandle);
                    programHandle = 0;
                }
            }

            if (programHandle == 0)
            {
                throw new RuntimeException("Error creating program.");
            }

            // Set program handles. These will later be used to pass in values to the program.
            mMVPMatrixHandle = GLES20.glGetUniformLocation(programHandle, "u_MVPMatrix");
            mPositionHandle = GLES20.glGetAttribLocation(programHandle, "a_Position");
            mColorHandle = GLES20.glGetAttribLocation(programHandle, "a_Color");

            // Tell OpenGL to use this program when rendering.
            GLES20.glUseProgram(programHandle);
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            // Set the OpenGL viewport to the same size as the surface.
            GLES20.glViewport(0, 0, width, height);

            ULog.d(TAG, "screen resolution " + width + "x" + height);
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

        @Override
        public void onDrawFrame(GL10 gl) {
            // Clear depth buffer and color buffer
            GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

            Matrix.setIdentityM(mModelMatrix, 0);
            drawStar(mTriangleFloatBuffer);
        }

        private void drawStar(final FloatBuffer aStarBuffer) {
            aStarBuffer.position(POSITION_OFFSET);
            GLES20.glVertexAttribPointer(mPositionHandle, POSITION_DATA_SIZE, GLES20.GL_FLOAT, false,
                    STRIDE_BYTES, aStarBuffer);

            GLES20.glEnableVertexAttribArray(mPositionHandle);

            // Pass in the color information
            aStarBuffer.position(COLOR_OFFSET);
            GLES20.glVertexAttribPointer(mColorHandle, COLOR_DATA_SIZE, GLES20.GL_FLOAT, false,
                    STRIDE_BYTES, aStarBuffer);

            GLES20.glEnableVertexAttribArray(mColorHandle);

            // This multiplies the view matrix by the model matrix, and stores the result in the MVP matrix
            // (which currently contains model * view).
            Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);

            // This multiplies the modelview matrix by the projection matrix, and stores the result in the MVP matrix
            // (which now contains model * view * projection).
            Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);

            GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 15);
        }
    }
}

package com.teok.android.opengles;

import android.content.Context;
import android.os.Bundle;
import com.teok.android.R;
import com.teok.android.common.ULog;
import rajawali.BaseObject3D;
import rajawali.RajawaliFragmentActivity;
import rajawali.lights.DirectionalLight;
import rajawali.materials.DiffuseMaterial;
import rajawali.materials.TextureInfo;
import rajawali.primitives.Sphere;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created with IntelliJ IDEA.
 * <p>Created at 5:39 PM, 8/1/13</p>
 *
 * @author teo
 */
public class RajawaliTutorial1Activity extends RajawaliFragmentActivity {
    private static final String TAG = RajawaliTutorial1Activity.class.getName();

    private RajawaliTutorial1Renderer mRenderer;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRenderer = new RajawaliTutorial1Renderer(this);
        mRenderer.setSurfaceView(mSurfaceView);
        super.setRenderer(mRenderer);
    }

    private final class RajawaliTutorial1Renderer extends rajawali.renderer.RajawaliRenderer {

        private DirectionalLight mLight;
        private BaseObject3D mSphere;

        public RajawaliTutorial1Renderer(Context context) {
            super(context);
            setFrameRate(60);
        }

        @Override
        protected void initScene() {
            mLight = new DirectionalLight(1f, 0.2f, -1.0f); // set the direction
            mLight.setColor(1.0f, 1.0f, 1.0f);
            mLight.setPower(2);

            DiffuseMaterial material = new DiffuseMaterial();
            material.addTexture(new TextureInfo(R.drawable.earthtruecolor_nasa_big));
            mSphere = new Sphere(1, 24, 24);
            mSphere.setMaterial(material);
            mSphere.addLight(mLight);
            addChild(mSphere); //Queue an addition task for mSphere

            getCamera().setZ(4.2f);
        }

        @Override
        public void onDrawFrame(GL10 glUnused) {
            super.onDrawFrame(glUnused);
            mSphere.setRotY(mSphere.getRotY() + 1);
        }
    }

}
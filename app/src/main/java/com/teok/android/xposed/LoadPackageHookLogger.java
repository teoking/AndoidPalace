package com.teok.android.xposed;

import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by teo on 10/9/14.
 */
public class LoadPackageHookLogger implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpp) throws Throwable {
        String pkg = lpp.packageName;
        // Clear log if it's on start up
        if ("android".equals(pkg)) {
            InnoLog.clear();
        }
        // prints to console by xposed log
        XposedBridge.log("Loaded app: " + pkg);
        // save to memory
        InnoLog.append(pkg);
    }

}

package com.teok.android.annotation;

/**
 * Created with IntelliJ IDEA.
 * <p>Created at 10:53 AM, 10/28/13</p>
 *
 * @author teo
 */
public class View {

    public static class Injector {

        public static int injectFoo(View view) {
            return 2;
        }
    }

    public int foo() {
        return 1;
    }
}

package com.teok.android.annotation;

import java.lang.annotation.*;

/**
 * Created with IntelliJ IDEA.
 * <p>Created at 10:03 AM, 10/28/13</p>
 *
 * @author teo
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@LeHook(LeHook.LeHookType.CHANGE_CODE)
public @interface LeHook {

    public LeHookType value();

    public enum LeHookType {
        CHANGE_ACCESS,
        CHANGE_BASE_CLASS,
        CHANGE_CODE,
        CHANGE_CODE_AND_ACCESS,
        CHANGE_PARAMETER,
        CHANGE_PARAMETER_AND_ACCESS,
        NEW_CLASS,
        NEW_FIELD,
        NEW_METHOD
    }
}

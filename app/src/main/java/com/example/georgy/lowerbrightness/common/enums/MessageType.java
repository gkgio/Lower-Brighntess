package com.example.georgy.lowerbrightness.common.enums;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Gigauri
 *
 * Type of message
 */

@IntDef({MessageType.INFO, MessageType.ERROR})
@Retention(RetentionPolicy.SOURCE)
public @interface MessageType {
    int INFO = 0;
    int ERROR = 1;
}

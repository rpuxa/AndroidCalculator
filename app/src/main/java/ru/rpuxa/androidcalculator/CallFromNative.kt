package ru.rpuxa.androidcalculator

/**
 * Member that declared with this annotation may be invoked from native code. Dont change api!
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.SOURCE)
annotation class CallFromNative
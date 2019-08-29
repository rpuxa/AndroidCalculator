package ru.rpuxa.androidcalculator.calc.stages;

public final class NativeCalculator {
    private NativeCalculator() {
    }

    static {
        System.loadLibrary("calculations");
    }

    public static native int calculate(byte[] expression, byte[] conversions);
}

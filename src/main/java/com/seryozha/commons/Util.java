package com.seryozha.commons;

import org.aspectj.lang.JoinPoint;

import java.util.Arrays;
import java.util.concurrent.Callable;

public class Util {
    private static final int DEFAULT_LOWER_BOUND = 0;
    private static final int DEFAULT_UPPER_BOUND = 1000;

    public static void pauseForMillis(long pause) {
        try {
            Thread.sleep(pause);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void pauseForMillis(long lowerBound, long upperBound) {
        checkBounds(lowerBound, upperBound);
        long pause = (long) (Math.random() * (upperBound - lowerBound) + lowerBound);
        try {
            Thread.sleep(pause);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static int randomInt() {
        return randomInt(DEFAULT_LOWER_BOUND, DEFAULT_UPPER_BOUND);
    }

    public static int randomIntExcept(int intToAvoid) {
        int spread = DEFAULT_LOWER_BOUND - DEFAULT_UPPER_BOUND;
        int randomInt;
        do {
            randomInt = (int) (Math.random() * spread + DEFAULT_LOWER_BOUND);
        } while (randomInt == intToAvoid);
        return randomInt;
    }

    public static int randomInt(int upperBound) {
        return randomInt(DEFAULT_LOWER_BOUND, upperBound);
    }

    public static int randomInt(int lowerBound, int upperBound) {
        checkBounds(lowerBound, upperBound);
        int spread = upperBound - lowerBound;
        return (int) (Math.random() * spread + lowerBound);
    }

    public static float randomFloat(float lowerBound, float upperBound) {
        if (upperBound <= lowerBound) {
            throw new IllegalArgumentException("The upper bound must be greater than the lower bound");
        }
        float spread = upperBound - lowerBound;
        return (float) (Math.random() * spread + lowerBound);
    }

    public static void requirePositive(int... intValues) {
        if (Arrays.stream(intValues).anyMatch(intValue -> intValue <= 0)) {
            throw new IllegalArgumentException("Values must be positive");
        }
    }

    private static void checkBounds(long lowerBound, long upperBound) {
        if (upperBound <= lowerBound) {
            throw new IllegalArgumentException("The upper bound must be greater than the lower bound");
        }
    }

    public static String getThisMethodName() {
        return getThisMethodName(MethodRendering.NAME_ONLY);
    }

    public static String getThisMethodName(MethodRendering methodRendering) {
        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
        return methodRendering.appendAsNeeded(methodName);
    }

    public static String getInterceptedMethodName(JoinPoint joinPoint) {
        return getInterceptedMethodName(joinPoint, MethodRendering.NAME_ONLY);
    }

    public static String getInterceptedMethodName(JoinPoint joinPoint, MethodRendering methodRendering) {
        String methodName = joinPoint.getSignature().getName();
        return methodRendering.appendAsNeeded(methodName);
    }

    public static String getInterceptedMethodType(JoinPoint joinPoint) {
        return joinPoint.getSignature().getDeclaringType().getSimpleName();
    }

    public static void wrapInTryCatch(ThrowingRunnable codeBlock) {
        try {
            codeBlock.run();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T wrapInTryCatchAndGet(Callable<T> returningCodeBlock) {
        try {
            return returningCodeBlock.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public enum MethodRendering {
        NAME_ONLY {
            @Override
            public String appendAsNeeded(String methodName) {
                return methodName;
            }
        }, WITH_BRACKETS {
            @Override
            public String appendAsNeeded(String methodName) {
                return methodName + "()";
            }
        };

        public abstract String appendAsNeeded(String methodName);
    }
}

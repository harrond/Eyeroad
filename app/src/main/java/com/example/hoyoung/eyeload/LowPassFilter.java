package com.example.hoyoung.eyeload;

public class LowPassFilter {//스무스한 움직임을 위한 클래스

    private static final float ALPHA_DEFAULT = 0.123f; //디폴트
    private static final float ALPHA_STEADY =  0.001f; //멈춰있는 상태일때 움직임 변화 값
    private static final float ALPHA_START_MOVING = 0.12f; //움직임이 시작되는 상태일때 이동하는 값
    private static final float ALPHA_MOVING = 0.22f; //움직이는 중일때 이동하는 값

    private LowPassFilter() {
    }

    public static float[] filter(float low, float high, float[] current, float[] previous) {
        if (current == null || previous == null)
            throw new NullPointerException("Input and prev float arrays must be non-NULL");
        if (current.length != previous.length)
            throw new IllegalArgumentException("Input and prev must be the same length");

        float alpha = computeAlpha(low, high, current, previous);//움직이는 거리 계산

        for (int i = 0; i < current.length; i++) {
            previous[i] = previous[i] + alpha * (current[i] - previous[i]); //현재위치에서 변화한 위치의 차이에서 변화 값을 곱해 전 위치에 그것을 더함(스무스하게 움직임)
        }
        return previous;
    }

    private static final float computeAlpha(float low, float high, float[] current, float[] previous) {
        if (previous.length != 3 || current.length != 3) return ALPHA_DEFAULT;

        float x1 = current[0], //현재 위치 저장
                y1 = current[1],
                z1 = current[2];

        float x2 = previous[0], //움직인 위치 저장
                y2 = previous[1],
                z2 = previous[2];

        float distance = (float) (Math.sqrt(Math.pow((double) (x2 - x1), 2d) +
                Math.pow((double) (y2 - y1), 2d) +
                Math.pow((double) (z2 - z1), 2d)) //움직인 거리 계산
        );

        if (distance < low) {
            return ALPHA_STEADY; //거리가 작으면 멈춰잇는 것
        } else if (distance >= low || distance < high) { //사이면 움직임이 시작된 것
            return ALPHA_START_MOVING;
        }
        return ALPHA_MOVING; //아니면 움직인것
    }
}
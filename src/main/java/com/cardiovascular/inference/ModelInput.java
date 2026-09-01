package com.cardiovascular.inference;

public record ModelInput(
        int sex,
        int age,
        int hyperlipidemia,
        int smoker,
        int diabetes,
        int obesity,
        int hypertension
) {

    public float[] toRawFeatureArray() {
        return new float[]{
                sex,
                age,
                hyperlipidemia,
                smoker,
                diabetes,
                obesity,
                hypertension
        };
    }
}

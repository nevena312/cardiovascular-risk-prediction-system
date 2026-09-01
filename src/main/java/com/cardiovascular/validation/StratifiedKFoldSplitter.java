package com.cardiovascular.validation;

import com.cardiovascular.data.PatientSample;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class StratifiedKFoldSplitter {

    public List<List<PatientSample>> split(
            List<PatientSample> samples,
            int folds,
            long seed
    ) {

        List<PatientSample> positive =
                new ArrayList<>();

        List<PatientSample> negative =
                new ArrayList<>();

        for (PatientSample sample : samples) {

            if (sample.getLabel() == 1) {
                positive.add(sample);
            } else {
                negative.add(sample);
            }
        }

        Collections.shuffle(
                positive,
                new Random(seed)
        );

        Collections.shuffle(
                negative,
                new Random(seed)
        );

        List<List<PatientSample>> result =
                new ArrayList<>();

        for (int i = 0; i < folds; i++) {
            result.add(new ArrayList<>());
        }

        for (int i = 0; i < positive.size(); i++) {
            result.get(i % folds)
                    .add(positive.get(i));
        }

        for (int i = 0; i < negative.size(); i++) {
            result.get(i % folds)
                    .add(negative.get(i));
        }

        for (List<PatientSample> fold : result) {
            Collections.shuffle(
                    fold,
                    new Random(seed)
            );
        }

        return result;
    }
}
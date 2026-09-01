package com.cardiovascular.model;

import deepnetts.net.FeedForwardNetwork;
import deepnetts.net.layers.activation.ActivationType;
import deepnetts.net.loss.LossType;

public class DeepNettsModelFactory {

    public FeedForwardNetwork createBaselineModel() {

        return FeedForwardNetwork.builder()
                .addInputLayer(7)
                .addFullyConnectedLayer(
                        8,
                        ActivationType.RELU
                )
                .addOutputLayer(
                        1,
                        ActivationType.SIGMOID
                )
                .lossFunction(
                        LossType.CROSS_ENTROPY
                )
                .randomSeed(42)
                .build();
    }

    public FeedForwardNetwork createModel(
            NeuralNetworkConfiguration configuration,
            long randomSeed
    ) {

        FeedForwardNetwork.Builder builder =
                FeedForwardNetwork.builder();

        builder.addInputLayer(7);

        for (
                int hiddenLayerSize :
                configuration.hiddenLayers()
        ) {

            builder.addFullyConnectedLayer(
                    hiddenLayerSize,
                    ActivationType.RELU
            );
        }

        builder.addOutputLayer(
                1,
                ActivationType.SIGMOID
        );

        builder.lossFunction(
                LossType.CROSS_ENTROPY
        );

        builder.randomSeed(randomSeed);

        return builder.build();
    }
}
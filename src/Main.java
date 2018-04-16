/**
 * Copyright 2010 Neuroph Project http://neuroph.sourceforge.net
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import com.sun.tools.internal.xjc.generator.bean.DualObjectFactoryGenerator;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.events.LearningEvent;
import org.neuroph.core.events.LearningEventListener;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.core.learning.LearningRule;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.BackPropagation;
import org.neuroph.nnet.learning.MomentumBackpropagation;
import org.neuroph.nnet.learning.ResilientPropagation;
import org.neuroph.util.TransferFunctionType;
import org.neuroph.util.data.norm.*;
import org.neuroph.util.random.WeightsRandomizer;


/**
 * This sample shows how to create, train, save and load simple Multi Layer Perceptron for the XOR problem.
 * This sample shows basics of Neuroph API.
 *
 * @author Zoran Sevarac <sevarac@gmail.com>
 */
public class Main implements LearningEventListener {

    static List<ArrayList<Double>> listIn = new ArrayList<>();
    static List<Double> listOut = new ArrayList<>();
    ArrayList<Double> subList = new ArrayList<>();


    public static void main(String[] args) {

      /*  Data data = new Data("src/resources/novgorod.txt");
        data.fileParse();
        Data dataTest = new Data("src/resources/novgorod.txt");
        dataTest.fileParse();*/

    ListInput l = new ListInput("src/resources/novgorodW.txt");
        l.fileParse();
        ListInput l2 = new ListInput("src/resources/novgorodW2.txt");
        l2.fileParse();

        new Main().run(l.inputList, l.outputList, l2.inputList, l2.outputList);


    }


    /**
     * Runs this sample
     */
    public void run(ArrayList<Double[]> input, ArrayList<Double[]> output, ArrayList<Double []> inputTest, ArrayList<Double []> outputTest) {

        // create training set (logical XOR function)
        DataSet trainingSet = new DataSet(3, 1);
        //     trainingSet.addRow(new DataSetRow(new double[]{1}, new double[]{1}));
/*        double [] tmp = new double[3];
        double [] dTmp = new double[1];
        int count = 0;
        for (int i = 0; i < output.length; i+=4) {
            tmp[0] = Double.valueOf(input[i])/31;
            tmp[1] = Double.valueOf(input[i+1])/12;
            tmp[2] = Double.valueOf(input[i+2])/50;
            dTmp[0] = Double.valueOf(output[count])/1000;

            trainingSet.addRow(new DataSetRow(tmp,  dTmp ));
            count++;
        }*/


        DataSet testSet = new DataSet(3, 1);
        //     trainingSet.addRow(new DataSetRow(new double[]{1}, new double[]{1}));

        for (int i = 0; i < inputTest.size(); i++) {
            testSet.addRow(new DataSetRow(doubleTodouble(i, inputTest), doubleTodouble(i, outputTest)));

           /* testSet.addRow(new DataSetRow(Double.parseDouble(inputTest[j]), Double.parseDouble(inputTest[j + 1]),
                    Double.parseDouble(inputTest[j + 2]), Double.parseDouble(outputTest[k])));*/
        }

        for (int i = 0; i < input.size(); i++) {
        trainingSet.addRow(new DataSetRow(doubleTodouble(i, input), doubleTodouble(i, output)));

           /* testSet.addRow(new DataSetRow(Double.parseDouble(inputTest[j]), Double.parseDouble(inputTest[j + 1]),
                    Double.parseDouble(inputTest[j + 2]), Double.parseDouble(outputTest[k])));*/
        }

//        trainingSet.addRow(new DataSetRow(new double[]{3}, new double[]{9}));
//        trainingSet.addRow(new DataSetRow(new double[]{4}, new double[]{16}));


        System.out.println("*********************************");
        Normalizer norm = new DecimalScaleNormalizer();
        norm.normalize(trainingSet);
        Normalizer norm1 = new DecimalScaleNormalizer();
        norm1.normalize(testSet);
        System.out.println("*********************************");


        // create multi layer perceptron
        MultiLayerPerceptron myMlPerceptron = new MultiLayerPerceptron(TransferFunctionType.SIGMOID, 3, 5, 1);
        myMlPerceptron.randomizeWeights(new WeightsRandomizer(new Random(123)));

        System.out.println(Arrays.toString(myMlPerceptron.getWeights()));

        myMlPerceptron.setLearningRule(new ResilientPropagation());
        myMlPerceptron.getLearningRule().setLearningRate(0.5);
        myMlPerceptron.getLearningRule().setMaxError(0.000008);
        // myMlPerceptron.getLearningRule().setMaxIterations(5000);

        // enable batch if using MomentumBackpropagation
//        if( myMlPerceptron.getLearningRule() instanceof MomentumBackpropagation )
//        	((MomentumBackpropagation)myMlPerceptron.getLearningRule()).setBatchMode(false);

        LearningRule learningRule = myMlPerceptron.getLearningRule();
        learningRule.addListener(this);

        // learn the training set
        System.out.println("Training neural network...");
        myMlPerceptron.learn(trainingSet);


        // test perceptron


        System.out.println("Testing trained neural network");
        testNeuralNetwork(myMlPerceptron, trainingSet);

        // save trained neural network
        myMlPerceptron.save("myMlPerceptron.nnet");

        // load saved neural network
        NeuralNetwork loadedMlPerceptron = NeuralNetwork.createFromFile("myMlPerceptron.nnet");

        // test loaded neural network
        System.out.println("Testing loaded neural network");
        testNeuralNetwork(loadedMlPerceptron, testSet);
    }

    /**
     * Prints network output for the each element from the specified training set.
     *
     * @param neuralNet neural network
     * @param testSet   test set
     */
    public static void testNeuralNetwork(NeuralNetwork neuralNet, DataSet testSet) {

        for (DataSetRow testSetRow : testSet.getRows()) {
            neuralNet.setInput(testSetRow.getInput());
            neuralNet.calculate();
            double[] networkOutput = neuralNet.getOutput();

            System.out.print("Input: " + Arrays.toString(testSetRow.getInput()));
            System.out.println(" Output: " + Arrays.toString(networkOutput));
        }
        for (DataSetRow testSetRow : testSet.getRows()) {
            neuralNet.setInput(testSetRow.getInput());
            neuralNet.calculate();
            double[] networkOutput = neuralNet.getOutput();
            System.out.println(Arrays.toString(networkOutput));
        }
    }

    @Override
    public void handleLearningEvent(LearningEvent event) {
        BackPropagation bp = (BackPropagation) event.getSource();
        if (event.getEventType() != LearningEvent.Type.LEARNING_STOPPED)
            System.out.println(bp.getCurrentIteration() + ". iteration : " + bp.getTotalNetworkError());
    }

    public double[] doubleTodouble (int index, ArrayList<Double[]> list){
        double[] array = new  double[list.get(index).length];
        for (int i = 0; i < list.get(index).length; i++) {
            array[i] = list.get(index)[i];
        }
        return array;
    }

}
package com.jhcomn.lambda.mllib.uw1000.training.svm;

import com.google.common.io.Files;
import com.jhcomn.lambda.mllib.base.serializer.ISerializer;
import com.jhcomn.lambda.mllib.base.serializer.LocalSerializer;
import libsvm.svm_model;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by shimn on 2017/11/13.
 */
public class SVMController extends SVMIrisMain {

    public static final String MODELNAME = File.separator + "svm.model";
    protected ISerializer serializer;

    public SVMController() {
        super();
        serializer = new LocalSerializer();
    }

    /**
     * svm 模型本地训练
     * @param dataPath
     * @param modelPath
     */
    public void trainLocal(String dataPath, String modelPath) {
        if (dataPath == null || modelPath == null || dataPath.equals("") || modelPath.equals("")) {
            System.out.println("train local params is null.");
            return;
        }
        //数据解析
        Dataset trainingData = MLFileReader.readFile(new File(dataPath));
        //模型训练
        svm_model model = svm.trainModel(trainingData);
        //模型本地序列化
        if (serializer == null)
            serializer = new LocalSerializer();
        serializer.serialize(model, modelPath + MODELNAME);
    }

    public double predictOneLocal(Observation obj, svm_model model) {
        if (obj == null || model == null || obj.equals("")) {
            System.out.println("predict one local params is null.");
            return -10;
        }
        if (svm == null)
            svm = new SVM();
        return svm.classifyInstance(obj, model);
    }

    public EvaluationMetrics predictAllLocal(String dataPath, String modelPath) {
        if (dataPath == null || modelPath == null || dataPath.equals("") || modelPath.equals("")) {
            System.out.println("train local params is null.");
            return null;
        }
//        List<Double> results = new ArrayList<Double>();
        int correct = 0;
        int incorrect = 0;
        //数据解析
        Dataset testingData = MLFileReader.readFile(new File(dataPath));
        //加载模型
        if (serializer == null)
            serializer = new LocalSerializer();
        svm_model model = serializer.deserialize(modelPath + MODELNAME);
        List<Observation> objs = testingData.getObservations();
        for (Observation obj : objs) {
            double predictedCode = predictOneLocal(obj, model);
            System.out.println("result = " + predictedCode);
            if (predictedCode == testingData.getClassCode(obj)) {
                correct++;
            } else {
                incorrect++;
            }
//            results.add(predictOneLocal(obj, model));
        }
        return new EvaluationMetrics(correct, incorrect);
    }

    public void writeResultsFile(String resultsPath, String modelPath,
                                  EvaluationMetrics metrics) {
        StringBuilder results = new StringBuilder();
        try {
            File resultsDirectory = new File(resultsPath);
            if (!resultsDirectory.exists()) {
                resultsDirectory.mkdir();
            }

            File resultsFile = new File(FilenameUtils.concat(resultsPath, RESULTS_FILE));

            results.append("\nSVM Model Information:\n");
            if (serializer == null)
                serializer = new LocalSerializer();
            svm_model model = serializer.deserialize(modelPath + MODELNAME);
            for (int i = 0; i < model.nSV.length; i++) {
                results.append("\tNumber of support Vectors for class: " + i + " is: "
                        + model.nSV[i] + "\n");
            }

            results.append("\nCorrectly Classified: " + metrics.getCorrectlyClassified() + "\nIncorrectly Classified: "
                    + metrics.getIncorrectlyClassified() + "\n");

            Files.write(results.toString().getBytes(), resultsFile);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }
}

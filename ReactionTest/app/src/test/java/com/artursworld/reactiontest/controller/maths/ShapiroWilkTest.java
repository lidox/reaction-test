package com.artursworld.reactiontest.controller.maths;


import com.artursworld.reactiontest.controller.analysis.forecast.NelderMeadOptimizer;
import com.artursworld.reactiontest.controller.analysis.forecast.TripleExponentialSmoothing;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Generate data to make a Shapiro Wilk Test
 */
public class ShapiroWilkTest {

    double[] data = {
            88, 93, 173, 196, 218, 172, 191, 86, 70, 163,
            164, 198, 189, 168, 86, 95, 204, 215, 217, 222,
            197, 96, 83, 192, 190, 171, 183, 188, 88, 83,
            151, 127, 174, 199, 153, 95, 71, 193, 189, 191,
            182, 162, 64, 74, 179, 176, 189, 183, 167, 85,
            69, 188, 185, 189, 199, 144, 86, 70, 176, 192,
            176, 153, 181, 80, 73, 175, 182, 194, 191, 159,
            64, 80, 171, 181, 171, 170, 160, 70, 68, 178,
            143, 164, 150, 120, 51, 66, 92, 134, 116, 120,
            94, 47, 50, 108, 157, 138, 169, 138, 74, 60,
            170, 187, 161, 180, 163, 79, 78, 164, 177, 169,
            185, 145, 73, 70, 183, 184, 187, 162, 157, 62,
            68, 162, 159, 193, 194, 158, 86, 82, 172, 208,
            143, 158, 158, 59, 61, 184, 173, 193, 188, 157,
            72, 83, 181, 179, 200, 181, 168, 68, 69, 149,
            176, 167, 193, 170, 62, 70, 167, 181, 180, 186,
            146, 60, 65, 181, 169, 152, 158, 159, 61, 57,
            167, 136, 161, 187, 129, 67, 70, 164, 177, 161,
            182, 137, 78, 53, 141, 167, 173, 177, 154, 55,
            46, 177, 135, 126, 138, 94, 74, 46, 119, 143,
            146, 159, 149, 73, 49, 166, 158, 158, 131, 125,
            67, 55, 95};


    @Test
    public void testGetFilteredData() {
        // remove every Sa and So
        int j = 0;
        List<Double> list = new ArrayList<>();
        for(int i = 0; j < data.length; i++){
            if(i!=0 && i!=1)
                list.add(data[j]);
            if(i==6){
                i = 0;
            }
            j++;
        }
        assertTrue(list.get(0)== 173);
    }

    @Test
    public void testGenerateErrors() {

        // initial dataList
        List<Double> dataList = new ArrayList<>();
        for(int i = 0; i < 90 ; i++){
            dataList.add(data[i]);
        }

        // init some configuration
        List<Double> errorList = new ArrayList<>();
        int seasonLength = 7;


        for(int j = dataList.size(); j < data.length; j++){

            // dataList to array
            Double[] currentData = new Double[dataList.size()];
            currentData = dataList.toArray(currentData);
            double[] dataArray = ArrayUtils.toPrimitive(currentData);

            NelderMeadOptimizer.Parameters optimizedParams = NelderMeadOptimizer.optimize(dataArray, seasonLength);
            double predictedValue = TripleExponentialSmoothing.getPredictions(dataArray, seasonLength, optimizedParams.getAlpha(), optimizedParams.getBeta(), optimizedParams.getGamma(), 1).get(0);
            double observedValue = data[j];
            double error = Math.abs(observedValue - predictedValue);
            errorList.add(error);
            dataList.add(observedValue);
            dataList.remove(0);
        }

        System.out.println("Errors: " +errorList.toString());
        System.out.println("");
        // [7.588313927260074, 40.99967401503547, 8.147484954877754, 38.62905611858588, 48.19070688078432, 22.33256720058472, 31.038280021499048, 9.898979895774715,
        // 17.287818156825693, 11.692189016172748, 16.078007342788453, 11.61981325635773, 27.113737490338195, 14.646792893771675, 4.411631757048752, 4.4329228121888775,
        // 2.9317946236431993, 8.072312330115437, 6.077395128124579, 6.339793634607247, 11.611303171377898, 19.213331664111962, 6.291374817965277, 0.8283547660566626,
        // 21.938109468774826, 6.5123665116890095, 2.699269113406075, 29.564842672963778, 18.85932367466603, 19.18064942574466, 8.860803355413694, 1.821964585285798,
        // 11.778436182432841, 38.353529178529584, 1.4888140923781918, 15.138102763230478, 7.554077522191321, 3.1038378561949997, 3.4444498128034695, 26.563481158481864,
        // 64.39670985628709, 12.91622020299019, 21.950296106384258, 21.72993219791256, 3.7849898136949065, 31.08353208002484, 23.291823894549566, 27.800868210757045,
        // 7.45633379935245, 9.985645428354701, 6.279982300259775, 12.02772569878232, 5.972196171972257, 13.10129894285231, 27.105347490704816, 20.50830540779242,
        // 5.753764462359186, 19.126380932738144, 0.9215386726066015, 11.764439015975086, 16.65264515038089, 5.728360290505549, 24.828017941908, 0.23296716538226292,
        // 27.142395054694248, 5.782192955137802, 7.958205652079442, 2.657469372396349, 4.37439094756715, 2.22420521988127, 18.384872866775254, 4.030248997395887,
        // 3.240138805921646, 26.341423588757806, 23.432640935256842, 13.457742645784208, 2.2376973435987395, 26.402979536992518, 15.514916794808926, 6.343919611216847,
        // 19.345541723247095, 44.11023896292008, 32.38735973725176, 21.25940151023616, 36.35977323158943, 21.98770745137675, 2.8092976076114837, 4.099985707326397,
        // 4.940427872168698, 14.227337034127657, 13.053275787697999, 20.524356116712738, 26.0440502412074, 25.57210435266815, 13.229938063044045, 21.428163851117745,
        // 9.838529335448186, 3.216689204999682, 2.5358750927616143, 13.999999792454545, 8.547046065592596, 32.12491179373549, 47.93940309619214, 8.42273223132537,
        // 6.228600771042181, 18.30241982656834, 65.96472171598633, 26.789768259594382, 29.42581372296388, 23.097608680956824, 5.023419483841138, 7.518759250672758,
        // 16.541920732089295, 5.608111703585422, 21.266105133224812, 16.956032486687377, 10.960923983218095, 0.24810301142312596, 35.8364007290167, 20.604273146003678,
        // 22.71925489709828, 5.4125601737379725, 62.524847268727996]
        Double[] tmp = new Double[errorList.size()];
        tmp = errorList.toArray(tmp);
        double[] errorArray = ArrayUtils.toPrimitive(tmp);
        DescriptiveStatistics maths = new DescriptiveStatistics(errorArray);
        System.out.println("Whitebyte Errors: ");
        System.out.println("Durchschnitt: " +maths.getMean());
        System.out.println("Median: " +maths.getPercentile(50));
        System.out.println("StandardDeviation: " +maths.getStandardDeviation());
        System.out.println("Max. Abweichung: " +maths.getMax());
        System.out.println("Min. Abweichung: " +maths.getMin());

    }

}

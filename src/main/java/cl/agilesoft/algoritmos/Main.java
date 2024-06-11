package cl.agilesoft.algoritmos;

import cl.agilesoft.algoritmos.algorithm.AlgorithmFactory;
import cl.agilesoft.algoritmos.algorithm.SearchAlgorithm;
import cl.agilesoft.algoritmos.dto.MapDef;
import cl.agilesoft.algoritmos.dto.MyMap;

import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) throws Exception {

        final long initTime = System.currentTimeMillis();
        final MyMap map = MapHelper.createMap(MapDef.ATT532);
        // final MyMap map = MapHelper.createMap(MapDef.BERLIN52);
        // System.out.println(map);
        // SearchAlgorithm searchAlgorithm = AlgorithmFactory.getInstance(AlgorithmFactory.DEEP_SEARCH, map);
        // SearchAlgorithm searchAlgorithm = AlgorithmFactory.getInstance(AlgorithmFactory.GENETIC_SEARCH, map);
        SearchAlgorithm searchAlgorithm = AlgorithmFactory.getInstance(AlgorithmFactory.ANT_COLONY, map);
        searchAlgorithm.search();
        var bestSolution = searchAlgorithm.getSolution();
        if (bestSolution != null) {
            System.out.println("--------------------------------------------------------");
            System.out.println("--------------------------------------------------------");
            System.out.println("--------------------------------------------------------");
            System.out.println("Best solution: " + bestSolution.getRouteCost());
            final long totalTime = System.currentTimeMillis() - initTime;
            System.out.println("Total time: " + (totalTime) + " ms" + " || " + TimeUnit.MILLISECONDS.toSeconds(totalTime) + " sec || " + TimeUnit.MILLISECONDS.toMinutes(totalTime) + " min");
        }
        System.out.println("FIN");
    }

}
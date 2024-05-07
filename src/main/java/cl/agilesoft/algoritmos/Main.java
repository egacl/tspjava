package cl.agilesoft.algoritmos;

import cl.agilesoft.algoritmos.algorithm.AlgorithmFactory;
import cl.agilesoft.algoritmos.algorithm.SearchAlgorithm;
import cl.agilesoft.algoritmos.dto.MyMap;

public class Main {

    public static void main(String[] args) throws Exception {
        final long initTime = System.currentTimeMillis();
        final MyMap map = MapHelper.createMap(MapHelper.MAP_ATT532);
        // final MyMap map = MapHelper.createMap(MapHelper.MAP_BERLIN52);
        // System.out.println(map);
        // SearchAlgorithm searchAlgorithm = AlgorithmFactory.getInstance(AlgorithmFactory.DEEP_SEARCH, map);
        SearchAlgorithm searchAlgorithm = AlgorithmFactory.getInstance(AlgorithmFactory.GENETIC_SEARCH, map);
        searchAlgorithm.search();
        var bestSolution = searchAlgorithm.getSolution();
        if (bestSolution != null) {
            System.out.println(bestSolution.getRouteCost());
            System.out.println("Total time: " + (System.currentTimeMillis() - initTime));
        }
    }
}
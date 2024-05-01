package cl.agilesoft.algoritmos;

import cl.agilesoft.algoritmos.algorithm.AlgorithmFactory;
import cl.agilesoft.algoritmos.algorithm.SearchAlgorithm;
import cl.agilesoft.algoritmos.dto.MyMap;

public class Main {

    public static void main(String[] args) throws Exception {
        // final MyMap map = MapHelper.createMap("berlin52.dat");
        final MyMap map = MapHelper.createMap("att532.dat");
        // System.out.println(map);
        SearchAlgorithm searchAlgorithm = AlgorithmFactory.getInstance(AlgorithmFactory.GENETIC_SEARCH, map);
        searchAlgorithm.search();
        var bestSolution = searchAlgorithm.getSolution();
    }
}
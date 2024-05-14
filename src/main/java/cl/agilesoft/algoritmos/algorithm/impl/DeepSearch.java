package cl.agilesoft.algoritmos.algorithm.impl;

import cl.agilesoft.algoritmos.MapHelper;
import cl.agilesoft.algoritmos.Parameters;
import cl.agilesoft.algoritmos.algorithm.SearchAlgorithm;
import cl.agilesoft.algoritmos.dto.MyMap;
import cl.agilesoft.algoritmos.dto.Tour;

public class DeepSearch implements SearchAlgorithm {

    private final MyMap map;
    private Tour bestNodesSolution;

    public DeepSearch(MyMap map) {
        this.map = map;
        this.bestNodesSolution = null;
    }

    @Override
    public void search() {
        long initTime = System.currentTimeMillis();
        for (int i = 1; i <= Parameters.SEARCH_ITERATIONS; i++) {
            final Tour actualTour = MapHelper.createTour(this.map);
            actualTour.findOptimalSolution(this.bestNodesSolution);
            if (this.bestNodesSolution == null
                    || actualTour.getRouteCost() < this.bestNodesSolution.getRouteCost()) {
                // System.out.println("Solucion mejor encontrada: " + actualTour.getRouteCost());
                this.bestNodesSolution = actualTour;
            }
        }
        long totalTime = System.currentTimeMillis() - initTime;
        // System.out.println("Tiempo total " + totalTime + " ms");
    }

    @Override
    public Tour getSolution() {
        return this.bestNodesSolution;
    }

}

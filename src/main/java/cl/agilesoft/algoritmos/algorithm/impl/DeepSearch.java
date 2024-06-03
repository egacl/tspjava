package cl.agilesoft.algoritmos.algorithm.impl;

import cl.agilesoft.algoritmos.MapHelper;
import cl.agilesoft.algoritmos.algorithm.SearchAlgorithm;
import cl.agilesoft.algoritmos.dto.DeepSearchParams;
import cl.agilesoft.algoritmos.dto.MyMap;
import cl.agilesoft.algoritmos.dto.Tour;

public class DeepSearch implements SearchAlgorithm {

    private final MyMap map;
    private final DeepSearchParams params;
    private Tour bestNodesSolution;

    public DeepSearch(final MyMap map, final DeepSearchParams params) {
        this.map = map;
        this.params = params;
        this.bestNodesSolution = null;
    }

    @Override
    public void search() {
        for (int i = 1; i <= params.getSearchIterations(); i++) {
            // final Tour actualTour = MapHelper.createRandomTour(this.map);
            final Tour actualTour = MapHelper.createEfficientRandomTour(this.map);
            actualTour.findOptimalSolution(this.bestNodesSolution, this.params);
            if (this.bestNodesSolution == null
                    || actualTour.getRouteCost() < this.bestNodesSolution.getRouteCost()) {
                this.bestNodesSolution = actualTour;
                if (this.bestNodesSolution.getRouteCost() == this.map.getBestSolutionResult()) {
                    return;
                }
            }
        }
    }

    @Override
    public Tour getSolution() {
        return this.bestNodesSolution;
    }

}

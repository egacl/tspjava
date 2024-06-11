package cl.agilesoft.algoritmos.dto;

import java.math.BigInteger;

public class Pheromones {

    private final MyMap map;
    private final int[][] route;

    public Pheromones(final MyMap map) {
        this.map = map;
        this.route = new int[this.map.getDistancesMap().length][];
        for (int i = 0; i < this.route.length; i++) {
            this.route[i] = new int[this.map.getDistancesMap().length];
            for (int j = 0; j < this.route[i].length; j++) {
                this.route[i][j] = 0;
            }
        }
    }

    public void emitPheromones(Node nodeA, Node nodeB, int pheromoneQty) {
        this.route[nodeA.id][nodeB.id] += pheromoneQty;
    }

    public int getPheromones(Node nodeA, Node nodeB) {
        return this.route[nodeA.id][nodeB.id];
    }

    public void evaporation(int evaporationQty) {
        for (int i = 0; i < this.route.length; i++) {
            this.route[i] = new int[this.map.getDistancesMap().length];
            for (int j = 0; j < this.route[i].length; j++) {
                int evaporationResult = this.route[i][j] - evaporationQty;
                this.route[i][j] = Math.max(evaporationResult, BigInteger.ZERO.intValue());
            }
        }
    }

}

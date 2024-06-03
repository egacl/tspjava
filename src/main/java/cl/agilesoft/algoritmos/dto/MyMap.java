package cl.agilesoft.algoritmos.dto;

import cl.agilesoft.algoritmos.Parameters;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class MyMap {

    private final MapDef mapDef;
    private final int[][] distancesMap;
    private final List<List<Integer>> nodesCandidates;

    public MyMap(final MapDef mapDef, final int[][] distancesMap) {
        this.mapDef = mapDef;
        this.distancesMap = distancesMap;
        this.nodesCandidates = this.computeCandidates(distancesMap);
    }

    public int getBestSolutionResult() {
        return this.mapDef.getBestSolution();
    }

    public List<Integer> getNodeCandidates(int nodeId) {
        return this.nodesCandidates.get(nodeId);
    }

    public int getNodesDistance(Node a, Node b) {
        return this.distancesMap[a.id][b.id];
    }

    private List<List<Integer>> computeCandidates(int[][] cityDistances) {
        int n = cityDistances.length;
        List<List<Integer>> nodesCandidates = new ArrayList<>();
        // Inicializar la lista de listas para almacenar los índices de candidatos.
        for (int i = 0; i < n; i++) {
            nodesCandidates.add(new ArrayList<>());
        }
        for (int i = 0; i < n; i++) {
            // Lista para mantener los costes y los índices de cada ciudad relacionada.
            List<CostIndexPair> queue = new ArrayList<>();
            for (int j = 0; j < n; j++) {
                if (i == j) {
                    continue;
                }
                queue.add(new CostIndexPair(cityDistances[i][j], j));
            }
            // Ordenar la lista de CostIndexPair basado en el coste de menor a mayor.
            Collections.sort(queue);
            // Agregar los índices de los candidatos de menor coste a nodesCandidates.
            for (int k = 0; k < Parameters.CANDIDATES_LENGTH && k < queue.size(); k++) {
                nodesCandidates.get(i).add(queue.get(k).index);
            }
        }
        return nodesCandidates;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder("Distances Map for ");
        builder.append(this.mapDef.getFileName()).append(":\n");
        for (int i = 0; i < this.distancesMap.length; i++) {
            builder.append("\t[");
            for (int j = 0; j < this.distancesMap[i].length; j++) {
                builder.append(this.distancesMap[i][j]).append(",");
            }
            builder.append("],\n");
        }
        return builder.toString();
    }

    private static class CostIndexPair implements Comparable<CostIndexPair> {

        int cost;
        int index;

        public CostIndexPair(int cost, int index) {
            this.cost = cost;
            this.index = index;
        }

        @Override
        public int compareTo(CostIndexPair other) {
            return Integer.compare(this.cost, other.cost);
        }

    }

}

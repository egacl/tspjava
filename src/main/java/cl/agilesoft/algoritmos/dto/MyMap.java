package cl.agilesoft.algoritmos.dto;

import cl.agilesoft.algoritmos.Parameters;
import lombok.Getter;
import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.apache.commons.math3.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class MyMap {

    private final MapDef mapDef;
    private final int[][] distancesMap;
    private List<List<Integer>> nodesCandidates;
    private List<EnumeratedDistribution<CostIndexPair>> candidatesCumulativeProbability;

    public MyMap(final MapDef mapDef, final int[][] distancesMap) {
        this.mapDef = mapDef;
        this.distancesMap = distancesMap;
        this.computeCandidates(distancesMap);
    }

    public int getBestSolutionResult() {
        return this.mapDef.getBestSolution();
    }

    public List<Integer> getNodeCandidates(int nodeId) {
        return this.nodesCandidates.get(nodeId);
    }

    public List<Integer> getLinearProbabilityNodeCandidate(int nodeId) {
        final List<Integer> candidates = this.nodesCandidates.get(nodeId);
        final EnumeratedDistribution<CostIndexPair> distribution = this.candidatesCumulativeProbability.get(nodeId);
        final List<Integer> probCandidatesList = new ArrayList<>(candidates.size());
        for (int i = 0; i < candidates.size(); i++) {
            probCandidatesList.add(distribution.sample().index);
        }
        return probCandidatesList;
    }

    public int getNodesDistance(Node a, Node b) {
        return this.distancesMap[a.id][b.id];
    }

    private void computeCandidates(int[][] cityDistances) {
        int n = cityDistances.length;
        List<Pair<CostIndexPair, Double>> distribution = new ArrayList<>();
        this.nodesCandidates = new ArrayList<>();
        this.candidatesCumulativeProbability = new ArrayList<>();
        // Inicializar la lista de listas para almacenar los índices de candidatos.
        for (int i = 0; i < n; i++) {
            this.nodesCandidates.add(new ArrayList<>());
            this.candidatesCumulativeProbability.add(i, null);
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
            double totalWeight = 0;
            // Agregar los índices de los candidatos de menor coste a nodesCandidates.
            for (int k = 0; k < Parameters.CANDIDATES_LENGTH && k < queue.size(); k++) {
                final CostIndexPair costIndex = queue.get(k);
                // se agregan los candidatos
                this.nodesCandidates.get(i).add(costIndex.index);
                totalWeight += (k + 1);
            }
            for (int k = 0; k < Parameters.CANDIDATES_LENGTH && k < queue.size(); k++) {
                final CostIndexPair costIndex = queue.get(k);
                double probability = (k + 1) / totalWeight;
                distribution.add(new Pair<>(costIndex, probability));
            }
            // se agrega candidatos con probabilidad lineal en base al costo
            this.candidatesCumulativeProbability.set(i, new EnumeratedDistribution<>(distribution));
        }
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

    private static final class CostIndexPair implements Comparable<CostIndexPair> {

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

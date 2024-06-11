package cl.agilesoft.algoritmos.dto;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Getter
public class Ant extends Tour {

    private final int id;

    public Ant(final int id, final MyMap map) {
        super(map);
        this.id = id;
    }

    public void createTour(final Pheromones pheromonesRoute) {
        final int totalNodes = this.map.getDistancesMap().length;
        Node[] nodesRoute = new Node[totalNodes];
        for (int i = 0; i < totalNodes; i++) {
            nodesRoute[i] = new Node(-1, null, null, -1);
        }
        final List<Boolean> visited = new ArrayList<>(Collections.nCopies(totalNodes, false));
        int initialNode = ThreadLocalRandom.current().nextInt(totalNodes);
        int actualNode = initialNode;
        int cont = 0;
        while (cont++ < totalNodes) {
            nodesRoute[actualNode].id = actualNode;
            visited.set(actualNode, true);
            int nextNode = initialNode;
            if (cont < totalNodes) {
                final List<Integer> availableCandidates = new ArrayList<>();
                final List<Integer> nodeCandidates = this.map.getLinearProbabilityNodeCandidate(actualNode);
                for (Integer nodeCandidate : nodeCandidates) {
                    if (!visited.get(nodeCandidate)) {
                        availableCandidates.add(nodeCandidate);
                    }
                }
                if (!availableCandidates.isEmpty()) {
                    int randomCandidateIndex = ThreadLocalRandom.current().nextInt(availableCandidates.size());
                    nextNode = availableCandidates.get(randomCandidateIndex);
                } else {
                    // TODO IMPLEMENTAR SELECCCION POR CANTIDAD DE FEROMONAS
                    int posibleCandidateIndex = ThreadLocalRandom.current().nextInt(totalNodes);
                    while (visited.get(posibleCandidateIndex)) {
                        posibleCandidateIndex = (posibleCandidateIndex + 1) % totalNodes;
                    }
                    nextNode = posibleCandidateIndex;
                }
            }
            nodesRoute[actualNode].next = nodesRoute[nextNode];
            nodesRoute[nextNode].previous = nodesRoute[actualNode];
            actualNode = nextNode;
        }
        Node nodeInit = nodesRoute[0];
        nodeInit.position = 0;
        Node nodeActual = nodeInit.next;
        int position = 1;
        while (nodeActual != nodeInit) {
            nodeActual.position = position++;
            nodeActual = nodeActual.next;
        }
        this.nodes = nodesRoute;
        this.routeCost = this.calculateDistance();
    }

    public void emitPheromone(final Pheromones pheromonesRoute, final int pheromonesQty) {
        Node currentNode = this.nodes[0];
        do {
            Node nextNode = currentNode.next;
            pheromonesRoute.emitPheromones(currentNode, nextNode, pheromonesQty);
            currentNode = nextNode;
        } while (currentNode != this.nodes[0]);
    }

}

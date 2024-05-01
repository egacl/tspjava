package cl.agilesoft.algoritmos.dto;

import cl.agilesoft.algoritmos.Parameters;
import lombok.Getter;
import lombok.ToString;

import java.util.Random;

@Getter
@ToString
public class Tour {

    private static Random RANDOM = new Random();

    private final MyMap map;
    private final Node[] nodes;
    private int routeCost;

    public Tour(final MyMap map, final Node[] nodes) {
        this.map = map;
        this.nodes = nodes;
    }

    public void findOptimalSolution(Tour bestNodesSolution) {
        routeCost = this.calculateDistance();
        Node t0;
        Node t1;
        int cont = 0;
        final int solutionLength = this.nodes.length;
        final int endSearch = Parameters.SOLUTION_MULTIPLIER * solutionLength;
        while (cont < endSearch) {
            t0 = this.getRandomNode();
            t1 = t0.next;
            if (bestNodesSolution != null) {
                if (this.hasSameBestEdge(bestNodesSolution, t0, t1)) {
                    cont++;
                    continue;
                }
            }
            final Movement movement = this.getMovement(t0, t1);
            if (movement != null) {
                if (movement.t4 == null) {
                    // movimiento 2-opt
                    this.makeMove(t0, t1, movement.t2, movement.t3);
                } else {
                    // movimiento 3-opt
                    this.makeMove(t0, t1, movement.t2, movement.t3);
                    this.makeMove(t0, movement.t3, movement.t4, movement.t5);
                }
                routeCost = routeCost - movement.revenue;
                cont = 0;
            } else {
                cont++;
            }
        }
    }

    public Node getNodeById(int nodeId) {
        return this.nodes[nodeId];
    }

    public Node getRandomNode() {
        int nodeIndex = RANDOM.nextInt(this.nodes.length);
        return nodes[nodeIndex];
    }

    public int calculateDistance() {
        int totalDistance = 0;
        Node currentNode = this.nodes[0];
        do {
            Node nextNode = currentNode.next;
            totalDistance += this.map.getNodesDistance(currentNode, nextNode);
            currentNode = nextNode;
        } while (currentNode != this.nodes[0]);
        return totalDistance;
    }

    private boolean hasSameBestEdge(Tour bestNodesSolution, Node t0, Node t1) {
        return (bestNodesSolution.getNodeById(t0.id).next.id == t1.id
                || bestNodesSolution.getNodeById(t0.id).previous.id == t1.id);
    }

    private Movement getMovement(final Node t0, final Node t1) {
        int t0t1ActualCost = this.map.getNodesDistance(t0, t1);
        for (int t2CandidateID : this.map.getNodeCandidates(t1.id)) {
            final Node t2Candidate = this.nodes[t2CandidateID];
            if (t2Candidate == t1 || t2Candidate == t1.next || t2Candidate == t1.previous) {
                continue;
            }
            int t1t2NewCost = this.map.getNodesDistance(t1, t2Candidate);
            int g0 = t0t1ActualCost - t1t2NewCost;
            if (g0 <= 0) {
                // se descarta ya que no cumple por la ganancia 0
                continue;
            }
            final Node t3Candidate = t2Candidate.previous;
            int t2t3ActualCost = this.map.getNodesDistance(t2Candidate, t3Candidate);
            int t0t3NewCost = this.map.getNodesDistance(t0, t3Candidate);
            int g1 = g0 + t2t3ActualCost - t0t3NewCost;
            if (g1 <= 0) {
                for (int t4CandidateID : this.map.getNodeCandidates(t3Candidate.id)) {
                    final Node t4Candidate = this.nodes[t4CandidateID];
                    if (t4Candidate == t3Candidate.next || t4Candidate == t3Candidate.previous) {
                        continue;
                    }
                    int t3t4NewCost = this.map.getNodesDistance(t3Candidate, t4Candidate);
                    int g1t4 = g0 + (t2t3ActualCost - t3t4NewCost);
                    if (g1t4 <= 0) {
                        continue;
                    }
                    Node t5Candidate = t4Candidate.previous;
                    if (this.between(t1, t3Candidate, t4Candidate)) {
                        t5Candidate = t4Candidate.next;
                    }
                    int t4t5ActualCost = this.map.getNodesDistance(t4Candidate, t5Candidate);
                    int t0t5NewCostCost = this.map.getNodesDistance(t5Candidate, t0);
                    int g2 = g1t4 + (t4t5ActualCost - t0t5NewCostCost);
                    if (g2 > 0) {
                        final Movement movement = new Movement();
                        movement.t2 = t2Candidate;
                        movement.t3 = t3Candidate;
                        movement.t4 = t4Candidate;
                        movement.t5 = t5Candidate;
                        movement.revenue = g2;
                        return movement;
                    }
                }
            } else {
                final Movement movement = new Movement();
                movement.t2 = t2Candidate;
                movement.t3 = t3Candidate;
                movement.revenue = g1;
                return movement;
            }
        }
        return null;
    }

    private boolean between(Node minor, Node mayor, Node between) {
        int betweenPos = between.position;
        int minorPos = minor.position;
        int mayorPos = mayor.position;
        return (minorPos <= betweenPos && betweenPos <= mayorPos)
                || (mayorPos < minorPos
                && (minorPos <= betweenPos || betweenPos <= mayorPos)
        );
    }

    private void makeMove(final Node t0, final Node t1, final Node t2, final Node t3) {
        final int routeSize = this.nodes.length;
        int nodesQtyT3t1 = t3.position - t1.position;
        if (nodesQtyT3t1 < 0) {
            nodesQtyT3t1 += routeSize;
        }
        int nodesQtyT0t2 = t0.position - t2.position;
        if (nodesQtyT0t2 < 0) {
            nodesQtyT0t2 += routeSize;
        }
        if (t0.next == t1) {
            if (nodesQtyT3t1 <= nodesQtyT0t2) {
                // si esto se cumple, se debe mover desde t0 a t3, hasta llegar a t2
                this.move(t0, t1, t2, t3);
            } else {
                // si esto se cumple, se debe mover desde t3 a t1 hasta llegar a t2
                this.move(t3, t2, t1, t0);
            }
        } else {
            if (nodesQtyT3t1 <= nodesQtyT0t2) {
                this.move(t1, t0, t3, t2);
            } else {
                this.move(t2, t3, t0, t1);
            }
        }
    }

    private void move(final Node t0, final Node t1, final Node t2, final Node t3) {
        Node actualNodeMove = t1;
        int position = t3.position;
        final int routeSize = this.nodes.length;
        while (actualNodeMove != t2) {
            actualNodeMove.position = position;
            position--;
            if (position < 0) {
                position = routeSize - 1;
            }
            ;
            Node aux = actualNodeMove.next;
            actualNodeMove.next = actualNodeMove.previous;
            actualNodeMove.previous = aux;
            actualNodeMove = actualNodeMove.previous;
        }
        t0.next = t3;
        t3.previous = t0;
        t1.next = t2;
        t2.previous = t1;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder("Tour Nodes \n");
        for (int i = 0; i < this.nodes.length; i++) {
            builder.append("\t[");
            builder.append(this.nodes[i]);
            builder.append("],\n");
        }
        return builder.toString();
    }

    private static class Movement {

        public Node t2;
        public Node t3;
        public Node t4;
        public Node t5;
        public int revenue;


    }

}

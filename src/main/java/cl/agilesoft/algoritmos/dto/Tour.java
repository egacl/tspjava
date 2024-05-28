package cl.agilesoft.algoritmos.dto;

import cl.agilesoft.algoritmos.Parameters;
import cl.agilesoft.algoritmos.PmxHelper;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Getter
public class Tour implements Comparable<Tour> {

    // private static final Random RANDOM = new Random();

    private final MyMap map;
    private final Node[] nodes;
    private int routeCost;

    public Tour(final MyMap map, final Node[] nodes) {
        this.map = map;
        this.nodes = nodes;
    }

    public void findOptimalSolution(final Tour bestNodesSolution, final DeepSearchParams params) {
        this.routeCost = this.calculateDistance();
        Node t0;
        Node t1;
        int cont = 0;
        final int endSearch = params.getSolutionMultiplier();
        while (cont < endSearch) {
            t0 = this.getRandomNode();
            final Node init = t0;
            while (true) {
                t1 = t0.next;
                if (bestNodesSolution == null) {
                    break;
                }
                if (bestNodesSolution.getNodeById(t0.id).next.id != t1.id
                        && bestNodesSolution.getNodeById(t0.id).previous.id != t1.id) {
                    break;
                }
                t0 = t0.next;
                if (t0 == init) {
                    t0 = this.getRandomNode();
                    t1 = t0.next;
                    break;
                }
            }
            final Movement movement = this.getMovement(t0, t1, params);
            if (movement != null) {
                if (movement.t4 == null) {
                    // movimiento 2-opt
                    this.makeMove(t0, t1, movement.t2, movement.t3);
                } else {
                    // movimiento 3-opt
                    this.makeMove(t0, t1, movement.t2, movement.t3);
                    this.makeMove(t0, movement.t3, movement.t4, movement.t5);
                }
                this.routeCost = this.routeCost - movement.revenue;
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
        int nodeIndex = ThreadLocalRandom.current().nextInt(this.nodes.length);
        return this.nodes[nodeIndex];
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

    public Tour createChild(Tour parent, Tour bestSolution, final DeepSearchParams params) {
        // final int[] child = PmxHelper.partiallyMappedCrossOver(this, parent);
        // return MapHelper.createTour(this.map, child);
        final Tour child = PmxHelper.partiallyMappedCrossOverv2(this, parent);
        child.routeCost = child.calculateDistance();
        child.findOptimalSolution(bestSolution, params);
        return child;
    }

    public void mutate() {
        final List<CostNodePair> candidates = new ArrayList<>();
        Node t0 = this.getRandomNode();
        Node t1 = t0.next;
        for (int t2CandidateID : this.map.getNodeCandidates(t1.id)) {
            final Node t2Candidate = this.nodes[t2CandidateID];
            if (t1.next.id == t2Candidate.id || t0.id == t2Candidate.id) {
                continue;
            }
            int g0 = this.map.getNodesDistance(t0, t1) - this.map.getNodesDistance(t1, t2Candidate);
            if (g0 > 0) {
                candidates.add(new CostNodePair(g0, t2Candidate));
            }
        }
        if (candidates.isEmpty()) {
            return;
        }
        final int randomCandidate = ThreadLocalRandom.current().nextInt(candidates.size());
        final CostNodePair selected = candidates.get(randomCandidate);
        final Node t2 = selected.node;
        final Node t3 = t2.previous;
        final int gtotal = selected.cost + this.map.getNodesDistance(t2, t3) - this.map.getNodesDistance(t3, t0);
        int t3t1 = t3.position - t1.position;
        if (t3t1 < 0) {
            t3t1 += this.nodes.length;
        }
        int t0t2 = t0.position - t2.position;
        if (t0t2 < 0) {
            t0t2 += this.nodes.length;
        }
        if (t3t1 < t0t2) {
            this.move(t0, t1, t2, t3);
        } else {
            this.move(t3, t2, t1, t0);
        }
        this.routeCost = this.routeCost - gtotal;
    }

    public int calcCompatibility(final Tour other) {
        int sum = 0;
        for (int i = 0; i <= Parameters.TOUR_AFINITY; i++) {
            final Node node = this.getRandomNode();
            if (node.next.id != other.getNodeById(node.id).next.id
                    && node.next.id != other.getNodeById(node.id).previous.id) {
                sum++;
            }
            if (node.previous.id != other.getNodeById(node.id).next.id
                    && node.previous.id != other.getNodeById(node.id).previous.id) {
                sum++;
            }
        }
        return sum;
        /*
        std::uniform_int_distribution<int> dist(0,largo-1);
        int suma=0;
        for (int i=0;i<CARACTERISTICAS_AFINIDAD;i++){
            int nodo=dist(engine);
            if (ady[nodo].siguiente->id!=otro->ady[nodo].siguiente->id && ady[nodo].siguiente->id!=otro->ady[nodo].anterior->id) suma++;
            if (ady[nodo].anterior->id!=otro->ady[nodo].siguiente->id && ady[nodo].anterior->id!=otro->ady[nodo].anterior->id) suma++;
        }
        return suma;

         */
    }

    private boolean hasSameBestEdge(Tour bestNodesSolution, Node t0, Node t1) {
        return (bestNodesSolution.getNodeById(t0.id).next.id == t1.id
                || bestNodesSolution.getNodeById(t0.id).previous.id == t1.id);
    }

    private Movement getMovement(final Node t0, final Node t1, final DeepSearchParams params) {
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
            /*
            if (params.isMoveWithJustG0()) {
                final Movement movement = new Movement();
                movement.t2 = t2Candidate;
                movement.t3 = t3Candidate;
                movement.revenue = g1;
                return movement;
            }
             */
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
        for (Node node : this.nodes) {
            builder.append("\t[");
            builder.append(node);
            builder.append("],\n");
        }
        return builder.toString();
    }

    @Override
    public int compareTo(Tour o) {
        return Integer.compare(this.routeCost, o.routeCost);
    }

    private static class Movement {

        public Node t2;
        public Node t3;
        public Node t4;
        public Node t5;
        public int revenue;

    }

    private static class CostNodePair {

        final int cost;
        final Node node;

        public CostNodePair(final int cost, final Node node) {
            this.cost = cost;
            this.node = node;
        }

    }

}

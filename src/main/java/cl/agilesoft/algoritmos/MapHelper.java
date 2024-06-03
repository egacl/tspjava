package cl.agilesoft.algoritmos;

import cl.agilesoft.algoritmos.dto.MapDef;
import cl.agilesoft.algoritmos.dto.MyMap;
import cl.agilesoft.algoritmos.dto.Node;
import cl.agilesoft.algoritmos.dto.Tour;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public final class MapHelper {

    private MapHelper() {
    }

    public static MyMap createMap(MapDef mapDef) throws IOException {
        InputStream inputStream = MapHelper.class.getClassLoader().getResourceAsStream(mapDef.getFileName());
        if (inputStream == null) {
            throw new IllegalArgumentException("El archivo " + mapDef.getFileName() + " no se encontró en el directorio resources.");
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            List<int[]> rows = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                int[] row = new int[values.length];
                for (int i = 0; i < values.length; i++) {
                    row[i] = Integer.parseInt(values[i]);
                }
                rows.add(row);
            }
            // Convertir la lista de arreglos en una matriz de enteros
            int[][] matrix = new int[rows.size()][];
            for (int i = 0; i < rows.size(); i++) {
                matrix[i] = rows.get(i);
            }
            return new MyMap(mapDef, matrix);
        }
    }

    public static Tour createRandomTour(final MyMap map) {
        return createTour(map, generateRandomRoute(map.getDistancesMap().length));
    }

    public static Tour createEfficientRandomTour(final MyMap map) {
        final int totalNodes = map.getDistancesMap().length;
        Node[] nodesRoute = new Node[totalNodes];
        for (int i = 0; i < totalNodes; i++) {
            nodesRoute[i] = new Node(-1, null, null, -1);
        }
        List<Boolean> visited = new ArrayList<>(Collections.nCopies(totalNodes, false));
        int initialNode = ThreadLocalRandom.current().nextInt(totalNodes);
        int actualNode = initialNode;
        int cont = 0;
        while (cont++ < totalNodes) {
            nodesRoute[actualNode].id = actualNode;
            visited.set(actualNode, true);
            int nextNode = initialNode;
            if (cont < totalNodes) {
                final List<Integer> availableCandidates = new ArrayList<>();
                final List<Integer> nodeCandidates = map.getNodeCandidates(actualNode);
                for (Integer nodeCandidate : nodeCandidates) {
                    if (!visited.get(nodeCandidate)) {
                        availableCandidates.add(nodeCandidate);
                    }
                }
                if (!availableCandidates.isEmpty()) {
                    int randomCandidateIndex = ThreadLocalRandom.current().nextInt(availableCandidates.size());
                    nextNode = availableCandidates.get(randomCandidateIndex);
                } else {
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
        return new Tour(map, nodesRoute);
    }

    private static Tour createTour(final MyMap map, final int[] route) {
        Node[] nodesRoute = new Node[route.length];
        Node nextNode = null;
        for (int i = 0; i < route.length - 1; i++) {
            int pos = route[i];
            Node actualNode = nodesRoute[pos];
            if (actualNode == null) {
                actualNode = new Node(pos, null, null, i);
                nodesRoute[pos] = actualNode;
            }
            pos = route[i + 1];
            nextNode = new Node(pos, null, actualNode, i + 1);
            actualNode.next = nextNode;
            nodesRoute[pos] = nextNode;
        }
        nextNode.next = nodesRoute[0];
        nodesRoute[0].previous = nextNode;
        return new Tour(map, nodesRoute);
    }

    public static int[] generateRandomRoute(int cityCount) {
        List<Integer> route = new ArrayList<>();
        for (int i = 1; i < cityCount; i++) {
            route.add(i);
        }
        Collections.shuffle(route, new Random());
        route.addFirst(0);
        return route.stream().mapToInt(Integer::intValue).toArray();
    }

}

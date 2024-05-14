package cl.agilesoft.algoritmos.algorithm.impl;

import cl.agilesoft.algoritmos.Parameters;
import cl.agilesoft.algoritmos.StatsHelper;
import cl.agilesoft.algoritmos.algorithm.SearchAlgorithm;
import cl.agilesoft.algoritmos.dto.MyMap;
import cl.agilesoft.algoritmos.dto.Tour;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class GeneticSearch implements SearchAlgorithm {

    private static final AtomicInteger atomicIndex = new AtomicInteger(0);

    private final MyMap map;
    private final ExecutorService executor;
    private Tour bestSolutionEver;
    private List<Tour> population;
    private List<Tour> nextGeneration;

    public GeneticSearch(final MyMap map) {
        this.map = map;
        this.executor = Executors.newFixedThreadPool(Parameters.CREATE_POPULATION_THREADS);
    }

    @Override
    public void search() {
        try {
            for (int iteration = 0; iteration < Parameters.GENETIC_SEARCH_ITERATIONS; iteration++) {
                final long initTime = System.currentTimeMillis();
                int counter = 0;
                // se crea la poblacion
                this.createPopulation();

                while (counter < Parameters.NEW_GENERATION_WITHOUT_IMPROVEMENT_COUNTER) {
                    counter++;
                    this.createNextGeneration();
                }
                this.printCreatePopulationStats(this.population, initTime);
            }
        } finally {
            this.executor.shutdown();
        }
    }

    private void createPopulation() {
        atomicIndex.set(0);
        this.population = new ArrayList<>(Parameters.POPULATION_QTY);
        final List<Future<Void>> asyncPopulationTask = new ArrayList<>();
        int chunkSize = (int) Math.ceil((double) Parameters.POPULATION_QTY / Parameters.CREATE_POPULATION_THREADS);
        final long initTime = System.currentTimeMillis();
        for (int k = 0; k < Parameters.CREATE_POPULATION_THREADS; k++) {
            int start = k * chunkSize;
            int end = Math.min(start + chunkSize, Parameters.POPULATION_QTY);
            // se crean las tareas asyncronas para crear la poblacion
            asyncPopulationTask.add(this.executor.submit(new CreatePopulation((end - start))));
        }
        try {
            // se espera a que todas las hebras entreguen sus resultados
            for (Future<Void> asyncResponse : asyncPopulationTask) {
                asyncResponse.get();
            }
        } catch (Exception err) {
            throw new RuntimeException("Error durante la creacion de la poblacion", err);
        }
        // se ordenan las poblaciones de menor a mayor
        Collections.sort(this.population);
        this.bestSolutionEver = this.population.getFirst();
        // this.printCreatePopulationStats(this.population, initTime);
    }

    private void createNextGeneration() {
        // System.out.println("Crear generacion");
        atomicIndex.set(0);
        this.nextGeneration = new ArrayList<>(Parameters.NEXT_GENERATION_POPULATION_QTY);
        final long initTime = System.currentTimeMillis();
        final List<Future<Void>> asyncPopulationTask = new ArrayList<>();
        for (int k = 0; k < Parameters.CREATE_POPULATION_THREADS; k++) {
            asyncPopulationTask.add(this.executor.submit(new CreateNextGenerationTask()));
        }
        try {
            // se espera a que todas las hebras entreguen sus resultados
            for (Future<Void> asyncResponse : asyncPopulationTask) {
                asyncResponse.get();
            }
        } catch (Exception err) {
            throw new RuntimeException("Error durante la creacion de la siguiente generacion", err);
        }
        for (int k = Parameters.PREVIOUS_GENERATION_SURVIVORS_QTY - 1; k < Parameters.NEXT_GENERATION_POPULATION_QTY; k++) {
            this.population.add(k, this.nextGeneration.get(k));
        }
        // se ordenan las poblaciones de menor a mayor
        Collections.sort(this.population);
        this.bestSolutionEver = this.population.getFirst();
        this.population = this.population.subList(0, Parameters.POPULATION_QTY);
        // this.printCreatePopulationStats(this.population, initTime);
    }

    @Override
    public Tour getSolution() {
        return this.bestSolutionEver;
    }

    private void printCreatePopulationStats(final List<Tour> list, final long initTime) {
        long totalTime = System.currentTimeMillis() - initTime;
        var avg = StatsHelper.calculateAvg(list);
        var standardDeviation = StatsHelper.calculateStandardDeviation(list);
        var minorMayor = StatsHelper.getMinorMayor(list);
        System.out.println("----------------------------------------");
        System.out.println("Creacion terminada");
        System.out.println("\t > Poblacion: " + list.size());
        System.out.println("\t > Promedio: " + avg);
        System.out.println("\t > Desviacion estandar: " + standardDeviation);
        System.out.println("\t > Menor : " + minorMayor.minor.getRouteCost() + " | Mayor : " + minorMayor.mayor.getRouteCost());
        System.out.println("Tiempo total " + totalTime + " ms");
    }

    private class CreatePopulation implements Callable<Void> {

        private static final Object CREATE_POP_SYNC_OBJECT = new Object();

        private final int populationQty;

        private CreatePopulation(final int populationQty) {
            this.populationQty = populationQty;
        }

        @Override
        public Void call() throws Exception {
            try {
                for (int k = 0; k < this.populationQty; k++) {
                    final DeepSearch deepSearch = new DeepSearch(GeneticSearch.this.map);
                    deepSearch.search();
                    var solution = deepSearch.getSolution();
                    synchronized (CREATE_POP_SYNC_OBJECT) {
                        GeneticSearch.this.population.add(atomicIndex.getAndIncrement(), solution);
                    }
                }
            } catch (Exception err) {
                err.printStackTrace();
            }
            return null;
        }
    }

    private class CreateNextGenerationTask implements Callable<Void> {

        private static final Object NEXTGEN_POP_SYNC_OBJECT = new Object();
        private static final Object BESTNODE_SYNC_OBJECT = new Object();
        private boolean run;

        private CreateNextGenerationTask() {
            this.run = true;
        }

        @Override
        public Void call() throws Exception {
            try {
                while (this.run) {
                    // System.out.println("Se inicia torneo");
                    final Tour[] parents = this.getTournamentParents();
                    for (int childrenIndex = 0; childrenIndex < Parameters.NUMBER_OF_CHILDREN; childrenIndex++) {
                        int individualIndex = 0;
                        synchronized (NEXTGEN_POP_SYNC_OBJECT) {
                            individualIndex = atomicIndex.getAndIncrement();
                            // System.out.println("Se incrementa indice " + individualIndex + " - total = " + Parameters.NEXT_GENERATION_POPULATION_QTY);
                            if (individualIndex >= Parameters.NEXT_GENERATION_POPULATION_QTY) {
                                this.run = false;
                                break;
                            }
                        }
                        // System.out.println("Se procede a crear hijo");
                        final Tour children = parents[0].createChild(parents[1], Optional.of(GeneticSearch.this.bestSolutionEver));
                        synchronized (BESTNODE_SYNC_OBJECT) {
                            if (children.getRouteCost() < GeneticSearch.this.bestSolutionEver.getRouteCost()) {
                                GeneticSearch.this.bestSolutionEver = children;
                            }
                        }
                        GeneticSearch.this.nextGeneration.add(individualIndex, children);
                    }
                }
            } catch (Exception err) {
                err.printStackTrace();
            }
            return null;
        }

        private Tour[] getTournamentParents() {
            final PriorityQueue<Tour> candidates = new PriorityQueue<>(Parameters.TOURNAMENT_SIZE, Tour::compareTo);
            for (int i = 0; i < Parameters.TOURNAMENT_SIZE; i++) {
                candidates.add(this.getRandomTour());
            }
            final Tour[] parents = new Tour[2];
            if (ThreadLocalRandom.current().nextInt(Parameters.HUNDRED_PERCENT) < 50) {
                parents[0] = candidates.poll();
                parents[1] = candidates.poll();
            } else {
                parents[1] = candidates.poll();
                parents[0] = candidates.poll();
            }
            return parents;
        }

        private Tour getRandomTour() {
            return GeneticSearch.this.population.get(ThreadLocalRandom.current().nextInt(Parameters.POPULATION_QTY));
        }

    }

}

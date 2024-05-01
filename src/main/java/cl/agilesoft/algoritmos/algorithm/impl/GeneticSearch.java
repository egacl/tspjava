package cl.agilesoft.algoritmos.algorithm.impl;

import cl.agilesoft.algoritmos.Parameters;
import cl.agilesoft.algoritmos.StatsHelper;
import cl.agilesoft.algoritmos.algorithm.SearchAlgorithm;
import cl.agilesoft.algoritmos.dto.MyMap;
import cl.agilesoft.algoritmos.dto.Tour;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class GeneticSearch implements SearchAlgorithm {

    private final MyMap map;
    private final ExecutorService executor;

    public GeneticSearch(final MyMap map) {
        this.map = map;
        this.executor = Executors.newFixedThreadPool(Parameters.CREATE_POPULATION_THREADS);
    }

    @Override
    public void search() throws Exception {
        final List<Tour> population = new ArrayList<>(Parameters.POPULATION_QTY);
        final List<Future<List<Tour>>> asyncPopulationTask = new ArrayList<>();
        int chunkSize = (int) Math.ceil((double) Parameters.POPULATION_QTY / Parameters.CREATE_POPULATION_THREADS);
        final long initTime = System.currentTimeMillis();
        for (int k = 0; k < Parameters.CREATE_POPULATION_THREADS; k++) {
            int start = k * chunkSize;
            int end = Math.min(start + chunkSize, Parameters.POPULATION_QTY);
            // se crean las tareas asyncronas para crear la poblacion
            asyncPopulationTask.add(this.executor.submit(new CreatePopulation(this.map, (end - start))));
        }
        // se espera a que todas las hebras entreguen sus resultados
        for (Future<List<Tour>> asyncResponse : asyncPopulationTask) {
            population.addAll(asyncResponse.get());
        }
        this.printCreatePopulationStats(population, initTime);
        this.executor.shutdown();
    }

    @Override
    public Tour getSolution() {
        return null;
    }

    private void printCreatePopulationStats(final List<Tour> population, final long initTime) {
        long totalTime = System.currentTimeMillis() - initTime;
        var avg = StatsHelper.calculateAvg(population);
        var standardDeviation = StatsHelper.calculateStandardDeviation(population);
        var minorMayor = StatsHelper.getMinorMayor(population);
        System.out.println("Creacion de problacion terminada");
        System.out.println("\t > Poblacion: " + population.size());
        System.out.println("\t > Promedio: " + avg);
        System.out.println("\t > Desviacion estandar: " + standardDeviation);
        System.out.println("\t > Menor : " + minorMayor.minor.getRouteCost() + " | Mayor : " + minorMayor.mayor.getRouteCost());
        System.out.println("Tiempo total " + totalTime + " ms");
    }

    private record CreatePopulation(MyMap map, int populationQty) implements Callable<List<Tour>> {

        @Override
        public List<Tour> call() throws Exception {
            List<Tour> population = new ArrayList<>(this.populationQty);
            for (int k = 0; k < this.populationQty; k++) {
                final DeepSearch deepSearch = new DeepSearch(this.map);
                deepSearch.search();
                population.add(deepSearch.getSolution());
            }
            return population;
        }
    }
}

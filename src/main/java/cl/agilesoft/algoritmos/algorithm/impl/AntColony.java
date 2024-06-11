package cl.agilesoft.algoritmos.algorithm.impl;

import cl.agilesoft.algoritmos.Parameters;
import cl.agilesoft.algoritmos.StatsHelper;
import cl.agilesoft.algoritmos.algorithm.SearchAlgorithm;
import cl.agilesoft.algoritmos.dto.Ant;
import cl.agilesoft.algoritmos.dto.MyMap;
import cl.agilesoft.algoritmos.dto.Pheromones;
import cl.agilesoft.algoritmos.dto.Tour;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class AntColony implements SearchAlgorithm {

    private final MyMap map;
    private final ExecutorService executor;
    private final Pheromones pheromonesRoute;

    public AntColony(final MyMap map) {
        this.map = map;
        this.executor = Executors.newFixedThreadPool(Parameters.ANT_THREADS_QTY);
        this.pheromonesRoute = new Pheromones(map);
    }

    @Override
    public void search() {
        try {
            List<Ant> antList = new ArrayList<>(Parameters.ANT_ANTS_SIZE);
            long initTime = System.currentTimeMillis();
            final List<Future<Ant>> asyncAntRouteTask = new ArrayList<>();
            for (int i = 0; i < Parameters.ANT_ANTS_SIZE; i++) {
                final int finalI = i;
                asyncAntRouteTask.add(this.executor.submit(new Callable<Ant>() {
                    @Override
                    public Ant call() throws Exception {
                        final Ant ant = new Ant(finalI, AntColony.this.map);
                        ant.createTour(AntColony.this.pheromonesRoute);
                        return ant;
                    }
                }));
            }
            try {
                // se espera a que todas las hebras entreguen sus resultados
                for (Future<Ant> asyncResponse : asyncAntRouteTask) {
                    final Ant createdAnt = asyncResponse.get();
                    System.out.println("Fin crear ruta hormiga ID " + createdAnt.getId() + " => " + createdAnt.getRouteCost());
                    antList.add(asyncResponse.get());
                }
                System.out.println("Fin creacion de hormigas");
            } catch (Exception err) {
                throw new RuntimeException("Error durante la generacion de rutas", err);
            }
            // se ordenan las poblaciones de menor a mayor
            Collections.sort(antList);
            final List<Future<Void>> asyncAntPheromones = new ArrayList<>();
            // se toman las N primeras para emitir feromonas
            final List<Ant> bestNFirst = antList.subList(0, Parameters.ANT_PHEROMONES_CANDIDATES);
            System.out.println("Se obtienen las mejores " + Parameters.ANT_PHEROMONES_CANDIDATES + " hormigas para emitir feromonas");
            for (int i = 0; i < bestNFirst.size(); i++) {
                final int finalI = i;
                asyncAntPheromones.add(this.executor.submit(new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        final Ant ant = bestNFirst.get(finalI);
                        final int multliplier = Parameters.ANT_PHEROMONE_MULTIPLIEAR / (finalI + 1);
                        final int pheromonesQty = Parameters.ANT_PHEROMONE_DEFAULT_QTY * multliplier;
                        System.out.println("Hormiga ID " + ant.getId() + " con costo => " + ant.getRouteCost() + " emite " + pheromonesQty + " feromonas");

                        ant.emitPheromone(AntColony.this.pheromonesRoute, pheromonesQty);
                        return null;
                    }
                }));
            }
            try {
                // se espera a que todas las hebras entreguen sus resultados
                for (Future<Void> asyncResponse : asyncAntPheromones) {
                    asyncResponse.get();
                }
                System.out.println("Fin emicion de feromonas");
            } catch (Exception err) {
                throw new RuntimeException("Error durante la emision de feromonas", err);
            }
            this.pheromonesRoute.evaporation(Parameters.ANT_PHEROMONE_EVAPORATION);
            System.out.println("Fin de la evaporacion");
            printCreatePopulationStats(antList, initTime, 0, 0);
        } finally {
            this.executor.shutdown();
        }
    }

    private void printCreatePopulationStats(final List<Ant> list, final long initTime, final int iteration, final int generation) {
        long totalTime = System.currentTimeMillis() - initTime;
        var avg = StatsHelper.calculateAvg(list);
        var standardDeviation = StatsHelper.calculateStandardDeviation(list);
        var minorMayor = StatsHelper.getMinorMayor(list);
        System.out.print("> Iteracion: " + iteration);
        System.out.print("\t > Generacion: " + generation);
        System.out.print("\t > Menor : " + minorMayor.minor.getRouteCost());
        System.out.print("\t > Mayor : " + minorMayor.mayor.getRouteCost());
        // System.out.print("\t > Best ever: " + this.bestSolutionEver.getRouteCost());
        System.out.print("\t\t > Tiempo total: " + totalTime + " ms");
        System.out.print("\t\t > Poblacion: " + list.size());
        System.out.print("\t\t > Promedio: " + avg);
        System.out.print("\t\t > Desviacion estandar: " + standardDeviation);
        System.out.print("\n");
    }

    @Override
    public Tour getSolution() {
        return null;
    }

}

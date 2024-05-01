package cl.agilesoft.algoritmos;

import cl.agilesoft.algoritmos.dto.Tour;

import java.util.List;

public final class StatsHelper {

    private StatsHelper() {
    }

    public static double calculateAvg(final List<Tour> arr) {
        double suma = 0;
        for (Tour tour : arr) {
            suma += tour.getRouteCost();
        }
        return suma / arr.size();
    }

    public static double calculateStandardDeviation(final List<Tour> arr) {
        double promedio = calculateAvg(arr);
        double sumaCuadrados = 0;
        for (Tour tour : arr) {
            double diferencia = tour.getRouteCost() - promedio;
            sumaCuadrados += diferencia * diferencia;
        }
        return Math.sqrt(sumaCuadrados / arr.size());
    }

    public static MinorMayorResponse getMinorMayor(final List<Tour> arr) {
        Tour minor = arr.getFirst();
        Tour mayor = arr.getFirst();

        for (Tour actualTour : arr) {
            if (actualTour.getRouteCost() < minor.getRouteCost()) {
                minor = actualTour;
            }
            if (actualTour.getRouteCost() > mayor.getRouteCost()) {
                mayor = actualTour;
            }
        }
        return new MinorMayorResponse(minor, mayor);
    }

    public static final class MinorMayorResponse {
        public final Tour minor;
        public final Tour mayor;

        public MinorMayorResponse(final Tour minor, final Tour mayor) {
            this.minor = minor;
            this.mayor = mayor;
        }
    }

}

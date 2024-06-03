package cl.agilesoft.algoritmos;

public final class Parameters {

    public static final int CANDIDATES_LENGTH = 25;
    public static final int DEFAULT_SOLUTION_MULTIPLIER = 8;
    public static final int DEFAULT_SEARCH_ITERATIONS = 5000;

    public static final int THREADS_QTY = 10;
    public static final int GENETIC_MAIN_ITERATIONS = 6;
    public static final int NEW_GENERATION_WITHOUT_IMPROVEMENT_COUNTER = 35;
    public static final int GENETIC_SOLUTION_MULTIPLIER = 1;
    public static final int NEW_GENERATION_GENETIC_SOLUTION_MULTIPLIER = 2000;
    public static final int GENETIC_SEARCH_ITERATIONS = 1;
    public static final int GENETIC_SEARCH_NEXTGEN_ITERATIONS = 532 * 2;
    public static final int POPULATION_QTY = 1000;
    public static final int TOURNAMENT_SIZE = 6;
    public static final int NEXT_GENERATION_POPULATION_QTY = (int) (POPULATION_QTY * 1.02);
    public static final int PREVIOUS_GENERATION_SURVIVORS_QTY = (int) (POPULATION_QTY * 0.005);
    public static final int NUMBER_OF_CHILDREN = 1;
    public static final int HUNDRED_PERCENT = 100;
    public static final int MUTATION_PROBABILITY = 10;
    public static final int TOUR_AFINITY = 15;
    public static final int UMBRAL_MAX_AFINIDAD = 8;
    public static final int UMBRAL_MEDIA_AFINIDAD = 4;

    private Parameters() {
    }

}

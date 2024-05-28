package cl.agilesoft.algoritmos;

public final class Parameters {

    public static final int CANDIDATES_LENGTH = 25;
    public static final int DEFAULT_SOLUTION_MULTIPLIER = 8;
    public static final int DEFAULT_SEARCH_ITERATIONS = 100;

    public static final int THREADS_QTY = 5;
    public static final int GENETIC_MAIN_ITERATIONS = 20;
    public static final int NEW_GENERATION_WITHOUT_IMPROVEMENT_COUNTER = 50;
    public static final int GENETIC_SOLUTION_MULTIPLIER = 1;
    public static final int NEW_GENERATION_GENETIC_SOLUTION_MULTIPLIER = 10;
    public static final int GENETIC_SEARCH_ITERATIONS = 10;
    public static final int GENETIC_SEARCH_NEXTGEN_ITERATIONS = 50;
    public static final int POPULATION_QTY = 1000;
    public static final int TOURNAMENT_SIZE = 10;
    public static final int NEXT_GENERATION_POPULATION_QTY = (int) (POPULATION_QTY * 1.02);
    public static final int PREVIOUS_GENERATION_SURVIVORS_QTY = (int) (POPULATION_QTY * 0.005);
    public static final int NUMBER_OF_CHILDREN = 3;
    public static final int HUNDRED_PERCENT = 100;

    private Parameters() {
    }

}

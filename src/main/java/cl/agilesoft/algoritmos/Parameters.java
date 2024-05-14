package cl.agilesoft.algoritmos;

public final class Parameters {

    public static final int CANDIDATES_LENGTH = 25;
    public static final int SOLUTION_MULTIPLIER = 1;
    public static final int SEARCH_ITERATIONS = 3;

    public static final int CREATE_POPULATION_THREADS = 1;

    public static final int POPULATION_QTY = 1000;
    public static final int TOURNAMENT_SIZE = 6;
    public static final int NEXT_GENERATION_POPULATION_QTY = (int) (POPULATION_QTY * 1.02);
    public static final int PREVIOUS_GENERATION_SURVIVORS_QTY = (int) (POPULATION_QTY * 0.005);
    public static final int GENETIC_SEARCH_ITERATIONS = 5;
    public static final int NEW_GENERATION_WITHOUT_IMPROVEMENT_COUNTER = 40;
    public static final int NUMBER_OF_CHILDREN = 2;
    public static final int HUNDRED_PERCENT = 100;
    public static final int SWAP_PARENTS_PROBABILITY = 50;
    public static final int CHILDREN_MUTATION_PROBABILITY = 10;

    private Parameters() {
    }

}

package cl.agilesoft.algoritmos.algorithm;

import cl.agilesoft.algoritmos.Parameters;
import cl.agilesoft.algoritmos.algorithm.impl.AntColony;
import cl.agilesoft.algoritmos.algorithm.impl.DeepSearch;
import cl.agilesoft.algoritmos.algorithm.impl.GeneticSearch;
import cl.agilesoft.algoritmos.dto.DeepSearchParams;
import cl.agilesoft.algoritmos.dto.MyMap;

import java.security.InvalidAlgorithmParameterException;

public final class AlgorithmFactory {

    public static final int DEEP_SEARCH = 1;
    public static final int GENETIC_SEARCH = 2;
    public static final int ANT_COLONY = 3;


    private AlgorithmFactory() {
    }

    public static SearchAlgorithm getInstance(final int algorithmIdType, final MyMap map) throws InvalidAlgorithmParameterException {
        if (DEEP_SEARCH == algorithmIdType) {
            final DeepSearchParams params =
                    new DeepSearchParams(Parameters.DEFAULT_SEARCH_ITERATIONS
                            , Parameters.DEFAULT_SOLUTION_MULTIPLIER * map.getNodesCandidates().size());
            return new DeepSearch(map, params);
        } else if (GENETIC_SEARCH == algorithmIdType) {
            return new GeneticSearch(map);
        } else if (ANT_COLONY == algorithmIdType) {
            return new AntColony(map);
        }
        throw new InvalidAlgorithmParameterException("No existe el algoritmo con id: " + algorithmIdType);
    }

}

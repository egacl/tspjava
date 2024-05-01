package cl.agilesoft.algoritmos.algorithm;

import cl.agilesoft.algoritmos.algorithm.impl.DeepSearch;
import cl.agilesoft.algoritmos.algorithm.impl.GeneticSearch;
import cl.agilesoft.algoritmos.dto.MyMap;

import java.security.InvalidAlgorithmParameterException;

public final class AlgorithmFactory {

    public static final int DEEP_SEARCH = 1;
    public static final int GENETIC_SEARCH = 2;

    private AlgorithmFactory() {
    }

    public static SearchAlgorithm getInstance(final int algorithmIdType, final MyMap map) throws InvalidAlgorithmParameterException {
        if (DEEP_SEARCH == algorithmIdType) {
            return new DeepSearch(map);
        } else if (GENETIC_SEARCH == algorithmIdType) {
            return new GeneticSearch(map);
        }
        throw new InvalidAlgorithmParameterException("No existe el algoritmo con id: " + algorithmIdType);
    }

}

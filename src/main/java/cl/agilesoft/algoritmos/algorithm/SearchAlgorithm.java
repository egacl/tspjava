package cl.agilesoft.algoritmos.algorithm;

import cl.agilesoft.algoritmos.dto.Tour;

public interface SearchAlgorithm {

    void search() throws Exception;

    Tour getSolution();

}

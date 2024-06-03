package cl.agilesoft.algoritmos.dto;

import lombok.Getter;

@Getter
public enum MapDef {

    ATT532("att532.dat", 86756),
    BERLIN52("berlin52.dat", 7544);

    private final String fileName;
    private final int bestSolution;

    private MapDef(final String fileName, final int bestSolution) {
        this.fileName = fileName;
        this.bestSolution = bestSolution;
    }

}

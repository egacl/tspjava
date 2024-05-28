package cl.agilesoft.algoritmos.dto;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class DeepSearchParams {

    private final int searchIterations;

    private final int solutionMultiplier;

    private final boolean moveWithJustG0;

    public DeepSearchParams(final int searchIterations, final int solutionMultiplier) {
        this.searchIterations = searchIterations;
        this.solutionMultiplier = solutionMultiplier;
        this.moveWithJustG0 = false;
    }

    public DeepSearchParams(final int searchIterations, final int solutionMultiplier, final boolean moveWithJustG0) {
        this.searchIterations = searchIterations;
        this.solutionMultiplier = solutionMultiplier;
        this.moveWithJustG0 = moveWithJustG0;
    }

}

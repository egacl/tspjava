package cl.agilesoft.algoritmos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class RandomNumberGenerator {
    
    private final List<Integer> numbers;
    private AtomicInteger currentIndex;

    public RandomNumberGenerator(int max) {
        if (max <= 0) {
            throw new IllegalArgumentException("El valor maximo debe ser mayor que 0");
        }
        this.numbers = new ArrayList<>();
        this.initializeNumbers(max);
    }

    private void initializeNumbers(int max) {
        for (int i = 0; i < max; i++) {
            this.numbers.add(i);
        }
        Collections.shuffle(this.numbers, ThreadLocalRandom.current());
        this.currentIndex = new AtomicInteger(0);
    }

    public synchronized int getRandomNumber() {
        try {
            int currIndex = this.currentIndex.incrementAndGet();
            if (currIndex >= this.numbers.size()) {
                Collections.shuffle(this.numbers, ThreadLocalRandom.current());
                this.currentIndex.set(0);
                currIndex = this.currentIndex.getAndIncrement();
            }
            return this.numbers.get(currIndex);
        } catch (Exception err) {
            err.printStackTrace();
            throw new RuntimeException(err);
        }
    }

}

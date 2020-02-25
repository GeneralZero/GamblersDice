package zero.general.gamblerdice.model;

/**
 * Created by generalzero on 8/15/2017.
 */

import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;

public class GamblerDie implements Die {
    private final int size;
    private int[] state;
    private Random rnd;

    public GamblerDie() {
        this(6);
    }

    public GamblerDie(int size) {
        this.size = size;
        state = new int[size];
        Arrays.fill(state, 1);
        rnd = new Random();
    }

    @Override
    public int roll() {
        final int sum = IntStream.of(state).sum();
        final int r = rnd.nextInt(sum);

        int runningSum = 0;
        int result = -1;

        for (int i = 0; i < size; i++) {
            final int mark = state[i];
            runningSum += mark;

            if (r < runningSum && result == -1) {
                result = i;
                this.state[i] = 1;
            } else {
                this.state[i]++;
            }
        }

        return result + 1;
    }

    public double[] weights()
    {
        double[] ret = new double[this.size];

        final int sum = IntStream.of(state).sum();

        for (int i = 0; i < this.size; i++) {
            ret[i] = ((double)this.state[i]) / ((double) sum);
        }

        return ret;
    }

}
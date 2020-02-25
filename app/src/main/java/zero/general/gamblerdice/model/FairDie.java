package zero.general.gamblerdice.model;

/**
 * Created by generalzero on 8/15/2017.
 */

import java.security.SecureRandom;

public class FairDie implements Die {
    int nsides;
    SecureRandom rnd;

    public FairDie() {
        this(6);
    }

    public FairDie(int nsides) {
        this.nsides = nsides;
        rnd = new SecureRandom();
    }

    @Override
    public int roll() {
        return rnd.nextInt(nsides) + 1;
    }


}
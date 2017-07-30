package lando.systems.ld39.objects;

public class Stats {

    public int currentMoney = 0;
    public int moneyCollected = 0;
    public int powerupsCollected = 0;
    public int enemiesScrapped = 0;
    public float distanceTraveledPercent = 0;

    public Stats() {}

    /**
     * Merges stats together.  Everything is cumulative, EXCEPT for the current funds.  Feed those in manually.
     * @param stats
     * @param currentMoney
     */
    public void addRoundStats(Stats stats, int currentMoney) {
        this.currentMoney = currentMoney;
        this.moneyCollected += stats.moneyCollected;
        this.powerupsCollected += stats.powerupsCollected;
        this.enemiesScrapped += stats.enemiesScrapped;
        this.distanceTraveledPercent += stats.distanceTraveledPercent;
    }

}

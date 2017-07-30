package lando.systems.ld39.objects;

public class Stats {

    public int money = 0;
    public int powerupsCollected = 0;
    public int enemiesScrapped = 0;
    public float distanceTraveled = 0;

    public Stats() {}

    public void addStats(Stats stats) {
        this.money += stats.money;
        this.powerupsCollected += stats.powerupsCollected;
        this.enemiesScrapped += stats.enemiesScrapped;
        this.distanceTraveled += stats.distanceTraveled;
    }

}

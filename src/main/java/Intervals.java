public enum Intervals {
    TEST(5),
    THIRHY(30*60),
    FOURTY_FIVE(45*60),
    ONE_HOUR(60*60);

    private int sec;

    Intervals(int millis){
        this.sec = millis;
    }

    public int getSeconds() {
        return sec;
    }
}

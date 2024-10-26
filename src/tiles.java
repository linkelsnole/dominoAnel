public class tiles {
    private int first, second;
    private int degree;
    private boolean dooble;

    public tiles(int fir, int sec, int degree, boolean dooble) {
        first = fir;
        second = sec;
        degree = 0;
        this.dooble = dooble;
    }
    public void addDegree(int val) {
        degree += val;
        if (degree >= 360) degree -= 360;
        if (degree <= -360) degree += 360;
    }
    public void setDegree(int val) {
        degree = val;
    }
    public int getFirst() {
        return first;
    }
    public int getSecond() {
        return second;
    }
    public int getDegree() {
        return degree;
    }
    public boolean getDooble() {
        return dooble;
    }
}

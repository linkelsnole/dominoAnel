public class positions {
    public int x, y, value, type; // 0 - universal. 1 - upper. 2 - lower. 3 - left. 4 - right
    public positions(int x, int y, int value, int type) {
        this.x = x;
        this.y = y;
        this.value = value;
        this.type = type;
    }
}

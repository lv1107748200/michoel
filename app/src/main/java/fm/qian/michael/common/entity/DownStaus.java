package fm.qian.michael.common.entity;


/*
 * lv   2018/9/21
 */
public class DownStaus {

    private String id;
    private int isDown;

    public DownStaus(String id, int isDown) {
        this.id = id;
        this.isDown = isDown;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getIsDown() {
        return isDown;
    }

    public void setIsDown(int isDown) {
        this.isDown = isDown;
    }
}

package fm.qian.michael.net.entry;

/**
 * https://github.com/CymChad/BaseRecyclerViewAdapterHelper
 */
public class Video {
    private String img;
    private String name;
    private int id;

    public Video(int id,String name) {
        this.name = name;
        this.id = id;
    }

    public Video(String img, String name) {
        this.img = img;
        this.name = name;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}

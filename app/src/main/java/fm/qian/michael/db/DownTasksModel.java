package fm.qian.michael.db;


import android.os.Parcel;
import android.os.Parcelable;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.OneToMany;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.List;

import fm.qian.michael.net.entry.response.ComAll;
import fm.qian.michael.net.entry.response.ComAll_Table;

/*
 * lv   2018/11/6
 */
@Table(database = AppDatabase.class)
public class DownTasksModel extends BaseModel implements Parcelable {

    @PrimaryKey
    private String id;
    @Column
    private String title;
    @Column
    private String cover;
    @Column
    private String brief;
    @Column
    private String sizeAll;
    @Column
    private String sizeDown;

    public List<ComAll> comAlls;

    @OneToMany(methods = {OneToMany.Method.ALL}, variableName = "comAlls")
    public List<ComAll> getComAlls() {
        if (comAlls == null || comAlls.isEmpty()) {
            comAlls = SQLite.select()
                    .from(ComAll.class)
                    .where(ComAll_Table.albumId.eq(id))
                    .queryList();
        }
        return comAlls;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public String getSizeAll() {
        return sizeAll;
    }

    public void setSizeAll(String sizeAll) {
        this.sizeAll = sizeAll;
    }

    public String getSizeDown() {
        return sizeDown;
    }

    public void setSizeDown(String sizeDown) {
        this.sizeDown = sizeDown;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.title);
        dest.writeString(this.cover);
        dest.writeString(this.brief);
        dest.writeString(this.sizeAll);
        dest.writeString(this.sizeDown);
        dest.writeTypedList(this.comAlls);
    }

    public DownTasksModel() {
    }

    protected DownTasksModel(Parcel in) {
        this.id = in.readString();
        this.title = in.readString();
        this.cover = in.readString();
        this.brief = in.readString();
        this.sizeAll = in.readString();
        this.sizeDown = in.readString();
        this.comAlls = in.createTypedArrayList(ComAll.CREATOR);
    }

    public static final Parcelable.Creator<DownTasksModel> CREATOR = new Parcelable.Creator<DownTasksModel>() {
        @Override
        public DownTasksModel createFromParcel(Parcel source) {
            return new DownTasksModel(source);
        }

        @Override
        public DownTasksModel[] newArray(int size) {
            return new DownTasksModel[size];
        }
    };
}

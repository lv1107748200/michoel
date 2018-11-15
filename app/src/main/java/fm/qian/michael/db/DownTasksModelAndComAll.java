package fm.qian.michael.db;


import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import fm.qian.michael.net.entry.response.ComAll;

/*
 * lv   2018/11/15
 */
@Table(database = AppDatabase.class)
public class DownTasksModelAndComAll extends BaseModel {

//    @PrimaryKey(
//            autoincrement = true
//    )
//    long _id;
    @PrimaryKey()
    private String id;

    @ForeignKey(
            saveForeignKeyModel = false
    )
    ComAll comAll;

    @ForeignKey(
            saveForeignKeyModel = false
    )
    DownTasksModel downTasksModel;

//    public final long getId() {
//        return _id;
//    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public  ComAll getComAll() {
        return comAll;
    }

    public  void setComAll(ComAll param) {
        comAll = param;
    }

    public  DownTasksModel getDownTasksModel() {
        return downTasksModel;
    }

    public  void setDownTasksModel(DownTasksModel param) {
        downTasksModel = param;
    }

}

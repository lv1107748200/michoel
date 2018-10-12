package fm.qian.michael.service;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/*
 * lv   2018/9/12
 */
public class MusicData implements Parcelable {

    private List<String> strings;
    private int num;

    public List<String> getStrings() {
        return strings;
    }

    public void setStrings(List<String> strings) {
        this.strings = strings;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(this.strings);
        dest.writeInt(this.num);
    }

    public MusicData() {
    }

    protected MusicData(Parcel in) {
        this.strings = in.createStringArrayList();
        this.num = in.readInt();
    }

    public static final Creator<MusicData> CREATOR = new Creator<MusicData>() {
        @Override
        public MusicData createFromParcel(Parcel source) {
            return new MusicData(source);
        }

        @Override
        public MusicData[] newArray(int size) {
            return new MusicData[size];
        }
    };
}

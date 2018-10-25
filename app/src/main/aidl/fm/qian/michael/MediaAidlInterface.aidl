package fm.qian.michael;
import fm.qian.michael.net.entry.response.ComAll;


interface MediaAidlInterface
{

    void openFile(String path);
    void openList(in List<String> ls,int i,boolean play,boolean clear);
    void updata(in List<ComAll> comAll,int num);
    int play();
    void playHistory();
    void playNum(int i);
    void stop();
    void pause();
    void next();
    void up();
    long duration();
    long position();
    long seek(long pos);
    void exit();
    void timing(int time);
    boolean isPlaying();
    boolean isPlayFirst();
    void playPattern(int pattern);
    void pOrq();
    ComAll getComAll();
    List<ComAll> getComAllList();
    int getPlayNumber();
    void login(int index);
}


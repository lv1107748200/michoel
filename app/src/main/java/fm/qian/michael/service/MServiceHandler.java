package fm.qian.michael.service;


import android.os.RemoteException;

import fm.qian.michael.MediaAidlInterface;
import fm.qian.michael.net.entry.response.ComAll;

import java.lang.ref.WeakReference;
import java.util.List;

/*
 * lv   2018/9/14
 */
public class MServiceHandler extends MediaAidlInterface.Stub{
    private final WeakReference<MqService> mService;

    public MServiceHandler(WeakReference<MqService> service) {
        this.mService = service;
    }
    @Override
    public void openFile(String path) throws RemoteException {

    }

    @Override
    public void openList(List<String> ls, int i,boolean play,boolean clear) throws RemoteException {

    }

    @Override
    public void updata(List<ComAll> comAll,int num) throws RemoteException {
        mService.get().updata(comAll,num);
    }

    @Override
    public void play() throws RemoteException {
        mService.get().play(null);
    }
    @Override
    public void playHistory() throws RemoteException {
        mService.get().playHistory();
    }

    @Override
    public void playNum(int i) throws RemoteException {
        mService.get().playNum(i);
    }

    @Override
    public void stop() throws RemoteException {
        mService.get().stop();
    }

    @Override
    public void pause() throws RemoteException {
        mService.get().pause();//APP调用
    }

    @Override
    public void next() throws RemoteException {
        mService.get().next();
    }

    @Override
    public void up() throws RemoteException {
        mService.get().up();
    }

    @Override
    public long duration() throws RemoteException {
        return mService.get().duration();
    }

    @Override
    public long position() throws RemoteException {
        return mService.get().position();
    }

    @Override
    public long seek(long pos) throws RemoteException {
        mService.get().seek(pos);
        return pos;
    }

    @Override
    public void exit() throws RemoteException {
        mService.get().exit();
    }

    @Override
    public void timing(int time) throws RemoteException {
        mService.get().timing(time);
    }
    @Override
    public boolean isPlaying() throws RemoteException {
        return mService.get().isPlaying();
    }
    @Override
    public boolean isPlayFirst() throws RemoteException {
        return mService.get().isPlayFirst();
    }
    @Override
    public void playPattern(int pattern) throws RemoteException {
         mService.get().playPattern(pattern);
    }
    @Override
    public void pOrq() throws RemoteException {
        mService.get().pOrq();
    }
    @Override
    public ComAll getComAll() throws RemoteException {
     return    mService.get().getComAll();
    }

    @Override
    public List<ComAll> getComAllList() throws RemoteException {
        return mService.get().getComAllList();
    }

    @Override
    public int getPlayNumber() throws RemoteException {
        return mService.get().getPlayNumber();
    }

}

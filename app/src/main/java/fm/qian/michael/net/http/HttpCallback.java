package fm.qian.michael.net.http;



public abstract class HttpCallback<T,A> extends CallBack<T,A>{

    public abstract  void onError(HttpException e);

    @Override
    public void onSuccess(T t) {

    }

    @Override
    public void onSuccessAll(A k) {

    }
}

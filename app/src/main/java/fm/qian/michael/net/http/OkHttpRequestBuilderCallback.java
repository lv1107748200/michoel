package fm.qian.michael.net.http;

import okhttp3.Request;

public interface OkHttpRequestBuilderCallback {
    void builder(Request.Builder builder);
}

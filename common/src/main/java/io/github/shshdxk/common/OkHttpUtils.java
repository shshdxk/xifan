package io.github.shshdxk.common;

import io.github.shshdxk.common.jackson.StandardObjectMapper;
import okhttp3.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 */
public class OkHttpUtils {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final MediaType FILE_MEDIA_TYPE = MediaType.parse("multipart/form-data");
    private static final MediaType FILE_MEDIA_OCTET_STREAM = MediaType.parse("application/octet-stream");

    public static enum OkHttpHolder {
        INSTANCE;

        private final OkHttpClient client;
        OkHttpHolder () {
            this.client = new OkHttpClient();
        }

        public static OkHttpClient getClient() {
            return INSTANCE.client;
        }
    }

    public static Response get(String url) throws IOException {
        return get(url, Collections.emptyMap());
    }

    public static Response get(String url, Map<String, String> params) throws IOException {
        return get(url, params, Headers.of());
    }

    public static Response get(String url, Map<String, String> queryParams, Headers headers) throws IOException {
        HttpUrl.Builder builder = Objects.requireNonNull(HttpUrl.parse(url)).newBuilder();

        if (queryParams != null) {
            for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                builder.addQueryParameter(entry.getKey(), entry.getValue());
            }
        }

        Request request = new Request.Builder()
            .url(builder.build())
            .headers(headers)
            .build();

        return call(request);
    }

    private static Response call(Request request) throws IOException {

        OkHttpClient.Builder bu = OkHttpHolder.getClient().newBuilder();

        // 设置连接超时时间
        bu.connectTimeout(60, TimeUnit.SECONDS);
        bu.readTimeout(60, TimeUnit.SECONDS);
        bu.callTimeout(60, TimeUnit.SECONDS);
//        bu.writeTimeout(1, TimeUnit.MINUTES);
//        bu.pingInterval(1, TimeUnit.MINUTES);

        return bu.build().newCall(request).execute();
    }

    public static Response post(String url, Map<String, Object> data) throws IOException {
        return post(url, StandardObjectMapper.stringify(data));
    }

    public static Response post(String url, String json) throws IOException {
        return post(url, json, new HashMap<>());
    }

    public static Response post(String url, String json, Map<String, String> headers) throws IOException {
        RequestBody body = RequestBody.create(json, JSON);
        if (headers == null) {
            headers = new HashMap<>();
        }

        Request request = new Request.Builder()
            .url(url)
            .headers(Headers.of(headers))
            .post(body)
            .build();
        return call(request);
    }

    public static Response post(String url, Headers headers, Map<String, String> queryParams, String json) throws IOException {
        HttpUrl.Builder builder = Objects.requireNonNull(HttpUrl.parse(url)).newBuilder();
        if (queryParams != null) {
            for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                builder.addQueryParameter(entry.getKey(), entry.getValue());
            }
        }

        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
            .url(builder.build().toString())
            .headers(headers)
            .post(body)
            .build();
        return call(request);
    }

    public static Response delete(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(json, JSON);

        Request request = new Request.Builder()
                .url(url)
                .delete(body)
                .build();
        return call(request);
    }

    public static Response delete(String url, Map<String, String> queryParams, Headers headers) throws IOException {
        HttpUrl.Builder builder = Objects.requireNonNull(HttpUrl.parse(url)).newBuilder();

        if (queryParams != null) {
            for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                builder.addQueryParameter(entry.getKey(), entry.getValue());
            }
        }

        Request request = new  Request.Builder()
            .url(builder.build().toString())
            .headers(headers)
            .delete()
            .build();

        return call(request);
    }

    public static Response put(String url, String json) throws IOException {
        return put(url,Headers.of(),  null, json);
    }

    public static Response put(String url, Headers headers, Map<String, String> queryParams, String json) throws IOException {
        HttpUrl.Builder builder = Objects.requireNonNull(HttpUrl.parse(url)).newBuilder();
        if (queryParams != null) {
            for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                builder.addQueryParameter(entry.getKey(), entry.getValue());
            }
        }

        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
            .url(builder.build().toString())
            .headers(headers)
            .put(body)
            .build();
        return call(request);
    }

    public static Response patch(String url, Headers headers, Map<String, String> queryParams, String json) throws IOException {
        HttpUrl.Builder builder = Objects.requireNonNull(HttpUrl.parse(url)).newBuilder();
        if (queryParams != null) {
            for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                builder.addQueryParameter(entry.getKey(), entry.getValue());
            }
        }

        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
            .url(builder.build().toString())
            .headers(headers)
            .patch(body)
            .build();
        return call(request);
    }

    public static Response send(String url, String method, Headers headers, Map<String, String> queryParams, String json) throws IOException {
        HttpUrl.Builder builder = Objects.requireNonNull(HttpUrl.parse(url)).newBuilder();
        if (queryParams != null) {
            for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                builder.addQueryParameter(entry.getKey(), entry.getValue());
            }
        }

        RequestBody body = json == null ? null : RequestBody.create(json, JSON);
        Request request = new Request.Builder()
            .url(builder.build().toString())
            .headers(headers)
            .method(method, body)
            .build();
        return call(request);
    }

    /**
     * 上传文件
     * @param url
     * @param file
     * @param name
     * @return
     * @throws IOException
     */
    public static Response upload(String url, File file, String name) throws IOException {
        RequestBody fileBody = RequestBody.create(file, FILE_MEDIA_OCTET_STREAM);
        MultipartBody body = new MultipartBody.Builder()
                .setType(FILE_MEDIA_TYPE)
                .addFormDataPart(name, URLEncoder.encode(file.getName(), StandardCharsets.UTF_8), fileBody)
                .build();

        Request request = new Request.Builder()
                .post(body)
                .url(url)
                .build();

        return call(request);
    }

    /**
     * 上传文件
     * @param url
     * @param file
     * @param name
     * @param params
     * @return
     * @throws IOException
     */
    public static Response upload(String url, File file, String name, Map<String, String> params) throws IOException {
        RequestBody fileBody = RequestBody.create(file, FILE_MEDIA_OCTET_STREAM);
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(FILE_MEDIA_TYPE)
                .addFormDataPart(name, URLEncoder.encode(file.getName(), StandardCharsets.UTF_8), fileBody);
        if (params != null && !params.isEmpty()) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                builder.addFormDataPart(entry.getKey(), entry.getValue());
            }
        }

        MultipartBody body = builder.build();
        Request request = new Request.Builder()
                .post(body)
                .url(url)
                .build();

        return call(request);
    }

    public static void download(String url, File file) throws IOException {
        FileUtils.forceMkdir(file.getParentFile());
        String errorString = null;
        IOException ex = null;
        for (int i = 0; i < 3; i++) {
            Response response = get(url);
            if (!response.isSuccessful()) {
                errorString = getResponseBody(response).orElse(null);
                continue;
            }
            try (InputStream in = response.body().byteStream();
                OutputStream fs = new FileOutputStream(file)) {
                IOUtils.copy(in, fs);
                return;
            } catch (IOException e) {
                ex = e;
            }
        }
        if (ex != null) {
            throw ex;
        }
        throw new IOException(errorString);
    }

    public static Optional<String> getResponseBody(Response response) {
        try (ResponseBody body = response.body()) {
            return body == null ? Optional.empty() : Optional.of(body.string());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}

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
 * @author shshdxk
 */
public class OkHttpUtils {

    /**
     * JSON格式
     */
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    /**
     * 文件上传的MediaType
     */
    private static final MediaType FILE_MEDIA_TYPE = MediaType.parse("multipart/form-data");
    /**
     * 文件上传的MediaType
     */
    private static final MediaType FILE_MEDIA_OCTET_STREAM = MediaType.parse("application/octet-stream");

    /**
     * OkHttpClient
     */
    public enum OkHttpHolder {
        /**
         * 单例
         */
        INSTANCE;

        private final OkHttpClient client;

        OkHttpHolder() {
            this.client = new OkHttpClient();
        }

        /**
         * OkHttpClient
         * @return OkHttpClient
         */
        public static OkHttpClient getClient() {
            return INSTANCE.client;
        }
    }

    /**
     * get
     *
     * @param url url
     * @return Response
     * @throws IOException IOException
     */
    public static Response get(String url) throws IOException {
        return get(url, Collections.emptyMap());
    }

    /**
     * get
     *
     * @param url    url
     * @param params params
     * @return Response
     * @throws IOException IOException
     */
    public static Response get(String url, Map<String, String> params) throws IOException {
        return get(url, params, Headers.of());
    }

    /**
     * get
     *
     * @param url         url
     * @param queryParams queryParams
     * @param headers     headers
     * @return Response
     * @throws IOException IOException
     */
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

    /**
     * post
     *
     * @param url  url
     * @param data data
     * @return Response
     * @throws IOException IOException
     */
    public static Response post(String url, Map<String, Object> data) throws IOException {
        return post(url, StandardObjectMapper.stringify(data));
    }

    /**
     * post
     *
     * @param url  url
     * @param json json
     * @return Response
     * @throws IOException IOException
     */
    public static Response post(String url, String json) throws IOException {
        return post(url, json, new HashMap<>());
    }

    /**
     * post
     *
     * @param url     url
     * @param json    json
     * @param headers headers
     * @return Response
     * @throws IOException IOException
     */
    public static Response post(String url, String json, Map<String, String> headers) throws IOException {
        if (headers == null) {
            headers = new HashMap<>();
        }

        RequestBody body;
        String contentType = headers.get("Content-Type");
        if (contentType == null) {
            body = RequestBody.create(json, JSON);
        } else {
            body = RequestBody.create(json, MediaType.parse(contentType));
        }

        Request request = new Request.Builder()
                .url(url)
                .headers(Headers.of(headers))
                .post(body)
                .build();
        return call(request);
    }

    /**
     * post
     *
     * @param url         url
     * @param headers     headers
     * @param queryParams queryParams
     * @param json        json
     * @return Response
     * @throws IOException IOException
     */
    public static Response post(String url, Headers headers, Map<String, String> queryParams, String json) throws IOException {
        HttpUrl.Builder builder = Objects.requireNonNull(HttpUrl.parse(url)).newBuilder();
        if (queryParams != null) {
            for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                builder.addQueryParameter(entry.getKey(), entry.getValue());
            }
        }

        RequestBody body;
        String contentType = headers.get("Content-Type");
        if (contentType == null) {
            body = RequestBody.create(json, JSON);
        } else {
            body = RequestBody.create(json, MediaType.parse(contentType));
        }

        Request request = new Request.Builder()
                .url(builder.build().toString())
                .headers(headers)
                .post(body)
                .build();
        return call(request);
    }

    /**
     * 删除
     *
     * @param url  url
     * @param json json
     * @return Response
     * @throws IOException IOException
     */
    public static Response delete(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(json, JSON);

        Request request = new Request.Builder()
                .url(url)
                .delete(body)
                .build();
        return call(request);
    }

    /**
     * 删除
     *
     * @param url         url
     * @param queryParams queryParams
     * @param headers     headers
     * @return Response
     * @throws IOException IOException
     */
    public static Response delete(String url, Map<String, String> queryParams, Headers headers) throws IOException {
        HttpUrl.Builder builder = Objects.requireNonNull(HttpUrl.parse(url)).newBuilder();

        if (queryParams != null) {
            for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                builder.addQueryParameter(entry.getKey(), entry.getValue());
            }
        }

        Request request = new Request.Builder()
                .url(builder.build().toString())
                .headers(headers)
                .delete()
                .build();

        return call(request);
    }

    /**
     * put
     *
     * @param url  url
     * @param json json
     * @return Response
     * @throws IOException IOException
     */
    public static Response put(String url, String json) throws IOException {
        return put(url, Headers.of(), null, json);
    }

    /**
     * put
     *
     * @param url         url
     * @param headers     headers
     * @param queryParams queryParams
     * @param json        json
     * @return Response
     * @throws IOException IOException
     */
    public static Response put(String url, Headers headers, Map<String, String> queryParams, String json) throws IOException {
        HttpUrl.Builder builder = Objects.requireNonNull(HttpUrl.parse(url)).newBuilder();
        if (queryParams != null) {
            for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                builder.addQueryParameter(entry.getKey(), entry.getValue());
            }
        }

        RequestBody body;
        String contentType = headers.get("Content-Type");
        if (contentType == null) {
            body = RequestBody.create(json, JSON);
        } else {
            body = RequestBody.create(json, MediaType.parse(contentType));
        }

        Request request = new Request.Builder()
                .url(builder.build().toString())
                .headers(headers)
                .put(body)
                .build();
        return call(request);
    }

    /**
     * patch
     *
     * @param url         url
     * @param headers     headers
     * @param queryParams queryParams
     * @param json        json
     * @return Response
     * @throws IOException IOException
     */
    public static Response patch(String url, Headers headers, Map<String, String> queryParams, String json) throws IOException {
        HttpUrl.Builder builder = Objects.requireNonNull(HttpUrl.parse(url)).newBuilder();
        if (queryParams != null) {
            for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                builder.addQueryParameter(entry.getKey(), entry.getValue());
            }
        }

        RequestBody body;
        String contentType = headers.get("Content-Type");
        if (contentType == null) {
            body = RequestBody.create(json, JSON);
        } else {
            body = RequestBody.create(json, MediaType.parse(contentType));
        }

        Request request = new Request.Builder()
                .url(builder.build().toString())
                .headers(headers)
                .patch(body)
                .build();
        return call(request);
    }

    /**
     * 发送请求
     *
     * @param url         请求地址
     * @param method      请求方法
     * @param headers     请求头
     * @param queryParams 请求参数
     * @param json        请求体
     * @return Response
     * @throws IOException IOException
     */
    public static Response send(String url, String method, Headers headers, Map<String, String> queryParams, String json) throws IOException {
        HttpUrl.Builder builder = Objects.requireNonNull(HttpUrl.parse(url)).newBuilder();
        if (queryParams != null) {
            for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                builder.addQueryParameter(entry.getKey(), entry.getValue());
            }
        }

        RequestBody body;
        String contentType = headers.get("Content-Type");
        if (contentType == null) {
            body = json == null ? null : RequestBody.create(json, JSON);
        } else {
            body = json == null ? null : RequestBody.create(json, MediaType.parse(contentType));
        }

        Request request = new Request.Builder()
                .url(builder.build().toString())
                .headers(headers)
                .method(method, body)
                .build();
        return call(request);
    }

    /**
     * 上传文件
     *
     * @param url  url
     * @param file 文件
     * @param name 文件名
     * @return Response
     * @throws IOException IOException
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
     *
     * @param url    url
     * @param file   文件
     * @param name   文件名
     * @param params 参数
     * @return Response
     * @throws IOException IOException
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

    /**
     * 下载文件
     *
     * @param url  url
     * @param file 文件
     * @throws IOException IOException
     */
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

    /**
     * 获取响应体
     *
     * @param response Response
     * @return Optional
     */
    public static Optional<String> getResponseBody(Response response) {
        try (ResponseBody body = response.body()) {
            return body == null ? Optional.empty() : Optional.of(body.string());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}

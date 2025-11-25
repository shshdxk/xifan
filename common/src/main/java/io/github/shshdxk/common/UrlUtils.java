package io.github.shshdxk.common;


import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * url工具类
 */
public class UrlUtils {
//    public static final String SCHEME_URL = "://";
    private static final Pattern SLASHES = Pattern.compile("//+");
    static final Set<String> IMAGE_EXTENSIONS = ImmutableSet.of("jpg", "svg", "jpeg", "png", "gif");


    /**
     * url编码
     *
     * @param segment url
     * @return 编码后的url
     */
    public static String encodeUrl(String segment) {
        return URLEncoder.encode(segment, StandardCharsets.UTF_8);
    }

    /**
     * url解码
     *
     * @param url url
     * @return 解码后的url
     */
    public static String decodeUrl(String url) {
        return URLDecoder.decode(url, StandardCharsets.UTF_8);
    }

    /**
     * url片段标准化
     *
     * @param segments url片段
     * @return 标准化后的url片段
     */
    public static List<String> normalizeUrlSegments(List<String> segments) {
        List<String> normalized = new ArrayList<>();

        for (String each : segments) {
            each = StringUtils.remove(each, '/');
            if (!each.isEmpty()) {
                normalized.add(each);
            }
        }

        return normalized;
    }

    /**
     * 获取url
     *
     * @param original url
     * @return url片段
     */
    public static String getUrlWithoutParameters(String original) {
        String url = original;
        int pos = original.indexOf(63);
        if (pos > 0) {
            url = original.substring(0, pos);
        }

        pos = url.indexOf(35);
        if (pos > 0) {
            url = url.substring(0, pos);
        }

        return url;
    }

    /**
     * 拼接url片段
     *
     * @param segments url片段
     * @return url
     */
    public static String concatSegments(String[] segments) {
        if (segments.length == 0) {
            return null;
        } else if (segments.length == 1) {
            return segments[0];
        } else {
            String base = segments[0];
            if (base.indexOf("://") > 0) {
                List<String> list = Lists.newArrayList(segments);
                list = list.subList(1, list.size());
                String url = Joiner.on("/").skipNulls().join(list);
                url = removeRedundantSlashes(url);
                base = StringUtils.stripEnd(base, "/");
                url = StringUtils.stripStart(url, "/");
                return base + "/" + url;
            } else {
                String url = Joiner.on("/").skipNulls().join(segments);
                return removeRedundantSlashes(url);
            }
        }
    }

    /**
     * 拼接url片段
     *
     * @param segments url片段
     * @return url
     */
    public static String concatSegments(List<String> segments) {
        return concatSegments(Iterables.toArray(segments, String.class));
    }

    /**
     * 拼接url片段
     *
     * @param base     url
     * @param segments url片段
     * @return url
     */
    public static String concatSegments(String base, String... segments) {
        if (segments == null) {
            return base;
        } else {
            List<String> list = ImmutableList.<String>builder().add(base).add(segments).build();
            return concatSegments(Iterables.toArray(list, String.class));
        }
    }

    /**
     * 移除url中的多余的斜杠
     *
     * @param path 路径
     * @return 路径
     */
    public static String removeRedundantSlashes(String path) {
        return path == null ? null : SLASHES.matcher(path).replaceAll("/");
    }

    /**
     * 移除url中的多余的斜杠
     *
     * @param uri uri
     * @return uri
     */
    public static URI trimTrailingSlashesFromPath(URI uri) {
        String path = uri.getPath();
        if (!path.endsWith("/")) {
            return uri;
        } else {
            while (path.endsWith("/")) {
                path = path.substring(0, path.length() - 1);
            }

            try {
                return new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), uri.getPort(), path, uri.getQuery(), uri.getFragment());
            } catch (URISyntaxException var3) {
                throw new RuntimeException(var3);
            }
        }
    }

    /**
     * 添加参数
     *
     * @param name  参数名
     * @param value 参数值
     * @param uri   url
     */
    public static void addParam(String name, String value, StringBuilder uri) {
        if (!uri.isEmpty()) {
            uri.append('&');
        }

        uri.append(encodeUrl(name)).append('=');
        if (value != null) {
            uri.append(encodeUrl(value));
        }

    }

    /**
     * 添加参数
     *
     * @param params 参数
     * @param uri    url
     */
    public static void addParams(Map<String, String> params, StringBuilder uri) {
        if (params != null && !params.isEmpty()) {

            for (Map.Entry<String, String> stringStringEntry : params.entrySet()) {
                addParam(stringStringEntry.getKey(), stringStringEntry.getValue(), uri);
            }

        }
    }

    /**
     * 获取参数
     *
     * @param uri  url
     * @param name 参数名
     * @return 参数值
     */
    public static String getParam(URI uri, String name) {
        String query = uri.getRawQuery();
        if (query != null && !query.isEmpty()) {
            String[] params = query.split("&");
            for (String param : params) {
                String[] parts = param.split("=");
                if (parts.length == 2 && name.equals(parts[0])) {
                    return decodeUrl(parts[1]);
                }
            }

            return null;
        } else {
            return null;
        }
    }

    /**
     * 判断url是否是图片
     *
     * @param url url
     * @return 是否是图片
     */
    public static boolean isImage(String url) {
        String text = getUrlWithoutParameters(url);
        String ext = FilenameUtils.getExtension(text.toLowerCase());
        return IMAGE_EXTENSIONS.contains(ext);
    }

    /**
     * 判断url是否是绝对路径
     *
     * @param url url
     * @return 是否是绝对路径
     */
    public static boolean isAbsolute(String url) {
        if (url == null) {
            return false;
        } else {
            URI u = URI.create(url);
            return u.isAbsolute();
        }
    }

    /**
     * 判断url是否是相对路径
     *
     * @param url url
     * @return 是否是相对路径
     */
    public static boolean isRelative(String url) {
        return !isAbsolute(url);
    }
}


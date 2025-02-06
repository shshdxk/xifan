package io.github.shshdxk.common;


import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.regex.Pattern;

public class UrlUtils {
    public static final String SCHEME_URL = "://";
    private static final Pattern SLASHES = Pattern.compile("//+");
    static final Set<String> IMAGE_EXTENSIONS = ImmutableSet.of("jpg", "svg", "jpeg", "png", "gif");

    private UrlUtils() {
    }

    public static long getLastModified(Set<URL> fileset) {
        long lastModified = 0L;

        URL each;
        for(Iterator<URL> var3 = fileset.iterator();
            var3.hasNext();
            lastModified = Math.max(lastModified, getLastModified(each))) {
            each = (URL)var3.next();
        }

        return lastModified;
    }

    public static long getLastModified(URL resourceURL) {
        switch (resourceURL.getProtocol()) {
            case "jar":
                try {
                    JarURLConnection jarConnection = (JarURLConnection)resourceURL.openConnection();
                    JarEntry entry = jarConnection.getJarEntry();
                    return entry.getTime();
                } catch (IOException var18) {
                    return 0L;
                }
            case "file":
                URLConnection connection = null;

                long var6;
                try {
                    connection = resourceURL.openConnection();
                    return connection.getLastModified();
                } catch (IOException var19) {
                    var6 = 0L;
                } finally {
                    if (connection != null) {
                        try {
                            connection.getInputStream().close();
                        } catch (IOException ignored) {
                        }
                    }

                }

                return var6;
            default:
                throw new IllegalArgumentException("Unsupported protocol " + resourceURL.getProtocol() + " for resource " + resourceURL);
        }
    }

    public static String encodeUrl(String segment) {
        return URLEncoder.encode(segment, StandardCharsets.UTF_8);
    }

    public static String decodeUrl(String url) {
        return URLDecoder.decode(url, StandardCharsets.UTF_8);
    }

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

    public static String concatSegments(List<String> segments) {
        return concatSegments(Iterables.toArray(segments, String.class));
    }

    public static String concatSegments(String base, String... segments) {
        if (segments == null) {
            return base;
        } else {
            List<String> list = ImmutableList.<String>builder().add(base).add(segments).build();
            return concatSegments(Iterables.toArray(list, String.class));
        }
    }

    @Nullable
    public static String removeRedundantSlashes(String path) {
        return path == null ? null : SLASHES.matcher(path).replaceAll("/");
    }

    public static URI trimTrailingSlashesFromPath(URI uri) {
        String path = uri.getPath();
        if (!path.endsWith("/")) {
            return uri;
        } else {
            while(path.endsWith("/")) {
                path = path.substring(0, path.length() - 1);
            }

            try {
                return new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), uri.getPort(), path, uri.getQuery(), uri.getFragment());
            } catch (URISyntaxException var3) {
                throw new RuntimeException(var3);
            }
        }
    }

    public static void addParam(String name, String value, StringBuilder uri) {
        if (!uri.isEmpty()) {
            uri.append('&');
        }

        uri.append(encodeUrl(name)).append('=');
        if (value != null) {
            uri.append(encodeUrl(value));
        }

    }

    public static void addParams(Map<String, String> params, StringBuilder uri) {
        if (params != null && !params.isEmpty()) {

            for (Map.Entry<String, String> stringStringEntry : params.entrySet()) {
                addParam(stringStringEntry.getKey(), stringStringEntry.getValue(), uri);
            }

        }
    }

    public static String getParam(URI uri, String name) {
        String query = uri.getRawQuery();
        if (query != null && !query.isEmpty()) {
            String[] params = query.split("&");
            int var5 = params.length;

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

    public static boolean isImage(String url) {
        String text = getUrlWithoutParameters(url);
        String ext = FilenameUtils.getExtension(text.toLowerCase());
        return IMAGE_EXTENSIONS.contains(ext);
    }

    public static boolean isAbsolute(String url) {
        if (url == null) {
            return false;
        } else {
            URI u = URI.create(url);
            return u.isAbsolute();
        }
    }

    public static boolean isRelative(String url) {
        return !isAbsolute(url);
    }
}


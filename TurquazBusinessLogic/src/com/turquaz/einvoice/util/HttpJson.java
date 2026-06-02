package com.turquaz.einvoice.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * Java 8'de gömülü {@link HttpURLConnection} üzerine ince bir JSON/HTTP istemcisi.
 * Build {@code -source 8} ile derlendiðinden Java 11 {@code HttpClient}
 * kullanýlamaz; bu sýnýf ek baðýmlýlýk olmadan REST çaðrýsý yapar ve Java 17
 * runtime'da da çalýþýr.
 */
public final class HttpJson {

    private static final Charset UTF8 = Charset.forName("UTF-8");

    /** Basit HTTP yanýtý: durum kodu + gövde. */
    public static final class Response {
        public final int code;
        public final String body;

        public Response(int code, String body) {
            this.code = code;
            this.body = body;
        }

        public boolean isSuccess() {
            return code >= 200 && code < 300;
        }
    }

    private HttpJson() {
    }

    public static Response post(String url, String jsonBody, Map<String, String> headers)
            throws IOException {
        return send("POST", url, jsonBody, headers);
    }

    public static Response get(String url, Map<String, String> headers) throws IOException {
        return send("GET", url, null, headers);
    }

    public static Response send(String method, String url, String jsonBody,
                                Map<String, String> headers) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        try {
            conn.setRequestMethod(method);
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(60000);
            conn.setRequestProperty("Accept", "application/json");
            if (headers != null) {
                for (Map.Entry<String, String> e : headers.entrySet()) {
                    conn.setRequestProperty(e.getKey(), e.getValue());
                }
            }
            if (jsonBody != null) {
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                byte[] payload = jsonBody.getBytes(UTF8);
                OutputStream os = conn.getOutputStream();
                try {
                    os.write(payload);
                } finally {
                    os.close();
                }
            }
            int code = conn.getResponseCode();
            String body = readBody(code < 400 ? conn.getInputStream() : conn.getErrorStream());
            return new Response(code, body);
        } finally {
            conn.disconnect();
        }
    }

    private static String readBody(InputStream in) throws IOException {
        if (in == null) {
            return "";
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[4096];
        int n;
        try {
            while ((n = in.read(buf)) != -1) {
                out.write(buf, 0, n);
            }
        } finally {
            in.close();
        }
        return new String(out.toByteArray(), UTF8);
    }
}

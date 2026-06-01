package com.turquaz.einvoice.util;

/**
 * Bağımlılık eklememek için minimal JSON yardımcısı. Tam bir parser değildir;
 * entegratör REST gövdesi üretmek (yazma) ve yanıttan tek tek alan çekmek
 * (sığ okuma) için yeterlidir. Karmaşık yanıt işleme gerekirse ileride
 * Jackson eklenebilir.
 */
public final class Json {

    private Json() {
    }

    /** Bir String değeri JSON için escape'ler (tırnak dahil değildir). */
    public static String escape(String s) {
        if (s == null) {
            return "";
        }
        StringBuilder b = new StringBuilder(s.length() + 16);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"':  b.append("\\\""); break;
                case '\\': b.append("\\\\"); break;
                case '\n': b.append("\\n");  break;
                case '\r': b.append("\\r");  break;
                case '\t': b.append("\\t");  break;
                default:
                    if (c < 0x20) {
                        b.append(String.format("\\u%04x", (int) c));
                    } else {
                        b.append(c);
                    }
            }
        }
        return b.toString();
    }

    /** {@code "key":"value"} biçiminde, escape edilmiş bir string alan üretir. */
    public static String str(String key, String value) {
        return "\"" + escape(key) + "\":\"" + escape(value == null ? "" : value) + "\"";
    }

    /** {@code "key":value} biçiminde ham (string olmayan) bir alan üretir. */
    public static String raw(String key, String value) {
        return "\"" + escape(key) + "\":" + (value == null ? "null" : value);
    }

    /**
     * Sığ bir okuma: verilen anahtarın string değerini döndürür. Yalnızca düz
     * (iç içe olmayan) {@code "key":"value"} kalıbını arar; bulamazsa null döner.
     */
    public static String getString(String json, String key) {
        if (json == null) {
            return null;
        }
        String needle = "\"" + key + "\"";
        int k = json.indexOf(needle);
        if (k < 0) {
            return null;
        }
        int colon = json.indexOf(':', k + needle.length());
        if (colon < 0) {
            return null;
        }
        int i = colon + 1;
        while (i < json.length() && Character.isWhitespace(json.charAt(i))) {
            i++;
        }
        if (i >= json.length() || json.charAt(i) != '"') {
            return null;   // string değil (sayı/bool/obje) — bu yardımcı kapsamaz
        }
        i++;
        StringBuilder b = new StringBuilder();
        while (i < json.length()) {
            char c = json.charAt(i);
            if (c == '\\' && i + 1 < json.length()) {
                char n = json.charAt(i + 1);
                switch (n) {
                    case 'n': b.append('\n'); break;
                    case 'r': b.append('\r'); break;
                    case 't': b.append('\t'); break;
                    default:  b.append(n);
                }
                i += 2;
                continue;
            }
            if (c == '"') {
                break;
            }
            b.append(c);
            i++;
        }
        return b.toString();
    }
}

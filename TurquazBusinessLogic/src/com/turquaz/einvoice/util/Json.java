package com.turquaz.einvoice.util;

/**
 * Baðýmlýlýk eklememek için minimal JSON yardýmcýsý. Tam bir parser deðildir;
 * entegratör REST gövdesi üretmek (yazma) ve yanýttan tek tek alan çekmek
 * (sýð okuma) için yeterlidir. Karmaþýk yanýt iþleme gerekirse ileride
 * Jackson eklenebilir.
 */
public final class Json {

    private Json() {
    }

    /** Bir String deðeri JSON için escape'ler (týrnak dahil deðildir). */
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

    /** {@code "key":"value"} biçiminde, escape edilmiþ bir string alan üretir. */
    public static String str(String key, String value) {
        return "\"" + escape(key) + "\":\"" + escape(value == null ? "" : value) + "\"";
    }

    /** {@code "key":value} biçiminde ham (string olmayan) bir alan üretir. */
    public static String raw(String key, String value) {
        return "\"" + escape(key) + "\":" + (value == null ? "null" : value);
    }

    /**
     * Sýð bir okuma: verilen anahtarýn string deðerini döndürür. Yalnýzca düz
     * (iç içe olmayan) {@code "key":"value"} kalýbýný arar; bulamazsa null döner.
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
            return null;   // string deðil (sayý/bool/obje) - bu yardýmcý kapsamaz
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

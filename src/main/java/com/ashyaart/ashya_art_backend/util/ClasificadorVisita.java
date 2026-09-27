package com.ashyaart.ashya_art_backend.util;

import java.net.URI;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Reglas para convertir una visita de la web pública en las claves del contador agregado
 * (página, origen, dispositivo e idioma). Todo se decide aquí, sin guardar nada de la persona.
 */
public final class ClasificadorVisita {

    public static final String INTERNAL = "INTERNAL";
    public static final String DIRECT = "DIRECT";
    public static final String GOOGLE = "GOOGLE";
    public static final String SEARCH = "SEARCH";
    public static final String INSTAGRAM = "INSTAGRAM";
    public static final String FACEBOOK = "FACEBOOK";
    public static final String NEWSLETTER = "NEWSLETTER";
    public static final String OTHER = "OTHER";

    public static final String MOBILE = "MOBILE";
    public static final String TABLET = "TABLET";
    public static final String DESKTOP = "DESKTOP";

    /** Primer segmento de las páginas públicas que se cuentan (el resto: admin, mantenimiento, 404...). */
    private static final Set<String> SECCIONES = Set.of(
            "about", "calendar", "conditions", "imprint", "privacy-policy",
            "shop", "studio", "workshops", "gift-cards", "products");

    private static final Pattern RUTA_VALIDA = Pattern.compile("^/[a-z0-9\\-/]*$");

    private static final Pattern BOT = Pattern.compile(
            "bot|crawl|spider|slurp|headless|lighthouse|pagespeed|preview|facebookexternalhit|whatsapp"
            + "|curl|wget|python|java/|node-fetch|axios|go-http|postman|httpclient");

    private static final Set<String> HOSTS_PROPIOS = Set.of("ashya-art.com", "ashya-art-frontend-pro.onrender.com", "localhost");

    private static final Set<String> IDIOMAS = Set.of("en", "de", "es");

    private ClasificadorVisita() {
    }

    /** Ruta pública normalizada ("/workshops/5-kintsugi"), o vacío si no es una página que se cuente. */
    public static Optional<String> ruta(String ruta) {
        if (ruta == null) return Optional.empty();
        String r = ruta.split("[?#]", 2)[0].trim().toLowerCase(Locale.ROOT);
        if (r.length() > 1) r = r.replaceAll("/+$", "");
        if (r.isEmpty() || r.length() > 200 || !RUTA_VALIDA.matcher(r).matches()) return Optional.empty();
        if (r.equals("/")) return Optional.of(r);
        String[] segmentos = r.substring(1).split("/");
        if (segmentos.length > 2 || !SECCIONES.contains(segmentos[0])) return Optional.empty();
        return Optional.of(r);
    }

    /**
     * De dónde llega la visita. Solo cuenta para la primera página (entrada); las navegaciones
     * dentro de la web son INTERNAL. utm_source manda sobre el referrer.
     */
    public static String origen(boolean entrada, String referrer, String utmSource) {
        if (!entrada) return INTERNAL;

        if (utmSource != null && !utmSource.isBlank()) {
            String u = utmSource.toLowerCase(Locale.ROOT);
            if (u.contains("newsletter") || u.contains("mail")) return NEWSLETTER;
            if (u.contains("instagram") || u.equals("ig")) return INSTAGRAM;
            if (u.contains("facebook") || u.equals("fb")) return FACEBOOK;
            if (u.contains("google")) return GOOGLE;
            return OTHER;
        }

        String host = host(referrer);
        if (host == null || HOSTS_PROPIOS.stream().anyMatch(h -> host.equals(h) || host.endsWith("." + h))) return DIRECT;
        if (host.startsWith("mail.") || host.contains("outlook.") || host.endsWith("gmx.net") || host.endsWith("web.de")) return NEWSLETTER;
        if (host.equals("google.com") || host.startsWith("google.") || host.contains(".google.")) return GOOGLE;
        if (host.contains("bing.") || host.contains("duckduckgo.") || host.contains("ecosia.") || host.contains("yahoo.")) return SEARCH;
        if (host.endsWith("instagram.com")) return INSTAGRAM;
        if (host.endsWith("facebook.com") || host.equals("fb.com") || host.equals("fb.me")) return FACEBOOK;
        return OTHER;
    }

    /** Tipo de dispositivo según el User-Agent, o vacío si parece un robot (no se cuenta). */
    public static Optional<String> dispositivo(String userAgent) {
        if (userAgent == null || userAgent.isBlank()) return Optional.empty();
        String ua = userAgent.toLowerCase(Locale.ROOT);
        if (BOT.matcher(ua).find()) return Optional.empty();
        if (ua.contains("ipad") || ua.contains("tablet") || (ua.contains("android") && !ua.contains("mobile"))) return Optional.of(TABLET);
        if (ua.contains("mobi") || ua.contains("iphone") || ua.contains("android")) return Optional.of(MOBILE);
        return Optional.of(DESKTOP);
    }

    /** Idioma elegido en la web (en, de, es); cualquier otro valor se guarda como "other". */
    public static String idioma(String idioma) {
        if (idioma == null) return "other";
        String i = idioma.trim().toLowerCase(Locale.ROOT);
        if (i.length() > 2) i = i.substring(0, 2);
        return IDIOMAS.contains(i) ? i : "other";
    }

    private static String host(String url) {
        if (url == null || url.isBlank()) return null;
        try {
            String h = URI.create(url.trim()).getHost();
            if (h == null) return null;
            h = h.toLowerCase(Locale.ROOT);
            return h.startsWith("www.") ? h.substring(4) : h;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}

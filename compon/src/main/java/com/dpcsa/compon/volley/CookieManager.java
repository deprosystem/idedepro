package com.dpcsa.compon.volley;

import com.dpcsa.compon.single.Injector;

import java.util.Map;

public class CookieManager {

    private static final String SET_COOKIE_KEY = "Set-Cookie";
    private static final String COOKIE_KEY = "Cookie";
    private static final String SESSION_COOKIE = "connect.sid";
    private static final String COOKIES_DIVIDER = ";";

    public static final void checkAndSaveSessionCookie( Map<String, String> headers) {
        if (headers != null && headers.containsKey(SET_COOKIE_KEY)
                && headers.get(SET_COOKIE_KEY).startsWith(SESSION_COOKIE)) {
            String cookies = headers.get(SET_COOKIE_KEY);
            if (cookies.length() > 0) {
                String[] allCookies = cookies.split(COOKIES_DIVIDER);
                for (String str : allCookies) {
                    if (str.contains(SESSION_COOKIE)) {
                        String[] sessCookies = str.split("=");
                        cookies = sessCookies[1];
                        break;
                    }
                }
                Injector.getPreferences().setSessionToken(cookies);
            }
        }
    }

    public static final void checkAndAddSessionCookie(Map<String, String> headers) {
        String cookie = Injector.getPreferences().getSessionToken();
        if (cookie != null && cookie.length() > 0) {
            StringBuilder builder = new StringBuilder();
            builder.append(SESSION_COOKIE);
            builder.append("=");
            builder.append(cookie);
            if (headers.containsKey(COOKIE_KEY)) {
                builder.append("; ");
                builder.append(headers.get(COOKIE_KEY));
            }
            headers.put(COOKIE_KEY, builder.toString());
        }
    }
}

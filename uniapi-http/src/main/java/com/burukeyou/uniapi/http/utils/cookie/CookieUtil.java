package com.burukeyou.uniapi.http.utils.cookie;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.burukeyou.uniapi.http.support.Cookie;
import okhttp3.internal.http.HttpDate;
import okhttp3.internal.publicsuffix.PublicSuffixDatabase;

import static okhttp3.internal.Util.UTC;
import static okhttp3.internal.Util.canonicalizeHost;
import static okhttp3.internal.Util.delimiterOffset;
import static okhttp3.internal.Util.indexOfControlOrNonAscii;
import static okhttp3.internal.Util.trimSubstring;
import static okhttp3.internal.Util.verifyAsIpAddress;

public class CookieUtil {

    private static final Pattern YEAR_PATTERN
            = Pattern.compile("(\\d{2,4})[^\\d]*");
    private static final Pattern MONTH_PATTERN
            = Pattern.compile("(?i)(jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec).*");
    private static final Pattern DAY_OF_MONTH_PATTERN
            = Pattern.compile("(\\d{1,2})[^\\d]*");
    private static final Pattern TIME_PATTERN
            = Pattern.compile("(\\d{1,2}):(\\d{1,2}):(\\d{1,2})[^\\d]*");


    public static List<Cookie> parseAll(String url, List<String> setCookiesString) {
        return setCookiesString.stream().map(e -> parse(url,e)).collect(Collectors.toList());
    }

    public static Cookie parse(String urlPath, String setCookie) {
        URL url = null;
        try {
            url = new URL(urlPath);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        long currentTimeMillis = System.currentTimeMillis();
        int pos = 0;
        int limit = setCookie.length();
        int cookiePairEnd = delimiterOffset(setCookie, pos, limit, ';');

        int pairEqualsSign = delimiterOffset(setCookie, pos, cookiePairEnd, '=');
        if (pairEqualsSign == cookiePairEnd) return null;

        String cookieName = trimSubstring(setCookie, pos, pairEqualsSign);
        if (cookieName.isEmpty() || indexOfControlOrNonAscii(cookieName) != -1) return null;

        String cookieValue = trimSubstring(setCookie, pairEqualsSign + 1, cookiePairEnd);
        if (indexOfControlOrNonAscii(cookieValue) != -1) return null;

        long expiresAt = HttpDate.MAX_DATE;
        long deltaSeconds = -1L;
        String domain = null;
        String path = null;
        boolean secureOnly = false;
        boolean httpOnly = false;
        boolean hostOnly = true;
        boolean persistent = false;

        pos = cookiePairEnd + 1;
        while (pos < limit) {
            int attributePairEnd = delimiterOffset(setCookie, pos, limit, ';');

            int attributeEqualsSign = delimiterOffset(setCookie, pos, attributePairEnd, '=');
            String attributeName = trimSubstring(setCookie, pos, attributeEqualsSign);
            String attributeValue = attributeEqualsSign < attributePairEnd
                    ? trimSubstring(setCookie, attributeEqualsSign + 1, attributePairEnd)
                    : "";

            if (attributeName.equalsIgnoreCase("expires")) {
                try {
                    expiresAt = parseExpires(attributeValue, 0, attributeValue.length());
                    persistent = true;
                } catch (IllegalArgumentException e) {
                    // Ignore this attribute, it isn't recognizable as a date.
                }
            } else if (attributeName.equalsIgnoreCase("max-age")) {
                try {
                    deltaSeconds = parseMaxAge(attributeValue);
                    persistent = true;
                } catch (NumberFormatException e) {
                    // Ignore this attribute, it isn't recognizable as a max age.
                }
            } else if (attributeName.equalsIgnoreCase("domain")) {
                try {
                    domain = parseDomain(attributeValue);
                    hostOnly = false;
                } catch (IllegalArgumentException e) {
                    // Ignore this attribute, it isn't recognizable as a domain.
                }
            } else if (attributeName.equalsIgnoreCase("path")) {
                path = attributeValue;
            } else if (attributeName.equalsIgnoreCase("secure")) {
                secureOnly = true;
            } else if (attributeName.equalsIgnoreCase("httponly")) {
                httpOnly = true;
            }

            pos = attributePairEnd + 1;
        }

        // If 'Max-Age' is present, it takes precedence over 'Expires', regardless of the order the two
        // attributes are declared in the cookie string.
        if (deltaSeconds == Long.MIN_VALUE) {
            expiresAt = Long.MIN_VALUE;
        } else if (deltaSeconds != -1L) {
            long deltaMilliseconds = deltaSeconds <= (Long.MAX_VALUE / 1000)
                    ? deltaSeconds * 1000
                    : Long.MAX_VALUE;
            expiresAt = currentTimeMillis + deltaMilliseconds;
            if (expiresAt < currentTimeMillis || expiresAt > HttpDate.MAX_DATE) {
                expiresAt = HttpDate.MAX_DATE; // Handle overflow & limit the date range.
            }
        }

        // If the domain is present, it must domain match. Otherwise we have a host-only cookie.
        String urlHost = url.getHost();
        if (domain == null) {
            domain = urlHost;
        } else if (!domainMatch(urlHost, domain)) {
            return null; // No domain match? This is either incompetence or malice!
        }

        // If the domain is a suffix of the url host, it must not be a public suffix.
        if (urlHost.length() != domain.length()
                && PublicSuffixDatabase.get().getEffectiveTldPlusOne(domain) == null) {
            return null;
        }

        // If the path is absent or didn't start with '/', use the default path. It's a string like
        // '/foo/bar' for a URL like 'http://example.com/foo/bar/baz'. It always starts with '/'.
        if (path == null || !path.startsWith("/")) {
            String encodedPath = encodedPath(urlPath, url);
            int lastSlash = encodedPath.lastIndexOf('/');
            path = lastSlash != 0 ? encodedPath.substring(0, lastSlash) : "/";
        }

        return new Cookie(cookieName, cookieValue, expiresAt, domain, path, secureOnly, httpOnly,
                hostOnly, persistent);
    }

    private static long parseExpires(String s, int pos, int limit) {
        pos = dateCharacterOffset(s, pos, limit, false);

        int hour = -1;
        int minute = -1;
        int second = -1;
        int dayOfMonth = -1;
        int month = -1;
        int year = -1;
        Matcher matcher = TIME_PATTERN.matcher(s);

        while (pos < limit) {
            int end = dateCharacterOffset(s, pos + 1, limit, true);
            matcher.region(pos, end);

            if (hour == -1 && matcher.usePattern(TIME_PATTERN).matches()) {
                hour = Integer.parseInt(matcher.group(1));
                minute = Integer.parseInt(matcher.group(2));
                second = Integer.parseInt(matcher.group(3));
            } else if (dayOfMonth == -1 && matcher.usePattern(DAY_OF_MONTH_PATTERN).matches()) {
                dayOfMonth = Integer.parseInt(matcher.group(1));
            } else if (month == -1 && matcher.usePattern(MONTH_PATTERN).matches()) {
                    String monthString = matcher.group(1).toLowerCase(Locale.US);
                month = MONTH_PATTERN.pattern().indexOf(monthString) / 4; // Sneaky! jan=1, dec=12.
            } else if (year == -1 && matcher.usePattern(YEAR_PATTERN).matches()) {
                year = Integer.parseInt(matcher.group(1));
            }

            pos = dateCharacterOffset(s, end + 1, limit, false);
        }

        // Convert two-digit years into four-digit years. 99 becomes 1999, 15 becomes 2015.
        if (year >= 70 && year <= 99) year += 1900;
        if (year >= 0 && year <= 69) year += 2000;

        // If any partial is omitted or out of range, return -1. The date is impossible. Note that leap
        // seconds are not supported by this syntax.
        if (year < 1601) throw new IllegalArgumentException();
        if (month == -1) throw new IllegalArgumentException();
        if (dayOfMonth < 1 || dayOfMonth > 31) throw new IllegalArgumentException();
        if (hour < 0 || hour > 23) throw new IllegalArgumentException();
        if (minute < 0 || minute > 59) throw new IllegalArgumentException();
        if (second < 0 || second > 59) throw new IllegalArgumentException();

        Calendar calendar = new GregorianCalendar(UTC);
        calendar.setLenient(false);
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    private static int dateCharacterOffset(String input, int pos, int limit, boolean invert) {
        for (int i = pos; i < limit; i++) {
            int c = input.charAt(i);
            boolean dateCharacter = (c < ' ' && c != '\t') || (c >= '\u007f')
                    || (c >= '0' && c <= '9')
                    || (c >= 'a' && c <= 'z')
                    || (c >= 'A' && c <= 'Z')
                    || (c == ':');
            if (dateCharacter == !invert) return i;
        }
        return limit;
    }

    private static long parseMaxAge(String s) {
        try {
            long parsed = Long.parseLong(s);
            return parsed <= 0L ? Long.MIN_VALUE : parsed;
        } catch (NumberFormatException e) {
            // Check if the value is an integer (positive or negative) that's too big for a long.
            if (s.matches("-?\\d+")) {
                return s.startsWith("-") ? Long.MIN_VALUE : Long.MAX_VALUE;
            }
            throw e;
        }
    }

    private static String parseDomain(String s) {
        if (s.endsWith(".")) {
            throw new IllegalArgumentException();
        }
        if (s.startsWith(".")) {
            s = s.substring(1);
        }
        String canonicalDomain = canonicalizeHost(s);
        if (canonicalDomain == null) {
            throw new IllegalArgumentException();
        }
        return canonicalDomain;
    }

    private static String encodedPath(String urlPath,URL url) {
        int pathStart = urlPath.indexOf('/', url.getProtocol().length() + 3); // "://".length() == 3.
        int pathEnd = delimiterOffset(urlPath, pathStart, urlPath.length(), "?#");
        return urlPath.substring(pathStart, pathEnd);
    }

    private static boolean domainMatch(String urlHost, String domain) {
        if (urlHost.equals(domain)) {
            return true; // As in 'example.com' matching 'example.com'.
        }

        if (urlHost.endsWith(domain)
                && urlHost.charAt(urlHost.length() - domain.length() - 1) == '.'
                && !verifyAsIpAddress(urlHost)) {
            return true; // As in 'example.com' matching 'www.example.com'.
        }

        return false;
    }

    public static void main(String[] args) {
        String co = "rememberMe=deleteMe; Path=/; Max-Age=0; Expires=Tue, 24-Dec-2024 06:15:35 GMT";
        Cookie parse = CookieUtil.parse("https://www.burrukeyou/#/dashboard", co);
        System.out.println();
    }
}

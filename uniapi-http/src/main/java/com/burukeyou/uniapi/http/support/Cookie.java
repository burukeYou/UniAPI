/*
 * Copyright (C) 2015 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.burukeyou.uniapi.http.support;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.burukeyou.uniapi.util.TimeUtil;
import lombok.Getter;
import lombok.Setter;
import okhttp3.internal.Util;
import org.springframework.lang.Nullable;

import static okhttp3.internal.Util.UTC;
import static okhttp3.internal.Util.canonicalizeHost;
import static okhttp3.internal.Util.verifyAsIpAddress;


/**
 *  Cookie
 */
@Getter
@Setter
public class Cookie implements Serializable {

  private static final long serialVersionUID = 135197726278905756L;

  public static final long MAX_DATE = 253402300799999L;

  private static final Pattern YEAR_PATTERN
      = Pattern.compile("(\\d{2,4})[^\\d]*");
  private static final Pattern MONTH_PATTERN
      = Pattern.compile("(?i)(jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec).*");
  private static final Pattern DAY_OF_MONTH_PATTERN
      = Pattern.compile("(\\d{1,2})[^\\d]*");
  private static final Pattern TIME_PATTERN
      = Pattern.compile("(\\d{1,2}):(\\d{1,2}):(\\d{1,2})[^\\d]*");

  private  String name;   // cookie pair name
  private  String value;    // cookie pair value
  private  long expiresAt;
  private  String domain;
  private  String path;
  private  boolean secure;
  private  boolean httpOnly;

  private  boolean persistent; // True if 'expires' or 'max-age' is present.
  private  boolean hostOnly; // True unless 'domain' is present.

  public Cookie() {
  }

  public Cookie(String name, String value) {
    this.name = name;
    this.value = value;
  }

  private Cookie(String name, String value, long expiresAt, String domain, String path,
                 boolean secure, boolean httpOnly, boolean hostOnly, boolean persistent) {
    this.name = name;
    this.value = value;
    this.expiresAt = expiresAt;
    this.domain = domain;
    this.path = path;
    this.secure = secure;
    this.httpOnly = httpOnly;
    this.hostOnly = hostOnly;
    this.persistent = persistent;
  }

  Cookie(Builder builder) {
    if (builder.name == null) throw new NullPointerException("builder.name == null");
    if (builder.value == null) throw new NullPointerException("builder.value == null");
    if (builder.domain == null) throw new NullPointerException("builder.domain == null");

    this.name = builder.name;
    this.value = builder.value;
    this.expiresAt = builder.expiresAt;
    this.domain = builder.domain;
    this.path = builder.path;
    this.secure = builder.secure;
    this.httpOnly = builder.httpOnly;
    this.persistent = builder.persistent;
    this.hostOnly = builder.hostOnly;
  }

  /** Returns a non-empty string with this cookie's name. */
  public String name() {
    return name;
  }

  /** Returns a possibly-empty string with this cookie's value. */
  public String value() {
    return value;
  }

  /** Returns true if this cookie does not expire at the end of the current session. */
  public boolean persistent() {
    return persistent;
  }

  /**
   * Returns the time that this cookie expires, in the same format as {@link
   * System#currentTimeMillis()}. This is December 31, 9999 if the cookie is {@linkplain
   * #persistent() not persistent}, in which case it will expire at the end of the current session.
   *
   * <p>This may return a value less than the current time, in which case the cookie is already
   * expired. Webservers may return expired cookies as a mechanism to delete previously set cookies
   * that may or may not themselves be expired.
   */
  public long expiresAt() {
    return expiresAt;
  }

  /**
   * Returns true if this cookie's domain should be interpreted as a single host name, or false if
   * it should be interpreted as a pattern. This flag will be false if its {@code Set-Cookie} header
   * included a {@code domain} attribute.
   *
   * <p>For example, suppose the cookie's domain is {@code example.com}. If this flag is true it
   * matches <strong>only</strong> {@code example.com}. If this flag is false it matches {@code
   * example.com} and all subdomains including {@code api.example.com}, {@code www.example.com}, and
   * {@code beta.api.example.com}.
   */
  public boolean hostOnly() {
    return hostOnly;
  }

  /**
   * Returns the cookie's domain. If {@link #hostOnly()} returns true this is the only domain that
   * matches this cookie; otherwise it matches this domain and all subdomains.
   */
  public String domain() {
    return domain;
  }

  /**
   * Returns this cookie's path. This cookie matches URLs prefixed with path segments that match
   * this path's segments. For example, if this path is {@code /foo} this cookie matches requests to
   * {@code /foo} and {@code /foo/bar}, but not {@code /} or {@code /football}.
   */
  public String path() {
    return path;
  }

  /**
   * Returns true if this cookie should be limited to only HTTP APIs. In web browsers this prevents
   * the cookie from being accessible to scripts.
   */
  public boolean httpOnly() {
    return httpOnly;
  }

  /** Returns true if this cookie should be limited to only HTTPS requests. */
  public boolean secure() {
    return secure;
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





  /** Parse a date as specified in RFC 6265, section 5.1.1. */
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

  /**
   * Returns the index of the next date character in {@code input}, or if {@code invert} the index
   * of the next non-date character in {@code input}.
   */
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

  /**
   * Returns the positive value if {@code attributeValue} is positive, or {@link Long#MIN_VALUE} if
   * it is either 0 or negative. If the value is positive but out of range, this returns {@link
   * Long#MAX_VALUE}.
   *
   * @throws NumberFormatException if {@code s} is not an integer of any precision.
   */
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

  /**
   * Returns a domain string like {@code example.com} for an input domain like {@code EXAMPLE.COM}
   * or {@code .example.com}.
   */
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


  /**
   * Builds a cookie. The {@linkplain #name() name}, {@linkplain #value() value}, and {@linkplain
   * #domain() domain} values must all be set before calling {@link #build}.
   */
  public static final class Builder {
    @Nullable
    String name;
    @Nullable String value;
    long expiresAt = 253402300799999L;
    @Nullable String domain;
    String path = "/";
    boolean secure;
    boolean httpOnly;
    boolean persistent;
    boolean hostOnly;

    public Builder name(String name) {
      if (name == null) throw new NullPointerException("name == null");
      if (!name.trim().equals(name)) throw new IllegalArgumentException("name is not trimmed");
      this.name = name;
      return this;
    }

    public Builder value(String value) {
      if (value == null) throw new NullPointerException("value == null");
      if (!value.trim().equals(value)) throw new IllegalArgumentException("value is not trimmed");
      this.value = value;
      return this;
    }

    public Builder expiresAt(long expiresAt) {
      if (expiresAt <= 0) expiresAt = Long.MIN_VALUE;
      if (expiresAt > MAX_DATE) expiresAt = MAX_DATE;
      this.expiresAt = expiresAt;
      this.persistent = true;
      return this;
    }

    /**
     * Set the domain pattern for this cookie. The cookie will match {@code domain} and all of its
     * subdomains.
     */
    public Builder domain(String domain) {
      return domain(domain, false);
    }

    /**
     * Set the host-only domain for this cookie. The cookie will match {@code domain} but none of
     * its subdomains.
     */
    public Builder hostOnlyDomain(String domain) {
      return domain(domain, true);
    }

    private Builder domain(String domain, boolean hostOnly) {
      if (domain == null) throw new NullPointerException("domain == null");
      String canonicalDomain = Util.canonicalizeHost(domain);
      if (canonicalDomain == null) {
        throw new IllegalArgumentException("unexpected domain: " + domain);
      }
      this.domain = canonicalDomain;
      this.hostOnly = hostOnly;
      return this;
    }

    public Builder path(String path) {
      if (!path.startsWith("/")) throw new IllegalArgumentException("path must start with '/'");
      this.path = path;
      return this;
    }

    public Builder secure() {
      this.secure = true;
      return this;
    }

    public Builder httpOnly() {
      this.httpOnly = true;
      return this;
    }

    public Cookie build() {
      return new Cookie(this);
    }
  }

  @Override public String toString() {
    return toString(false);
  }

  /**
   * @param forObsoleteRfc2965 true to include a leading {@code .} on the domain pattern. This is
   *     necessary for {@code example.com} to match {@code www.example.com} under RFC 2965. This
   *     extra dot is ignored by more recent specifications.
   */
  String toString(boolean forObsoleteRfc2965) {
    StringBuilder result = new StringBuilder();
    result.append(name);
    result.append('=');
    result.append(value);

    if (persistent) {
      if (expiresAt == Long.MIN_VALUE) {
        result.append("; max-age=0");
      } else {
        result.append("; expires=").append(TimeUtil.formatNormal(new Date(expiresAt)));
      }
    }

    if (!hostOnly) {
      result.append("; domain=");
      if (forObsoleteRfc2965) {
        result.append(".");
      }
      result.append(domain);
    }

    result.append("; path=").append(path);

    if (secure) {
      result.append("; secure");
    }

    if (httpOnly) {
      result.append("; httponly");
    }

    return result.toString();
  }

  @Override public boolean equals(@Nullable Object other) {
    if (!(other instanceof Cookie)) return false;
    Cookie that = (Cookie) other;
    return that.name.equals(name)
        && that.value.equals(value)
        && that.domain.equals(domain)
        && that.path.equals(path)
        && that.expiresAt == expiresAt
        && that.secure == secure
        && that.httpOnly == httpOnly
        && that.persistent == persistent
        && that.hostOnly == hostOnly;
  }

  @Override public int hashCode() {
    int hash = 17;
    hash = 31 * hash + name.hashCode();
    hash = 31 * hash + value.hashCode();
    hash = 31 * hash + domain.hashCode();
    hash = 31 * hash + path.hashCode();
    hash = 31 * hash + (int) (expiresAt ^ (expiresAt >>> 32));
    hash = 31 * hash + (secure ? 0 : 1);
    hash = 31 * hash + (httpOnly ? 0 : 1);
    hash = 31 * hash + (persistent ? 0 : 1);
    hash = 31 * hash + (hostOnly ? 0 : 1);
    return hash;
  }
}

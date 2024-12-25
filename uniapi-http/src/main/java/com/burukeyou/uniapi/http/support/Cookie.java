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
import java.util.Date;

import com.burukeyou.uniapi.util.TimeUtil;
import lombok.Getter;
import lombok.Setter;
import okhttp3.internal.Util;
import org.springframework.lang.Nullable;


/**
 *  Cookie
 */
@Getter
@Setter
public class Cookie implements Serializable {

  private static final long serialVersionUID = -1L;

  public static final long MAX_DATE = 253402300799999L;

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

  public Cookie(String name, String value, long expiresAt, String domain, String path,
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

  public Cookie(Builder builder) {
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

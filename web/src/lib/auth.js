/**
 * Decode a JWT token payload without a library.
 * Returns the parsed JSON payload, or null on failure.
 */
export function decodeToken(token) {
  try {
    const base64Url = token.split(".")[1];
    const base64 = base64Url.replace(/-/g, "+").replace(/_/g, "/");
    const jsonPayload = decodeURIComponent(
      atob(base64)
        .split("")
        .map((c) => "%" + ("00" + c.charCodeAt(0).toString(16)).slice(-2))
        .join("")
    );
    return JSON.parse(jsonPayload);
  } catch {
    return null;
  }
}

/**
 * Extract the user ID from a JWT token.
 * Checks numeric claims first (id, userId), falls back to sub.
 */
export function getUserIdFromToken(token) {
  const payload = decodeToken(token);
  if (!payload) return null;
  return payload.id || payload.userId || payload.sub || null;
}

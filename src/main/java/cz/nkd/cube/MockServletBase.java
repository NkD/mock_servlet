package cz.nkd.cube;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Michal Nikodim (michal.nikodim@topmonks.com)
 */
public abstract class MockServletBase implements Servlet {

    public final void init(ServletConfig config) throws ServletException {
        //nothing
    }

    public final ServletConfig getServletConfig() {
        return null;
    }

    public final void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        HttpServletRequest httpReq = (HttpServletRequest) req;
        HttpServletResponse httpResp = (HttpServletResponse) res;
        System.out.println("request: " + getServletInfo() + " - [" + new SimpleDateFormat("hh:MM:ss.SSS").format(new Date()) + "] " + httpReq.getMethod() + " " + httpReq.getPathInfo());

        int code = getResponseCode(httpReq);

        httpResp.getOutputStream().write(requestToJson(httpReq).getBytes("utf-8"));
        httpResp.setContentType("application/json");
        httpResp.setStatus(code);
        System.out.println("response: " + getServletInfo() + " - [" + new SimpleDateFormat("hh:MM:ss.SSS").format(new Date()) + "] " + httpReq.getMethod() + " " + httpReq.getPathInfo());

    }

    public abstract int getResponseCode(HttpServletRequest req);

    public final String getServletInfo() {
        return getClass().getSimpleName();
    }

    public void destroy() {
        //nothing
    }

    boolean match(HttpServletRequest req, String method, String path) {
        String m = req.getMethod();
        if (m.equalsIgnoreCase(method)) {
            String p = req.getPathInfo();
            if (!path.startsWith("/")) path = "/" + path;
            if (p.equalsIgnoreCase(path)) return true;
        }
        return false;
    }

    void wait(int milis) {
        System.out.println("    waiting: " + getServletInfo() + " (" + milis + " milis)");
        long doneTime = System.currentTimeMillis() + milis;
        while (System.currentTimeMillis() < doneTime) {
            //do nothing
        }
    }

    @SuppressWarnings("rawtypes")
    String requestToJson(HttpServletRequest req) {
        StringBuilder sb = new StringBuilder("{\n");

        sb.append("    \"servlet\" : \"").append(getServletInfo()).append("\",\n");
        sb.append("    \"method\" : \"").append(req.getMethod()).append("\",\n");
        sb.append("    \"incoming\" : {");
        sb.append("        \"url\" : \"").append(req.getRequestURL().toString()).append("\",\n");
        sb.append("        \"pathInfo\" : \"").append(req.getPathInfo()).append("\",\n");
        sb.append("        \"query\" : \"").append(req.getQueryString()).append("\",\n");
        sb.append("     },\n");
        sb.append("    \"decoded\" : {");
        sb.append("        \"url\" : \"").append(urlDecode(req.getRequestURL().toString())).append("\",\n");
        sb.append("        \"pathInfo\" : \"").append(urlDecode(req.getPathInfo())).append("\",\n");
        sb.append("        \"query\" : \"").append(urlDecode(req.getQueryString())).append("\",\n");
        sb.append("     },\n");
        sb.append("    \"headers\" : {\n");

        Enumeration headers = req.getHeaderNames();
        List<String> keys = new ArrayList<String>();
        while (headers.hasMoreElements()) {
            keys.add((String) headers.nextElement());
        }
        Collections.sort(keys);
        int i = 0;
        for (String key : keys) {
            if (i != 0) sb.append(",\n");
            i++;
            sb.append("        \"").append(key).append("\" : \"").append(req.getHeader(key)).append("\"");
        }
        sb.append("\n    },\n");

        sb.append("    \"payload\" : ").append(getPayload(req)).append("\n");

        sb.append("}");
        return sb.toString();
    }

    String urlDecode(String value) {
        try {
            if (value==null) return null;
            return URLDecoder.decode(value, "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    String getPayload(HttpServletRequest req) {
        String payload = null;
        ServletInputStream is = null;
        try {
            is = req.getInputStream();
            if (is != null) {
                StringWriter sw = new StringWriter();
                InputStreamReader isr = new InputStreamReader(is, "utf-8");
                char[] buffer = new char[4096];
                int n = 0;
                while (-1 != (n = isr.read(buffer))) {
                    sw.write(buffer, 0, n);
                }
                payload = sw.toString();
            }
        } catch (Throwable t) {
            payload = "\"read payload error: " + t.getMessage() + "\"";
        } finally {
            if (is != null) try {
                is.close();
            } catch (IOException e) {
                //nothing
            }
        }
        return jsonEscape(payload);
    }

    String jsonEscape(String string) {
        if (string == null || string.length() == 0) return "null";
        char c = 0;
        int i, len = string.length();
        StringBuilder sb = new StringBuilder(len + 4);
        String t;
        sb.append('"');
        for (i = 0; i < len; i += 1) {
            c = string.charAt(i);
            switch (c) {
                case '\\':
                case '"':
                    sb.append('\\');
                    sb.append(c);
                    break;
                case '/':
                    sb.append('\\');
                    sb.append(c);
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                default:
                    if (c < ' ') {
                        t = "000" + Integer.toHexString(c);
                        sb.append("\\u" + t.substring(t.length() - 4));
                    } else {
                        sb.append(c);
                    }
            }
        }
        sb.append('"');
        return sb.toString();
    }

}

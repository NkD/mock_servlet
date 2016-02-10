package cz.nkd.cube;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * @author Michal Nikodim (michal.nikodim@topmonks.com)
 */
public class SpeedServlet implements Servlet{

    private byte[] responsePayloadBytes = "{\"response\":\"payload\"}".getBytes();
    
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        HttpServletResponse resp = (HttpServletResponse) res;
        resp.setStatus(200);
        resp.getOutputStream().write(responsePayloadBytes);
        resp.flushBuffer();
    }

    public void init(ServletConfig config) throws ServletException {
        //nothing
    }

    public ServletConfig getServletConfig() {
        return null;
    }
    
    public String getServletInfo() {
        return "SpeedServlet";
    }

    public void destroy() {
        //nothing
    }

}

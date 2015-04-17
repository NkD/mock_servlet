package cz.nkd.cube;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Michal Nikodim (michal.nikodim@topmonks.com)
 */
public class MockServlet2 extends MockServletBase {

    @Override
    public int getResponseCode(HttpServletRequest req) {
        int code = 200;
        //wait(10000);
        //if (4 > 2) throw new RuntimeException("test exception");
        return code;
    }

}

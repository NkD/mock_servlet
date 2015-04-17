package cz.nkd.cube;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Michal Nikodim (michal.nikodim@topmonks.com)
 */
public class MockServlet3 extends MockServletBase {

    @Override
    public int getResponseCode(HttpServletRequest req) {
        int code = 200;
        //wait(10000);
        return code;
    }

}

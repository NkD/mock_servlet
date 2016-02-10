package cz.nkd.cube;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Michal Nikodim (michal.nikodim@topmonks.com)
 */
public class MockServlet1 extends MockServletBase {

    @Override
    public int getResponseCode(HttpServletRequest req, HttpServletResponse resp) {
        int code = 200;
        
        //wait(3000);
        return code;
    }
    
    @Override
    int getSlowlyResponse() {
        return 0;
    }

}

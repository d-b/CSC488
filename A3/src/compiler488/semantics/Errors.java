package compiler488.semantics;

import java.util.HashMap;
import java.util.Map;

class Errors {
    @SuppressWarnings("serial")
    static final Map<Integer, String> messageMap = new HashMap<Integer, String>() {{
        put(10, "Name of scalar variable collides with a previous declaration.");
        put(11, "Name of function collides with a previous declaration.");
        put(12, "Name of function collides with a previous declaration.");
        put(15, "Duplicate parameter name.");
        put(17, "Name of procedure collides with a previous declaration.");
        put(18, "Name of procedure collides with a previous declaration.");
        put(19, "Name of one dimensional array collides with a previous declaration.");
        put(46, "Lower bound must be less than or equal to upper bound.");
        put(48, "Name of two dimensional array collides with a previous declaration.");
        put(49, "The routine does not match the forward declaration.");
    }};
    
    static final public String getError(int actionNumber) {
        return messageMap.get(actionNumber);
    }
}

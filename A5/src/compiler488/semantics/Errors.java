package compiler488.semantics;

import java.util.HashMap;
import java.util.Map;

/** Error library for semantic analysis
 *  @author Daniel Bloemendal
 */
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
        put(29, "Identifier is not visible or has not been declared.");
        put(30, "Expression is not of type 'boolean'.");
        put(31, "Expression is not of type 'integer'.");
        put(32, "Left and right operands are not of the same type.");
        put(33, "The two expressions in the conditional are not of the same type.");
        put(34, "Variable and expression are not of the same type.");
        put(35, "Expression type does not match the return type of the enclosing function.");
        put(36, "The type of the passed argument does not match the type of the corresponding formal parameter.");
        put(37, "Identifier was not declared as a scalar variable.");
        put(38, "Identifier was not declared as a one dimensional array.");
        put(40, "Identifier was not declared as a function.");
        put(41, "Identifier was not declared as a procedure.");
        put(43, "Number of arguments is not equal to the number of formal parameters.");
        put(46, "Lower bound must be less than or equal to upper bound.");
        put(48, "Name of two dimensional array collides with a previous declaration.");
        put(49, "The routine does not match the forward declaration.");
        put(50, "Exit statement cannot lie outside of a loop.");
        put(51, "Result statement must lie directly within a function.");
        put(52, "Return statement must lie directly within a procedure.");
        put(55, "Identifier was not declared as a two dimensional array.");
        put(56, "Forward declared routine has no body.");
    }};
    
    static final public String getError(int actionNumber) {
        return messageMap.get(actionNumber);
    }
}

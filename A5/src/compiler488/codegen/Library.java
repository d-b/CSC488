package compiler488.codegen;

/**
 * Code generator library
 *
 * @author Mike
 * @author Daniel Bloemendal
 */
public final class Library {
    public static final String code
              = "; ------------------------------------\n"
              + "; Start of runtime library \n"
              + "; ------------------------------------\n"
              + "    SECTION .library\n\n"
              + "print:\n"
              + "    SAVECTX 0\n"
              + "    PUSH 0\n"
              + "__print_start:\n"
              + "    DUP\n"
              + "    ADDR 0 -2\n"
              + "    LOAD\n"
              + "    LOAD\n"
              + "    LT\n"
              + "    PUSH __print_end\n"
              + "    BF\n"
              + "    DUP\n"
              + "    ADDR 0 -2\n"
              + "    LOAD\n"
              + "    PUSH 1\n"
              + "    ADD\n"
              + "    ADD\n"
              + "    LOAD\n"
              + "    PRINTC\n"
              + "    PUSH 1\n"
              + "    ADD\n"
              + "    PUSH __print_start\n"
              + "    BR\n"
              + "__print_end: POP\n"
              + "    RESTORECTX 0 1\n"
              + "    BR\n\n"
              + "; ------------------------------------\n"
              + "; End of runtime library\n"
              + "; ------------------------------------\n\n";
}

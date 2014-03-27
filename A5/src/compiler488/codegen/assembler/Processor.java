package compiler488.codegen.assembler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Instruction processor annotation
 *
 * @author Daniel Bloemendal
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Processor {
 String target();
 Operand.OperandType[] operands();
 int size();
}

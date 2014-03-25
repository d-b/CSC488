package compiler488.codegen;

class LabelNotResolvedError extends Exception {
    private static final long serialVersionUID = 1L;
    LabelNotResolvedError(String message) {
        super(message);
    }
}

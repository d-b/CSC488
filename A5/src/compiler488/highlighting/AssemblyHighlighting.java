package compiler488.highlighting;

import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import compiler488.highlighting.Operand.OperandType;

public class AssemblyHighlighting {
    public static final String ANSI_RESET  = "\u001B[0m";
    public static final String ANSI_BRIGHT = "\u001B[1m";
    public static final String ANSI_BLACK  = "\u001B[30m";
    public static final String ANSI_RED    = "\u001B[31m";
    public static final String ANSI_GREEN  = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE   = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN   = "\u001B[36m";
    public static final String ANSI_WHITE  = "\u001B[37m";

    public AssemblyHighlighting(PrintStream stream) {
        this(stream, true);
    }

    public AssemblyHighlighting(PrintStream stream, boolean highlighting) {
        this.stream        = stream;
        this.highlighting  = highlighting;
        buffer             = new StringBuffer();
        patternLine        = Pattern.compile("(.*?\\n)(.*)", Pattern.DOTALL);
        patternComment     = Pattern.compile("([^;]*)(.*)");
        patternInstruction = Pattern.compile("\\s*(?:(\\w+):)?\\s*(?:(\\w+)(?:\\s+(.*))?)?");
        patternSection     = Pattern.compile("\\s*SECTION\\s+(\\.\\w+)\\s*", Pattern.CASE_INSENSITIVE);
        patternOpBoolean   = Pattern.compile("\\$true|\\$false", Pattern.CASE_INSENSITIVE);
        patternOpString    = Pattern.compile("\"[^\"]*\"");
        patternOpLabel     = Pattern.compile("[a-zA-Z_]\\w*");
    }

    public void setHighlighting(boolean on) {
        highlighting = on; process();
    }

    public void print(String str) {
        buffer.append(str); process();
    }

    public void println(String line) {
        buffer.append(line + "\n"); process();
    }

    public void flush() {
        if(buffer.length() > 0) {
            buffer.append('\n'); process();
        }
    }

    void bright(String line) {
        stream.println(ANSI_BRIGHT + line + ANSI_RESET);
    }

    void highlight(String line) {
        // Separate instruction and comment
        Matcher m = patternComment.matcher(line);
        if(!m.matches()) throw new RuntimeException("expected a match");
        String instruction = m.group(1);
        String comment = m.group(2);

        // See if it is a section
        m = patternSection.matcher(instruction);
        if(m.matches()) {
            bright(ANSI_GREEN + "    SECTION " + ANSI_RED + m.group(1)); return;
        }

        // Try to parse the instruction
        m = patternInstruction.matcher(instruction);
        if(!m.matches()) return;
        String label = m.group(1);
        String operation = m.group(2);
        List<Operand> operands = parseOperands(m.group(3));

        // If there is no operation
        if(operation == null) {
            String result = new String();
            if(label != null ) result += ANSI_RED + label + ":" + " ";
            if(comment != null) result += ANSI_RESET + ANSI_CYAN + comment;
            bright(result); return;
        }

        // Build the highlighted instruction
        String result = new String();
        if(label != null) bright(ANSI_RED + label + ":");
        result += "    " + ANSI_BLUE + operation + " ";
        for(Operand op : operands) {
            switch(op.type) {
            case OPERAND_BOOLEAN: result += ANSI_YELLOW; break;
            case OPERAND_STRING:  result += ANSI_WHITE; break;
            case OPERAND_LABEL:   result += ANSI_RED; break;
            case OPERAND_INTEGER: result += ANSI_CYAN; break;
            } result += op.operand + " ";
        }

        // Append the comment
        if(comment != null) result += ANSI_RESET + ANSI_CYAN + comment;

        // Output the result
        if(operation != null || comment.length() > 0)
            bright(result);
    }

    List<Operand> parseOperands(String operands) {
        List<Operand> result = new LinkedList<Operand>();
        if(operands == null) return result;
        for(String part : operands.split("[ ]+(?=([^\"]*\"[^\"]*\")*[^\"]*$)")) {
            // If it is a label
            if(patternOpLabel.matcher(part).matches())
                result.add(new Operand(OperandType.OPERAND_LABEL, part));
            // If it is a string
            else if(patternOpString.matcher(part).matches())
                result.add(new Operand(OperandType.OPERAND_STRING, part));
            // If it is a boolean
            else if(patternOpBoolean.matcher(part).matches())
                result.add(new Operand(OperandType.OPERAND_BOOLEAN, part.toLowerCase()));
            // If it is a number
            else try { result.add(new Operand(OperandType.OPERAND_INTEGER, ((Integer)Integer.parseInt(part)).toString())); }
            catch(NumberFormatException e) {}
        } return result;
    }

    void process() {
        // Flush the buffer and bail out if we aren't highlighting
        if(!highlighting) {
            stream.print(buffer.toString());
            buffer.setLength(0); return;
        }

        for(;;) {
            // Line matching
            Matcher m = patternLine.matcher(buffer.toString());
            if(!m.matches()) break;

            // Get next line
            String line = m.group(1);
            if(line == null) break;
            buffer.delete(0, line.length());

            // Process the line
            highlight(line.replace("\n", ""));
        }
    }

    // Internal members
    PrintStream  stream;
    StringBuffer buffer;
    boolean      highlighting;

    // Regular expressions
    private Pattern patternLine;
    private Pattern patternComment;
    private Pattern patternInstruction;
    private Pattern patternSection;
    private Pattern patternOpBoolean;
    private Pattern patternOpString;
    private Pattern patternOpLabel;
}

class Operand {
    // The types of operands
    enum OperandType { OPERAND_BOOLEAN, OPERAND_STRING, OPERAND_LABEL, OPERAND_INTEGER };

    // Instantiate an operand
    public Operand(OperandType type, String operand) {
        this.type = type; this.operand = operand;
    }

    // Operand info
    public OperandType type;
    public String operand;
}

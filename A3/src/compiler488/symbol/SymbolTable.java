package compiler488.symbol;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/** Symbol Table
 *  This almost empty class is a framework for implementing
 *  a Symbol Table class for the CSC488S compiler
 *  
 *  Each implementation can change/modify/delete this class
 *  as they see fit.
 *
 *  @author Daniel Bloemendal
 */

class SymbolScope {
    Map<String, Symbol> symbolsMap;
    
    SymbolScope() {
        symbolsMap = new HashMap<String, Symbol>();
    }
}

public class SymbolTable {
    public enum ScopeType { Program, Function, Statement, Procedure }
    
    Stack<SymbolScope> scopeStack;
    
    public SymbolTable() {
        scopeStack = new Stack<SymbolScope>();
    }
    
    public void Initialize() {
    }

    public void Finalize() {
    }
    
    public void scopeEnter(ScopeType type) {
        SymbolScope scope = new SymbolScope();
        scopeStack.push(scope);
    }
    
    public void scopeExit() {
        scopeStack.pop();
    }
}

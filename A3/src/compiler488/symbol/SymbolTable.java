package compiler488.symbol;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/** Symbol Table
 *  This almost empty class is a framework for implementing
 *  a Symbol Table class for the CSC488S compiler
 *  
 *  Each implementation can change/modify/delete this class
 *  as they see fit.
 *
 *  @author Daniel Bloemendal
 */

class Scope {
    Map<String, Symbol> symbolsMap;
    
    Scope() {
        symbolsMap = new HashMap<String, Symbol>();
    }
    public Symbol get(String name) {
        return symbolsMap.get(name);
    }
    public void set(String name, Symbol symbol) {
        symbolsMap.put(name, symbol);
    }
}

public class SymbolTable {
    public enum ScopeType { Program, Function, Statement, Procedure }
    
    Deque<Scope> scopeStack;
    
    Scope scopeTop() {
        return scopeStack.peek(); 
    }
    
    public SymbolTable() {
        scopeStack = new LinkedList<Scope>();
    }
    
    public void Initialize() {}
    public void Finalize()   {}
    
    public Symbol find(String name) {
        return find(name, true /* allScopes*/);
    }
    
    public Symbol find(String name, Boolean allScopes) {
        if(!allScopes)
            return scopeTop().get(name);
        for(Scope scope: scopeStack) {
            Symbol symbol = scope.get(name);
            if(symbol != null) return symbol;
        } return null;
    }
    
    public void scopeEnter(ScopeType type) {
        Scope scope = new Scope();
        scopeStack.push(scope);
    }
    
    public Boolean scopeSet(String name, Symbol symbol) {
        Scope scope = scopeTop();
        if(scope.get(name) != null) return false;
        scope.set(name, symbol);
        return true;
    }
    
    public void scopeExit() {
        scopeStack.pop();
    }
}

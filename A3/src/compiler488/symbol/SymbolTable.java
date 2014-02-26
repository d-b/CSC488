package compiler488.symbol;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import compiler488.ast.decl.RoutineDecl;

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
    SymbolTable.ScopeType type;
    Map<String, Symbol>   map;
    RoutineDecl           routine;
    
    Scope(SymbolTable.ScopeType type) {
        this(type, null);
    }
    Scope(SymbolTable.ScopeType type, RoutineDecl routine) {
        map = new HashMap<String, Symbol>();
        this.routine = routine;
    }    
    
    public Symbol get(String name) {
        return map.get(name);
    }
    public void set(String name, Symbol symbol) {
        map.put(name, symbol);
    }
    public SymbolTable.ScopeType type() {
        return type;
    }
    public RoutineDecl routine() {
        return routine;
    }
}

public class SymbolTable {
    public enum ScopeType { Program, Function, Statement, Procedure, Wrapper, Unknown }
    
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
        if(scopeTop() == null) return null;
        if(!allScopes)
            return scopeTop().get(name);
        for(Scope scope: scopeStack) {
            Symbol symbol = scope.get(name);
            if(symbol != null) return symbol;
        } return null;
    }
    
    public void scopeEnter(ScopeType type) {
        Scope scope = new Scope(type);
        scopeStack.push(scope);
    }
    
    public void scopeEnter(ScopeType type, RoutineDecl routine) {
        Scope scope = new Scope(type, routine);
        scopeStack.push(scope);
    }    
    
    public Boolean scopeSet(String name, Symbol symbol) {
        Scope scope = scopeTop();
        if(scope == null
        || scope.get(name) != null) return false;
        scope.set(name, symbol);
        return true;
    }
    
    public ScopeType scopeType() {
        return (scopeTop() != null) ?
                scopeTop().type(): ScopeType.Unknown;
    }    
    
    public RoutineDecl scopeRoutine() {
        return (scopeTop() != null) ?
                scopeTop().routine() : null; 
    }  
    
    public void scopeExit() {
        scopeStack.pop();
    }
}

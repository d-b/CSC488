package compiler488.symbol;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import compiler488.ast.type.Type;
import compiler488.ast.type.FunctionType;

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
    public enum ScalarType { Integer, Boolean }
    
    Deque<Scope> scopeStack;
    
    Scope scopeTop() {
        return scopeStack.peek(); 
    }
    
    public SymbolTable() {
        scopeStack = new LinkedList<Scope>();
    }
    
    public void Initialize() {}
    public void Finalize()   {}
    
    public void scopeEnter(ScopeType type) {
        Scope scope = new Scope();
        scopeStack.push(scope);
    }
    
    public Symbol scopeFind(String name) {
        for(Scope scope: scopeStack) {
            Symbol symbol = scope.get(name);
            if(symbol != null) return symbol;
        } return null;
    }
    
    public void scopeExit() {
        scopeStack.pop();
    }
    
    public void declareVariable(String name, Type type) {
        scopeTop().set(name, new VariableSymbol(name, type));
    }
    
    public void declareVariable(String name, Type type, int lb, int ub) {
        scopeTop().set(name, new VariableSymbol(name, type, lb, ub));
    }
    
    public void declareVariable(String name, Type type, int lb1, int ub1, int lb2, int ub2) {
        scopeTop().set(name, new VariableSymbol(name, type, lb1, ub1, lb2, ub2));
    }
    
    public void declareFunction(String name, FunctionType type) {
        scopeTop().set(name, new FunctionSymbol(name, type));
    }
}

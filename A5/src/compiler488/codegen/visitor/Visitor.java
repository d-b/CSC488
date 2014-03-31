package compiler488.codegen.visitor;

import java.lang.reflect.Method;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import compiler488.ast.AST;
import compiler488.ast.ASTList;

/**
 * Helper for traversing an AST automatically and manually
 *
 * @author Daniel Bloemendal
 */
public class Visitor {
    //
    // Visitor interface
    //

    public Visitor() { this(null); }
    public Visitor(List<String> source) {
        // Instantiate internals
        processorsMap     = new HashMap<String, Method>();
        preProcessorsMap  = new HashMap<String, Method>();
        postProcessorsMap = new HashMap<String, Method>();
        visitorGrey       = new HashSet<AST>();
        visitorExcluded   = new HashSet<AST>();
        visitorStack      = new LinkedList<AST>();
        // Populate the processors
        populateMappings();
    }

    /**
     * Manual visitation of an AST node
     *
     * @param node the AST node to visit
     * @throws Exception any exception that occurred during visitation
     */
    public void visit(AST node) throws Exception {
        invokeProcessor(node, processorsMap);
    }

    /**
     * Manual visitation of all AST nodes in a list
     *
     * @param list the list of AST nodes to visit
     * @throws Exception any exception that occurred during visitation
     */
    public void visit(List<AST> list) throws Exception {
        for(AST node : list) visit(node);
    }

    /**
     * Traverse an AST starting from a specified root node
     *
     * @param root the root of the AST
     * @throws Exception any exception that occurred during traversal
     */
    public void traverse(AST root) throws Exception {
        // Add the initial element to the stack
        visitorStack.add(root);

        // Traverse the AST
        while(!visitorStack.isEmpty()) {
            // Fetch top of the analysis stack
            AST top = visitorStack.peek();

            // Skip excluded nodes
            if(visitorExcluded.contains(top)) {
                visitorStack.pop(); continue;
            }

            // If the node has not yet been seen
            if(!visitorGrey.contains(top)) {
                // Add node to grey set and invoke preprocessor
                visitorGrey.add(top);
                invokePreProcessor(top);

                // Add children to the stack
                List<AST> children = top.getChildren();
                ListIterator<AST> li = children.listIterator(children.size());
                while(li.hasPrevious()) {
                    AST node = li.previous();
                    if(node != null) visitorStack.push(node);
                }
            }
            // Finish processing node and pop it off of the stack
            else {
                invokePostProcessor(top);
                visitorStack.pop();
            }
        }

        // Reset state
        visitorGrey.clear();
        visitorExcluded.clear();
    }

    /**
     * Exclude an AST node from automatic traversal
     *
     * @param node the node to exclude
     */
    public void exclude(AST node) {
        visitorExcluded.add(node);
    }

    //
    // Default manual visitors
    //

    @Processor(target="ASTList")
    void processAstList(ASTList<? extends AST> list) throws Exception {
        for(AST node : list.getList()) visit(node);
    }

    //
    // Processor management
    //

    void populateMappings() {
        // Get class tree
        Deque<Class<?>> classes = new LinkedList<Class<?>>();
        for(Class<?> cls = this.getClass(); !cls.equals(Object.class); cls = cls.getSuperclass())
            classes.push(cls);
        // Loop over classes
        while(!classes.isEmpty()) {
            Class<?> cls = classes.pop();
            for(Method method : cls.getDeclaredMethods()) {
                Processor procInfo = method.getAnnotation(Processor.class);
                PreProcessor preProcInfo = method.getAnnotation(PreProcessor.class);
                PostProcessor postProcInfo = method.getAnnotation(PostProcessor.class);
                if(procInfo != null) processorsMap.put(procInfo.target(), method);
                if(preProcInfo != null) preProcessorsMap.put(preProcInfo.target(), method);
                if(postProcInfo != null) postProcessorsMap.put(postProcInfo.target(), method);
            }
        }
    }

    void invokePreProcessor(AST node) throws Exception {
        invokeProcessor(node, preProcessorsMap);
    }

    void invokePostProcessor(AST node) throws Exception {
        invokeProcessor(node, postProcessorsMap);
    }

    void invokeProcessor(AST node, Map<String, Method> map) throws Exception {
        // Get class tree
        Deque<Class<?>> classes = new LinkedList<Class<?>>();
        for(Class<?> cls = node.getClass(); !cls.equals(Object.class); cls = cls.getSuperclass())
            classes.push(cls);
        // Loop over classes
        while(!classes.isEmpty()) {
            Class<?> cls = classes.pop();
            Method m = map.get(cls.getSimpleName());
            if(m == null) continue;
            m.setAccessible(true);

            // Invoke the processor on object
            m.invoke(this, node);
        }
    }

    // Processor maps
    private Map<String, Method> processorsMap;
    private Map<String, Method> preProcessorsMap;
    private Map<String, Method> postProcessorsMap;

    // Visitor state
    private Set<AST>   visitorGrey;     // AST nodes which have been seen
    private Set<AST>   visitorExcluded; // AST nodes which have been excluded
    private Deque<AST> visitorStack;    // AST node stack
}

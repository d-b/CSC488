// Generated from W:\workspace\CSC488\ANTLR\Grammar\CSC488.g4 by ANTLR 4.1
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link CSC488Parser}.
 */
public interface CSC488Listener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link CSC488Parser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpression(@NotNull CSC488Parser.ExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link CSC488Parser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpression(@NotNull CSC488Parser.ExpressionContext ctx);

	/**
	 * Enter a parse tree produced by {@link CSC488Parser#variablename}.
	 * @param ctx the parse tree
	 */
	void enterVariablename(@NotNull CSC488Parser.VariablenameContext ctx);
	/**
	 * Exit a parse tree produced by {@link CSC488Parser#variablename}.
	 * @param ctx the parse tree
	 */
	void exitVariablename(@NotNull CSC488Parser.VariablenameContext ctx);

	/**
	 * Enter a parse tree produced by {@link CSC488Parser#and_expr}.
	 * @param ctx the parse tree
	 */
	void enterAnd_expr(@NotNull CSC488Parser.And_exprContext ctx);
	/**
	 * Exit a parse tree produced by {@link CSC488Parser#and_expr}.
	 * @param ctx the parse tree
	 */
	void exitAnd_expr(@NotNull CSC488Parser.And_exprContext ctx);

	/**
	 * Enter a parse tree produced by {@link CSC488Parser#boolean_literal}.
	 * @param ctx the parse tree
	 */
	void enterBoolean_literal(@NotNull CSC488Parser.Boolean_literalContext ctx);
	/**
	 * Exit a parse tree produced by {@link CSC488Parser#boolean_literal}.
	 * @param ctx the parse tree
	 */
	void exitBoolean_literal(@NotNull CSC488Parser.Boolean_literalContext ctx);

	/**
	 * Enter a parse tree produced by {@link CSC488Parser#scope}.
	 * @param ctx the parse tree
	 */
	void enterScope(@NotNull CSC488Parser.ScopeContext ctx);
	/**
	 * Exit a parse tree produced by {@link CSC488Parser#scope}.
	 * @param ctx the parse tree
	 */
	void exitScope(@NotNull CSC488Parser.ScopeContext ctx);

	/**
	 * Enter a parse tree produced by {@link CSC488Parser#or_expr}.
	 * @param ctx the parse tree
	 */
	void enterOr_expr(@NotNull CSC488Parser.Or_exprContext ctx);
	/**
	 * Exit a parse tree produced by {@link CSC488Parser#or_expr}.
	 * @param ctx the parse tree
	 */
	void exitOr_expr(@NotNull CSC488Parser.Or_exprContext ctx);

	/**
	 * Enter a parse tree produced by {@link CSC488Parser#functionHead}.
	 * @param ctx the parse tree
	 */
	void enterFunctionHead(@NotNull CSC488Parser.FunctionHeadContext ctx);
	/**
	 * Exit a parse tree produced by {@link CSC488Parser#functionHead}.
	 * @param ctx the parse tree
	 */
	void exitFunctionHead(@NotNull CSC488Parser.FunctionHeadContext ctx);

	/**
	 * Enter a parse tree produced by {@link CSC488Parser#type}.
	 * @param ctx the parse tree
	 */
	void enterType(@NotNull CSC488Parser.TypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link CSC488Parser#type}.
	 * @param ctx the parse tree
	 */
	void exitType(@NotNull CSC488Parser.TypeContext ctx);

	/**
	 * Enter a parse tree produced by {@link CSC488Parser#procedurename}.
	 * @param ctx the parse tree
	 */
	void enterProcedurename(@NotNull CSC488Parser.ProcedurenameContext ctx);
	/**
	 * Exit a parse tree produced by {@link CSC488Parser#procedurename}.
	 * @param ctx the parse tree
	 */
	void exitProcedurename(@NotNull CSC488Parser.ProcedurenameContext ctx);

	/**
	 * Enter a parse tree produced by {@link CSC488Parser#parametername}.
	 * @param ctx the parse tree
	 */
	void enterParametername(@NotNull CSC488Parser.ParameternameContext ctx);
	/**
	 * Exit a parse tree produced by {@link CSC488Parser#parametername}.
	 * @param ctx the parse tree
	 */
	void exitParametername(@NotNull CSC488Parser.ParameternameContext ctx);

	/**
	 * Enter a parse tree produced by {@link CSC488Parser#unr_expr}.
	 * @param ctx the parse tree
	 */
	void enterUnr_expr(@NotNull CSC488Parser.Unr_exprContext ctx);
	/**
	 * Exit a parse tree produced by {@link CSC488Parser#unr_expr}.
	 * @param ctx the parse tree
	 */
	void exitUnr_expr(@NotNull CSC488Parser.Unr_exprContext ctx);

	/**
	 * Enter a parse tree produced by {@link CSC488Parser#add_expr}.
	 * @param ctx the parse tree
	 */
	void enterAdd_expr(@NotNull CSC488Parser.Add_exprContext ctx);
	/**
	 * Exit a parse tree produced by {@link CSC488Parser#add_expr}.
	 * @param ctx the parse tree
	 */
	void exitAdd_expr(@NotNull CSC488Parser.Add_exprContext ctx);

	/**
	 * Enter a parse tree produced by {@link CSC488Parser#parameter}.
	 * @param ctx the parse tree
	 */
	void enterParameter(@NotNull CSC488Parser.ParameterContext ctx);
	/**
	 * Exit a parse tree produced by {@link CSC488Parser#parameter}.
	 * @param ctx the parse tree
	 */
	void exitParameter(@NotNull CSC488Parser.ParameterContext ctx);

	/**
	 * Enter a parse tree produced by {@link CSC488Parser#procedureHead}.
	 * @param ctx the parse tree
	 */
	void enterProcedureHead(@NotNull CSC488Parser.ProcedureHeadContext ctx);
	/**
	 * Exit a parse tree produced by {@link CSC488Parser#procedureHead}.
	 * @param ctx the parse tree
	 */
	void exitProcedureHead(@NotNull CSC488Parser.ProcedureHeadContext ctx);

	/**
	 * Enter a parse tree produced by {@link CSC488Parser#functionname}.
	 * @param ctx the parse tree
	 */
	void enterFunctionname(@NotNull CSC488Parser.FunctionnameContext ctx);
	/**
	 * Exit a parse tree produced by {@link CSC488Parser#functionname}.
	 * @param ctx the parse tree
	 */
	void exitFunctionname(@NotNull CSC488Parser.FunctionnameContext ctx);

	/**
	 * Enter a parse tree produced by {@link CSC488Parser#parameters}.
	 * @param ctx the parse tree
	 */
	void enterParameters(@NotNull CSC488Parser.ParametersContext ctx);
	/**
	 * Exit a parse tree produced by {@link CSC488Parser#parameters}.
	 * @param ctx the parse tree
	 */
	void exitParameters(@NotNull CSC488Parser.ParametersContext ctx);

	/**
	 * Enter a parse tree produced by {@link CSC488Parser#term_expr}.
	 * @param ctx the parse tree
	 */
	void enterTerm_expr(@NotNull CSC488Parser.Term_exprContext ctx);
	/**
	 * Exit a parse tree produced by {@link CSC488Parser#term_expr}.
	 * @param ctx the parse tree
	 */
	void exitTerm_expr(@NotNull CSC488Parser.Term_exprContext ctx);

	/**
	 * Enter a parse tree produced by {@link CSC488Parser#pred_expr}.
	 * @param ctx the parse tree
	 */
	void enterPred_expr(@NotNull CSC488Parser.Pred_exprContext ctx);
	/**
	 * Exit a parse tree produced by {@link CSC488Parser#pred_expr}.
	 * @param ctx the parse tree
	 */
	void exitPred_expr(@NotNull CSC488Parser.Pred_exprContext ctx);

	/**
	 * Enter a parse tree produced by {@link CSC488Parser#generalBound}.
	 * @param ctx the parse tree
	 */
	void enterGeneralBound(@NotNull CSC488Parser.GeneralBoundContext ctx);
	/**
	 * Exit a parse tree produced by {@link CSC488Parser#generalBound}.
	 * @param ctx the parse tree
	 */
	void exitGeneralBound(@NotNull CSC488Parser.GeneralBoundContext ctx);

	/**
	 * Enter a parse tree produced by {@link CSC488Parser#text}.
	 * @param ctx the parse tree
	 */
	void enterText(@NotNull CSC488Parser.TextContext ctx);
	/**
	 * Exit a parse tree produced by {@link CSC488Parser#text}.
	 * @param ctx the parse tree
	 */
	void exitText(@NotNull CSC488Parser.TextContext ctx);

	/**
	 * Enter a parse tree produced by {@link CSC488Parser#declaration}.
	 * @param ctx the parse tree
	 */
	void enterDeclaration(@NotNull CSC488Parser.DeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link CSC488Parser#declaration}.
	 * @param ctx the parse tree
	 */
	void exitDeclaration(@NotNull CSC488Parser.DeclarationContext ctx);

	/**
	 * Enter a parse tree produced by {@link CSC488Parser#not_expr}.
	 * @param ctx the parse tree
	 */
	void enterNot_expr(@NotNull CSC488Parser.Not_exprContext ctx);
	/**
	 * Exit a parse tree produced by {@link CSC488Parser#not_expr}.
	 * @param ctx the parse tree
	 */
	void exitNot_expr(@NotNull CSC488Parser.Not_exprContext ctx);

	/**
	 * Enter a parse tree produced by {@link CSC488Parser#statement}.
	 * @param ctx the parse tree
	 */
	void enterStatement(@NotNull CSC488Parser.StatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link CSC488Parser#statement}.
	 * @param ctx the parse tree
	 */
	void exitStatement(@NotNull CSC488Parser.StatementContext ctx);

	/**
	 * Enter a parse tree produced by {@link CSC488Parser#arrayname}.
	 * @param ctx the parse tree
	 */
	void enterArrayname(@NotNull CSC488Parser.ArraynameContext ctx);
	/**
	 * Exit a parse tree produced by {@link CSC488Parser#arrayname}.
	 * @param ctx the parse tree
	 */
	void exitArrayname(@NotNull CSC488Parser.ArraynameContext ctx);

	/**
	 * Enter a parse tree produced by {@link CSC488Parser#input}.
	 * @param ctx the parse tree
	 */
	void enterInput(@NotNull CSC488Parser.InputContext ctx);
	/**
	 * Exit a parse tree produced by {@link CSC488Parser#input}.
	 * @param ctx the parse tree
	 */
	void exitInput(@NotNull CSC488Parser.InputContext ctx);

	/**
	 * Enter a parse tree produced by {@link CSC488Parser#arguments}.
	 * @param ctx the parse tree
	 */
	void enterArguments(@NotNull CSC488Parser.ArgumentsContext ctx);
	/**
	 * Exit a parse tree produced by {@link CSC488Parser#arguments}.
	 * @param ctx the parse tree
	 */
	void exitArguments(@NotNull CSC488Parser.ArgumentsContext ctx);

	/**
	 * Enter a parse tree produced by {@link CSC488Parser#program}.
	 * @param ctx the parse tree
	 */
	void enterProgram(@NotNull CSC488Parser.ProgramContext ctx);
	/**
	 * Exit a parse tree produced by {@link CSC488Parser#program}.
	 * @param ctx the parse tree
	 */
	void exitProgram(@NotNull CSC488Parser.ProgramContext ctx);

	/**
	 * Enter a parse tree produced by {@link CSC488Parser#mul_expr}.
	 * @param ctx the parse tree
	 */
	void enterMul_expr(@NotNull CSC488Parser.Mul_exprContext ctx);
	/**
	 * Exit a parse tree produced by {@link CSC488Parser#mul_expr}.
	 * @param ctx the parse tree
	 */
	void exitMul_expr(@NotNull CSC488Parser.Mul_exprContext ctx);

	/**
	 * Enter a parse tree produced by {@link CSC488Parser#integer_literal}.
	 * @param ctx the parse tree
	 */
	void enterInteger_literal(@NotNull CSC488Parser.Integer_literalContext ctx);
	/**
	 * Exit a parse tree produced by {@link CSC488Parser#integer_literal}.
	 * @param ctx the parse tree
	 */
	void exitInteger_literal(@NotNull CSC488Parser.Integer_literalContext ctx);

	/**
	 * Enter a parse tree produced by {@link CSC488Parser#output}.
	 * @param ctx the parse tree
	 */
	void enterOutput(@NotNull CSC488Parser.OutputContext ctx);
	/**
	 * Exit a parse tree produced by {@link CSC488Parser#output}.
	 * @param ctx the parse tree
	 */
	void exitOutput(@NotNull CSC488Parser.OutputContext ctx);

	/**
	 * Enter a parse tree produced by {@link CSC488Parser#variable}.
	 * @param ctx the parse tree
	 */
	void enterVariable(@NotNull CSC488Parser.VariableContext ctx);
	/**
	 * Exit a parse tree produced by {@link CSC488Parser#variable}.
	 * @param ctx the parse tree
	 */
	void exitVariable(@NotNull CSC488Parser.VariableContext ctx);

	/**
	 * Enter a parse tree produced by {@link CSC488Parser#bound}.
	 * @param ctx the parse tree
	 */
	void enterBound(@NotNull CSC488Parser.BoundContext ctx);
	/**
	 * Exit a parse tree produced by {@link CSC488Parser#bound}.
	 * @param ctx the parse tree
	 */
	void exitBound(@NotNull CSC488Parser.BoundContext ctx);

	/**
	 * Enter a parse tree produced by {@link CSC488Parser#variablenames}.
	 * @param ctx the parse tree
	 */
	void enterVariablenames(@NotNull CSC488Parser.VariablenamesContext ctx);
	/**
	 * Exit a parse tree produced by {@link CSC488Parser#variablenames}.
	 * @param ctx the parse tree
	 */
	void exitVariablenames(@NotNull CSC488Parser.VariablenamesContext ctx);

	/**
	 * Enter a parse tree produced by {@link CSC488Parser#literal}.
	 * @param ctx the parse tree
	 */
	void enterLiteral(@NotNull CSC488Parser.LiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link CSC488Parser#literal}.
	 * @param ctx the parse tree
	 */
	void exitLiteral(@NotNull CSC488Parser.LiteralContext ctx);
}
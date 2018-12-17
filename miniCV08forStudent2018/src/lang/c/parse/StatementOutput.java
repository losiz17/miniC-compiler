package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class StatementOutput extends CParseRule {
    // StatementOutput ::= OUTPUT expression SEMI
    private CParseRule expression;
    public StatementOutput(CParseContext pcx) {
    }
    public static boolean isFirst(CToken tk) {
        return tk.getType()==CToken.TK_OUTPUT;
    }
    public void parse(CParseContext pcx) throws FatalErrorException {
        // ここにやってくるときは、必ずisFirst()が満たされている
        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getNextToken(pcx);
        if(Expression.isFirst(tk)){
            expression = new Expression(pcx);
            expression.parse(pcx);
        }else{
            pcx.fatalError(tk.toExplainString() + "OUTPUTの後ろはexpressionです");
        }
        tk = ct.getCurrentToken(pcx);
        if(tk.getType() != CToken.TK_SEMI){
            pcx.fatalError(tk.toExplainString() + "OUTPUT文の最後に;が抜けています");
        }
        tk = ct.getNextToken(pcx);

    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        if (expression != null) {
            expression.semanticCheck(pcx);
        }
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; StatementOutput starts");
        if (expression != null) {
            expression.codeGen(pcx);
            o.println("\tMOV\t#0xFFE0,R0\t;StatementOutput:メモリマップトI/Oアドレス ");
            o.println("\tMOV\t -(R6),R1\t; StatementOutput:");
            o.println("\tMOV\t R1,(R0)\t; StatementOutput:スタックトップの値を出力");
        }
        o.println(";;; StatementOutput completes");
    }
}

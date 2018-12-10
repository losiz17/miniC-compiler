package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class StatementAssign extends CParseRule {
    // statementAssign ::=  primary ASSIGN expression SEMI
    private CParseRule primary,expression;
    public StatementAssign(CParseContext pcx) {
    }
    public static boolean isFirst(CToken tk) {
        return Primary.isFirst(tk);
    }
    public void parse(CParseContext pcx) throws FatalErrorException {
        // ここにやってくるときは、必ずisFirst()が満たされている
        primary = new Primary(pcx);
        primary.parse(pcx);
        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getCurrentToken(pcx);
        if(tk.getType() != CToken.TK_ASSIGN){
            pcx.fatalError(tk.toExplainString() + "primaryの後ろに=がありません");
        }
        tk = ct.getNextToken(pcx);
        if(Expression.isFirst(tk)){
            expression = new Expression(pcx);
            expression.parse(pcx);
        }
        tk = ct.getCurrentToken(pcx);
        if(tk.getType() != CToken.TK_SEMI){
            pcx.fatalError(tk.toExplainString() + "expressionの後ろに;がありません");
        }
        tk = ct.getNextToken(pcx);
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        if (primary != null && expression != null) {
            primary.semanticCheck(pcx);
            expression.semanticCheck(pcx);
            if(primary.getCType() != expression.getCType()){
                pcx.fatalError("左辺の型[ "+ primary.getCType().toString() + " ]と右辺の型[" + expression.getCType().toString() + "]が一致していません");
            }
            if(!primary.isConstant()){
                pcx.fatalError("定数に代入はできません");
            }
            //System.out.println(primary.isConstant());
        }
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; statementasign starts");
        if (primary != null && expression != null) {
            primary.codeGen(pcx);
            expression.codeGen(pcx);
            o.println("\tMOV\t-(R6), R0\t; Statement:スタックトップから右辺の式の値を取り出す");
            o.println("\tMOV\t-(R6), R1\t; Statement:スタックトップから左辺の書き込むべきのアドレスを取り出す");
            o.println("\tMOV\t  R0 ,(R1)\t; Statement:書き込むべきアドレスに値を代入する");
        }
        o.println(";;; statementassign completes");
    }
}
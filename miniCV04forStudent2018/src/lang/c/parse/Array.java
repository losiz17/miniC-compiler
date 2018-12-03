package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class Array extends CParseRule {
    // array ::= LBRA expression RBRA
    private CParseRule array;
    public Array(CParseContext pcx) {
    }
    public static boolean isFirst(CToken tk) {
        return tk.getType()==CToken.TK_LBRA;
    }
    public void parse(CParseContext pcx) throws FatalErrorException {
        // ここにやってくるときは、必ずisFirst()が満たされている
        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getNextToken(pcx);
        if(Expression.isFirst(tk)){
            array = new Expression(pcx);
            array.parse(pcx);
        }else{
            pcx.fatalError(tk.toExplainString() + "[の後ろはexpressionです");
        }
        tk = ct.getCurrentToken(pcx);
        if(tk.getType() != CToken.TK_RBRA){
            pcx.fatalError(tk.toExplainString() + "]で閉じ忘れています");
        }
        tk = ct.getNextToken(pcx);

    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        if (array != null) {
            array.semanticCheck(pcx);
            setCType(array.getCType());		// number の型をそのままコピー
            setConstant(array.isConstant());	// number は常に定数
        }
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; factor starts");
        if (array != null) { array.codeGen(pcx); }
        o.println(";;; factor completes");
    }
}
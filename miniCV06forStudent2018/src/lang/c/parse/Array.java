package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class Array extends CParseRule {
    // array ::= LBRA expression RBRA
    private CParseRule array;
    private CToken op;
    public Array(CParseContext pcx) {
    }
    public static boolean isFirst(CToken tk) {
        return tk.getType()==CToken.TK_LBRA;
    }
    public void parse(CParseContext pcx) throws FatalErrorException {
        // ここにやってくるときは、必ずisFirst()が満たされている
        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getNextToken(pcx);
        op = tk;
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
            if(array.getCType().getType() != CType.T_int){
                pcx.fatalError("配列の添字はint型です");
            }
            if(array.getCType().getType() == CType.T_int){
                setCType(CType.getCType(CType.T_int));		// array の型をそのままコピー
                setConstant(array.isConstant());	// array は常に定数
            }else{
                setCType(array.getCType());		// array の型をそのままコピー
                setConstant(array.isConstant());	// array は常に定数
            }
        }
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; Array starts");
        if (array != null) {
            o.println("\tMOV\t#" + op.getText() +",(R6)+\t; Array:添字をスタックに積む ");
            o.println("\tMOV\t -(R6),R0\t; Array:配列の引数を取り出す");
            o.println("\tADD\t -(R6),R0\t; Array:配列の変数を取り出す");
            o.println("\tMOV\t R0,(R6)+\t; Array:配列のアドレスを積む");
        }
        o.println(";;; Array completes");
    }
}
package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class StatementInput extends CParseRule {
    // ConditionInput ::= INPUT primary SEMI
    private CParseRule primary;
    public StatementInput(CParseContext pcx) {
    }
    public static boolean isFirst(CToken tk) {
        return tk.getType()==CToken.TK_INPUT;
    }
    public void parse(CParseContext pcx) throws FatalErrorException {
        // ここにやってくるときは、必ずisFirst()が満たされている
        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getNextToken(pcx);
        if(Primary.isFirst(tk)){
            primary = new Primary(pcx);
            primary.parse(pcx);
        }else{
            pcx.fatalError(tk.toExplainString() + "INPUTの後ろはprimaryです");
        }
        tk = ct.getCurrentToken(pcx);
        if(tk.getType() != CToken.TK_SEMI){
            pcx.fatalError(tk.toExplainString() + "INPUT文の最後に;が足りません");
        }
        tk = ct.getNextToken(pcx);

    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        if (primary != null) {
            primary.semanticCheck(pcx);
            //setCType(primary.getCType());	//子節点の型をコピー
            if(primary.isConstant()){
                pcx.fatalError("INPUT文において定数(CONST)は許されません");
            }
        }
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; StatementInput starts");
        if (primary != null) {
            primary.codeGen(pcx);
            o.println("\tMOV\t#0xFFE0,R0\t;StatementInput:メモリマップトI/Oアドレス ");
            o.println("\tMOV\t -(R6),R1\t; StatementInput:");
            o.println("\tMOV\t (R0),R1\t; StatementInput:スタックトップの値を入力");
        }
        o.println(";;; StatementInput completes");
    }
}
package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class PrimaryMult extends CParseRule {
    // PrimaryMult ::= MULT variable
    private CToken op;
    private CParseRule primarymult;
    public PrimaryMult(CParseContext pcx) {
    }
    public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_MULT;
    }
    public void parse(CParseContext pcx) throws FatalErrorException {
        // ここにやってくるときは、必ずisFirst()が満たされている
        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getNextToken(pcx);
        op = tk;
        if (Variable.isFirst(tk)) {
            primarymult = new Variable(pcx);
            primarymult.parse(pcx);
        }else{
            pcx.fatalError("MULTの後ろにvariableが必要です");
        }
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        if(primarymult != null){
            primarymult.semanticCheck(pcx);
            setCType(primarymult.getCType());	//primarymultの型をコピー
            if(primarymult.getCType().getType() == CType.T_int){
                //System.out.println("here");
                pcx.fatalError("ポインタ参照においてint型は許されません");
            }
            if(this.getCType().getType() == CType.T_pint){
                this.setCType(CType.getCType(CType.T_int));//*aはint型に
            }

            setConstant(isConstant());//常に定数
        }

    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; primarymult starts");
        if (primarymult != null) {
            primarymult.codeGen(pcx);
            o.println("\tMOV\t-(R6), R0\t; PrimaryMult:アドレスを取り出して、内容を参照して、積む<" + op.toExplainString() +">");
            o.println("\tMOV\t(R0), (R6)+\t; PrimaryMult:");
        }
        o.println(";;; primarymult completes");
    }
}
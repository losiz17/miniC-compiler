package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class PrimaryMult extends CParseRule {
    // PrimaryMult ::= MULT variable
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
        if (Variable.isFirst(tk)) {
            primarymult = new Variable(pcx);
            primarymult.parse(pcx);
        }else{
            pcx.fatalError("MULTの後ろにvariableが必要です");
        }
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        this.setCType(CType.getCType(CType.T_pint));//ポインタ型
        this.setConstant(true);//常に定数
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; factoramp starts");
        if (primarymult != null) { primarymult.codeGen(pcx); }
        o.println(";;; factoramp completes");
    }
}
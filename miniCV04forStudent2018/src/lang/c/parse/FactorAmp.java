package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class FactorAmp extends CParseRule {
    // factorAmp ::= AMP (number | primary)
    private CParseRule factoramp;
    public FactorAmp(CParseContext pcx) {
    }
    public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_AMP;
    }
    public void parse(CParseContext pcx) throws FatalErrorException {
        // ここにやってくるときは、必ずisFirst()が満たされている
        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getNextToken(pcx);
        if (Number.isFirst(tk)) {
            factoramp = new Number(pcx);
            factoramp.parse(pcx);
        }else if(Primary.isFirst(tk)){
            factoramp = new Primary(pcx);
            factoramp.parse(pcx);
        }else{
            pcx.fatalError(tk.toExplainString()+"&の後ろに数字またはprimaryが必要です");
        }
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        if(factoramp instanceof Primary){
            if( ((Primary)factoramp).getprimary() instanceof PrimaryMult){//Primary型にキャスト
                pcx.fatalError("&*は使えません。");
            }
        }
        this.setCType(CType.getCType(CType.T_pint));//ポインタ型
        this.setConstant(true);//常に定数
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; factoramp starts");
        if (factoramp != null) { factoramp.codeGen(pcx); }
        o.println(";;; factoramp completes");
    }
}
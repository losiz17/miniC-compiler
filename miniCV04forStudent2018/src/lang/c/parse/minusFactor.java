package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class minusFactor extends CParseRule {
    // minusFactor ::= MINUS unsignedFactor
    private CParseRule factor;
    public minusFactor(CParseContext pcx) {
    }
    public static boolean isFirst(CToken tk) {
        return  tk.getType() == CToken.TK_MINUS;
    }
    public void parse(CParseContext pcx) throws FatalErrorException {
        // ここにやってくるときは、必ずisFirst()が満たされている
        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getCurrentToken(pcx);
        tk = ct.getNextToken(pcx);
        if(UnsignedFactor.isFirst(tk)){
            factor = new UnsignedFactor(pcx);
            factor.parse(pcx);
        }else{
            pcx.fatalError(tk.toExplainString() + "-の次はUnsignedFactorが来ます");
        }
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {

        if (factor != null) {
            factor.semanticCheck(pcx);
            setCType(factor.getCType());		// factor の型をそのままコピー
            if(this.getCType().getType() == CType.T_pint){
                pcx.fatalError("番地の値が負になり得るので、負のポインタは許されません");
            }
            setConstant(factor.isConstant());	// factor は常に定数
        }
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; minusfactor starts");
        if (factor != null) { factor.codeGen(pcx); }
        o.println("\tMOV\t-(R6), R1\t; minusFactor:スタックトップから値を取り出し、その値から0を引く");

        o.println("\tMOV\t#0, R0\t; minusFactor:");
//        o.println("\tMOV\t-(R6), R1\t; minusFactor:");
        o.println("\tSUB\tR1, R0\t; minusFactor:");
        o.println("\tMOV\tR0, (R6)+\t; minusFactor:");
        o.println(";;; minusfactor completes");
    }
}
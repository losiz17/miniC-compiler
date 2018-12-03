package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class UnsignedFactor extends CParseRule {
    // unsignedFactor ::= factorAmp | number | LPAR expression RPAP
    private CParseRule number,factor;
    public UnsignedFactor(CParseContext pcx) {
    }
    public static boolean isFirst(CToken tk) {
        return Number.isFirst(tk) | FactorAmp.isFirst(tk) | tk.getType() == CToken.TK_LPAR;
    }
    public void parse(CParseContext pcx) throws FatalErrorException {
        // ここにやってくるときは、必ずisFirst()が満たされている
        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getCurrentToken(pcx);
        if(Number.isFirst(tk)){
            number = new Number(pcx);
            number.parse(pcx);
        }else if(FactorAmp.isFirst(tk)){
            factor = new FactorAmp(pcx);
            factor.parse(pcx);
        }else{// '('のとき
            tk = ct.getNextToken(pcx);
            if(Expression.isFirst(tk)){
                factor = new Expression(pcx);
                factor.parse(pcx);
            }else{
                pcx.fatalError(tk.toExplainString() + "(の後ろはexpressionです");
            }
            tk = ct.getCurrentToken(pcx);
            if(tk.getType() != CToken.TK_RPAR){
                pcx.fatalError(tk.toExplainString() + ")で閉じ忘れています");
            }
            tk = ct.getNextToken(pcx);
        }
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        if (number != null) {
            number.semanticCheck(pcx);
            setCType(number.getCType());		// number の型をそのままコピー
            setConstant(number.isConstant());	// number は常に定数
        }else if (factor != null) {
            factor.semanticCheck(pcx);
            setCType(factor.getCType());		// factor の型をそのままコピー
            setConstant(factor.isConstant());	// factor は常に定数
        }
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; unsignedfactor starts");
        if (number != null) { number.codeGen(pcx); }
        if (factor != null) { factor.codeGen(pcx); }
        o.println(";;; unsignedfactor completes");
    }
}
package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class ConditionLT extends CParseRule {
    // conditionLT ::= LT expression
    private CToken op;
    private CParseRule left,right;
    private int seq;
    public ConditionLT(CParseContext pcx, CParseRule left) {
        this.left = left;
    }
    public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_LT;
    }
    public void parse(CParseContext pcx) throws FatalErrorException {
        // ここにやってくるときは、必ずisFirst()が満たされている
        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getNextToken(pcx);
        System.out.println(tk.getText()+"conditionLT");
        op = tk;
        if (Expression.isFirst(tk)) {
            right = new Expression(pcx);
            right.parse(pcx);
            //System.out.println(left.getCType().toString()+":left");
            //System.out.println(right.getCType().toString()+":right");
        }else{
            pcx.fatalError("Expressionが抜けています：conditionLT");
        }
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        if(left != null && right != null){
            left.semanticCheck(pcx);
            right.semanticCheck(pcx);
            if(!left.getCType().equals(right.getCType())){
                pcx.fatalError("左辺の型[" + left.getCType().toString() + "]と右辺の型[" + right.getCType().toString() + "]が一致しないので比較できません");
            }else{
                this.setCType(CType.getCType(CType.T_bool));
                this.setConstant(true);
            }
        }
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; condition < (compare) starts");
        if(left != null && right != null){
            left.codeGen(pcx);
            right.codeGen(pcx);
            int seq = pcx.getSeqId();
            o.println("\tMOV\t-(R6),R0\t;conditionLT: 2数を取り出して比べる");
            o.println("\tMOV\t-(R6),R1\t;conditionLT");
            o.println("\tMOV\t#0x0001,R2\t;conditionLT: set TRUE(1)");
            o.println("\tCMP\tR0,R1\t\t;conditionLT: R1<R0 = R1-R0<0");
            o.println("\tBRN\tLT" + seq + "\t\t;conditionLT:trueのとき遷移");
            o.println("\tCLR\tR2\t\t;conditionLT: set FALSE(0)");
            o.println("LT" + seq + ":\tMOV\tR2,(R6)+\t;conditionLT:");
        }
        o.println(";;; condition < (compare) completes");
    }
}
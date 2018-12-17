package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class ConditionLE extends CParseRule {
    // conditionLE ::= LE expression
    private CToken op;
    private CParseRule left,right;
    public ConditionLE(CParseContext pcx , CParseRule left) {
        this.left = left;//左辺の値の記憶
    }
    public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_LE;
    }
    public void parse(CParseContext pcx) throws FatalErrorException {
        // ここにやってくるときは、必ずisFirst()が満たされている
        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getNextToken(pcx);
        op = tk;
        if (Expression.isFirst(tk)) {
            right = new Expression(pcx);
            right.parse(pcx);
        }else{
            pcx.fatalError("Expressionが抜けています：conditionLE");
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
        o.println(";;; condition <= (compare) starts");
        if(left != null && right != null){
            left.codeGen(pcx);
            right.codeGen(pcx);
            int seq = pcx.getSeqId();
            o.println("\tMOV\t-(R6),R0\t;conditionLE: 2数を取り出して比べる");
            o.println("\tMOV\t-(R6),R1\t;conditionLE");
            o.println("\tMOV\t#0x0001,R2\t;conditionLE: set TRUE(1)");
            o.println("\tCMP\tR0,R1\t\t;conditionLE: R1<=R0 = R1-R0<=0");
            o.println("\tBRN\tLE" + seq + "\t\t;conditionLE:trueのとき遷移");
            o.println("\tBRZ\tLE" + seq + "\t\t;conditionLE:trueのとき遷移");
            o.println("\tCLR\tR2\t\t;conditionLE: set FALSE(0)");
            o.println("LE" + seq + ":\tMOV\tR2,(R6)+\t;conditionLE:");
        }
        o.println(";;; condition <= (compare) completes");
    }
}
package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class Condition extends CParseRule {
    // Condition ::= expression (conditionLT | conditionLE | conditionGT | conditionGE | conditionEQ | conditionNE) | TRUE | FALSE
    private CParseRule left,condition;
    private boolean flag;
    public Condition(CParseContext pcx) {
    }
    public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_TRUE | tk.getType() == CToken.TK_FALSE | Expression.isFirst(tk);
    }
    public void parse(CParseContext pcx) throws FatalErrorException {
        // ここにやってくるときは、必ずisFirst()が満たされている
        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getCurrentToken(pcx);
        if (Expression.isFirst(tk)) {
            left = new Expression(pcx);//左辺の情報
            left.parse(pcx);
            tk = ct.getCurrentToken(pcx);
            System.out.println(tk.getText()+":condition");
            if(tk.getType() == CToken.TK_LT){
                condition = new ConditionLT(pcx,left);//左辺の情報も渡してやる
                condition.parse(pcx);
            }else if(tk.getType() == CToken.TK_LE){
                condition = new ConditionLE(pcx,left);//左辺の情報も渡してやる
                condition.parse(pcx);
            }else if(tk.getType() == CToken.TK_GT){
                condition = new ConditionGT(pcx,left);//左辺の情報も渡してやる
                condition.parse(pcx);
            }else if(tk.getType() == CToken.TK_GE){
                condition = new ConditionGE(pcx,left);//左辺の情報も渡してやる
                condition.parse(pcx);
            }else if(tk.getType() == CToken.TK_EQ){
                condition = new ConditionEQ(pcx,left);//左辺の情報も渡してやる
                condition.parse(pcx);
            }else if(tk.getType() == CToken.TK_NE){
                condition = new ConditionNE(pcx,left);//左辺の情報も渡してやる
                condition.parse(pcx);
            }else{
                pcx.fatalError("条件式が抜けています：Condition");
            }
        }else if(tk.getType() == CToken.TK_TRUE){
            tk = ct.getNextToken(pcx);
            flag = true;
        }else if(tk.getType() == CToken.TK_FALSE){
            tk = ct.getNextToken(pcx);
            flag = false;
        }
        else{
            pcx.fatalError("条件式が正しくありません：Condition");
        }
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        if(left != null){
            left.semanticCheck(pcx);
            this.setCType(left.getCType());
            this.setConstant(true);
            condition.semanticCheck(pcx);
            this.setCType(condition.getCType());
            this.setConstant(true);
        }else{//true or false のとき
            this.setCType(CType.getCType(CType.T_bool));
            this.setConstant(true);
        }
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; condition starts");
//		if (left != null) { left.codeGen(pcx); }
        if(condition != null) {condition.codeGen(pcx);}
        if(flag && left == null){
            o.println("\tMOV\t#0x0001,(R6)+\t;condition: set TRUE(1)");
        }else if(!flag && left == null){
            o.println("\tMOV\t#0x0000,(R6)+\t;condition: set FALSE(0)");
        }
        o.println(";;; condition completes");
    }
}
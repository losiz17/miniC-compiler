package lang.c.parse;

import java.io.PrintStream;
import java.util.ArrayList;

import lang.*;
import lang.c.*;

public class StatementWhile extends CParseRule {
    // StatementWhile ::= WHILE LPAR condition RPAR LCUR {statement} RCUR
    private CParseRule condition,statement;
    private CToken op;
    private ArrayList<CParseRule> list;
    private int seq;
    public StatementWhile(CParseContext pcx) {
        list = new ArrayList<CParseRule>();
    }
    public static boolean isFirst(CToken tk) {
        return tk.getType()==CToken.TK_WHILE;
    }
    public void parse(CParseContext pcx) throws FatalErrorException {
        // ここにやってくるときは、必ずisFirst()が満たされている
        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getNextToken(pcx);
        op = tk;
        if(tk.getType() != CToken.TK_LPAR){
            pcx.fatalError(tk.toExplainString() + "whileの後に(が抜けています");
        }
        tk = ct.getNextToken(pcx);
        //System.out.println(tk.getText()+"while");
        if(Condition.isFirst(tk)){
            condition = new Condition(pcx);
            condition.parse(pcx);
        }else{
            pcx.fatalError(tk.toExplainString() + "(の後ろはexpressionです");
        }
        tk = ct.getCurrentToken(pcx);
        //System.out.println(tk.getText()+"while");
        if(tk.getType() != CToken.TK_RPAR){
            pcx.fatalError(tk.toExplainString() + ")で閉じ忘れています");
        }
        tk = ct.getNextToken(pcx);

        if(tk.getType() != CToken.TK_LCUR){
            pcx.fatalError(tk.toExplainString() + ")の後に{が抜けています");
        }
        tk = ct.getNextToken(pcx);
        System.out.println(tk.getText()+"while");
        while(Statement.isFirst(tk)){
            statement = new Statement(pcx);
            statement.parse(pcx);
            list.add(statement);
            tk = ct.getCurrentToken(pcx);
        }

        tk = ct.getCurrentToken(pcx);

        if(tk.getType() != CToken.TK_RCUR){
            pcx.fatalError(tk.toExplainString() + "While文の最後に}が抜けています");
        }
        tk = ct.getNextToken(pcx);
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        if (condition != null) {
            condition.semanticCheck(pcx);
        }
        for (int i= 0; i < list.size(); i++) {
            list.get(i).semanticCheck(pcx);
        }
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; StatementWhile starts");
        seq = pcx.getSeqId();
        if (condition != null) {
            o.println("WHILE" + seq + ":\t\t;StatementWhile:trueの遷移先");
            condition.codeGen(pcx);
            o.println("\tMOV\t -(R6),R0\t;  StatementWhile:スタックトップから真偽値を取り出す");
            o.println("\tBRZ\tWHILE_N" + seq + "\t\t;StatementWhile:falseの遷移");
        }
        for(int i= 0; i < list.size(); i++){
            list.get(i).codeGen(pcx);//trueのときの実行文
        }
        o.println("\tJMP\tWHILE" + seq + "\t\t;StatementWhile:以下のelse部分をとばす");
        o.println("WHILE_N" + seq + ":\t\t;StatementWhile:falseの遷移先");

        o.println(";;; StatementWhile completes");
    }
}

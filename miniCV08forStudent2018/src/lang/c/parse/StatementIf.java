package lang.c.parse;

import java.io.PrintStream;
import java.util.ArrayList;

import lang.*;
import lang.c.*;

public class StatementIf extends CParseRule {
    // Statement ::= IF LPAR condition RPAR LCUR {statement} RCUR [ELSE (StatementIf | LCUR {Statement} RCUR)]
    private CParseRule condition,statement,statement_elseif;
    private ArrayList<CParseRule> list;//true用
    private ArrayList<CParseRule> list2;//false用
    private ArrayList<CParseRule> listc;//condition用
    private int seq;
    public StatementIf(CParseContext pcx) {
        list = new ArrayList<CParseRule>();
        list2 = new ArrayList<CParseRule>();
        listc = new ArrayList<CParseRule>();
    }
    public static boolean isFirst(CToken tk) {
        return tk.getType()==CToken.TK_IF;
    }
    public void parse(CParseContext pcx) throws FatalErrorException {
        // ここにやってくるときは、必ずisFirst()が満たされている
        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getCurrentToken(pcx);
        if(tk.getType() == CToken.TK_IF){
            tk = ct.getNextToken(pcx);
        }
        if(tk.getType() != CToken.TK_LPAR){
            pcx.fatalError(tk.toExplainString() + "IFの後に(が抜けています");
        }
        tk = ct.getNextToken(pcx);

        if(Condition.isFirst(tk)){
            condition = new Condition(pcx);
            condition.parse(pcx);
            listc.add(condition);
            //System.out.println(listc.subList(0, 0));
            //System.out.println("here:if_codnition");
        }else{
            pcx.fatalError(tk.toExplainString() + "(の後ろにconditionが抜けています");
        }
        tk = ct.getCurrentToken(pcx);
        if(tk.getType() != CToken.TK_RPAR){
            pcx.fatalError(tk.toExplainString() + "consitionの後に)を忘れています");
        }

        tk = ct.getNextToken(pcx);
        if(tk.getType() != CToken.TK_LCUR){
            pcx.fatalError(tk.toExplainString() + ")の後に{を忘れています");
        }

        tk = ct.getNextToken(pcx);
        while(Statement.isFirst(tk)){
            statement = new Statement(pcx);
            statement.parse(pcx);
            list.add(statement);//複数書けるように
            tk = ct.getCurrentToken(pcx);
        }
        //System.out.println(tk.getText()+":if_before}");
        tk = ct.getCurrentToken(pcx);
        if(tk.getType() != CToken.TK_RCUR){
            pcx.fatalError(tk.toExplainString() + "conditionの後に}を忘れています");
        }
        tk = ct.getNextToken(pcx);
        //System.out.println(tk.getText()+":if");
        if(tk.getType() == CToken.TK_ELSE){
            tk = ct.getNextToken(pcx);

            if(StatementIf.isFirst(tk)){
                statement_elseif = new StatementIf(pcx);
                statement_elseif.parse(pcx);
                //System.out.println("elseif_here");
            }else if(tk.getType() == CToken.TK_LCUR){
                tk = ct.getNextToken(pcx);
                while(Statement.isFirst(tk)){
                    statement = new Statement(pcx);
                    statement.parse(pcx);
                    list2.add(statement);
                    tk = ct.getCurrentToken(pcx);
                    //System.out.println(tk.getText()+":if_list2");
                }
                tk = ct.getCurrentToken(pcx);

                if(tk.getType() != CToken.TK_RCUR ){
                    pcx.fatalError(tk.toExplainString() + "statementの後に}を忘れています");
                }
            }else{
                pcx.fatalError(tk.toExplainString() + "elseの後に{を忘れています");
            }

            tk = ct.getNextToken(pcx);
        }
        //いけるか微妙
//		tk = ct.getCurrentToken(pcx);
//		if(tk.getType() != CToken.TK_ELSE){
//			tk = ct.getNextToken(pcx);
//			if(tk.getType() != CToken.TK_LCUR){
//				tk = ct.getNextToken(pcx);
//			}else{
//				pcx.fatalError(tk.toExplainString() + "elseの後に{を忘れています");
//			}
//			while(Statement.isFirst(tk)){
//				statement = new StatementIf(pcx);
//				statement.parse(pcx);
//			}
//			tk = ct.getCurrentToken(pcx);
//			if(tk.getType() != CToken.TK_RCUR){
//				tk = ct.getNextToken(pcx);
//			}else{
//				pcx.fatalError(tk.toExplainString() + "statementの後に}を忘れています");
//			}
//			tk = ct.getNextToken(pcx);
//		}
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        for (int i= 0; i < listc.size(); i++) {
            listc.get(i).semanticCheck(pcx);
        }
        for (int i= 0; i < list.size(); i++) {
            list.get(i).semanticCheck(pcx);
        }
        for (int i= 0; i < list2.size(); i++) {
            list2.get(i).semanticCheck(pcx);
        }
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; StatementIf starts");
        seq = pcx.getSeqId();
        for (int a = 0; a < listc.size(); a++) {
            //System.out.println(listc.size()+"listc");
            listc.get(a).codeGen(pcx);
            o.println("\tMOV\t -(R6),R0\t;  StatementIf:スタックトップから真偽値を取り出す");
            o.println("\tBRZ\tIF_N" + seq + "\t\t;StatementIf:falseの遷移");

            for(int i= 0; i < list.size(); i++){
                list.get(i).codeGen(pcx);//trueのときの実行文
                //System.out.println(list.size() +"list");
            }

            o.println("\tJMP\tIF" + seq + "\t\t;StatementIf:以下のelse部分をとばす");


            o.println("IF_N" + seq + ":\t\t;StatementIf:falseの遷移先");
            if(statement_elseif != null){
                statement_elseif.codeGen(pcx);
            }
            for(int i= 0; i < list2.size(); i++){
                list2.get(i).codeGen(pcx);//falseのときの実行文
            }

            o.println("IF" + seq + ":\t\t;StatementIF:trueの遷移先");

        }
        o.println(";;; StatementIf completes");
    }
}

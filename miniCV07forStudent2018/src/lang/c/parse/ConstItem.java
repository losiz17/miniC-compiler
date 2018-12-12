package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class ConstItem extends CParseRule {
    // constItem ::= [ MULT ] IDENT ASSIGN [ AMP ] NUM
    private CParseRule ident,variable,mult;
    private CToken ident_word;//識別子保存
    private CToken mul,amp;
    private CSymbolTableEntry d,e;
    private int num;//constの定数保存
    public ConstItem(CParseContext pcx) {
    }
    public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_MULT || tk.getType() == CToken.TK_IDENT;
    }
    public void parse(CParseContext pcx) throws FatalErrorException {
        // ここにやってくるときは、必ずisFirst()が満たされている
        int size = 1;
        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getCurrentToken(pcx);
        CType type = CType.getCType(CType.T_int);
        if(tk.getType() == CToken.TK_MULT){
            mul = tk;
            tk = ct.getNextToken(pcx);
            type = CType.getCType(CType.T_pint);
        }
        if(tk.getType() == CToken.TK_IDENT){
            ident_word = tk;
//			System.out.println(ident_word.getText()+":識別子");
            tk = ct.getNextToken(pcx);
//			System.out.println(tk.getText()+"constItem");
        }else{
            pcx.fatalError(tk.toExplainString() + "IDENTが抜けています");
        }
        tk = ct.getCurrentToken(pcx);
        if(tk.getType() == CToken.TK_ASSIGN){
            tk = ct.getNextToken(pcx);
        }else{
            pcx.fatalError(tk.toExplainString() + "=が抜けています");
        }
        tk = ct.getCurrentToken(pcx);
        if(tk.getType() == CToken.TK_AMP){
            amp = tk;
            tk = ct.getNextToken(pcx);
        }
        if(tk.getType() == CToken.TK_NUM){
//			System.out.println(tk.getText()+"constItem");
            num = tk.getIntValue();
            tk = ct.getNextToken(pcx);
        }

        if(ident_word!=null){
            CSymbolTable Table = pcx.getCSymbolTable();
            d = new CSymbolTableEntry(type,size,true,true,num);
            e = Table.register(ident_word.getText(),d);
            //Table.showGlobalSymbolTable();
            if(e != null){
                pcx.fatalError("識別子:"+ident_word.getText() + "すでに宣言済みです");
            }
        }

        if(mul != null && amp == null){
            pcx.fatalError("代入する両辺の型が一致していません");
        }else if(mul == null && amp != null){
            pcx.fatalError("代入する両辺の型が一致していません");
        }

    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {

    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; ConstItem starts");
        if(d.getCType().getType() == CType.T_int_array || d.getCType().getType() == CType.T_pint_array){
            o.println(ident_word.getText() +";\t " + ".BLKW\t" + d.getSize());
        }else{
            o.println(ident_word.getText() +";\t "+".WORD\t" + d.getAddress());
        }
        o.println(";;; ConstItem completes");
    }
}
package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class DeclItem extends CParseRule {
    // DeclItem::= [MULT] IDENT [ LBRA NUM RBRA]
    private CParseRule ident ,variable;
    private CToken ident_word;//識別子
    private CSymbolTableEntry d,e;
    public DeclItem(CParseContext pcx) {
    }
    public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_MULT || tk.getType() == CToken.TK_IDENT;
    }
    public void parse(CParseContext pcx) throws FatalErrorException {
        // ここにやってくるときは、必ずisFirst()が満たされている
        int size = 1;
        CType type = CType.getCType(CType.T_int);
        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getCurrentToken(pcx);
        if(tk.getType() == CToken.TK_MULT){
            type = CType.getCType(CType.T_pint);
            tk = ct.getNextToken(pcx);
        }
        if(tk.getType() == CToken.TK_IDENT){
            ident_word = tk;
            size = 1;
//			System.out.println("here:DeclItem");
            tk = ct.getNextToken(pcx);
//			System.out.println(tk.getText()+":DeclItem");
        }else{
            pcx.fatalError(tk.toExplainString() + "IDENTが抜けています。");
        }
        tk = ct.getCurrentToken(pcx);
        if(tk.getType() == CToken.TK_LBRA){
            tk = ct.getNextToken(pcx);
            if(tk.getType() == CToken.TK_NUM){
                size = tk.getIntValue();//NUMの数だけ確保
                if(type == CType.getCType(CType.T_pint)){
                    type =  CType.getCType(CType.T_pint_array);
                }else{
                    type =  CType.getCType(CType.T_int_array);
                }
                tk = ct.getNextToken(pcx);
                if(tk.getType() == CToken.TK_RBRA){
                    tk = ct.getNextToken(pcx);
                }else{
                    pcx.fatalError(tk.toExplainString() + "]で閉じ忘れています");
                }
            }else{
                pcx.fatalError(tk.toExplainString() + "[の後にNUMがありません");
            }

        }

        if(ident_word!=null){
            CSymbolTable Table = pcx.getCSymbolTable();
            d = new CSymbolTableEntry(type,size,false,true,0);
            e = Table.register(ident_word.getText(),d);
            //Table.showGlobalSymbolTable();
            if(e != null){
                pcx.fatalError("識別子:"+ident_word.getText() + "はすでに宣言済みです");
            }
        }


    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {

    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; DeclItem starts");
        if(d.getCType().getType() == CType.T_int_array || d.getCType().getType() == CType.T_pint_array){
            o.println(ident_word.getText() +":\t" + ".BLKW\t" + d.getSize());
        }else{
            o.println(ident_word.getText() +":\t" + ".WORD\t" + +d.getAddress());
        }
        o.println(";;; DeclItem completes");
    }
}
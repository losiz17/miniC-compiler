package lang.c;

import lang.SymbolTable;

public class CSymbolTable {
    private class OneSymbolTable extends SymbolTable<CSymbolTableEntry>{
        @Override
        public CSymbolTableEntry register(String name, CSymbolTableEntry e){ return put(name,e); }
        @Override
        public CSymbolTableEntry search(String name){ return get(name); }
    }

    private OneSymbolTable global;//大域変数
    private OneSymbolTable local;//局所変数

    public CSymbolTable(){
        global = new OneSymbolTable();
    }

    public CSymbolTableEntry search(String name){
        CSymbolTableEntry d = global.search(name);
        return d;
    }
    public CSymbolTableEntry register(String name, CSymbolTableEntry e){
        CSymbolTableEntry d = global.register(name, e);
        return d;
    }
    public void showGlobalSymbolTable(){
        global.show();
    }
}

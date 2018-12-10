// 代入文に関する意味解析テスト（構文解析のテストは、各自が行うこと）
//
// ident.javaのsemanticCheckメソッドのところは、こうなっているはず
//
// public void semanticCheck(CParseContext pcx) throws FatalErrorException {
// 	this.setCType(CType.getCType(CType.XXXXX));						// 	this.setConstant(YYYYY);
// }

// (1) XXXXX を整数型 T_int に、YYYYYをfalseにして
//a=1;	// 正当（生成コードが正しいかどうかも確認）
//*a=1;	// 不当
//a[3]=1;	// 不当
//a=&1;	// 不当
//
//// (2) XXXXX をポインタ型 T_pint に、YYYYYをfalseにして
//a=1;	// 不当
//a=&1;	// 正当（生成コードが正しいかどうかも確認）
//*a=1;	// 正当（生成コードが正しいかどうかも確認）
//
//// (3) XXXXX を配列型 T_array（人によってこの名前は異なる）に、YYYYYをfalseにして
//a=1;	// 不当
//a=a;	// 不当（正当だとすると、配列全体をごっそりコピーするのですか？）
//a[3]=1;	// 正当（生成コードが正しいかどうかも確認）

//// (4) XXXXX整数型 T_int に、YYYYYをtrueにして（定数には代入できないことの確認）
a=1;	// 不当

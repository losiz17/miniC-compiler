// ������Ɋւ���Ӗ���̓e�X�g�i�\����͂̃e�X�g�́A�e�����s�����Ɓj
//
// ident.java��semanticCheck���\�b�h�̂Ƃ���́A�����Ȃ��Ă���͂�
//
// public void semanticCheck(CParseContext pcx) throws FatalErrorException {
// 	this.setCType(CType.getCType(CType.XXXXX));						// 	this.setConstant(YYYYY);
// }

// (1) XXXXX �𐮐��^ T_int �ɁAYYYYY��false�ɂ���
a=1;	// �����i�����R�[�h�����������ǂ������m�F�j
*a=1;	// �s��
a[3]=1;	// �s��
a=&1;	// �s��

// (2) XXXXX ���|�C���^�^ T_pint �ɁAYYYYY��false�ɂ���
a=1;	// �s��
a=&1;	// �����i�����R�[�h�����������ǂ������m�F�j
*a=1;	// �����i�����R�[�h�����������ǂ������m�F�j

// (3) XXXXX ��z��^ T_array�i�l�ɂ���Ă��̖��O�͈قȂ�j�ɁAYYYYY��false�ɂ���
a=1;	// �s��
a=a;	// �s���i�������Ƃ���ƁA�z��S�̂���������R�s�[����̂ł����H�j
a[3]=1;	// �����i�����R�[�h�����������ǂ������m�F�j

// (4) XXXXX�����^ T_int �ɁAYYYYY��true�ɂ��āi�萔�ɂ͑���ł��Ȃ����Ƃ̊m�F�j
a=1;	// �s��

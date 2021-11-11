package br.com.limosapp.limos.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {

    //Nome do banco de dados
    public static final String NOME_BANCO = "Limos";
    //Versão do banco de dados
    public static final int VERSAO_DB = 1;

    public DbHelper(Context context) {
        super(context, NOME_BANCO, null, VERSAO_DB);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS ProdutosCarrinho (ID INTEGER PRIMARY KEY AUTOINCREMENT, hashrestaurante TEXT, hashproduto TEXT, produto TEXT, quantidade INTEGER, valorunitario DOUBLE, observacoes TEXT);");
        db.execSQL("CREATE TABLE IF NOT EXISTS AdicionaisCarrinho (_id INTEGER PRIMARY KEY AUTOINCREMENT, hashrestaurante TEXT, idproduto TEXT, hashproduto TEXT, hashadicional TEXT, descricao TEXT, quantidade INTEGER, valorunitario DOUBLE)");
        db.execSQL("CREATE TABLE IF NOT EXISTS OpcionaisCarrinho (_id INTEGER PRIMARY KEY AUTOINCREMENT, hashrestaurante TEXT, idproduto TEXT, hashproduto TEXT, hashopcional TEXT, descricao TEXT, quantidade INTEGER, valorunitario DOUBLE)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table ProdutosCarrinho");
        db.execSQL("drop table AdicionaisCarrinho");
        db.execSQL("drop table OpcionaisCarrinho");
        onCreate(db);
    }
}
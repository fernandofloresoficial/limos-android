package br.com.limosapp.limos.sqlite;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class ProdutosCarrinhoDAO {

    private SQLiteDatabase db;
    private Boolean verifica;

    public ProdutosCarrinhoDAO(Context ctx){
        DbHelper auxBd = new DbHelper(ctx);
        db = auxBd.getWritableDatabase();
    }

    public long salvar(String hashrestaurante,  String hashproduto, String produto, Integer quantidade, Double valorunitario, String obs){
        ContentValues cv = new ContentValues();
        cv.put("hashrestaurante", hashrestaurante);
        cv.put("hashproduto", hashproduto);
        cv.put("produto", produto);
        cv.put("quantidade", quantidade);
        cv.put("valorunitario", valorunitario);
        cv.put("observacoes", obs);

        return db.insert("ProdutosCarrinho", null, cv);
    }

    public void salvarAdicionais(String hashrestaurante, String idproduto, String hashproduto, String hashadicional,String descricao, Integer quantidade, Double valorunitario){
        ContentValues cv = new ContentValues();
        cv.put("hashrestaurante", hashrestaurante);
        cv.put("idproduto", idproduto);
        cv.put("hashadicional", hashadicional);
        cv.put("hashproduto", hashproduto);
        cv.put("descricao", descricao);
        cv.put("quantidade", quantidade);
        cv.put("valorunitario", valorunitario);

        db.insert("AdicionaisCarrinho", null, cv);
    }

    public void salvarOpcionais(String hashrestaurante, String idproduto, String hashproduto, String hashopcional,String descricao, Integer quantidade, Double valorunitario){
        ContentValues cv = new ContentValues();
        cv.put("hashrestaurante", hashrestaurante);
        cv.put("idproduto", idproduto);
        cv.put("hashopcional", hashopcional);
        cv.put("hashproduto", hashproduto);
        cv.put("descricao", descricao);
        cv.put("quantidade", quantidade);
        cv.put("valorunitario", valorunitario);

        db.insert("OpcionaisCarrinho", null, cv);
    }

    public boolean verificaCarrinhoRestaurante(String hashrestaurante){
        verifica = false;
        Cursor cur = db.rawQuery("SELECT EXISTS (SELECT 1 FROM ProdutosCarrinho WHERE hashrestaurante <> '" + hashrestaurante + "')", null);
        if (cur != null) {
            cur.moveToFirst();
            if (cur.getInt (0) > 0) { verifica = true; }
        }
        cur.close();
        return verifica;
    }

    public void excluirCarrinhoRestaurante(){
        db.delete("AdicionaisCarrinho", null, null);
        db.delete("OpcionaisCarrinho", null, null);
        db.delete("ProdutosCarrinho", null, null);
    }

    public boolean excluir(Long id){
        db.delete("AdicionaisCarrinho", "idproduto=" + id, null);
        db.delete("OpcionaisCarrinho", "idproduto=" + id, null);
        return db.delete("ProdutosCarrinho", "ID=" + id, null) > 0;
    }

    public boolean verificaRegistro(){
        verifica = false;
        Cursor cur = db.rawQuery("SELECT EXISTS (SELECT 1 FROM ProdutosCarrinho)", null);

        if (cur != null) {
            cur.moveToFirst();
            if (cur.getInt (0) > 0) { verifica = true; }
        }
        cur.close();
        return verifica;
    }

    public String carregaDetalhes(Long idproduto, String obs){
        String texto="", adicionais=null, opcionais=null, desc, qtde;
        Cursor cur;

        cur = db.rawQuery("SELECT * FROM AdicionaisCarrinho WHERE idproduto=" + idproduto, null);
        while(cur.moveToNext()){
            desc = cur.getString(cur.getColumnIndex("descricao"));
            qtde = cur.getString(cur.getColumnIndex("quantidade"));
            if (qtde == null || qtde == "0"){qtde="1";}
            if (adicionais == null) {
                adicionais = qtde + "x " + desc;
            }else{
                adicionais = adicionais + "\n" + qtde + "x " + desc;
            }
        }
        cur.close();

        cur = db.rawQuery("SELECT * FROM OpcionaisCarrinho WHERE idproduto=" + idproduto, null);
        while(cur.moveToNext()){
            desc = cur.getString(cur.getColumnIndex("descricao"));
            qtde = cur.getString(cur.getColumnIndex("quantidade"));
            if (qtde == null || qtde == "0"){qtde="1";}
            if (opcionais == null) {
                opcionais = qtde + "x " + desc;
            }else{
                opcionais = opcionais + "\n" + qtde + "x " + desc;
            }
        }
        cur.close();

        if (obs != null){
            texto = obs;
            if (adicionais != null){
                texto = texto + "\n" + adicionais;
            }
            if (opcionais != null){
                texto = texto + "\n" + opcionais;
            }
        }else{
            if (adicionais != null){
                texto = adicionais;
                if (opcionais != null){
                    texto = texto + "\n" + opcionais;
                }
            }else{
                if (opcionais != null){
                    texto = opcionais;
                }
            }
        }
        return texto;
    }

    public Double somaProdutos(){
        Double preco = 0.0;
        Cursor cur = db.rawQuery("SELECT * FROM ProdutosCarrinho", null);

        while(cur.moveToNext()){
            Integer qtde = cur.getInt(cur.getColumnIndex("quantidade"));
            Double vlr = cur.getDouble(cur.getColumnIndex("valorunitario"));
            preco = preco + (qtde * vlr);
        }
        cur.close();
        return preco;
    }

    public String[][] arrayProdutos(){
        Cursor cur = db.rawQuery("SELECT * FROM ProdutosCarrinho", null);
        int cont=0;
        String[][] array = new String[cur.getCount()][4];
        while(cur.moveToNext()){
            array[cont][0] = cur.getString(cur.getColumnIndex("ID"));
            array[cont][1] = cur.getString(cur.getColumnIndex("produto"));
            array[cont][2] = cur.getString(cur.getColumnIndex("quantidade"));
            array[cont][3] = cur.getString(cur.getColumnIndex("valorunitario"));
            cont++;
        }
        cur.close();
        return array;
    }

    public List<ProdutosCarrinho> retornaProdutosCarrinho(){
        List<ProdutosCarrinho> listProdutosCarrinho = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM ProdutosCarrinho", null);
        while(cursor.moveToNext()){
            int id = cursor.getInt(cursor.getColumnIndex("ID"));
            String hashproduto = cursor.getString(cursor.getColumnIndex("hashproduto"));
            String obs = cursor.getString(cursor.getColumnIndex("observacoes"));
            String produto = cursor.getString(cursor.getColumnIndex("produto"));
            Integer quantidade = cursor.getInt(cursor.getColumnIndex("quantidade"));
            Double valorunitario = cursor.getDouble(cursor.getColumnIndex("valorunitario"));
            listProdutosCarrinho.add(new ProdutosCarrinho(id, hashproduto, produto, quantidade, valorunitario, obs));
        }
        cursor.close();
        return listProdutosCarrinho;
    }

    public String retornoObs(Long idproduto){
        String obs="";
        Cursor cursor = db.rawQuery("SELECT * FROM ProdutosCarrinho WHERE ID=" + idproduto, null);
        if (cursor != null) {
            cursor.moveToFirst();
            obs = cursor.getString(cursor.getColumnIndex("observacoes"));
            cursor.close();
        }
        if (obs==null){obs="";}
        return obs;
    }
}

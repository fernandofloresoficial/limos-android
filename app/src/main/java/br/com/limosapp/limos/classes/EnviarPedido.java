package br.com.limosapp.limos.classes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import br.com.limosapp.limos.firebase.CuponsFirebase;
import br.com.limosapp.limos.firebase.PedidoFirebase;
import br.com.limosapp.limos.firebase.PedidoProdutosFirebase;
import br.com.limosapp.limos.firebase.PedidosHorarioFirebase;
import br.com.limosapp.limos.firebase.UsuariosCashFirebase;
import br.com.limosapp.limos.firebase.UsuariosPedidosFirebase;
import br.com.limosapp.limos.sqlite.ProdutosCarrinhoDAO;

import static br.com.limosapp.limos.ListaRestaurantesActivity.restaurantes;
import static br.com.limosapp.limos.MainActivity.idusuario;

public class EnviarPedido {

    private String hashpedido, numero, endereco, bairro, cidade, complemento, uf, cep, email, fantasia, formapagamento, nomeusuario, telefone, cpfcnpj, hashcupom, balcao, brinde;
    private Double valorcash, valordesconto, valorprodutos, valorfrete, valorsemfrete, valorlimos, valortotal;
    private Long numeropedido;
    private String[][] arrayprodutos;

    private DecimalFormat formata = new DecimalFormat("0.00");
    private Double valorcashganho = 0.0;

    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat horaFormat = new SimpleDateFormat("HH:mm:ss");
    private String horaFormatada = horaFormat.format(new Date());
    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private String dataFormatada = dateFormat.format(new Date());
    private Long datamili = new Date().getTime();

    private DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    private Context contexto;

    public EnviarPedido(Context contexto, String numero,String hashcupom,String endereco,String  bairro,String  cep , String  cidade, String  complemento, String  uf, String  email, String  fantasia, String  formapagamento, String  nomeusuario, String  telefone, String  cpfcnpj, Double valorfrete, Double  valorprodutos, Double  valorcash, Double  valordesconto, Double valorsemfrete, Double valortotal, String balcao, String[][] arrayprodutos, String brinde){
        this.contexto = contexto;
        this.hashcupom = hashcupom;
        this.endereco = endereco;
        this.numero = numero;
        this.bairro = bairro;
        this.cidade = cidade;
        this.cep = cep;
        this.complemento = complemento;
        this.uf = uf;
        this.email = email;
        this.fantasia = fantasia;
        this.formapagamento = formapagamento;
        this.nomeusuario = nomeusuario;
        this.telefone = telefone;
        this.cpfcnpj = cpfcnpj;
        this.valorfrete = valorfrete;
        this.valorprodutos = valorprodutos;
        this.valorsemfrete = valorsemfrete;
        this.valorcash = valorcash;
        this.valordesconto = valordesconto;
        this.valortotal = valortotal;
        this.balcao = balcao;
        this.arrayprodutos = arrayprodutos;
        this.brinde = brinde;
    }

    @SuppressLint("DefaultLocale")
    private void salvarPedido() {
        DatabaseReference dbpedido = db.child("pedidos");

        PedidoFirebase pedido = new PedidoFirebase();
        pedido.setNumero(numero);
        pedido.setEndereco(endereco);
        pedido.setBairro(bairro);
        pedido.setCidade(cidade);
        pedido.setUf(uf);
        pedido.setCep(cep);
        pedido.setComplemento(complemento);
        pedido.setCpfcnpj(cpfcnpj);
        pedido.setData(dataFormatada);
        pedido.setDatamilisegundos(datamili);
        if (valortotal == 0.0){pedido.setFormapagamento("Sem forma de pagamento");}else{pedido.setFormapagamento(formapagamento);}
        pedido.setHashcupom(hashcupom);
        pedido.setNomeusuario(nomeusuario);
        pedido.setFantasia(fantasia);
        pedido.setEmail(email);
        pedido.setPedido(numeropedido);
        pedido.setRestaurante(restaurantes.getIdrestaurante());
        pedido.setStatus(0);
        pedido.setTelefone(telefone);
        pedido.setUsuario(idusuario);
        pedido.setValorcash(valorcash);
        pedido.setValordesconto(valordesconto);
        pedido.setValorprodutos(valorprodutos);
        pedido.setValorfrete(valorfrete);

        if (valorsemfrete < 0){
            valorlimos=0.0;
        }else{
            String valorstring = formata.format((valorsemfrete + valordesconto) * 0.06).replace(",",".");
            valorlimos = Double.valueOf(valorstring);
        }
        pedido.setValorlimos(valorlimos);

        if (valorcash==0.0){
            String valorstring = formata.format(valorsemfrete*0.03).replace(",",".");
            valorcashganho = Double.valueOf(valorstring);
        }
        pedido.setValorcashganho(valorcashganho);

        pedido.setValortotal(valortotal);
        if (balcao.equals("Retirada no balcão")){pedido.setRetirabalcao(1);}else{pedido.setRetirabalcao(0);}
        pedido.setHora();
        hashpedido = dbpedido.push().getKey();
        if (hashpedido!=null){
            dbpedido.child(hashpedido).setValue(pedido);
            salvarRestPedido();
            if (brinde != null){ salvarBrinde();}
            salvarProduto();
            salvarHorarioPedido();
            salvarUsuarioPedido();
            if (valorcash>0.0) {
                salvarUsuarioCash();
                salvarCashUsuario();
            }
            if (hashcupom != null){salvarCupom(hashcupom);}
            new ProdutosCarrinhoDAO(contexto).excluirCarrinhoRestaurante();
        }
    }

    private void salvarRestPedido() {
        DatabaseReference dbrestpedido = db.child("restaurantepedidos/"+restaurantes.getIdrestaurante()+"/"+hashpedido);

        PedidoFirebase pedido = new PedidoFirebase();
        pedido.setNumero(numero);
        pedido.setEndereco(endereco);
        pedido.setBairro(bairro);
        pedido.setCidade(cidade);
        pedido.setCep(cep);
        pedido.setUf(uf);
        pedido.setComplemento(complemento);
        pedido.setData(dataFormatada);
        pedido.setDatamilisegundos(datamili);
        pedido.setFormapagamento(formapagamento);
        pedido.setHashcupom(hashcupom);
        pedido.setNomeusuario(nomeusuario);
        pedido.setEmail(email);
        pedido.setPedido(numeropedido);
        pedido.setRestaurante(restaurantes.getIdrestaurante());
        pedido.setStatus(0);
        pedido.setTelefone(telefone);
        pedido.setUsuario(idusuario);
        pedido.setValorcash(valorcash);
        pedido.setValordesconto(valordesconto);
        pedido.setValorprodutos(valorprodutos);
        pedido.setValorfrete(valorfrete);
        pedido.setValorlimos(valorlimos);
        pedido.setValorcashganho(valorcashganho);
        pedido.setValortotal(valortotal);
        pedido.setHora();
        dbrestpedido.setValue(pedido);
    }

    private void salvarHorarioPedido() {
        DatabaseReference dbhorario = db.child("pedidohorarios/"+hashpedido);

        PedidosHorarioFirebase horario = new PedidosHorarioFirebase();
        horario.setRealizado(horaFormatada);
        horario.setAprovado("");
        horario.setCancelado("");
        horario.setConcluido("");
        horario.setEnviado("");
        horario.setRecusado("");
        if (balcao.equals("Retirada no balcão")){horario.setRetirabalcao(1);}else{horario.setRetirabalcao(0);}
        dbhorario.setValue(horario);
    }

    private void salvarUsuarioPedido() {
        DatabaseReference dbusuario = db.child("usuariopedidos/"+idusuario+"/"+hashpedido);

        UsuariosPedidosFirebase usuarioclasse = new UsuariosPedidosFirebase();
        usuarioclasse.setAvaliado(0);
        usuarioclasse.setComentario("");
        usuarioclasse.setDatapedido(dataFormatada);
        usuarioclasse.setNomeusuario(nomeusuario);
        usuarioclasse.setNumeropedido(numeropedido);
        usuarioclasse.setResposta("");
        usuarioclasse.setRestaurante(restaurantes.getIdrestaurante());
        usuarioclasse.setStatuspedido(0);
        usuarioclasse.setNota(0.0);
        dbusuario.setValue(usuarioclasse);
    }

    private void salvarUsuarioCash() {
        DatabaseReference dbusuariocash = db.child("usuariocashbackextrato/"+idusuario);

        UsuariosCashFirebase usuariocash = new UsuariosCashFirebase();
        usuariocash.setDatahora(dataFormatada + " " + horaFormatada);
        usuariocash.setDatahoramseg(datamili);
        usuariocash.setPedido(numeropedido);
        usuariocash.setRestaurante(restaurantes.getIdrestaurante());
        usuariocash.setValor(valorcash*-1);
        dbusuariocash.push().setValue(usuariocash);
    }

    private void salvarCupom(final String hashcupons) {
        final DatabaseReference dbcupomresgatado = db.child("usuariocuponsresgatados/"+idusuario+"/"+restaurantes.getIdrestaurante()+"/"+hashcupons);
        dbcupomresgatado.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("")){
                    DatabaseReference dbcupomutilizado = db.child("usuariocuponsutilizados/"+idusuario+"/"+restaurantes.getIdrestaurante()+"/"+hashcupons);
                    CuponsFirebase cupons = new CuponsFirebase();
                    if (dataSnapshot.child("desconto").exists() && !Objects.requireNonNull(dataSnapshot.child("desconto").getValue()).toString().isEmpty()) { cupons.setDesconto(Integer.parseInt(Objects.requireNonNull(dataSnapshot.child("desconto").getValue()).toString())); }
                    if (dataSnapshot.child("dom").exists() && !Objects.requireNonNull(dataSnapshot.child("dom").getValue()).toString().isEmpty()) { cupons.setDom(Integer.parseInt(Objects.requireNonNull(dataSnapshot.child("dom").getValue()).toString())); }
                    if (dataSnapshot.child("seg").exists() && !Objects.requireNonNull(dataSnapshot.child("seg").getValue()).toString().isEmpty()) { cupons.setSeg(Integer.parseInt(Objects.requireNonNull(dataSnapshot.child("seg").getValue()).toString())); }
                    if (dataSnapshot.child("ter").exists() && !Objects.requireNonNull(dataSnapshot.child("ter").getValue()).toString().isEmpty()) { cupons.setTer(Integer.parseInt(Objects.requireNonNull(dataSnapshot.child("ter").getValue()).toString())); }
                    if (dataSnapshot.child("qua").exists() && !Objects.requireNonNull(dataSnapshot.child("qua").getValue()).toString().isEmpty()) { cupons.setQua(Integer.parseInt(Objects.requireNonNull(dataSnapshot.child("qua").getValue()).toString())); }
                    if (dataSnapshot.child("qui").exists() && !Objects.requireNonNull(dataSnapshot.child("qui").getValue()).toString().isEmpty()) { cupons.setQui(Integer.parseInt(Objects.requireNonNull(dataSnapshot.child("qui").getValue()).toString())); }
                    if (dataSnapshot.child("sex").exists() && !Objects.requireNonNull(dataSnapshot.child("sex").getValue()).toString().isEmpty()) { cupons.setSex(Integer.parseInt(Objects.requireNonNull(dataSnapshot.child("sex").getValue()).toString())); }
                    if (dataSnapshot.child("sab").exists() && !Objects.requireNonNull(dataSnapshot.child("sab").getValue()).toString().isEmpty()) { cupons.setSab(Integer.parseInt(Objects.requireNonNull(dataSnapshot.child("sab").getValue()).toString())); }
                    if (dataSnapshot.child("validade").exists() && !Objects.requireNonNull(dataSnapshot.child("validade").getValue()).toString().isEmpty()) { cupons.setValidade((String) dataSnapshot.child("validade").getValue()); }
                    if (dataSnapshot.child("validademseg").exists() && !Objects.requireNonNull(dataSnapshot.child("validademseg").getValue()).toString().isEmpty()) { cupons.setValidademseg(Long.parseLong(Objects.requireNonNull(dataSnapshot.child("validademseg").getValue()).toString()) ); }
                    dbcupomutilizado.setValue(cupons);
                    dbcupomresgatado.removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void salvarCashUsuario() {
        DatabaseReference dbusuarios = db.child("usuarios/"+idusuario+"/cashbacksaldo");
        dbusuarios.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull final MutableData currentData) {
                if (currentData.getValue() == null) {
                    currentData.setValue(0);
                }else {
                    currentData.setValue(Double.parseDouble(currentData.getValue().toString()) - valorcash);
                }
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                if (databaseError != null) {
                    System.out.println("Firebase counter increment failed.");
                } else {
                    System.out.println("Firebase counter increment succeeded.");
                }
            }
        });
    }

    public void salvarNumeroPedido() {
        DatabaseReference dbusuarios = db.child("restaurantes/"+restaurantes.getIdrestaurante()+"/numeropedidos");
        dbusuarios.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull final MutableData currentData) {
                if (currentData.getValue() == null) {
                    currentData.setValue(1);
                } else {
                    currentData.setValue((Long) currentData.getValue() + 1);
                }
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                if (databaseError != null) {
                    System.out.println("Firebase counter increment failed.");
                } else {
                    numeropedido = (Long) Objects.requireNonNull(dataSnapshot).getValue();
                    salvarPedido();
                }
            }
        });
    }

    private void salvarProduto() {
        DatabaseReference dbproutos = db.child("pedidos/"+hashpedido+"/produtos");
        PedidoProdutosFirebase produtosupdate = new PedidoProdutosFirebase();
        ProdutosCarrinhoDAO dbprodutoSQL = new ProdutosCarrinhoDAO(contexto);
        if (arrayprodutos != null){
            for (String[] arrayproduto : arrayprodutos) {
                if (arrayproduto != null) {
                    if (arrayproduto[2] != null && !arrayproduto[2].equals("0")) {
                        produtosupdate.setComplemento(dbprodutoSQL.carregaDetalhes(Long.parseLong(arrayproduto[0]),null));
                        produtosupdate.setObs(dbprodutoSQL.retornoObs(Long.parseLong(arrayproduto[0])));
                        produtosupdate.setProduto(arrayproduto[1]);
                        produtosupdate.setQuantidade(Integer.parseInt(arrayproduto[2]));
                        produtosupdate.setValor(Double.parseDouble(arrayproduto[3]));
                        String valorstring = formata.format(Integer.parseInt(arrayproduto[2]) * Double.parseDouble(arrayproduto[3])).replace(",",".");
                        produtosupdate.setValortotal(Double.parseDouble(valorstring));
                        dbproutos.push().setValue(produtosupdate);
                    }
                }
            }
        }
    }

    private void salvarBrinde() {
        DatabaseReference dbbrinde = db.child("pedidos/"+hashpedido+"/produtos");
        PedidoProdutosFirebase brindeupdate = new PedidoProdutosFirebase();
        brindeupdate.setComplemento("");
        brindeupdate.setObs("**Brinde");
        brindeupdate.setProduto(brinde);
        brindeupdate.setQuantidade(1);
        brindeupdate.setValor(0.0);
        brindeupdate.setValortotal(0.0);
        dbbrinde.push().setValue(brindeupdate);
    }
}

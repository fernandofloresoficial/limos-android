package br.com.limosapp.limos.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import br.com.limosapp.limos.R;

public  class FormasPagViewHolder extends RecyclerView.ViewHolder {
    public View view;
    ImageView imgfotoformapag;
    public TextView txtformapag;

    public FormasPagViewHolder(View itemView) {
        super(itemView);
        view = itemView;
        imgfotoformapag = view.findViewById(R.id.imgFotoFormaPag);
        txtformapag = view.findViewById(R.id.txtFormaPag);
    }

    public void setDadosFormaPag(String formapag) {
        switch (formapag) {
            case "alelovl":
                imgfotoformapag.setImageResource(R.drawable.imgalelo);
                txtformapag.setText("Alelo refeição / Visa vale (vale)");
                break;
            case "americancr":
                imgfotoformapag.setImageResource(R.drawable.imgamex);
                txtformapag.setText("American express (crédito)");
                break;
            case "banricomprascr":
                imgfotoformapag.setImageResource(R.drawable.imgbanricompras);
                txtformapag.setText("Banricompras (crédito)");
                break;
            case "banricomprasdb":
                imgfotoformapag.setImageResource(R.drawable.imgbanricompras);
                txtformapag.setText("Banricompras (débito)");
                break;
            case "cheque":
                imgfotoformapag.setImageResource(R.drawable.imgcheque);
                txtformapag.setText("Cheque");
                break;
            case "coopervl":
                imgfotoformapag.setImageResource(R.drawable.imgcooper);
                txtformapag.setText("Cooper Card (Vale)");
                break;
            case "dinercr":
                imgfotoformapag.setImageResource(R.drawable.imgdiners);
                txtformapag.setText("Diners (crédito)");
                break;
            case "diners":
                imgfotoformapag.setImageResource(R.drawable.imgdiners);
                txtformapag.setText("Diners (débito)");
                break;
            case "dinheiro":
                imgfotoformapag.setImageResource(R.drawable.imgdinheiro);
                txtformapag.setText("Dinheiro");
                break;
            case "elocr":
                imgfotoformapag.setImageResource(R.drawable.imgelo);
                txtformapag.setText("Elo (crédito)");
                break;
            case "elodb":
                imgfotoformapag.setImageResource(R.drawable.imgelo);
                txtformapag.setText("Elo (débito)");
                break;
            case "goodcardcr":
                imgfotoformapag.setImageResource(R.drawable.imggoodcard);
                txtformapag.setText("Goodcard (crédito)");
                break;
            case "greenvl":
                imgfotoformapag.setImageResource(R.drawable.imggreencard);
                txtformapag.setText("Green Card (Vale)");
                break;
            case "hipercrdcr":
                imgfotoformapag.setImageResource(R.drawable.imghipercard);
                txtformapag.setText("Hipercard (crédito)");
                break;
            case "mastercardcr":
                imgfotoformapag.setImageResource(R.drawable.imgmastercard);
                txtformapag.setText("Mastercard (crédito)");
                break;
            case "mastercarddb":
                imgfotoformapag.setImageResource(R.drawable.imgmastercard);
                txtformapag.setText("Mastercard (débito)");
                break;
            case "refeisulvl":
                imgfotoformapag.setImageResource(R.drawable.imgrefeisul);
                txtformapag.setText("Refeisul (Vale)");
                break;
            case "sodexovl":
                imgfotoformapag.setImageResource(R.drawable.imgsodexo);
                txtformapag.setText("Sodexo (Vale)");
                break;
            case "ticketvl":
                imgfotoformapag.setImageResource(R.drawable.imgticket);
                txtformapag.setText("Ticket (Vale)");
                break;
            case "valecard":
                imgfotoformapag.setImageResource(R.drawable.imgvalecard);
                txtformapag.setText("Vale Card");
                break;
            case "verdecardcr":
                imgfotoformapag.setImageResource(R.drawable.imgverdecard);
                txtformapag.setText("Verdecard (crédito)");
                break;
            case "verocardvl":
                imgfotoformapag.setImageResource(R.drawable.imgverocard);
                txtformapag.setText("Verocard (Vale)");
                break;
            case "visacr":
                imgfotoformapag.setImageResource(R.drawable.imgvisa);
                txtformapag.setText("Visa (crédito)");
                break;
            case "visadb":
                imgfotoformapag.setImageResource(R.drawable.imgvisa);
                txtformapag.setText("Visa (débito)");
                break;
            case "vrsmartvl":
                imgfotoformapag.setImageResource(R.drawable.imgvrsmartvl);
                txtformapag.setText("VR Smart (Vale)");
                break;
        }
    }
}

package com.example.controler;

import static android.content.ContentValues.TAG;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BotaoAdapter extends RecyclerView.Adapter<BotaoAdapter.BotaoViewHolder> {
    private List<Botao> botoes;
    private SelecionarBotoesActivity activity;

    public BotaoAdapter(List<Botao> botoes, SelecionarBotoesActivity activity) {
        this.botoes = botoes;
        this.activity = activity;
    }

    static class BotaoViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        CheckBox checkBox;
        Button btnDelete;

        BotaoViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
            checkBox = itemView.findViewById(R.id.checkBox);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        void bind(Botao botao) {
            textView.setText(botao.getTexto());
            checkBox.setChecked(botao.isSelected());
            btnDelete.setVisibility(botao.getTipo() ? View.GONE : View.VISIBLE);
        }
    }

    @NonNull
    @Override
    public BotaoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.botao_item, parent, false);
        return new BotaoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BotaoViewHolder holder, int position) {
        Botao botao = botoes.get(position);
        holder.bind(botao);

        Log.d(TAG, "onBindViewHolder: Botão " + botao.getTexto() + " - tipo: " + botao.getTipo());

        holder.checkBox.setOnCheckedChangeListener(null); // remove previous listener
        holder.checkBox.setChecked(botao.isSelected()); // ensure checkbox reflects the current state
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            botao.setSelecionado(isChecked);
            activity.onBotaoSelected(botao);
            Log.d(TAG, "onBindViewHolder: Botão " + botao.getTexto() + " selecionado - " + isChecked);
        });

        holder.itemView.setOnClickListener(v -> holder.checkBox.setChecked(!holder.checkBox.isChecked()));

        holder.btnDelete.setOnClickListener(v -> {
            Log.d(TAG, "onBindViewHolder: Botão " + botao.getTexto() + " deletado");
            activity.onBotaoDeleted(botao);
        });
    }

    @Override
    public int getItemCount() {
        return botoes.size();
    }

    public void updateBotoes(List<Botao> botoes) {
        this.botoes = botoes;
        notifyDataSetChanged();
    }
}
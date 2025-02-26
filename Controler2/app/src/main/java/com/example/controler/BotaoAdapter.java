package com.example.controler;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BotaoAdapter extends RecyclerView.Adapter<BotaoAdapter.BotaoViewHolder> {
    private SelecionarBotoesActivity activity;
    private List<Botao> botoes;

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

        holder.checkBox.setOnCheckedChangeListener(null); // remove previous listener
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                botao.setSelecionado(isChecked);
                activity.onBotaoSelected(botao);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.checkBox.setChecked(!holder.checkBox.isChecked());
            }
        });

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.onBotaoDeleted(botao);
            }
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
package com.example.controler;

public class Botao {
    private String texto;
    private boolean selecionado;
    private boolean tipo;
    private String acao;

    public Botao(String texto, boolean selecionado, boolean tipo, String acao) {
        this.texto = texto;
        this.selecionado = selecionado;
        this.tipo = tipo;
        this.acao = acao;
    }

    public String getTexto() {
        return texto;
    }

    public boolean isSelected() {
        return selecionado;
    }

    public void setSelecionado(boolean selecionado) {
        this.selecionado = selecionado;
    }

    public boolean getTipo() {
        return tipo;
    }

    public String getAcao() {
        return acao;
    }
}
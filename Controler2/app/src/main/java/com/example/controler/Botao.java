package com.example.controler;

public class Botao {
    private String texto;
    private boolean selecionado;
    private boolean tipo;
    private String acao;

    public Botao(String texto, boolean selecionado, boolean tipo) {
        this.texto = texto;
        this.selecionado = selecionado;
        this.tipo = tipo;
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

    public void setAcao(String acao) {
        this.acao = acao;
    }
}
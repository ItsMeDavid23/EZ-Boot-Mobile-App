package com.example.controler;

public class Botao {
    private String texto;
    private boolean selecionado;
    private boolean tipo;

    public Botao(String texto, boolean selecionado, boolean tipo) {
        this.texto = texto;
        this.selecionado = selecionado;
        this.tipo = tipo;
    }

    public String getTexto() {
        return texto;
    }

    public boolean getTipo() { return tipo; }

    public boolean isSelected() {
        return selecionado;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public void setSelecionado(boolean selecionado) {
        this.selecionado = selecionado;
    }
}
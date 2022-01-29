package com.eldercare.eldercare.model;

public class Paciente {

    private String idPaciente;
    private String nome;

    public Paciente(String nome, String idPaciente) {

        this.nome = nome;
        this.idPaciente = idPaciente;

    }

    public String getIdPaciente() {
        return idPaciente;
    }

    public void setIdPaciente(String idPaciente) {
        this.idPaciente = idPaciente;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}

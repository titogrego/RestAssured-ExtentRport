package DTO;

public class ContaDTO {
    private String nome;


    public String getNome() {
        return nome;
    }

    public ContaDTO setNome(String nome) {
        this.nome = nome;
        return this;
    }

    public ContaDTO(String nome) {
        this.nome = nome;
    }


}

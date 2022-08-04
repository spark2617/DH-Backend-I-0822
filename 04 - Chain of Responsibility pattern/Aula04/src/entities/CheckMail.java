package entities;

public class CheckMail {

    Gerenciador inicial;

    public CheckMail() {
        this.inicial = new GerenciadorGerencia();
        Gerenciador comercial = new GerenciadorComercial();
        Gerenciador tecnica = new GerenciadorTecnica();
        Gerenciador spam = new GerenciadorSpam();

        // Não faça igual ao cara aquele!!! Digite esta parte também.
        inicial.setGerenciadorSeguinte(comercial);
        comercial.setGerenciadorSeguinte(tecnica);
        tecnica.setGerenciadorSeguinte(spam);
    }

    public void verificar(Mail email) {
        inicial.verificar(email);
    }

}

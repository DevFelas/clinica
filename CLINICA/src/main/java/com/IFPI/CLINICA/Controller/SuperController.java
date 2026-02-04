package com.IFPI.CLINICA.Controller;

import com.IFPI.CLINICA.Model.Perfil;
import com.IFPI.CLINICA.Model.Usuario;
import com.IFPI.CLINICA.Service.FxmlLoaderService;
import com.IFPI.CLINICA.Util.Navigator;
import com.IFPI.CLINICA.Util.SessaoUsuario;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.springframework.beans.factory.annotation.Autowired;


import javafx.event.ActionEvent;

public abstract class SuperController {

    /**
     * Injetado pelo Spring: responsável por executar navegação/troca de telas na aplicação JavaFX.
     */
    @Autowired protected Navigator navigator;

    /**
     * Injetado pelo Spring: serviço utilitário para carregar arquivos FXML e/ou preparar cenas/controllers.
     */
    @Autowired protected FxmlLoaderService fxmlLoaderService;

    /**
     * Aplica regras de permissão na interface com base no usuário logado na sessão.
     *
     * <p>Este método:
     * <ul>
     *   <li>Obtém o usuário logado via {@link SessaoUsuario#getInstance()}.</li>
     *   <li>Se não houver usuário (ou perfil), esconde o botão de financeiro e informa "SEM USUÁRIO".</li>
     *   <li>Se o perfil for {@link Perfil#RECEPCIONISTA}, esconde o botão e define o texto como "RECEPCIONISTA".</li>
     *   <li>Se o perfil for {@link Perfil#ADMIN}, exibe o botão e define o texto como "ADMINISTRADOR".</li>
     *   <li>Para outros perfis, esconde o botão e mostra o nome do perfil.</li>
     * </ul>
     * </p>
     *
     * @param btnFinanceiro botão associado à área/funcionalidade financeira (pode ser ocultado conforme perfil)
     * @param textUsuario label onde será exibida a identificação do perfil/usuário na interface
     */
    protected void aplicarPermissoesUI(javafx.scene.control.Button btnFinanceiro,
                                       javafx.scene.control.Label textUsuario) {

        Usuario usuario = SessaoUsuario.getInstance().getUsuarioLogado();

        if (usuario == null || usuario.getPerfil() == null) {
            textUsuario.setText("SEM USUÁRIO");
            btnFinanceiro.setVisible(false);
            return;
        }

        if (usuario.getPerfil() == Perfil.RECEPCIONISTA) {
            btnFinanceiro.setVisible(false);
            textUsuario.setText("RECEPCIONISTA");
        } else if (usuario.getPerfil() == Perfil.ADMIN) {
            btnFinanceiro.setVisible(true);
            textUsuario.setText("ADMINISTRADOR");
        } else {
            // opcional: outros perfis
            btnFinanceiro.setVisible(false);
            textUsuario.setText(usuario.getPerfil().name());
        }
    }

    /**
     * Realiza navegação para outra tela a partir do evento de ação (ex.: clique em botão).
     *
     * <p>O componente de origem do evento deve possuir um {@code userData} contendo o caminho
     * do arquivo FXML de destino. Exemplo no FXML:
     * {@code <Button onAction="#irPara" userData="/view/pages/AlgumaTela.fxml" ... />}</p>
     *
     * <p>Se {@code userData} estiver ausente, lança {@link IllegalArgumentException}.</p>
     *
     * @param event evento de ação disparado pelo JavaFX
     * @throws IllegalArgumentException se o nó de origem não possuir {@code userData} com o caminho do FXML
     */
    @FXML
    protected void irPara(ActionEvent event) {
        Node origem = (Node) event.getSource();

        Object ud = origem.getUserData();
        if (ud == null) {
            throw new IllegalArgumentException("Botão sem userData com o caminho do FXML.");
        }

        navigator.trocarPagina(origem, ud.toString());
    }

    /**
     * Executa o fluxo de saída (logout) do sistema.
     *
     * <p>Exibe um {@link javafx.scene.control.Alert} de confirmação. Caso o usuário confirme (OK),
     * o método limpa a sessão atual via {@link SessaoUsuario#limparSessao()} e redireciona para
     * a tela de login.</p>
     *
     * @param event evento de ação disparado pelo JavaFX (usado para obter o nó de origem na navegação)
     */
    @FXML
    public void sair(ActionEvent event) {

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Sair do sistema");
        confirm.setHeaderText(null);
        confirm.setContentText("Deseja realmente sair do sistema?");

        confirm.showAndWait().ifPresent(resposta -> {
            if (resposta == ButtonType.OK) {

                // limpa sessão
                SessaoUsuario.getInstance().limparSessao();

                // volta para login
                navigator.trocarPagina(
                        (Node) event.getSource(),
                        "/view/pages/Login.fxml"
                );
            }
        });
    }

}

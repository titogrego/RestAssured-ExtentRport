package tests;

import DTO.ContaDTO;
import DTO.MovimentacaoDTO;
import core.BaseTest;
import static org.hamcrest.Matchers.*;

import io.restassured.RestAssured;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.specification.FilterableRequestSpecification;
import org.testng.annotations.Test;
import utilities.Utils;
import static io.restassured.RestAssured.given;

public class TransacoesTest extends BaseTest{

    private static String CONTA_NAME = "conta "+ faker.number().digits(10);
    private  static ContaDTO conta = new ContaDTO(CONTA_NAME);

    private static Integer CONTA_ID;
    private static Integer MOV_ID;
    private String PATH_CONTA = "contas";
    private String PATH_TRANSACOES ="transacoes";
    private String PATH_SALDO ="saldo";


    @Test(description = "Teste para inclusão de uma nova conta", priority = 2)
    public void deveIncluirContaComSucesso() {

        CONTA_ID=
                given()
                        .body(conta)
                        .when()
                        .post(PATH_CONTA)
                        .then()
                        .statusCode(201)
                        .extract().path("id")
        ;
    }
    @Test(description = "Teste para alteração de uma conta", priority = 3)
    public void deveAlterarContaComSucesso() {
        conta.setNome(CONTA_NAME+" alterada");
        given()

                .body(conta)
                .pathParam("id", CONTA_ID)
                .when()
                .put(PATH_CONTA+"/{id}")
                .then()
                .statusCode(200)
                .body("nome",is(CONTA_NAME+" alterada") )
        ;
    }

    @Test(description = "Teste para validar que não é possível incluir conta repetida", priority = 4)
    public void naoDeveInserirContaRepetida() {

            given()

                .body(conta)
                .when()
                .post(PATH_CONTA)
                .then()
                .statusCode(400)
                .body("error",is("Já existe uma conta com esse nome!") )
        ;
    }
    @Test(description = "Teste para inclusão de uma nova movimentação", priority = 5)
    public void deveInserirMovimentacaoComSucesso() {

        MovimentacaoDTO mov =getMovimentacaoValida();
        MOV_ID = given()

                .body(mov)
                .when()
                .post(PATH_TRANSACOES)
                .then()
                .statusCode(201)
                .extract().path("id")

        ;


    }

    @Test(description = "Teste para validar campos obrigatórios na movimentação", priority = 6)
    public void deveValidarCamposObrigatoriosNaMovimentacao() {


        given()

                .body("{}")
                .when()
                .post(PATH_TRANSACOES)
                .then()
                .statusCode(400)
                .body("$",hasSize(8))
                .body("msg",hasItems(
                        "Data da Movimentação é obrigatório",
                        "Descrição é obrigatório",
                        "Interessado é obrigatório",
                        "Valor é obrigatório",
                        "Valor deve ser um número",
                        "Conta é obrigatório",
                        "Situação é obrigatório"
                ))
        ;

    }

    @Test(description = "Teste para validar não ser possível a inclusão de movimentação com data futura", priority = 7)
    public void naoDeveInserirMovimentacaoComDataFutura() {

        MovimentacaoDTO mov = getMovimentacaoValida();
        mov.setData_transacao(Utils.getDataDiferencaDias(3));
        given()

                .body(mov)
                .when()
                .post(PATH_TRANSACOES)
                .then()
                .statusCode(400)
                .body("$", hasSize(1))
                .body("msg", hasItem("Data da Movimentação deve ser menor ou igual à data atual"))

        ;


    }

    @Test(description = "Teste para validar que não seja possivel remoção de uma conta com movimentação", priority = 8)
    public void naoDeveRemoverContaComMovimentacao() {

        given()

                .pathParam("id", CONTA_ID)
                .when()
                .delete(PATH_CONTA+"/{id}")
                .then()
                .statusCode(500)
                .body("constraint", is("transacoes_conta_id_foreign"))

        ;


    }

    @Test(description = "Teste para calcular o saldo da Conta", priority = 9)
    public void deveCalcularSaldoContas() {


        given()

                .when()
                .get(PATH_SALDO)
                .then()
                .statusCode(200)
                .body("find{it.conta_id=="+CONTA_ID+"}.saldo", is("100.00"))

        ;


    }

    @Test(description = "Teste para remover movimentação", priority = 10)
    public void deveRemoverMovimentacao() {


        given()

                .pathParam("id", MOV_ID)
                .when()
                .delete(PATH_TRANSACOES+"/{id}")
                .then()
                .statusCode(204)


        ;


    }



    @Test(description = "Teste validar Schema das contas", priority = 11)
    public void deveValidaroSchemaDasContas() {

        given()
                .when()
                .get(PATH_CONTA)
                .then()
                .statusCode(200)
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("conta.json"))
        ;
    }

    @Test(description = "Teste para validar a exclusão de uma conta", priority = 12)
    public void deveRemoverConta() {

        given()

                .pathParam("id", CONTA_ID)
                .when()
                .delete(PATH_CONTA+"/{id}")
                .then()
                .statusCode(204)
              ;


    }
    @Test(description = "Teste para validar se conta foi realmente excluída", priority = 13)
    public void deveValidarSeContaFoiRemovida() {

        given()

                .pathParam("id", CONTA_ID)
                .when()
                .get(PATH_CONTA+"/{id}")
                .then()
                .statusCode(404)
                .body("error" ,is("Conta com id "+CONTA_ID+" não encontrada"))

        ;


    }

    @Test(description = "Teste validar que não é possivel acessar a API sem o tokem", priority = 14)
    public void naoDeveAcessarApiSemToken() {
        FilterableRequestSpecification req = (FilterableRequestSpecification) RestAssured.requestSpecification;
        req.removeHeader("Authorization");
        given()
                .when()
                .get(PATH_CONTA)
                .then()
                .statusCode(401)
        ;
    }
    private MovimentacaoDTO getMovimentacaoValida() {
        MovimentacaoDTO mov = new MovimentacaoDTO();
        mov.setConta_id(CONTA_ID);
        mov.setDescricao("Descrição da movimentação: " + faker.lorem().sentence());
        mov.setEnvolvido("Envolvido na movimentação: " + faker.name().fullName());
        mov.setTipo("REC");
        mov.setData_transacao(Utils.getDataDiferencaDias(-40));
        mov.setData_pagamento(Utils.getDataDiferencaDias(-10));
        mov.setValor(100f);
        mov.setStatus(true);
        return mov;
    }
}
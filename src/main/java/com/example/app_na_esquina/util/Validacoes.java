package com.example.app_na_esquina.util;

import com.example.app_na_esquina.exceptions.RegraNegocioException;

import java.time.LocalTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;

// Regras de validação reutilizáveis (espelham o utils/validacoes.js do front).
//
// Por que existe: o front valida para o usuário ver o erro na hora, mas o BACK é a barreira de
// verdade (alguém pode chamar a API sem passar pelo app). Cada método abaixo:
//   - devolve o valor já "limpo" (sem espaços sobrando, só dígitos...) quando está tudo certo;
//   - lança RegraNegocioException (HTTP 400) com uma mensagem em português quando está errado.
// O GlobalExceptionHandler transforma essa exceção na resposta que o app mostra no toast.
public final class Validacoes {

    private Validacoes() {
    }

    // ---------------------------------------------------------------- utilidades

    // "  Ana   Maria " -> "Ana Maria"
    public static String limparEspacos(String valor) {
        return valor == null ? "" : valor.trim().replaceAll("\\s+", " ");
    }

    // "(71) 99999-9999" -> "71999999999"
    public static String somenteDigitos(String valor) {
        return valor == null ? "" : valor.replaceAll("\\D", "");
    }

    // ---------------------------------------------------------------- usuário

    public static String nome(String valor) {
        String nome = limparEspacos(valor);
        if (nome.isEmpty()) throw new RegraNegocioException("O nome é obrigatório.");
        if (nome.length() < 3 || nome.length() > 100)
            throw new RegraNegocioException("O nome deve ter entre 3 e 100 caracteres.");
        if (!nome.matches("^[\\p{L}][\\p{L} .'-]*$"))
            throw new RegraNegocioException("O nome deve conter apenas letras e espaços.");
        return nome;
    }

    // DDD + número, só dígitos: 71999999999 (celular) ou 7133334444 (fixo)
    public static String telefone(String valor) {
        String digitos = somenteDigitos(valor);
        if (digitos.isEmpty()) throw new RegraNegocioException("O telefone é obrigatório.");
        if (!digitos.matches("^[1-9][1-9](9\\d{8}|[2-5]\\d{7})$"))
            throw new RegraNegocioException("Telefone inválido. Informe DDD + número (ex.: 71 99999-9999).");
        return digitos;
    }

    // Mesma regra do cadastro: 8 a 72 caracteres, com pelo menos uma letra e um número.
    public static void senha(String valor, String rotulo) {
        if (valor == null || valor.isEmpty()) throw new RegraNegocioException(rotulo + " é obrigatória.");
        if (valor.length() < 8 || valor.length() > 72)
            throw new RegraNegocioException(rotulo + " deve ter entre 8 e 72 caracteres.");
        if (!valor.matches("^(?=.*[A-Za-z])(?=.*\\d).*$"))
            throw new RegraNegocioException(rotulo + " deve conter pelo menos uma letra e um número.");
    }

    // ---- e-mail: pega erros de digitação em provedores conhecidos (gmail.co, gmial.com...) ----

    private static final List<String> DOMINIOS_VALIDOS = List.of(
            "gmail.com", "hotmail.com", "hotmail.com.br", "outlook.com", "outlook.com.br",
            "yahoo.com", "yahoo.com.br", "icloud.com", "live.com");

    // nome do provedor -> domínio correto
    private static final Map<String, String> PROVEDORES = Map.of(
            "gmail", "gmail.com", "hotmail", "hotmail.com", "outlook", "outlook.com",
            "yahoo", "yahoo.com", "icloud", "icloud.com");

    // grafias erradas comuns -> domínio correto
    private static final Map<String, String> ERROS_COMUNS = Map.ofEntries(
            Map.entry("gmial", "gmail.com"), Map.entry("gmai", "gmail.com"),
            Map.entry("gmal", "gmail.com"), Map.entry("gnail", "gmail.com"),
            Map.entry("gmeil", "gmail.com"), Map.entry("hotmial", "hotmail.com"),
            Map.entry("hotmai", "hotmail.com"), Map.entry("hotnail", "hotmail.com"),
            Map.entry("outlok", "outlook.com"), Map.entry("yaho", "yahoo.com"),
            Map.entry("yahooo", "yahoo.com"));

    // Recusa "joao@gmail.co" e sugere "gmail.com". Domínios que não conhecemos passam normalmente.
    public static void dominioDoEmail(String email) {
        if (email == null || !email.contains("@")) return;
        String dominio = email.substring(email.lastIndexOf('@') + 1).toLowerCase(Locale.ROOT);
        if (DOMINIOS_VALIDOS.contains(dominio)) return;

        String nomeProvedor = dominio.split("\\.")[0];
        String sugestao = PROVEDORES.getOrDefault(nomeProvedor, ERROS_COMUNS.get(nomeProvedor));
        if (sugestao != null)
            throw new RegraNegocioException("Confira o domínio do e-mail. Você quis dizer " + sugestao + "?");
    }

    // ---------------------------------------------------------------- mercadinho

    // Para nome fantasia, endereço etc. `rotulo` vem com artigo: "o nome do mercadinho", "o endereço".
    public static String texto(String valor, String rotulo, int minimo, int maximo) {
        String texto = limparEspacos(valor);
        if (texto.isEmpty()) throw new RegraNegocioException("Informe " + rotulo + ".");
        if (texto.length() < minimo || texto.length() > maximo)
            throw new RegraNegocioException("Confira " + rotulo + ": use de " + minimo + " a " + maximo + " caracteres.");
        return texto;
    }

    // CEP: aceita "40015-000" ou "40015000" e devolve só os 8 números.
    public static String cep(String valor) {
        String digitos = somenteDigitos(valor);
        if (digitos.length() != 8) throw new RegraNegocioException("O CEP deve ter 8 números.");
        return digitos;
    }

    public static void horarios(LocalTime abertura, LocalTime fechamento) {
        if (abertura == null) throw new RegraNegocioException("Informe o horário de abertura.");
        if (fechamento == null) throw new RegraNegocioException("Informe o horário de fechamento.");
        if (!fechamento.isAfter(abertura))
            throw new RegraNegocioException("O horário de fechamento deve ser depois do horário de abertura.");
    }

    // Latitude vai de -90 a 90; longitude de -180 a 180. Vazio (null) é permitido.
    public static void coordenadas(Double latitude, Double longitude) {
        if (latitude != null && (latitude < -90 || latitude > 90))
            throw new RegraNegocioException("Latitude inválida.");
        if (longitude != null && (longitude < -180 || longitude > 180))
            throw new RegraNegocioException("Longitude inválida.");
    }

    // ---------------------------------------------------------------- produto

    public static String nomeProduto(String valor) {
        String nome = limparEspacos(valor);
        if (nome.isEmpty()) throw new RegraNegocioException("O nome do produto é obrigatório.");
        if (nome.length() < 2) throw new RegraNegocioException("O nome do produto deve ter pelo menos 2 caracteres.");
        if (nome.length() > 100) throw new RegraNegocioException("O nome do produto deve ter no máximo 100 caracteres.");
        return nome;
    }

    public static void preco(Double valor) {
        if (valor == null || valor.isNaN()) throw new RegraNegocioException("O preço é obrigatório.");
        if (valor <= 0) throw new RegraNegocioException("O preço deve ser maior que zero.");
        if (valor > 99999.99) throw new RegraNegocioException("O preço máximo é R$ 99.999,99.");
    }
    // ---------------------------------------------------------------- endereço

    private static final List<String> UFS = List.of(
            "AC", "AL", "AP", "AM", "BA", "CE", "DF", "ES", "GO", "MA", "MT", "MS", "MG", "PA",
            "PB", "PR", "PE", "PI", "RJ", "RN", "RS", "RO", "RR", "SC", "SP", "SE", "TO");

    // Endereço já validado e limpo (o "record" é uma classe de dados imutável do Java).
    // texto() é o que vai para a coluna "endereco", exibida em pedidos e buscas:
    //   "Rua Chile, 123, Sala 2, Centro, Salvador - BA"
    public record Endereco(String cep, String rua, String numero, String bairro,
                           String complemento, String cidade, String uf) {
        public String texto() {
            StringBuilder sb = new StringBuilder(rua).append(", ").append(numero);
            if (!complemento.isEmpty()) sb.append(", ").append(complemento);
            sb.append(", ").append(bairro).append(", ").append(cidade).append(" - ").append(uf);
            return sb.toString();
        }
    }

    // true se pelo menos um desses campos veio preenchido (usado para saber se o endereço foi enviado)
    public static boolean temEndereco(String... campos) {
        for (String campo : campos) {
            if (!limparEspacos(campo).isEmpty()) return true;
        }
        return false;
    }

    // Valida o endereço em campos separados (mesmas regras do utils/validacoes.js do front).
    // O complemento é opcional.
    public static Endereco endereco(String cep, String rua, String numero, String bairro,
                                    String complemento, String cidade, String uf) {
        String cepOk = cep(cep);
        String ruaOk = texto(rua, "a rua", 3, 80);

        String numeroOk = limparEspacos(numero);
        if (numeroOk.isEmpty()) throw new RegraNegocioException("Informe o número ou S/N.");
        if (numeroOk.length() > 10) throw new RegraNegocioException("O número deve ter no máximo 10 caracteres.");
        if (!numeroOk.matches("^[0-9A-Za-z/\\-\\s]+$"))
            throw new RegraNegocioException("Use só letras, números, / ou - no número.");

        String bairroOk = texto(bairro, "o bairro", 2, 40);
        String cidadeOk = texto(cidade, "a cidade", 2, 50);

        String ufOk = limparEspacos(uf).toUpperCase(Locale.ROOT);
        if (ufOk.isEmpty()) throw new RegraNegocioException("Informe o estado.");
        if (!UFS.contains(ufOk)) throw new RegraNegocioException("Estado inválido. Ex.: BA.");

        String complementoOk = limparEspacos(complemento);
        if (complementoOk.length() > 60)
            throw new RegraNegocioException("O complemento deve ter no máximo 60 caracteres.");

        return new Endereco(cepOk, ruaOk, numeroOk, bairroOk, complementoOk, cidadeOk, ufOk);
    }
}
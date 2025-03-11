package main;

import java.security.*;
import java.util.Base64;
import java.util.HashMap;
import java.util.Scanner;

public class SecureTokenSystem {
    private static HashMap<String, String> users = new HashMap<>(); // Armazena usuários (Nome -> Senha)
    private static KeyPair keyPair; // Chave pública e privada

    public static void main(String[] args) throws Exception {
        // Gera o par de chaves RSA
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        keyPair = keyGen.generateKeyPair();

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n1. Cadastrar usuário");
            System.out.println("2. Fazer login e realizar ação");
            System.out.println("3. Sair");
            System.out.print("Escolha uma opção: ");
            int option = scanner.nextInt();
            scanner.nextLine(); // Consumir quebra de linha

            switch (option) {
                case 1:
                    registerUser(scanner);
                    break;
                case 2:
                    loginAndPerformAction(scanner);
                    break;
                case 3:
                    System.out.println("Encerrando...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Opção inválida!");
            }
        }
    }

    // Método para cadastrar um usuário
    private static void registerUser(Scanner scanner) {
        System.out.print("Nome de usuário: ");
        String username = scanner.nextLine();
        System.out.print("Senha: ");
        String password = scanner.nextLine();

        if (users.containsKey(username)) {
            System.out.println("Usuário já existe!");
        } else {
            users.put(username, password);
            System.out.println("Usuário cadastrado com sucesso!");
        }
    }

    // Método para login e uso do token
    private static void loginAndPerformAction(Scanner scanner) throws Exception {
        System.out.print("Nome de usuário: ");
        String username = scanner.nextLine();
        System.out.print("Senha: ");
        String password = scanner.nextLine();

        // Verifica se o usuário existe e a senha está correta
        if (!users.containsKey(username) || !users.get(username).equals(password)) {
            System.out.println("Usuário ou senha inválidos!");
            return;
        }

        System.out.println("✅ Login bem-sucedido!");

        // Criando um token para o usuário autenticado
        String tokenData = "User:" + username + "|Action:Compra|Timestamp:" + System.currentTimeMillis();
        String signedToken = signData(tokenData);

        System.out.println("🔑 Token gerado: " + signedToken);

        // Simulando a verificação antes de permitir o acesso/compra
        if (verifySignature(tokenData, signedToken)) {
            System.out.println("✅ Token verificado! Ação autorizada.");
        } else {
            System.out.println("❌ Falha na verificação do token!");
        }
    }

    // Método para assinar os dados com a chave privada
    private static String signData(String data) throws Exception {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(keyPair.getPrivate());
        signature.update(data.getBytes());
        byte[] signedBytes = signature.sign();
        return Base64.getEncoder().encodeToString(signedBytes);
    }

    // Método para verificar a assinatura usando a chave pública
    private static boolean verifySignature(String data, String signedData) throws Exception {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(keyPair.getPublic());
        signature.update(data.getBytes());
        byte[] signedBytes = Base64.getDecoder().decode(signedData);
        return signature.verify(signedBytes);
    }
}

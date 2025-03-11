package main;

import java.security.*;
import java.util.Base64;

public class DigitalSignature {
    public static void main(String[] args) throws Exception {
        // Gerar chave RSA
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA"); //Cria um gerador de pares de chaves RSA.
        keyGen.initialize(2048); //Define o tamanho da chave como 2048 bits (padrão seguro).
        KeyPair keyPair = keyGen.generateKeyPair(); //Gera o par de chaves RSA.
        PrivateKey privateKey = keyPair.getPrivate(); //Obtém a chave privada (para assinar os dados).
        PublicKey publicKey = keyPair.getPublic(); //Obtém a chave pública (para verificar a assinatura).

        // Dado a ser assinado
        String tokenData = "TokenID:1234|Owner:UserXYZ|Timestamp:1710172334"; 
        //Define uma string com dados que representam o token (pode ser um ID, dono e timestamp)

        // Criar um hash SHA-256 do token
        MessageDigest digest = MessageDigest.getInstance("SHA-256");// Cria um objeto MessageDigest para calcular um hash SHA-256
        byte[] tokenHash = digest.digest(tokenData.getBytes()); //Converte tokenData em bytes e calcula o hash.

        // Assinar o hash com a chave privada
        Signature signature = Signature.getInstance("SHA256withRSA"); // Cria um objeto Signature que usa SHA-256 com RSA.
        signature.initSign(privateKey); //Inicializa o objeto para assinar com a chave privada.
        signature.update(tokenHash); //Adiciona o hash do token ao objeto de assinatura.
        byte[] signedData = signature.sign(); //Gera a assinatura digital.

        System.out.println("Assinatura: " + Base64.getEncoder().encodeToString(signedData)); //A assinatura é convertida para Base64 para que possa ser impressa em formato legível.

        // Verificar a assinatura com a chave pública
        signature.initVerify(publicKey); //Inicializa o objeto Signature para verificação com a chave pública.
        signature.update(tokenHash); // Adiciona o mesmo hash do token.
        boolean isValid = signature.verify(signedData); //Verifica se a assinatura é válida.

        if (isValid) {
            System.out.println("✅ Assinatura válida!");
        } else { //Se isValid for true, significa que a assinatura corresponde ao hash e foi gerada pela chave privada correta.
            System.out.println("❌ Assinatura inválida!");
        }
    }
}

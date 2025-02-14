package dev.mixsource.application;

public interface AuthService {
    // Tenta autenticar o usuário e retorna o token em caso de sucesso.
    // Lança uma exceção se a autenticação falhar.
    String authenticate(String username, String password) throws Exception;
} 
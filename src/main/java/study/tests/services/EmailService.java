package study.tests.services;

import study.tests.entities.Locacao;

public interface EmailService {
    void notificarEmail(Locacao locacao);
}

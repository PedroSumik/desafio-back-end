package com.desafio.backend.ultralims.entity;

import com.desafio.backend.ultralims.exception.TrocaStatusInvalidException;

public enum StatusAmostra {
    PENDENTE{
        @Override
        public StatusAmostra avancarStatus() {
            return EM_ANALISE;
        }
    },
    EM_ANALISE{
        @Override
        public StatusAmostra avancarStatus() {
            return CONCLUIDA;
        }
    },
    CONCLUIDA{
        @Override
        public StatusAmostra avancarStatus() {
            throw new TrocaStatusInvalidException("Não é possível avançar o status além de CONCLUIDA.");
        }

        @Override
        public StatusAmostra rejeitarAmostra() {
            return REJEITADA;
        }

        @Override
        public StatusAmostra aprovarAmostra() {
            return APROVADA;
        }
    },
    APROVADA{
        @Override
        public StatusAmostra aprovarAmostra() {
            throw new TrocaStatusInvalidException("Amotras ja está aprovada.");
        }

        @Override
        public StatusAmostra rejeitarAmostra() {
            throw new TrocaStatusInvalidException("Não é possível rejeitar uma amostra aprovada.");
        }
    },
    REJEITADA{
        @Override
        public StatusAmostra rejeitarAmostra() {
            throw new TrocaStatusInvalidException("Amostra já está rejeitada.");
        }
        @Override
        public StatusAmostra aprovarAmostra() {
            throw new TrocaStatusInvalidException("Não é possível aprovar uma amostra rejeitada.");
        }
    };

    public StatusAmostra avancarStatus() {
        throw new TrocaStatusInvalidException("Não é possível avançar o status a partir do status " + this.name());
    }

    public StatusAmostra rejeitarAmostra() {
        throw new TrocaStatusInvalidException("Não é possível rejeitar a amostra a partir do status " + this.name());
    }

    public StatusAmostra aprovarAmostra() {
        throw new TrocaStatusInvalidException("Não é possível aprovar a amostra a partir do status " + this.name());
    }
}

package com.desafio.backend.ultralims;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.desafio.backend.ultralims.entity.Amostra;
import com.desafio.backend.ultralims.entity.AmostraStatusHistorico;
import com.desafio.backend.ultralims.entity.StatusAmostra;
import com.desafio.backend.ultralims.exception.AmostraDuplicadaException;
import com.desafio.backend.ultralims.exception.TrocaStatusInvalidException;
import com.desafio.backend.ultralims.mapper.AmostraMapper;
import com.desafio.backend.ultralims.repository.AmostraRepository;
import com.desafio.backend.ultralims.repository.AmostraStatusHistoricoRepository;
import com.desafio.backend.ultralims.request.AmostraRequest;
import com.desafio.backend.ultralims.request.AtualizarAmostraRequest;
import com.desafio.backend.ultralims.service.AmostraService;

@ExtendWith(MockitoExtension.class)
class AmostraTests {

        @Mock
        private AmostraRepository amostraRepository;

        @Mock
        private AmostraStatusHistoricoRepository amostraStatusHistoricoRepository;

        @Mock
        private AmostraMapper amostraMapper;

        @InjectMocks
        private AmostraService amostraService;

        @Test
        public void criarAmostraTestePositivo() {
                AmostraRequest request = new AmostraRequest();
                request.setCodAmostra("123");
                request.setTipoColeta("SANGUE");
                request.setDataColeta(LocalDateTime.now());

                Amostra amostraSalva = new Amostra(
                                request.getCodAmostra(),
                                request.getTipoColeta(),
                                request.getDataColeta());

                when(amostraMapper.AmostraRequestToAmostra(request))
                                .thenReturn(amostraSalva);

                when(amostraRepository.findByCodAmostra("123"))
                                .thenReturn(Optional.empty());

                when(amostraRepository.save(any(Amostra.class)))
                                .thenReturn(amostraSalva);

                Amostra resultado = amostraService.criaAmostra(request);

                assertNotNull(resultado);
                assertEquals("123", resultado.getCodAmostra());

                verify(amostraMapper).AmostraRequestToAmostra(request);
                verify(amostraRepository).save(any(Amostra.class));
                verify(amostraStatusHistoricoRepository)
                                .save(any(AmostraStatusHistorico.class));
        }

        @Test
        public void criarAmostraTesteNegativo() {
                AmostraRequest request = new AmostraRequest();
                request.setCodAmostra("123");
                request.setTipoColeta("SANGUE");
                request.setDataColeta(LocalDateTime.now());

                Amostra amostraExistente = new Amostra(
                                "123",
                                "SANGUE",
                                LocalDateTime.now());

                when(amostraRepository.findByCodAmostra("123"))
                                .thenReturn(Optional.of(amostraExistente));

                Exception exception = Assertions.assertThrows(
                                AmostraDuplicadaException.class,
                                () -> amostraService.criaAmostra(request));

                assertTrue(exception.getMessage().contains("123"));

                verify(amostraRepository).findByCodAmostra("123");
                verify(amostraRepository, Mockito.never()).save(any());
                verify(amostraStatusHistoricoRepository, Mockito.never()).save(any());
        }

        @Test
        public void atualizarAmostraTestePositivo() {
                AmostraRequest request = new AmostraRequest();
                request.setCodAmostra("123");
                request.setTipoColeta("SANGUE");
                request.setDataColeta(LocalDateTime.now());

                Amostra amostraSalva = new Amostra(
                                request.getCodAmostra(),
                                request.getTipoColeta(),
                                request.getDataColeta());

                amostraSalva.setId(UUID.randomUUID());

                AtualizarAmostraRequest requestAtualizado = new AtualizarAmostraRequest(
                                "1234",
                                "URINA",
                                LocalDateTime.now());

                Amostra amostraAtualizada = new Amostra(
                                requestAtualizado.getCodAmostra(),
                                requestAtualizado.getTipoColeta(),
                                requestAtualizado.getDataColeta());

                when(amostraMapper.AmostraRequestToAmostra(request))
                                .thenReturn(amostraSalva);

                when(amostraRepository.findByCodAmostra("123"))
                                .thenReturn(Optional.empty());

                when(amostraRepository.save(any(Amostra.class)))
                                .thenReturn(amostraSalva)
                                .thenReturn(amostraAtualizada);

                when(amostraRepository.findById(amostraSalva.getId()))
                                .thenReturn(Optional.of(amostraSalva));

                doAnswer(invocation -> {
                        Amostra amostra = invocation.getArgument(0);
                        AtualizarAmostraRequest req = invocation.getArgument(1);
                        amostra.setCodAmostra(req.getCodAmostra());
                        amostra.setTipoColeta(req.getTipoColeta());
                        amostra.setDataColeta(req.getDataColeta());
                        return null;
                }).when(amostraMapper).AmostraAtualizarRequestToAmostra(any(Amostra.class),
                                any(AtualizarAmostraRequest.class));

                Amostra amostra = amostraService.criaAmostra(request);
                String resultado = amostraService.atualizarAmostra(amostra.getId(), requestAtualizado);

                assertNotNull(amostra);
                assertTrue(resultado.contains("Amostra atualizada com sucesso!"));
                assertEquals("1234", amostra.getCodAmostra());
                assertEquals(requestAtualizado.getTipoColeta(), amostra.getTipoColeta());
                assertEquals(requestAtualizado.getDataColeta(), amostra.getDataColeta());

                verify(amostraMapper).AmostraRequestToAmostra(request);
                verify(amostraMapper).AmostraAtualizarRequestToAmostra(amostra, requestAtualizado);
                verify(amostraRepository).findById(amostra.getId());
                verify(amostraRepository, times(2)).save(any());
        }

        @Test
        public void atualizarAmostraComValoresInvalidosTesteNegativo() {
                AmostraRequest request = new AmostraRequest();
                request.setCodAmostra("123");
                request.setTipoColeta("SANGUE");
                request.setDataColeta(LocalDateTime.now());

                Amostra amostraSalva = new Amostra(
                                request.getCodAmostra(),
                                request.getTipoColeta(),
                                request.getDataColeta());
                amostraSalva.setId(UUID.randomUUID());

                AtualizarAmostraRequest requestAtualizado = new AtualizarAmostraRequest(
                                null,
                                "URINA",
                                LocalDateTime.now().plusDays(1));

                when(amostraMapper.AmostraRequestToAmostra(request))
                                .thenReturn(amostraSalva);

                when(amostraRepository.findByCodAmostra("123"))
                                .thenReturn(Optional.empty());

                when(amostraRepository.save(any(Amostra.class)))
                                .thenReturn(amostraSalva);

                Amostra amostra = amostraService.criaAmostra(request);

                Exception exception = Assertions.assertThrows(
                                IllegalArgumentException.class,
                                () -> amostraService.atualizarAmostra(amostra.getId(), requestAtualizado));

                assertTrue(exception.getMessage().contains("Data de coleta nao pode ser futura ao dia atual."));

                assertEquals("123", amostra.getCodAmostra());
                assertEquals("SANGUE", amostra.getTipoColeta());

                verify(amostraRepository).save(amostra);
        }

        @Test
        public void atualizarStatusAmostraPositivo() {
                AmostraRequest request = new AmostraRequest();
                request.setCodAmostra("123");
                request.setTipoColeta("SANGUE");
                request.setDataColeta(LocalDateTime.now());

                Amostra amostraSalva = new Amostra(
                                request.getCodAmostra(),
                                request.getTipoColeta(),
                                request.getDataColeta());
                amostraSalva.setId(UUID.randomUUID());

                when(amostraMapper.AmostraRequestToAmostra(request))
                                .thenReturn(amostraSalva);

                when(amostraRepository.findByCodAmostra("123"))
                                .thenReturn(Optional.empty());

                when(amostraRepository.save(any(Amostra.class)))
                                .thenReturn(amostraSalva);

                when(amostraRepository.findById(amostraSalva.getId()))
                                .thenReturn(Optional.of(amostraSalva));

                Amostra amostra = amostraService.criaAmostra(request);
                String resultado = amostraService.atualizarStatusAmostra(amostra.getId());

                assertTrue(resultado.contains("atualizado para"));
                assertEquals(StatusAmostra.EM_ANALISE, amostra.getStatus());

                verify(amostraMapper).AmostraRequestToAmostra(request);
                verify(amostraRepository).findById(amostra.getId());
                verify(amostraStatusHistoricoRepository, times(2)).save(any());
        }

        @Test
        public void atualizarStatusAmostraRejeitarNegativo() {
                AmostraRequest request = new AmostraRequest();
                request.setCodAmostra("123");
                request.setTipoColeta("SANGUE");
                request.setDataColeta(LocalDateTime.now());

                Amostra amostraSalva = new Amostra(
                                request.getCodAmostra(),
                                request.getTipoColeta(),
                                request.getDataColeta());
                amostraSalva.setId(UUID.randomUUID());

                when(amostraMapper.AmostraRequestToAmostra(request))
                                .thenReturn(amostraSalva);

                when(amostraRepository.findByCodAmostra("123"))
                                .thenReturn(Optional.empty());

                when(amostraRepository.save(any(Amostra.class)))
                                .thenReturn(amostraSalva);

                when(amostraRepository.findById(amostraSalva.getId()))
                                .thenReturn(Optional.of(amostraSalva));

                Amostra amostra = amostraService.criaAmostra(request);

                Exception exception = Assertions.assertThrows(
                                TrocaStatusInvalidException.class,
                                () -> amostraService.rejeitarAmostra(amostra.getId()));

                assertTrue(exception.getMessage().contains("Não é possível rejeitar a amostra"));
                assertEquals(StatusAmostra.PENDENTE, amostra.getStatus());

                verify(amostraRepository).findByCodAmostra("123");
        }

        @Test
        public void atualizarStatusAmostraAprovarNegativo() {
                AmostraRequest request = new AmostraRequest();
                request.setCodAmostra("123");
                request.setTipoColeta("SANGUE");
                request.setDataColeta(LocalDateTime.now());

                Amostra amostraSalva = new Amostra(
                                request.getCodAmostra(),
                                request.getTipoColeta(),
                                request.getDataColeta());
                amostraSalva.setId(UUID.randomUUID());

                when(amostraMapper.AmostraRequestToAmostra(request))
                                .thenReturn(amostraSalva);

                when(amostraRepository.findByCodAmostra("123"))
                                .thenReturn(Optional.empty());

                when(amostraRepository.save(any(Amostra.class)))
                                .thenReturn(amostraSalva);

                when(amostraRepository.findById(amostraSalva.getId()))
                                .thenReturn(Optional.of(amostraSalva));

                Amostra amostra = amostraService.criaAmostra(request);

                Exception exception = Assertions.assertThrows(
                                TrocaStatusInvalidException.class,
                                () -> amostraService.aprovarAmostra(amostra.getId()));

                assertTrue(exception.getMessage().contains("Não é possível aprovar a amostra"));
                assertEquals(StatusAmostra.PENDENTE, amostra.getStatus());

                verify(amostraRepository).findByCodAmostra("123");
        }

        @Test
        public void atualizarStatusAmostraAprovarPositivo() {
                AmostraRequest request = new AmostraRequest();
                request.setCodAmostra("123");
                request.setTipoColeta("SANGUE");
                request.setDataColeta(LocalDateTime.now());

                Amostra amostraSalva = new Amostra(
                                request.getCodAmostra(),
                                request.getTipoColeta(),
                                request.getDataColeta());
                amostraSalva.setId(UUID.randomUUID());

                when(amostraMapper.AmostraRequestToAmostra(request))
                                .thenReturn(amostraSalva);

                when(amostraRepository.findByCodAmostra("123"))
                                .thenReturn(Optional.empty());

                when(amostraRepository.save(any(Amostra.class)))
                                .thenReturn(amostraSalva);

                when(amostraRepository.findById(amostraSalva.getId()))
                                .thenReturn(Optional.of(amostraSalva));

                Amostra amostra = amostraService.criaAmostra(request);
                amostraService.atualizarStatusAmostra(amostra.getId());
                amostraService.atualizarStatusAmostra(amostra.getId());
                amostraService.aprovarAmostra(amostra.getId());

                assertEquals(StatusAmostra.APROVADA, amostra.getStatus());

                verify(amostraMapper).AmostraRequestToAmostra(request);
                verify(amostraRepository).findByCodAmostra("123");
                verify(amostraStatusHistoricoRepository, times(4)).save(any());
        }

        @Test
        public void atualizarStatusAmostraRejeitarPositivo() {
                AmostraRequest request = new AmostraRequest();
                request.setCodAmostra("123");
                request.setTipoColeta("SANGUE");
                request.setDataColeta(LocalDateTime.now());

                Amostra amostraSalva = new Amostra(
                                request.getCodAmostra(),
                                request.getTipoColeta(),
                                request.getDataColeta());
                amostraSalva.setId(UUID.randomUUID());

                when(amostraMapper.AmostraRequestToAmostra(request))
                                .thenReturn(amostraSalva);

                when(amostraRepository.findByCodAmostra("123"))
                                .thenReturn(Optional.empty());

                when(amostraRepository.save(any(Amostra.class)))
                                .thenReturn(amostraSalva);

                when(amostraRepository.findById(amostraSalva.getId()))
                                .thenReturn(Optional.of(amostraSalva));

                Amostra amostra = amostraService.criaAmostra(request);
                amostraService.atualizarStatusAmostra(amostra.getId());
                amostraService.atualizarStatusAmostra(amostra.getId());
                amostraService.rejeitarAmostra(amostra.getId());

                assertEquals(StatusAmostra.REJEITADA, amostra.getStatus());

                verify(amostraMapper).AmostraRequestToAmostra(request);
                verify(amostraRepository).findByCodAmostra("123");
                verify(amostraStatusHistoricoRepository, times(4)).save(any());
        }

}
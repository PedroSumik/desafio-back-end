package com.desafio.backend.ultralims;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
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
import com.desafio.backend.ultralims.repository.AmostraRepository;
import com.desafio.backend.ultralims.repository.AmostraStatusHistoricoRepository;
import com.desafio.backend.ultralims.request.AmostraRequest;
import com.desafio.backend.ultralims.service.AmostraService;



@ExtendWith(MockitoExtension.class)
class AmostraTests {

	@Mock
	private AmostraRepository amostraRepository;

	@Mock
	private AmostraStatusHistoricoRepository amostraStatusHistoricoRepository;

	@InjectMocks
	private AmostraService amostraService;



	@Test
	public void criarAmostraTestePositivo(){
		AmostraRequest request = new AmostraRequest();
        request.setCodAmostra("123");
        request.setTipoColeta("SANGUE");
        request.setDataColeta(LocalDateTime.now());

        Amostra amostraSalva = new Amostra(
                request.getCodAmostra(),
                request.getTipoColeta(),
                request.getDataColeta()
        );

        when(amostraRepository.findByCodAmostra("123"))
                .thenReturn(Optional.empty());

        when(amostraRepository.save(any(Amostra.class)))
                .thenReturn(amostraSalva);

        // Act (ação)
        Amostra resultado = amostraService.criaAmostra(request);

        // Assert (verificação)
        assertNotNull(resultado);
        assertEquals("123", resultado.getCodAmostra());

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
				LocalDateTime.now()
		);

		when(amostraRepository.findByCodAmostra("123"))
				.thenReturn(Optional.of(amostraExistente));

		Exception exception = Assertions.assertThrows(
				AmostraDuplicadaException.class,
				() -> amostraService.criaAmostra(request)
		);

		assertTrue(exception.getMessage().contains("123"));
		
		verify(amostraRepository).findByCodAmostra("123");
		verify(amostraRepository, Mockito.never()).save(any());
		verify(amostraStatusHistoricoRepository, Mockito.never()).save(any());
	}

    @Test
    public void atualizarStatusAmostraPositivo(){
        AmostraRequest request = new AmostraRequest();
        request.setCodAmostra("123");
        request.setTipoColeta("SANGUE");
        request.setDataColeta(LocalDateTime.now());

        Amostra amostraSalva = new Amostra(
                request.getCodAmostra(),
                request.getTipoColeta(),
                request.getDataColeta()
        );
        amostraSalva.setId(UUID.randomUUID());

        when(amostraRepository.findByCodAmostra("123"))
                .thenReturn(Optional.empty());

        when(amostraRepository.save(any(Amostra.class)))
                .thenReturn(amostraSalva);

        when(amostraRepository.findById(amostraSalva.getId()))
            .thenReturn(Optional.of(amostraSalva));

        Amostra resultado = amostraService.criaAmostra(request);
        resultado = amostraService.atualizarStatusAmostra(resultado.getId());

        assertNotNull(resultado);
        assertEquals(StatusAmostra.EM_ANALISE, resultado.getStatus());

        verify(amostraRepository).findById(resultado.getId());
        verify(amostraStatusHistoricoRepository, times(2)).save(any());
    }

    @Test
    public void atualizarStatusAmostraRejeitarNegativo(){
        AmostraRequest request = new AmostraRequest();
        request.setCodAmostra("123");
        request.setTipoColeta("SANGUE");
        request.setDataColeta(LocalDateTime.now());

        Amostra amostraSalva = new Amostra(
                request.getCodAmostra(),
                request.getTipoColeta(),
                request.getDataColeta()
        );
        amostraSalva.setId(UUID.randomUUID());

        when(amostraRepository.findByCodAmostra("123"))
                .thenReturn(Optional.empty());

        when(amostraRepository.save(any(Amostra.class)))
                .thenReturn(amostraSalva);

        when(amostraRepository.findById(amostraSalva.getId()))
            .thenReturn(Optional.of(amostraSalva));


        Amostra amostra = amostraService.criaAmostra(request);

        Exception exception = Assertions.assertThrows(
				TrocaStatusInvalidException.class,
				() -> amostraService.rejeitarAmostra(amostra.getId())
		);

		assertTrue(exception.getMessage().contains("Não é possível rejeitar a amostra"));
        assertEquals(StatusAmostra.PENDENTE, amostra.getStatus());

        verify(amostraRepository).findByCodAmostra("123");
    }

    @Test
    public void atualizarStatusAmostraAprovarNegativo(){
        AmostraRequest request = new AmostraRequest();
        request.setCodAmostra("123");
        request.setTipoColeta("SANGUE");
        request.setDataColeta(LocalDateTime.now());

        Amostra amostraSalva = new Amostra(
                request.getCodAmostra(),
                request.getTipoColeta(),
                request.getDataColeta()
        );
        amostraSalva.setId(UUID.randomUUID());

        when(amostraRepository.findByCodAmostra("123"))
                .thenReturn(Optional.empty());

        when(amostraRepository.save(any(Amostra.class)))
                .thenReturn(amostraSalva);

        when(amostraRepository.findById(amostraSalva.getId()))
            .thenReturn(Optional.of(amostraSalva));


        Amostra amostra = amostraService.criaAmostra(request);

        Exception exception = Assertions.assertThrows(
				TrocaStatusInvalidException.class,
				() -> amostraService.aprovarAmostra(amostra.getId())
		);

		assertTrue(exception.getMessage().contains("Não é possível aprovar a amostra"));
        assertEquals(StatusAmostra.PENDENTE, amostra.getStatus());

        verify(amostraRepository).findByCodAmostra("123");
    }

    @Test
    public void atualizarStatusAmostraAprovarPositivo(){
        AmostraRequest request = new AmostraRequest();
        request.setCodAmostra("123");
        request.setTipoColeta("SANGUE");
        request.setDataColeta(LocalDateTime.now());

        Amostra amostraSalva = new Amostra(
                request.getCodAmostra(),
                request.getTipoColeta(),
                request.getDataColeta()
        );
        amostraSalva.setId(UUID.randomUUID());

        when(amostraRepository.findByCodAmostra("123"))
                .thenReturn(Optional.empty());

        when(amostraRepository.save(any(Amostra.class)))
                .thenReturn(amostraSalva);

        when(amostraRepository.findById(amostraSalva.getId()))
            .thenReturn(Optional.of(amostraSalva));


        Amostra amostra = amostraService.criaAmostra(request);
        amostra = amostraService.atualizarStatusAmostra(amostra.getId());
        amostra = amostraService.atualizarStatusAmostra(amostra.getId());
        amostra = amostraService.aprovarAmostra(amostra.getId());

        assertEquals(StatusAmostra.APROVADA, amostra.getStatus());

        verify(amostraRepository).findByCodAmostra("123");
        verify(amostraStatusHistoricoRepository, times(4)).save(any());
    }

    @Test
    public void atualizarStatusAmostraRejeitarPositivo(){
        AmostraRequest request = new AmostraRequest();
        request.setCodAmostra("123");
        request.setTipoColeta("SANGUE");
        request.setDataColeta(LocalDateTime.now());

        Amostra amostraSalva = new Amostra(
                request.getCodAmostra(),
                request.getTipoColeta(),
                request.getDataColeta()
        );
        amostraSalva.setId(UUID.randomUUID());

        when(amostraRepository.findByCodAmostra("123"))
                .thenReturn(Optional.empty());

        when(amostraRepository.save(any(Amostra.class)))
                .thenReturn(amostraSalva);

        when(amostraRepository.findById(amostraSalva.getId()))
            .thenReturn(Optional.of(amostraSalva));


        Amostra amostra = amostraService.criaAmostra(request);
        amostra = amostraService.atualizarStatusAmostra(amostra.getId());
        amostra = amostraService.atualizarStatusAmostra(amostra.getId());
        amostra = amostraService.rejeitarAmostra(amostra.getId());

        assertEquals(StatusAmostra.REJEITADA, amostra.getStatus());

        verify(amostraRepository).findByCodAmostra("123");
        verify(amostraStatusHistoricoRepository, times(4)).save(any());
    }


}
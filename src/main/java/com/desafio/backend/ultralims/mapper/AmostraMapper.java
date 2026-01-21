package com.desafio.backend.ultralims.mapper;

import org.springframework.stereotype.Component;

import com.desafio.backend.ultralims.entity.Amostra;
import com.desafio.backend.ultralims.request.AmostraRequest;
import com.desafio.backend.ultralims.request.AtualizarAmostraRequest;
import com.desafio.backend.ultralims.response.AmostraDetalheResponse;
import com.desafio.backend.ultralims.response.AmostraResponse;

@Component
public class AmostraMapper {

    public Amostra AmostraRequestToAmostra(AmostraRequest amostraRequest){
        return new Amostra(
                        amostraRequest.getCodAmostra(),
                        amostraRequest.getTipoColeta(),
                        amostraRequest.getDataColeta()
                    );
    }

    public AmostraDetalheResponse AmostraToAmostraDetalheResponse(Amostra amostra){
        return new AmostraDetalheResponse(
                        amostra.getId(),
                        amostra.getCodAmostra(),
                        amostra.getTipoColeta(),
                        amostra.getDataColeta(),
                        amostra.getStatus(),
                        amostra.getHistoricoStatus()
                    );
    }

    public AmostraResponse AmostraToAmostraResponse(Amostra amostra){
        return new AmostraResponse(
                        amostra.getId(),
                        amostra.getCodAmostra(),
                        amostra.getTipoColeta(),
                        amostra.getDataColeta(),
                        amostra.getStatus()
                    );
    }

    public void AmostraAtualizarRequestToAmostra(Amostra amostra, AtualizarAmostraRequest amostraAtualizada){
        if (amostraAtualizada.getTipoColeta() != null) {
            amostra.setTipoColeta(amostraAtualizada.getTipoColeta());
        }

        if (amostraAtualizada.getDataColeta() != null) {
            amostra.setDataColeta(amostraAtualizada.getDataColeta());
        }

        if (amostraAtualizada.getCodAmostra() != null) {
            amostra.setCodAmostra(amostraAtualizada.getCodAmostra());
        }
    }
}

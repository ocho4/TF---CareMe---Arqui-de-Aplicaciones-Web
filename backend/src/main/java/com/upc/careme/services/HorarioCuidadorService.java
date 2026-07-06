package com.upc.careme.services;

import com.upc.careme.dtos.HorarioRequestDTO;
import com.upc.careme.entidades.Cuidador;
import com.upc.careme.entidades.HorarioCuidador;
import com.upc.careme.repositorios.CuidadorRepository;
import com.upc.careme.repositorios.HorarioCuidadorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HorarioCuidadorService {

    @Autowired
    private HorarioCuidadorRepository horarioRepository;

    @Autowired
    private CuidadorRepository cuidadorRepository;

    public String registrarHorario(HorarioRequestDTO request) {

        Cuidador cuidador = cuidadorRepository.findById(request.getIdCuidador())
                .orElseThrow(() -> new IllegalArgumentException("El cuidador especificado no existe."));


        if (request.getHoraInicio().isAfter(request.getHoraFin())) {
            throw new IllegalArgumentException("La hora de inicio no puede ser posterior a la hora de fin.");
        }


        HorarioCuidador horario = new HorarioCuidador();
        horario.setCuidador(cuidador);
        horario.setDiaSemana(request.getDiaSemana());
        horario.setHoraInicio(request.getHoraInicio());
        horario.setHoraFin(request.getHoraFin());




        horarioRepository.save(horario);

        return "Horario registrado exitosamente: Los " + request.getDiaSemana() + " de " + request.getHoraInicio() + " a " + request.getHoraFin() + ".";
    }
}
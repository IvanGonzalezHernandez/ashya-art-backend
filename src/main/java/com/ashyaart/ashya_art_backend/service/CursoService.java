package com.ashyaart.ashya_art_backend.service;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ashyaart.ashya_art_backend.assembler.CursoAssembler;
import com.ashyaart.ashya_art_backend.entity.Curso;
import com.ashyaart.ashya_art_backend.filter.CursoFilter;
import com.ashyaart.ashya_art_backend.model.ClienteSolicitudCursoDto;
import com.ashyaart.ashya_art_backend.model.CursoDto;
import com.ashyaart.ashya_art_backend.repository.CursoDao;

@Service
public class CursoService {

    private static final Logger logger = LoggerFactory.getLogger(CursoService.class);

    @Autowired
    private CursoDao cursoDao;
    
    @Autowired
    private EmailService emailService;

    public List<CursoDto> findByFilter(CursoFilter filter) {
        logger.info("findByFilter - Iniciando búsqueda de cursos con filtro: {}", filter);
        List<Curso> cursos = cursoDao.findByFiltros(filter.getNombre());
        List<CursoDto> resultado = cursos.stream().map(CursoAssembler::toDto).toList();
        logger.info("findByFilter - Se encontraron {} cursos con el filtro", resultado.size());
        return resultado;
    }
    

    public CursoDto obtenerCursoPorId(Long id) {
        logger.info("obtenerCursoPorId - Buscando curso con ID: {}", id);
        Optional<Curso> cursoOpt = cursoDao.findById(id);

        if (cursoOpt.isPresent()) {
            CursoDto dto = CursoAssembler.toDto(cursoOpt.get());
            logger.info("obtenerCursoPorId - Curso encontrado con ID: {}", id);
            return dto;
        } else {
            logger.warn("obtenerCursoPorId - Curso con ID {} no encontrado", id);
            return null;
        }
    }



    @Transactional
    public CursoDto crearCurso(CursoDto cursoDto) {
        logger.info("crearCurso - Creando nuevo curso: {}", cursoDto);
        Curso curso = CursoAssembler.toEntity(cursoDto);
        curso.setId(null);
        Curso cursoGuardado = cursoDao.save(curso);
        CursoDto dtoGuardado = CursoAssembler.toDto(cursoGuardado);
        logger.info("crearCurso - Curso creado con ID: {}", dtoGuardado.getId());
        return dtoGuardado;
    }

    @Transactional
    public CursoDto actualizarCurso(CursoDto cursoDto) {
        Curso curso = cursoDao.findById(cursoDto.getId())
            .orElseThrow(() -> new EntityNotFoundException("Curso no encontrado con ID: " + cursoDto.getId()));

        // Campos normales
        curso.setNombre(cursoDto.getNombre());
        curso.setSubtitulo(cursoDto.getSubtitulo());
        curso.setDescripcion(cursoDto.getDescripcion());
        curso.setPrecio(cursoDto.getPrecio());
        curso.setFechaBaja(cursoDto.getFechaBaja());
        curso.setNivel(cursoDto.getNivel());
        curso.setDuracion(cursoDto.getDuracion());
        curso.setPiezas(cursoDto.getPiezas());
        curso.setMateriales(cursoDto.getMateriales());
        curso.setLocalizacion(cursoDto.getLocalizacion());
        curso.setPlazasMaximas(cursoDto.getPlazasMaximas());
        curso.setInformacionExtra(cursoDto.getInformacionExtra());

        // Borrados explícitos
        if (Boolean.TRUE.equals(cursoDto.getDeleteImg1())) curso.setImg1(null);
        if (Boolean.TRUE.equals(cursoDto.getDeleteImg2())) curso.setImg2(null);
        if (Boolean.TRUE.equals(cursoDto.getDeleteImg3())) curso.setImg3(null);
        if (Boolean.TRUE.equals(cursoDto.getDeleteImg4())) curso.setImg4(null);
        if (Boolean.TRUE.equals(cursoDto.getDeleteImg5())) curso.setImg5(null);

        // Reemplazos (si llegaron bytes nuevos tienen prioridad sobre el flag)
        if (cursoDto.getImg1() != null) curso.setImg1(cursoDto.getImg1());
        if (cursoDto.getImg2() != null) curso.setImg2(cursoDto.getImg2());
        if (cursoDto.getImg3() != null) curso.setImg3(cursoDto.getImg3());
        if (cursoDto.getImg4() != null) curso.setImg4(cursoDto.getImg4());
        if (cursoDto.getImg5() != null) curso.setImg5(cursoDto.getImg5());

        Curso guardado = cursoDao.save(curso);
        return CursoAssembler.toDto(guardado);
    }


    @Transactional
    public void eliminarCurso(Long id) {
        logger.info("eliminarCurso - Intentando eliminar curso con ID: {}", id);
        if (!cursoDao.existsById(id)) {
            logger.warn("eliminarCurso - Curso con ID {} no encontrado", id);
            throw new RuntimeException("Curso con id " + id + " no encontrado");
        }
        Integer filasAfectadas = cursoDao.borradoLogico(id);
        if (filasAfectadas == 0) {
            logger.error("eliminarCurso - No se pudo eliminar el curso con ID: {}", id);
            throw new RuntimeException("No se pudo eliminar el curso con id " + id);
        }
        logger.info("eliminarCurso - Curso con ID {} eliminado correctamente (borrado lógico)", id);
    }
    
    public void solicitarCurso(ClienteSolicitudCursoDto solicitud) {
        logger.info("Procesando solicitud de curso para el cliente: {} {}", solicitud.getNombre(), solicitud.getApellido());
        
        // Email de confirmación al cliente
        String destinatario = solicitud.getEmail();
        String asunto = "Course request confirmation";
        String cuerpo = "Hello " + solicitud.getNombre() + ",\n\n" +
                        "We have received your request for the course: " + solicitud.getTipoClase() + ".\n" +
                        "We will contact you soon to coordinate the details.\n\n" +
                        "Best regards,\nAshya Art";

        emailService.enviarEmailConfirmacion(destinatario, asunto, cuerpo);

        // Email de notificación al administrador
        emailService.enviarSolicitudCursoAdmin(
            solicitud.getNombre(),
            solicitud.getApellido(),
            solicitud.getEmail(),
            solicitud.getTelefono(),
            solicitud.getTipoClase(),
            solicitud.getPersonasInteresadas(),
            solicitud.getDisponibilidad(),
            solicitud.getPreguntasAdicionales()
        );
    }

}

package com.ashyaart.ashya_art_backend.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.ashyaart.ashya_art_backend.assembler.CursoAssembler;
import com.ashyaart.ashya_art_backend.entity.Curso;
import com.ashyaart.ashya_art_backend.filter.CursoFilter;
import com.ashyaart.ashya_art_backend.model.ClienteSolicitudCursoDto;
import com.ashyaart.ashya_art_backend.model.CursoDto;
import com.ashyaart.ashya_art_backend.repository.CursoDao;

@Service
public class CursoService {

    private static final Logger logger = LoggerFactory.getLogger(CursoService.class);

    private static final String SUBFOLDER = "cursos";

    @Autowired
    private CursoDao cursoDao;

    @Autowired
    private EmailService emailService;

    @Autowired
    private FileStorageService fileStorageService;

    public List<CursoDto> findByFilter(CursoFilter filter) {
        logger.info("findByFilter - Iniciando búsqueda de cursos con filtro: {}", filter);
        List<Curso> cursos = cursoDao.findByFiltros(filter.getNombre());
        List<CursoDto> resultado = cursos.stream().map(CursoAssembler::toDto).toList();
        logger.info("findByFilter - Se encontraron {} cursos con el filtro", resultado.size());
        return resultado;
    }
    
    public List<CursoDto> findCursosHabilitados() {
        List<Curso> cursos = cursoDao.findCursosHabilitados();
        return cursos.stream().map(CursoAssembler::toDto).toList();
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
    public CursoDto crearCurso(CursoDto cursoDto, MultipartFile img1, MultipartFile img2, MultipartFile img3,
            MultipartFile img4, MultipartFile img5) throws IOException {
        logger.info("crearCurso - Creando nuevo curso: {}", cursoDto);
        Curso curso = CursoAssembler.toEntity(cursoDto);
        curso.setId(null);

        curso.setImg1Url(storeIfPresent(img1));
        curso.setImg2Url(storeIfPresent(img2));
        curso.setImg3Url(storeIfPresent(img3));
        curso.setImg4Url(storeIfPresent(img4));
        curso.setImg5Url(storeIfPresent(img5));

        Curso cursoGuardado = cursoDao.save(curso);
        CursoDto dtoGuardado = CursoAssembler.toDto(cursoGuardado);
        logger.info("crearCurso - Curso creado con ID: {}", dtoGuardado.getId());
        return dtoGuardado;
    }

    @Transactional
    public CursoDto actualizarCurso(CursoDto cursoDto, MultipartFile img1, MultipartFile img2, MultipartFile img3,
            MultipartFile img4, MultipartFile img5) throws IOException {
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
        curso.setOrden(cursoDto.getOrden());
        curso.setEstado(cursoDto.getEstado());

        // Imágenes: borrar -> elimina archivo y deja null; reemplazar -> borra la anterior y guarda la nueva; nada -> conserva
        curso.setImg1Url(mergeImagen(curso.getImg1Url(), img1, Boolean.TRUE.equals(cursoDto.getDeleteImg1())));
        curso.setImg2Url(mergeImagen(curso.getImg2Url(), img2, Boolean.TRUE.equals(cursoDto.getDeleteImg2())));
        curso.setImg3Url(mergeImagen(curso.getImg3Url(), img3, Boolean.TRUE.equals(cursoDto.getDeleteImg3())));
        curso.setImg4Url(mergeImagen(curso.getImg4Url(), img4, Boolean.TRUE.equals(cursoDto.getDeleteImg4())));
        curso.setImg5Url(mergeImagen(curso.getImg5Url(), img5, Boolean.TRUE.equals(cursoDto.getDeleteImg5())));

        Curso guardado = cursoDao.save(curso);
        return CursoAssembler.toDto(guardado);
    }

    private String storeIfPresent(MultipartFile file) throws IOException {
        if (file != null && !file.isEmpty()) {
            return fileStorageService.store(file, SUBFOLDER);
        }
        return null;
    }

    private String mergeImagen(String urlActual, MultipartFile nuevaImagen, boolean eliminar) throws IOException {
        if (nuevaImagen != null && !nuevaImagen.isEmpty()) {
            fileStorageService.delete(urlActual);
            return fileStorageService.store(nuevaImagen, SUBFOLDER);
        }
        if (eliminar) {
            fileStorageService.delete(urlActual);
            return null;
        }
        return urlActual;
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

        // Email al cliente
        emailService.enviarConfirmacionSolicitudCursoCliente(
            solicitud.getNombre(),
            solicitud.getTipoClase(),
            solicitud.getEmail()
        );

        // Email al administrador
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
